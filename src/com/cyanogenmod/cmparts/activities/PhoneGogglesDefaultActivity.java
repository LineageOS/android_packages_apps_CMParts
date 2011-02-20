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
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;

public class PhoneGogglesDefaultActivity
extends PhoneGogglesAbstractActivity
implements OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String appName = getString(R.string.title_phone_goggles_default);
        String appId = "default";

        addPreferencesFromResource(R.xml.phone_goggles_default_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

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

        setAppValues(appId, appName);
        setTitle(appName);
    }
}
