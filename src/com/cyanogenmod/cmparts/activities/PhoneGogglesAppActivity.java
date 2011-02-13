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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;

public class PhoneGogglesAppActivity
extends PhoneGogglesAbstractActivity {

    private CheckBoxPreference mPhoneGogglesEnabled;
    private CheckBoxPreference mPhoneGogglesUseCustom;

    private String mAppEnabledKey;
    private String mAppUseCustomKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String appName = getIntent().getStringExtra("appName");
        String appId = getIntent().getStringExtra("appId");
        mAppEnabledKey = Settings.System.PHONE_GOGGLES_APP_ENABLED + "_" + appId;
        mAppUseCustomKey = Settings.System.PHONE_GOGGLES_USE_CUSTOM + "_" + appId;

        addPreferencesFromResource(R.xml.phone_goggles_app_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mPhoneGogglesEnabled = (CheckBoxPreference) prefSet.
        findPreference(Settings.System.PHONE_GOGGLES_APP_ENABLED);
        mPhoneGogglesUseCustom = (CheckBoxPreference) prefSet.
        findPreference(Settings.System.PHONE_GOGGLES_USE_CUSTOM);
        mPhoneGogglesFilter = prefSet.findPreference(FILTER_ID);
        mPhoneGogglesConfirmation = (ListPreference) prefSet.
        findPreference(Settings.System.PHONE_GOGGLES_CONFIRMATION_MODE);
        mPhoneGogglesMathsLevel = (ListPreference) prefSet.
        findPreference(Settings.System.PHONE_GOGGLES_MATHS_LEVEL);
        mPhoneGogglesStarts = prefSet.
        findPreference(Settings.System.PHONE_GOGGLES_START);
        mPhoneGogglesEnds = prefSet.
        findPreference(Settings.System.PHONE_GOGGLES_END);

        mPhoneGogglesConfirmation.setOnPreferenceChangeListener(this);
        mPhoneGogglesMathsLevel.setOnPreferenceChangeListener(this);

        mPhoneGogglesEnabled.setChecked(Settings.System.getInt(getContentResolver(),
                mAppEnabledKey, 0) != 0);
        mPhoneGogglesUseCustom.setChecked(Settings.System.getInt(getContentResolver(),
                mAppUseCustomKey, 0) != 0);

        setAppValues(appId, appName);
        setTitle(appName);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {

        if (preference == mPhoneGogglesEnabled) {
            return Settings.System.putInt(getContentResolver(), mAppEnabledKey,
                    mPhoneGogglesEnabled.isChecked() ? 1 : 0);
        }
        else if (preference == mPhoneGogglesUseCustom) {
            return Settings.System.putInt(getContentResolver(), mAppUseCustomKey,
                    mPhoneGogglesUseCustom.isChecked() ? 1 : 0);
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
