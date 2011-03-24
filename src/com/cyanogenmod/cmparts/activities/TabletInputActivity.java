/*
 * Created by Sven Dawitz; Copyright (C) 2011 CyanogenMod Project
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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.CmSystem;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;

public class TabletInputActivity extends PreferenceActivity implements OnPreferenceChangeListener {
    private static final String PREF_LONG_VOLP_ACTION = "pref_long_volp_action";
    private static final String PREF_LONG_VOLM_ACTION = "pref_long_volm_action";
    private static final String PREF_LONG_VOL_BOTH_ACTION = "pref_long_vol_both_action";
    private static final String PREF_VOL_BOTH_ACTION = "pref_vol_both_action";

    private ListPreference mLongVolpAction;
    private ListPreference mLongVolmAction;
    private ListPreference mVolBothAction;
    private ListPreference mLongVolBothAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.tablet_tweaks_input_head);
        addPreferencesFromResource(R.xml.tablet_input_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mLongVolpAction = (ListPreference) prefSet.findPreference(PREF_LONG_VOLP_ACTION);
        mLongVolmAction = (ListPreference) prefSet.findPreference(PREF_LONG_VOLM_ACTION);
        mVolBothAction = (ListPreference) prefSet.findPreference(PREF_VOL_BOTH_ACTION);
        mLongVolBothAction = (ListPreference) prefSet.findPreference(PREF_LONG_VOL_BOTH_ACTION);

        int defValue;

        defValue=CmSystem.getDefaultInt(getBaseContext(), CmSystem.CM_DEFAULT_REMAPPED_LONG_VOL_UP_INDEX);
        mLongVolpAction.setOnPreferenceChangeListener(this);
        mLongVolpAction.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.LONG_VOLP_ACTION, defValue));

        defValue=CmSystem.getDefaultInt(getBaseContext(), CmSystem.CM_DEFAULT_REMAPPED_LONG_VOL_DOWN_INDEX);
        mLongVolmAction.setOnPreferenceChangeListener(this);
        mLongVolmAction.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.LONG_VOLM_ACTION, defValue));

        defValue=CmSystem.getDefaultInt(getBaseContext(), CmSystem.CM_DEFAULT_REMAPPED_BOTH_VOL_INDEX);
        mVolBothAction.setOnPreferenceChangeListener(this);
        mVolBothAction.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.VOL_BOTH_ACTION, defValue));

        defValue=CmSystem.getDefaultInt(getBaseContext(), CmSystem.CM_DEFAULT_REMAPPED_LONG_BOTH_VOL_INDEX);
        mLongVolBothAction.setOnPreferenceChangeListener(this);
        mLongVolBothAction.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.LONG_VOL_BOTH_ACTION, defValue));
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLongVolpAction) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.LONG_VOLP_ACTION,
                    value);
            return true;
        } else if (preference == mLongVolmAction) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.LONG_VOLM_ACTION,
                    value);
            return true;
        } else if (preference == mVolBothAction) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.VOL_BOTH_ACTION,
                    value);
            return true;
        } else if (preference == mLongVolBothAction) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.LONG_VOL_BOTH_ACTION,
                    value);
            return true;
        }
        return false;
    }
}
