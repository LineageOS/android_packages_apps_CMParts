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

import com.cyanogenmod.cmparts.R;

public class LockscreenActivity extends PreferenceActivity {

    private final static String LOCKSCREEN_DISABLE_ON_SECURITY = "pref_lockscreen_disable_on_security";

    private CheckBoxPreference mLockscreenDisableOnSecurity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.lockscreen_settings_title_subhead);
        addPreferencesFromResource(R.xml.lockscreen_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mLockscreenDisableOnSecurity = (CheckBoxPreference) prefSet
                .findPreference(LOCKSCREEN_DISABLE_ON_SECURITY);

        mLockscreenDisableOnSecurity.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_DISABLE_ON_SECURITY, 0) == 1);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mLockscreenDisableOnSecurity) {
            value = mLockscreenDisableOnSecurity.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_DISABLE_ON_SECURITY, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
