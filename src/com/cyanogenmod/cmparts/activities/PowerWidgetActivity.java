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

import com.android.internal.telephony.Phone;

import android.content.ContentResolver;
import android.net.wimax.WimaxHelper;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import com.cyanogenmod.cmparts.R;
import com.cyanogenmod.cmparts.utils.PowerWidgetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PowerWidgetActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "PowerWidgetActivity";

    private static final String BUTTONS_CATEGORY = "pref_buttons";
    private static final String SELECT_BUTTON_KEY_PREFIX = "pref_button_";

    private static final String EXP_BRIGHTNESS_MODE = "pref_brightness_mode";
    private static final String EXP_NETWORK_MODE = "pref_network_mode";
    private static final String EXP_SCREENTIMEOUT_MODE = "pref_screentimeout_mode";
    private static final String EXP_RING_MODE = "pref_ring_mode";
    private static final String EXP_FLASH_MODE = "pref_flash_mode";
    private static final String EXP_MOBILEDATANETWORK_MODE = "pref_mobiledatanetwork_mode";

    private HashMap<CheckBoxPreference, String> mCheckBoxPrefs = new HashMap<CheckBoxPreference, String>();

    MultiSelectListPreference mBrightnessMode;
    ListPreference mNetworkMode;
    ListPreference mScreenTimeoutMode;
    MultiSelectListPreference mRingMode;
    ListPreference mFlashMode;
    ListPreference mMobileDataNetworkMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_widget_buttons);
        addPreferencesFromResource(R.xml.power_widget);

        PreferenceScreen prefSet = getPreferenceScreen();

        mBrightnessMode = (MultiSelectListPreference) prefSet.findPreference(EXP_BRIGHTNESS_MODE);
        mBrightnessMode.setValue(Settings.System.getString(getContentResolver(), Settings.System.EXPANDED_BRIGHTNESS_MODE));
        mBrightnessMode.setOnPreferenceChangeListener(this);
        mNetworkMode = (ListPreference) prefSet.findPreference(EXP_NETWORK_MODE);
        mNetworkMode.setOnPreferenceChangeListener(this);
        mScreenTimeoutMode = (ListPreference) prefSet.findPreference(EXP_SCREENTIMEOUT_MODE);
        mScreenTimeoutMode.setOnPreferenceChangeListener(this);
        mRingMode = (MultiSelectListPreference) prefSet.findPreference(EXP_RING_MODE);
        mRingMode.setValue(Settings.System.getString(getContentResolver(), Settings.System.EXPANDED_RING_MODE));
        mRingMode.setOnPreferenceChangeListener(this);
        mFlashMode = (ListPreference) prefSet.findPreference(EXP_FLASH_MODE);
        mFlashMode.setOnPreferenceChangeListener(this);
        mMobileDataNetworkMode = (ListPreference) prefSet.findPreference(EXP_MOBILEDATANETWORK_MODE);
        mMobileDataNetworkMode.setOnPreferenceChangeListener(this);

        PreferenceCategory prefButtons = (PreferenceCategory) prefSet.findPreference(BUTTONS_CATEGORY);

        // empty our preference category and set it to order as added
        prefButtons.removeAll();
        prefButtons.setOrderingAsAdded(false);

        // emtpy our checkbox map
        mCheckBoxPrefs.clear();

        // get our list of buttons
        ArrayList<String> buttonList = PowerWidgetUtil.getButtonListFromString(PowerWidgetUtil.getCurrentButtons(this));

        // Don't show WiMAX option if not supported
        boolean isWimaxEnabled = WimaxHelper.isWimaxSupported(this);
        if (!isWimaxEnabled) {
            PowerWidgetUtil.BUTTONS.remove(PowerWidgetUtil.BUTTON_WIMAX);
        }

        // fill that checkbox map!
        for(PowerWidgetUtil.ButtonInfo button : PowerWidgetUtil.BUTTONS.values()) {
            // create a checkbox
            CheckBoxPreference cb = new CheckBoxPreference(this);

            // set a dynamic key based on button id
            cb.setKey(SELECT_BUTTON_KEY_PREFIX + button.getId());

            // set vanity info
            cb.setTitle(button.getTitleResId());

            // set our checked state
            cb.setChecked(buttonList.contains(button.getId()));

            // add to our prefs set
            mCheckBoxPrefs.put(cb, button.getId());

            // specific checks for availability on some platforms
            if (PowerWidgetUtil.BUTTON_FLASHLIGHT.equals(button.getId()) &&
                    !getResources().getBoolean(R.bool.has_led_flash)) { // disable flashlight if it's not supported
                cb.setEnabled(false);
                mFlashMode.setEnabled(false);
            } else if (PowerWidgetUtil.BUTTON_NETWORKMODE.equals(button.getId())) {
                // some phones run on networks not supported by this button, so disable it
                boolean knownState = false;

                try {
                    int networkState = Settings.Secure.getInt(getContentResolver(),
                            Settings.Secure.PREFERRED_NETWORK_MODE);

                    switch (networkState) {
                        // list of supported network modes
                        case Phone.NT_MODE_WCDMA_PREF:
                        case Phone.NT_MODE_WCDMA_ONLY:
                        case Phone.NT_MODE_GSM_UMTS:
                        case Phone.NT_MODE_GSM_ONLY:
                            knownState = true;
                            break;
                    }
                } catch(Settings.SettingNotFoundException e) {
                    Log.e(TAG, "Unable to retrieve PREFERRED_NETWORK_MODE", e);
                }

                if (!knownState) {
                    cb.setEnabled(false);
                    mNetworkMode.setEnabled(false);
                }
            } else if (PowerWidgetUtil.BUTTON_WIMAX.equals(button.getId())) {
                if (!isWimaxEnabled) {
                    cb.setEnabled(false);
                }
            }

            // add to the category
            prefButtons.addPreference(cb);
        }

        ContentResolver cr = getContentResolver();
        String value;

        value = Settings.System.getString(cr, Settings.System.EXPANDED_BRIGHTNESS_MODE);
        if (value != null) {
            mBrightnessMode.setValue(value);
        }
        value = Settings.System.getString(cr, Settings.System.EXPANDED_RING_MODE);
        if (value != null) {
            mRingMode.setValue(value);
        }
        mNetworkMode.setValueIndex(Settings.System.getInt(
                cr, Settings.System.EXPANDED_NETWORK_MODE, 0));
        mScreenTimeoutMode.setValueIndex(Settings.System.getInt(
                cr, Settings.System.EXPANDED_SCREENTIMEOUT_MODE, 0));
        mFlashMode.setValueIndex(Settings.System.getInt(
                cr, Settings.System.EXPANDED_FLASH_MODE, 0));
        mMobileDataNetworkMode.setValueIndex(Settings.System.getInt(
                cr, Settings.System.EXPANDED_MOBILEDATANETWORK_MODE, 0));
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        // we only modify the button list if it was one of our checks that was clicked
        boolean buttonWasModified = false;
        ArrayList<String> buttonList = new ArrayList<String>();
        for(Map.Entry<CheckBoxPreference, String> entry : mCheckBoxPrefs.entrySet()) {
            if(entry.getKey().isChecked()) {
                buttonList.add(entry.getValue());
            }

            if(preference == entry.getKey()) {
                buttonWasModified = true;
            }
        }

        if(buttonWasModified) {
            // now we do some wizardry and reset the button list
            PowerWidgetUtil.saveCurrentButtons(this, PowerWidgetUtil.mergeInNewButtonString(
                    PowerWidgetUtil.getCurrentButtons(this), PowerWidgetUtil.getButtonStringFromList(buttonList)));
            return true;
        }

        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference == mBrightnessMode) {
            Settings.System.putString(getContentResolver(), Settings.System.EXPANDED_BRIGHTNESS_MODE, (String) newValue);
        } else if(preference == mNetworkMode) {
            int value = Integer.valueOf((String)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_NETWORK_MODE, value);
        } else if(preference == mScreenTimeoutMode) {
            int value = Integer.valueOf((String)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_SCREENTIMEOUT_MODE, value);
        } else if(preference == mRingMode) {
            Settings.System.putString(getContentResolver(), Settings.System.EXPANDED_RING_MODE, (String) newValue);
        } else if(preference == mFlashMode) {
            int value = Integer.valueOf((String)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_FLASH_MODE, value);
        } else if(preference == mMobileDataNetworkMode) {
            int value = Integer.valueOf((String)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.EXPANDED_MOBILEDATANETWORK_MODE, value);
        }
        return true;
    }
}
