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

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.PhoneGoggles;
import android.widget.TimePicker;

public abstract class PhoneGogglesAbstractActivity
extends PreferenceActivity
implements OnPreferenceChangeListener {

    protected static final String FILTER_ID = "phone_goggles_filter_settings";
    private static final int DIALOG_PHONE_GOGGLES_START = 1;
    private static final int DIALOG_PHONE_GOGGLES_END = 2;

    private String mAppId;
    private String mAppName;

    protected String mConfirmationModeKey;
    protected String mMathLevelKey;
    protected String mStartKey;
    protected String mEndKey;

    protected Preference mPhoneGogglesStarts;
    protected Preference mPhoneGogglesEnds;
    protected Preference mPhoneGogglesFilter;

    protected ListPreference mPhoneGogglesConfirmation;
    protected ListPreference mPhoneGogglesMathsLevel;

    protected void setAppValues(String appId, String appName) {
        mAppId = appId;
        mAppName = appName;
        mConfirmationModeKey = Settings.System.PHONE_GOGGLES_CONFIRMATION_MODE + "_" + appId;
        mMathLevelKey = Settings.System.PHONE_GOGGLES_MATHS_LEVEL + "_" + appId;
        mStartKey = Settings.System.PHONE_GOGGLES_START + "_" + appId;
        mEndKey = Settings.System.PHONE_GOGGLES_END + "_" + appId;
    }

    @Override
    protected void onResume() {
        int confirmationValue = Settings.System.getInt(getContentResolver(),
                mConfirmationModeKey, PhoneGoggles.CONFIRMATION_MODE_NONE);
        updateMathLevelEnabled(confirmationValue);
        mPhoneGogglesConfirmation.setValue(Integer.toString(confirmationValue));
        int levelValue = Settings.System.getInt(getContentResolver(),
                mMathLevelKey, 1);
        mPhoneGogglesMathsLevel.setValue(Integer.toString(levelValue));
        super.onResume();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {

        if (preference == mPhoneGogglesStarts) {
            showDialog(DIALOG_PHONE_GOGGLES_START);
            return true;
        }
        else if (preference == mPhoneGogglesEnds) {
            showDialog(DIALOG_PHONE_GOGGLES_END);
            return true;
        }
        else if (preference == mPhoneGogglesFilter) {
            Intent intent = new Intent(this, PhoneGogglesFilterActivity.class);
            intent.putExtra("appName", mAppName);
            intent.putExtra("appId", mAppId);
            startActivity(intent);
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference == mPhoneGogglesConfirmation) {
            int value = Integer.parseInt((String)newValue);
            updateMathLevelEnabled(value);
            return Settings.System.putInt(getContentResolver(), mConfirmationModeKey,
                    value);
        }
        else if (preference == mPhoneGogglesMathsLevel) {
            int value = Integer.parseInt((String)newValue);
            return Settings.System.putInt(getContentResolver(), mMathLevelKey,
                    value);
        }

        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        switch (id) {
            case DIALOG_PHONE_GOGGLES_START:
                return createTimePicker(mStartKey);
            case DIALOG_PHONE_GOGGLES_END:
                return createTimePicker(mEndKey);
        }

        return super.onCreateDialog(id, bundle);
    }

    private TimePickerDialog createTimePicker(final String key) {
        int hour, minutes, value, defaultValue;

        if (key.startsWith(Settings.System.PHONE_GOGGLES_START)) {
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

    private void updateMathLevelEnabled(
            int value) {

        mPhoneGogglesMathsLevel.setEnabled(value == PhoneGoggles.CONFIRMATION_MODE_MATHS);
    }
}
