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

import android.app.admin.DevicePolicyManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;

public class DockActivity extends PreferenceActivity {
    private static CheckBoxPreference mDockLockPref;
    private static String DOCK_LOCK_PREF = "pref_dock_nolock";

    private static CheckBoxPreference mDockBluetoothPref;
    private static String DOCK_BLUETOOTH_PREF = "pref_dock_bluetooth";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.dock_settings_title_subhead);
        addPreferencesFromResource(R.xml.dock_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        mDockLockPref = (CheckBoxPreference) prefSet.findPreference(DOCK_LOCK_PREF);
        mDockLockPref.setChecked(Settings.System.getInt(getContentResolver(),
            Settings.System.DOCK_NOLOCK, 0) == 1);
        mDockLockPref.setOnPreferenceClickListener(setPrefListener);

        mDockBluetoothPref = (CheckBoxPreference) prefSet.findPreference(DOCK_BLUETOOTH_PREF);
        mDockBluetoothPref.setChecked(Settings.System.getInt(getContentResolver(),
            Settings.System.DOCK_BLUETOOTH, 0) == 1);
        mDockBluetoothPref.setOnPreferenceClickListener(setPrefListener);
    }

    private OnPreferenceClickListener setPrefListener = new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) { 
            if(preference == mDockLockPref) {
                boolean value = mDockLockPref.isChecked();
                Settings.System.putInt(getContentResolver(), Settings.System.DOCK_NOLOCK, value ? 1 : 0);
                return true;
            }
            if(preference == mDockBluetoothPref) {
                boolean value = mDockBluetoothPref.isChecked();
                Settings.System.putInt(getContentResolver(), Settings.System.DOCK_BLUETOOTH, value ? 1 : 0);
                return true;
            }
            return false;
        }
    }; 
}
