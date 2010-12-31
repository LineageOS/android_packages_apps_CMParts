package com.cyanogenmod.cmparts.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.cyanogenmod.cmparts.activities.CPUActivity;

public class CPUService extends Service {

    private static final String UserFile = ".nocpu";
    public static final String TAG = "CPUSettings";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private SharedPreferences prefs;

    private String Gov;
    private String MinFreq;
    private String MaxFreq;
    private boolean SetOnBoot;

    @Override
    public void onCreate() {

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        SetOnBoot = prefs.getBoolean(CPUActivity.SOB_PREF, false);
        if (!SetOnBoot) {
            Log.i(TAG, "SetOnBoot is false. Exiting.");
            stopSelf();
        }

        Gov = prefs.getString(CPUActivity.GOV_PREF, null);
        MinFreq = prefs.getString(CPUActivity.MIN_FREQ_PREF, null);
        MaxFreq = prefs.getString(CPUActivity.MAX_FREQ_PREF, null);

        boolean noSettings = (Gov==null) && (MinFreq==null) && (MaxFreq==null) ;
        if (noSettings) {
            Log.i(TAG, "No settings saved. Nothing to restore.");
            stopSelf();
        }

        // Wait 120s for external storage to settle
        final Handler hndl = new Handler();
        hndl.postDelayed(new Runnable() {
            public void run() {
                restoreCPU();
            }
        }, 120000);
    }

    private void restoreCPU () {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
            Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            String fullPath = Environment.getExternalStorageDirectory().toString() + "/" + UserFile;
            File file = new File(fullPath);
            if (file.exists()) {
                Log.i(TAG, "Nothing to do.");
                stopSelf();
            }

            List GovLst = Arrays.asList(
                    CPUActivity.readOneLine(CPUActivity.GOVERNORS_LIST_FILE).split(" "));
            List FreqLst = Arrays.asList(
                    CPUActivity.readOneLine(CPUActivity.FREQ_LIST_FILE).split(" "));

            if (Gov != null && GovLst.contains(Gov)) {
                CPUActivity.writeOneLine(CPUActivity.GOVERNOR, Gov);
            }
            if (MaxFreq != null && FreqLst.contains(MaxFreq)) {
                CPUActivity.writeOneLine(CPUActivity.FREQ_MAX_FILE, MaxFreq);
            }
            if (MinFreq != null && FreqLst.contains(MinFreq)) {
                CPUActivity.writeOneLine(CPUActivity.FREQ_MIN_FILE, MinFreq);
            }
            Log.i(TAG, "CPU Settings restored.");
        }
        stopSelf();
    }
}
