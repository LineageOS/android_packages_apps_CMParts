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

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.PhoneGoggles;
import android.widget.TimePicker;

import com.cyanogenmod.cmparts.R;

/**
 * Phone goggles allows the user to indicate that he wants his communications to
 * be filtered. When filtering communications, phone goggles will ask for a
 * confirmation before performing a professional communication (or simply cancel
 * it) during a given period of the day. This should allow the user to avoid any
 * inconveniance like calling his boss in the middle of a party...
 *
 * 'Professional communications' refers to any communication where the phone
 * number is in the ContactProvider and it type is setted as TYPE_WORK,
 * TYPE_WORK_MOBILE or TYPE_WORK_PAGER.
 *
 */
public class PhoneGogglesActivity
extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final int DIALOG_PHONE_GOGGLES_START = 1;
    private static final int DIALOG_PHONE_GOGGLES_END = 2;

    private Preference mPhoneGogglesStarts;
    private Preference mPhoneGogglesEnds;
    private CheckBoxPreference mPhoneGogglesEnabled;
    private ListPreference mPhoneGogglesConfirmation;
    private ListPreference mPhoneGogglesMathsLevel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_phone_goggles);

        addPreferencesFromResource(R.xml.phone_goggles_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mPhoneGogglesEnabled = (CheckBoxPreference) prefSet.
        findPreference(Settings.System.PHONE_GOGGLES_ENABLED);
        mPhoneGogglesEnabled.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.PHONE_GOGGLES_ENABLED, 0) != 0);
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
    }

    @Override
    protected void onResume() {
        updateMathLevelEnabled();
        int confirmationValue = Settings.System.getInt(getContentResolver(),
                Settings.System.PHONE_GOGGLES_CONFIRMATION_MODE, 0);
        mPhoneGogglesConfirmation.setValue(Integer.toString(confirmationValue));
        int levelValue = Settings.System.getInt(getContentResolver(),
                Settings.System.PHONE_GOGGLES_MATHS_LEVEL, 1);
        mPhoneGogglesMathsLevel.setValue(Integer.toString(levelValue));
        super.onResume();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mPhoneGogglesEnabled) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PHONE_GOGGLES_ENABLED,
                    mPhoneGogglesEnabled.isChecked() ? 1 : 0);
            return true;
        }
        else if (preference == mPhoneGogglesStarts) {
            showDialog(DIALOG_PHONE_GOGGLES_START);
            return true;
        }
        else if (preference == mPhoneGogglesEnds) {
            showDialog(DIALOG_PHONE_GOGGLES_END);
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference == mPhoneGogglesConfirmation) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PHONE_GOGGLES_CONFIRMATION_MODE,
                    Integer.parseInt((String)newValue));
            updateMathLevelEnabled();
            return true;
        } else if (preference == mPhoneGogglesMathsLevel) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PHONE_GOGGLES_MATHS_LEVEL,
                    Integer.parseInt((String)newValue));
            return true;
        }

        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        switch (id) {
            case DIALOG_PHONE_GOGGLES_START:
                return createTimePicker(Settings.System.PHONE_GOGGLES_START);
            case DIALOG_PHONE_GOGGLES_END:
                return createTimePicker(Settings.System.PHONE_GOGGLES_END);
        }

        return super.onCreateDialog(id, bundle);
    }

    private TimePickerDialog createTimePicker(final String key) {
        int hour, minutes, value, defaultValue;

        if (key.equals(Settings.System.PHONE_GOGGLES_START)) {
            defaultValue = 1320;
        } else {
            defaultValue = 300;
        }

        value = Settings.System.getInt(getContentResolver(), key, defaultValue);
        hour = value / 60;
        minutes = value % 60;

        TimePickerDialog dlg = new TimePickerDialog(
                this, /* context */
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker v, int hours, int minutes) {
                        Settings.System.putInt(getContentResolver(),
                                key, (hours * 60) + minutes);
                    };
                },
                hour, minutes, DateFormat.is24HourFormat(this)
        );
        return dlg;
    }

    private void updateMathLevelEnabled() {
        int value = Settings.System.getInt(getContentResolver(),
                Settings.System.PHONE_GOGGLES_CONFIRMATION_MODE,
                PhoneGoggles.CONFIRMATION_MODE_NONE);

        mPhoneGogglesMathsLevel.setEnabled(value == PhoneGoggles.CONFIRMATION_MODE_MATHS);
    }
}
