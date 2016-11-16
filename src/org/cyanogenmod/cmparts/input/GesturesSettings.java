/*
 * Copyright (C) 2016 The CyanogenMod project
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

package org.cyanogenmod.cmparts.input;

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v14.preference.SwitchPreference;

import org.cyanogenmod.cmparts.R;
import org.cyanogenmod.cmparts.SettingsPreferenceFragment;

import cyanogenmod.providers.CMSettings;

public class GesturesSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_FLIP_TO_MUTE = "flip_to_mute_gesture";

    private Context mContext;
    private SwitchPreference mFlipToMuitePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.gestures_settings);

        mContext = getActivity();

        mFlipToMuitePref = (SwitchPreference) findPreference(KEY_FLIP_TO_MUTE);
        mFlipToMuitePref.setOnPreferenceChangeListener(this);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFlipToMuitePref) {
            boolean isFlipOn = (Boolean) newValue;
            int mValue = isFlipOn ?
                    CMSettings.Secure.MOTION_BEHAVIOR_FLIP_TO_MUTE_INCOMING_CALL :
                    CMSettings.Secure.MOTION_BEHAVIOR_NOTHING;
            CMSettings.Secure.putInt(mContext.getContentResolver(),
                    CMSettings.Secure.MOTION_BEHAVIOR, mValue);
            return true;
        }

        return true;
    }

}
