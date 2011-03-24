/*
 * Created by Sven Dawitz; Copyright (C) 2011 CyanogenMod Project
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
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.CmSystem;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;

public class TabletTweaksActivity extends PreferenceActivity implements OnPreferenceChangeListener{
    private static final String PREF_STATUS_BAR_BOTTOM = "pref_status_bar_bottom";
    private static final String PREF_STATUS_BAR_DEAD_ZONE = "pref_status_bar_dead_zone";
    private static final String PREF_SOFT_BUTTONS_LEFT = "pref_soft_buttons_left";
    private static final String PREF_DISABLE_LOCKSCREEN = "pref_disable_lockscreen";
    private static final String PREF_DISABLE_FULLSCREEN = "pref_disable_fullscreen";
    private static final String PREF_UNHIDE_BUTTON = "pref_unhide_button";
    private static final String PREF_EXTEND_PM = "pref_extend_pm";
    // cm71 nightlies: will be re-enabled there
    //private static final String PREF_REVERSE_VOLUME_BEHAVIOR = "pref_reverse_volume_behavior";
    private static final String PREF_GENERAL_CATEGORY = "pref_general_category";
    private static final String PREF_INTERFACE_CATEGORY = "pref_interface_category";
    private static final String PREF_BUTTON_CATEGORY = "pref_button_category";
    private static final String PREF_EXTEND_PM_LIST = "pref_extend_pm_list";
    private static final String PREF_SOFT_BUTTON_LIST = "pref_soft_button_list";

    private CheckBoxPreference mStatusBarBottom;
    private CheckBoxPreference mStatusBarDeadZone;
    private CheckBoxPreference mSoftButtonsLeft;
    private CheckBoxPreference mDisableLockscreen;
    private CheckBoxPreference mDisableFullscreen;
    private CheckBoxPreference mExtendPm;
    // cm71 nightlies: will be re-enabled there
    //private CheckBoxPreference mReverseVolumeBehavior;
    private ListPreference mUnhideButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.tablet_tweaks_title_head);
        addPreferencesFromResource(R.xml.tablet_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarBottom = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_BOTTOM);
        mStatusBarDeadZone = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_DEAD_ZONE);
        mSoftButtonsLeft = (CheckBoxPreference) prefSet.findPreference(PREF_SOFT_BUTTONS_LEFT);
        mDisableLockscreen = (CheckBoxPreference) prefSet.findPreference(PREF_DISABLE_LOCKSCREEN);
        mDisableFullscreen = (CheckBoxPreference) prefSet.findPreference(PREF_DISABLE_FULLSCREEN);
        mUnhideButton = (ListPreference) prefSet.findPreference(PREF_UNHIDE_BUTTON);
        mExtendPm = (CheckBoxPreference) prefSet.findPreference(PREF_EXTEND_PM);
        // cm71 nightlies: will be re-enabled there
        //mReverseVolumeBehavior = (CheckBoxPreference) prefSet.findPreference(PREF_REVERSE_VOLUME_BEHAVIOR);

        int defValue;

        defValue=CmSystem.getDefaultBool(getBaseContext(), CmSystem.CM_DEFAULT_BOTTOM_STATUS_BAR)==true ? 1 : 0;
        mStatusBarBottom.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_BOTTOM, defValue) == 1));
        defValue=CmSystem.getDefaultBool(getBaseContext(), CmSystem.CM_DEFAULT_USE_DEAD_ZONE)==true ? 1 : 0;
        mStatusBarDeadZone.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_DEAD_ZONE, defValue) == 1));
        defValue=CmSystem.getDefaultBool(getBaseContext(), CmSystem.CM_DEFAULT_SOFT_BUTTONS_LEFT)==true ? 1 : 0;
        mSoftButtonsLeft.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.SOFT_BUTTONS_LEFT, defValue) == 1));
        defValue=CmSystem.getDefaultBool(getBaseContext(), CmSystem.CM_DEFAULT_DISABLE_LOCKSCREEN)==true ? 1 : 0;
        mDisableLockscreen.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_DISABLED, defValue) == 1));
        defValue=CmSystem.getDefaultBool(getBaseContext(), CmSystem.CM_DEFAULT_DISABLE_FULLSCREEN)==true ? 1 : 0;
        mDisableFullscreen.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FULLSCREEN_DISABLED, defValue) == 1));
        defValue=CmSystem.getDefaultBool(getBaseContext(), CmSystem.CM_DEFAULT_EXTEND_POWER_MENU)==true ? 1 : 0;
        mExtendPm.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.EXTEND_PM, defValue) == 1));
        defValue=CmSystem.getDefaultBool(getBaseContext(), CmSystem.CM_DEFAULT_REVERSE_VOLUME_BEHAVIOR)==true ? 1 : 0;
        // cm71 nightlies: will be re-enabled there
        //mReverseVolumeBehavior.setChecked((Settings.System.getInt(getContentResolver(),
                //Settings.System.REVERSE_VOLUME_BEHAVIOR, defValue) == 1));

        defValue=CmSystem.getDefaultInt(getBaseContext(), CmSystem.CM_DEFAULT_UNHIDE_BUTTON_INDEX);
        mUnhideButton.setOnPreferenceChangeListener(this);
        mUnhideButton.setValueIndex(Settings.System.getInt(getContentResolver(),
                Settings.System.UNHIDE_BUTTON, defValue));

        // hide all soft button related option for devices without soft buttons (usually all phones)
        if(!CmSystem.getDefaultBool(getBaseContext(), CmSystem.CM_HAS_SOFT_BUTTONS)){
            PreferenceCategory cGeneral=(PreferenceCategory) prefSet.findPreference(PREF_GENERAL_CATEGORY);
            PreferenceCategory cInterface=(PreferenceCategory) prefSet.findPreference(PREF_INTERFACE_CATEGORY);
            PreferenceCategory cButtons=(PreferenceCategory) prefSet.findPreference(PREF_BUTTON_CATEGORY);

            PreferenceScreen sSoftButtons=(PreferenceScreen) prefSet.findPreference(PREF_SOFT_BUTTON_LIST);
            PreferenceScreen sExtendPmList=(PreferenceScreen) prefSet.findPreference(PREF_EXTEND_PM_LIST);

            cGeneral.removePreference(mSoftButtonsLeft);
            cGeneral.removePreference(sSoftButtons);

            cInterface.removePreference(mDisableFullscreen);
            cInterface.removePreference(mUnhideButton);

            cButtons.removePreference(mExtendPm);
            cButtons.removePreference(sExtendPmList);

            // as for cm7stable without volume remapping, remove the whole button category for phone devices
            prefSet.removePreference(cButtons);
        }

        updateDependencies();
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        /* Preference Screens */
        if (preference == mStatusBarBottom) {
            value = mStatusBarBottom.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_BOTTOM,
                    value ? 1 : 0);
            updateDependencies();
            return true;
        } else if (preference == mStatusBarDeadZone) {
            value = mStatusBarDeadZone.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_DEAD_ZONE,
                    value ? 1 : 0);
            return true;
        } else if (preference == mSoftButtonsLeft) {
            value = mSoftButtonsLeft.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.SOFT_BUTTONS_LEFT,
                    value ? 1 : 0);
            return true;
        } else if (preference == mDisableLockscreen) {
            value = mDisableLockscreen.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_DISABLED,
                    value ? 1 : 0);
            return true;
        } else if (preference == mDisableFullscreen) {
            value = mDisableFullscreen.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.FULLSCREEN_DISABLED,
                    value ? 1 : 0);
            return true;
        } else if (preference == mExtendPm) {
            value = mExtendPm.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.EXTEND_PM,
                    value ? 1 : 0);
            return true;
        // cm71 nightlies: will be re-enabled there
        /*
        } else if (preference == mReverseVolumeBehavior) {
            value = mReverseVolumeBehavior.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.REVERSE_VOLUME_BEHAVIOR,
                    value ? 1 : 0);
            updateDependencies();
            return true;*/
        }

        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mUnhideButton) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.UNHIDE_BUTTON, value);
            return true;
        }
        return false;
    }

    private void updateDependencies() {
        if(!mStatusBarBottom.isChecked()){
            mStatusBarDeadZone.setChecked(false);
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_DEAD_ZONE, 0);
        }

        // cm71 nightlies: will be re-enabled there
        /*
        if(mReverseVolumeBehavior.isChecked())
            mReverseVolumeBehavior.setSummary(R.string.tablet_tweaks_reverse_volume_behavior_summary_on);
        else
            mReverseVolumeBehavior.setSummary(R.string.tablet_tweaks_reverse_volume_behavior_summary_off);
        */
    }
}
