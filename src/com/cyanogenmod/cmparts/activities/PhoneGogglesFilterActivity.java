/*
 * Copyright (C) 2006 The Android Open Source Project
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

public class PhoneGogglesFilterActivity extends PreferenceActivity {

    private CheckBoxPreference mWorkEnabled;
    private CheckBoxPreference mMobileEnabled;
    private CheckBoxPreference mOtherEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_phone_goggles_filter);

        addPreferencesFromResource(R.xml.phone_goggles_type_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mWorkEnabled = (CheckBoxPreference) prefSet.findPreference(
                Settings.System.PHONE_GOGGLES_WORK_FILTERED);
        mWorkEnabled.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PHONE_GOGGLES_WORK_FILTERED, 0) != 0);
        mMobileEnabled = (CheckBoxPreference) prefSet.findPreference(
                Settings.System.PHONE_GOGGLES_MOBILE_FILTERED);
        mMobileEnabled.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PHONE_GOGGLES_MOBILE_FILTERED, 0) != 0);
        mOtherEnabled = (CheckBoxPreference) prefSet.findPreference(
                Settings.System.PHONE_GOGGLES_OTHER_FILTERED);
        mOtherEnabled.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PHONE_GOGGLES_OTHER_FILTERED, 0) != 0);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mWorkEnabled) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PHONE_GOGGLES_WORK_FILTERED,
                    mWorkEnabled.isChecked() ? 1 : 0);
            return true;
        }
        else if (preference == mMobileEnabled) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PHONE_GOGGLES_MOBILE_FILTERED,
                    mMobileEnabled.isChecked() ? 1 : 0);
        }
        else if (preference == mOtherEnabled) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PHONE_GOGGLES_OTHER_FILTERED,
                    mOtherEnabled.isChecked() ? 1 : 0);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
