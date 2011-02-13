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

public class PhoneGogglesFilterActivity
extends PreferenceActivity {

    protected String mWorkFilterKey;
    protected String mMobileFilterKey;
    protected String mOtherFilterKey;

    private CheckBoxPreference mWorkEnabled;
    private CheckBoxPreference mMobileEnabled;
    private CheckBoxPreference mOtherEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getIntent().getStringExtra("appName"));

        String appId = getIntent().getStringExtra("appId");
        mWorkFilterKey = Settings.System.PHONE_GOGGLES_WORK_FILTERED + "_" + appId;
        mMobileFilterKey = Settings.System.PHONE_GOGGLES_MOBILE_FILTERED + "_" + appId;
        mOtherFilterKey = Settings.System.PHONE_GOGGLES_OTHER_FILTERED + "_" + appId;

        addPreferencesFromResource(R.xml.phone_goggles_type_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mWorkEnabled = (CheckBoxPreference) prefSet.findPreference(
                Settings.System.PHONE_GOGGLES_WORK_FILTERED);
        mWorkEnabled.setChecked(Settings.System.getInt(getContentResolver(),
                mWorkFilterKey, 0) != 0);
        mMobileEnabled = (CheckBoxPreference) prefSet.findPreference(
                Settings.System.PHONE_GOGGLES_MOBILE_FILTERED);
        mMobileEnabled.setChecked(Settings.System.getInt(getContentResolver(),
                mMobileFilterKey, 0) != 0);
        mOtherEnabled = (CheckBoxPreference) prefSet.findPreference(
                Settings.System.PHONE_GOGGLES_OTHER_FILTERED);
        mOtherEnabled.setChecked(Settings.System.getInt(getContentResolver(),
                mOtherFilterKey, 0) != 0);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {

        if (preference == mWorkEnabled) {
            return Settings.System.putInt(getContentResolver(), mWorkFilterKey,
                    mWorkEnabled.isChecked() ? 1 : 0);
        }
        else if (preference == mMobileEnabled) {
            return Settings.System.putInt(getContentResolver(), mMobileFilterKey,
                    mMobileEnabled.isChecked() ? 1 : 0);
        }
        else if (preference == mOtherEnabled) {
            return Settings.System.putInt(getContentResolver(), mOtherFilterKey,
                    mOtherEnabled.isChecked() ? 1 : 0);
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
