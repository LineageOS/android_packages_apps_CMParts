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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.cyanogenmod.cmparts.R;

public class UIStatusBarActivity extends PreferenceActivity {

    private static final String PREF_STATUS_BAR_CLOCK = "pref_status_bar_clock";

    private static final String PREF_STATUS_BAR_CM_BATTERY = "pref_status_bar_cm_battery";

    private static final String PREF_STATUS_BAR_COMPACT_CARRIER = "pref_status_bar_compact_carrier";
    
    private static final String PREF_FORCE_PLMN_DISPLAY = "pref_status_bar_force_plmn_display";

    private CheckBoxPreference mStatusBarClock;

    private CheckBoxPreference mStatusBarCmBattery;

    private CheckBoxPreference mStatusBarCompactCarrier;
    
    private CheckBoxPreference mStatusBarForcePlmnDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.ui_status_bar_title);
        addPreferencesFromResource(R.xml.ui_status_bar);

        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarClock = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_CLOCK);
        mStatusBarCompactCarrier = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_COMPACT_CARRIER);
        mStatusBarCmBattery = (CheckBoxPreference) prefSet
                .findPreference(PREF_STATUS_BAR_CM_BATTERY);
        mStatusBarForcePlmnDisplay = (CheckBoxPreference) prefSet.findPreference(PREF_FORCE_PLMN_DISPLAY);

        mStatusBarClock.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, 1) == 1));
        mStatusBarCmBattery.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CM_BATTERY, 0) == 1));
        mStatusBarCompactCarrier.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_COMPACT_CARRIER, 0) == 1));
        mStatusBarForcePlmnDisplay.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_FORCE_PLMN_DISPLAY, 0) == 1));

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        /* Preference Screens */
        if (preference == mStatusBarClock) {
            value = mStatusBarClock.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_CLOCK,
                    value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarCmBattery) {
            value = mStatusBarCmBattery.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_CM_BATTERY,
                    value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarCompactCarrier) {
            value = mStatusBarCompactCarrier.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_COMPACT_CARRIER,
                    value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarForcePlmnDisplay) {
            value = mStatusBarForcePlmnDisplay.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_FORCE_PLMN_DISPLAY,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }
}
