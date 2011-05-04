/*
 * Copyright (C) 2011 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Button;

public class PermissionsActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    /* Preference Screens */
    private static final String GENERAL_CATEGORY = "general_category";

    private static final String ENABLE_PERMISSIONS_MANAGMENT = "enable_permissions_managment";

    private static final int DIALOG_ENABLE_WARNING = 0;

    private static final int DIALOG_DISABLE_WARNING = 1;

    private static final int ENABLE = 0;
    private final static int YES=1;
    private final static int NO=2;

    private CheckBoxPreference mEnableManagment;

    private Context mContext = this;

    private Resources mRes;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mRes = mContext.getResources();
        setTitle(R.string.permissions_settings_title_subhead);
        addPreferencesFromResource(R.xml.permissions_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mEnableManagment = (CheckBoxPreference)prefSet.findPreference(ENABLE_PERMISSIONS_MANAGMENT);
        mEnableManagment.setChecked(Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.ENABLE_PERMISSIONS_MANAGMENT,
                getResources().getBoolean(com.android.internal.R.bool.config_enablePermissionsManagment) ? 1 : 0) == 1);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (preference == mEnableManagment) {
            final boolean value = mEnableManagment.isChecked();
            if (value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showDialog(DIALOG_ENABLE_WARNING);
                        }
                        catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else {
                Settings.Secure.putInt(getContentResolver(),
                        Settings.Secure.ENABLE_PERMISSIONS_MANAGMENT, 0);
            }
        }

        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        final AlertDialog ad = new AlertDialog.Builder(this).create();
        switch (id) {
        case DIALOG_ENABLE_WARNING:
            ad.setTitle(mRes.getString(R.string.perm_enable_warning_title));
            ad.setMessage(mRes.getString(R.string.perm_enable_warning_message));
            ad.setCancelable(false);
            final Handler handler = new Handler() {
                public void handleMessage(final Message msg) {
                    switch (msg.what) {
                    case ENABLE:
                        Button b = ad.getButton(DialogInterface.BUTTON_POSITIVE);
                        if (b != null) {
                            b.setEnabled(true);
                        }
                        b = ad.getButton(DialogInterface.BUTTON_NEGATIVE);
                        if (b != null) {
                            b.setEnabled(true);
                        }
                        break;
                    case YES:
                        Settings.Secure.putInt(getContentResolver(),
                                Settings.Secure.ENABLE_PERMISSIONS_MANAGMENT, 1);
                        break;
                    case NO:
                        mEnableManagment.setChecked(false);
                        Settings.Secure.putInt(getContentResolver(),
                                Settings.Secure.ENABLE_PERMISSIONS_MANAGMENT, 0);
                        break;
                    }
                }
            };

            ad.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button b = ad.getButton(DialogInterface.BUTTON_POSITIVE);
                    b.setEnabled(false);
                    b = ad.getButton(DialogInterface.BUTTON_NEGATIVE);
                    b.setEnabled(false);
                    handler.sendMessageDelayed(handler.obtainMessage(ENABLE), 1000);
                }
            });

            //ad.takeKeyEvents(false);
            ad.setButton(DialogInterface.BUTTON_POSITIVE,
                        "Yes",
                        handler.obtainMessage(YES));
            ad.setButton(DialogInterface.BUTTON_NEGATIVE,
                        "No",
                        handler.obtainMessage(NO));
            return ad;
        case DIALOG_DISABLE_WARNING:
            break;
        }
        return null;
    }
}
