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

public class PhoneGogglesAppActivity extends PreferenceActivity {

    private CheckBoxPreference mPhoneEnabled;
    private CheckBoxPreference mSmsEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_phone_goggles_app);

        addPreferencesFromResource(R.xml.phone_goggles_app_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mPhoneEnabled = (CheckBoxPreference) prefSet.findPreference(
                Settings.System.PHONE_GOGGLES_PHONE_ENABLED);
        mPhoneEnabled.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PHONE_GOGGLES_PHONE_ENABLED, 1) != 0);
        mSmsEnabled = (CheckBoxPreference) prefSet.findPreference(
                Settings.System.PHONE_GOGGLES_SMS_ENABLED);
        mSmsEnabled.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PHONE_GOGGLES_SMS_ENABLED, 1) != 0);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mPhoneEnabled) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PHONE_GOGGLES_PHONE_ENABLED,
                    mPhoneEnabled.isChecked() ? 1 : 0);
            return true;
        }
        else if (preference == mSmsEnabled){
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PHONE_GOGGLES_SMS_ENABLED,
                    mSmsEnabled.isChecked() ? 1 : 0);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
