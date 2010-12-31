package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Process;

//
// CPU Related Settings
//
public class CPUActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    public static final String GOV_PREF = "pref_cpu_gov";
    public static final String GOVERNORS_LIST_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    public static final String GOVERNOR = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";

    public static final String MIN_FREQ_PREF = "pref_freq_min";
    public static final String MAX_FREQ_PREF = "pref_freq_max";
    public static final String FREQ_LIST_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    public static final String FREQ_MAX_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
    public static final String FREQ_MIN_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
    public static final String SOB_PREF = "pref_set_on_boot";
    private static final String TAG = "CPUSettings";
    private String GOV_FMT;
    private String MIN_FMT;
    private String MAX_FMT;

    private ListPreference govPref;
    private ListPreference minFreqPref;
    private ListPreference maxFreqPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GOV_FMT = getString(R.string.cpu_governors_list);
        MIN_FMT = getString(R.string.cpu_min_freq);
        MAX_FMT = getString(R.string.cpu_max_freq);

        String[] Governors = readOneLine(GOVERNORS_LIST_FILE).split(" ");
        String[] FreqValues = readOneLine(FREQ_LIST_FILE).split(" ");
        String[] Freqs;
        String temp;

        Freqs = new String[FreqValues.length];
        for(int i=0; i <Freqs.length; i++) {
            Freqs[i] = MHerzed(FreqValues[i]);
        }

        setTitle(R.string.cpu_title);
        addPreferencesFromResource(R.xml.cpu_settings);

        PreferenceScreen PrefScreen = getPreferenceScreen();

        temp = readOneLine(GOVERNOR);

        govPref = (ListPreference) PrefScreen.findPreference(GOV_PREF);
        govPref.setEntryValues(Governors);
        govPref.setEntries(Governors);
        govPref.setValue(temp);
        govPref.setSummary(String.format(GOV_FMT, temp));
        govPref.setOnPreferenceChangeListener(this);

        temp = readOneLine(FREQ_MIN_FILE);

        minFreqPref = (ListPreference) PrefScreen.findPreference(MIN_FREQ_PREF);
        minFreqPref.setEntryValues(FreqValues);
        minFreqPref.setEntries(Freqs);
        minFreqPref.setValue(temp);
        minFreqPref.setSummary(String.format(MIN_FMT, MHerzed(temp)));
        minFreqPref.setOnPreferenceChangeListener(this);

        temp = readOneLine(FREQ_MAX_FILE);

        maxFreqPref = (ListPreference) PrefScreen.findPreference(MAX_FREQ_PREF);
        maxFreqPref.setEntryValues(FreqValues);
        maxFreqPref.setEntries(Freqs);
        maxFreqPref.setValue(temp);
        maxFreqPref.setSummary(String.format(MAX_FMT, MHerzed(temp)));
        maxFreqPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        String temp;

        super.onResume();

        temp = readOneLine(FREQ_MAX_FILE);
        maxFreqPref.setValue(temp);
        maxFreqPref.setSummary(String.format(MAX_FMT, MHerzed(temp)));

        temp = readOneLine(FREQ_MIN_FILE);
        minFreqPref.setValue(temp);
        minFreqPref.setSummary(String.format(MIN_FMT, MHerzed(temp)));

        temp = readOneLine(GOVERNOR);
        govPref.setSummary(String.format(GOV_FMT, temp));
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String fname = "";

        if (newValue != null) {
            if (preference == govPref) {
                fname = GOVERNOR;
            } else if (preference == minFreqPref) {
                fname = FREQ_MIN_FILE;
            } else if (preference == maxFreqPref) {
                fname = FREQ_MAX_FILE;
            }

           if (writeOneLine(fname, (String) newValue)) {
               if (preference == govPref) {
                   govPref.setSummary(String.format(GOV_FMT, (String) newValue));
               } else if (preference == minFreqPref) {
                   minFreqPref.setSummary(String.format(MIN_FMT, MHerzed((String) newValue)));
               } else if (preference == maxFreqPref) {
                   maxFreqPref.setSummary(String.format(MAX_FMT, MHerzed((String) newValue)));
               }
               return true;
           } else {
               return false;
           }
        }
        return false;
    }

    public static String readOneLine(String fname) {
        BufferedReader br;
        String line = null;

        try {
            br = new BufferedReader (new FileReader(fname), 512);
            try {
                line = br.readLine();
            } finally {
                br.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "IO Exception when reading /sys/ file", e);
        }
        return line;
    }

    public static boolean writeOneLine(String fname, String value) {
        try {
            FileWriter fw = new FileWriter(fname);
            try {
                fw.write(value);
            } finally {
                fw.close();
            }
        } catch(IOException e) {
            String Error = "Error writing to " + fname + ". Exception: ";
            Log.e(TAG, Error, e);
            return false;
        }
        return true;
    }


    private String MHerzed(String str) {
        String temp;

        temp = str.substring(0, str.length() - 3);

        return (temp + " MHz");
    }
}
