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

import java.util.ArrayList;

import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;

public class InputDoubleTapHomeActivity extends PreferenceActivity {

    private static final String INPUT_DOUBLE_TAP_HOME_APP_TOGGLE = "pref_input_double_tap_home_app_toggle";

    private static final String INPUT_DOUBLE_TAP_HOME_APP_ACTIVITY = "pref_input_double_tap_homeh_app_activity";

    private CheckBoxPreference mCustomDoubleTapHomeAppTogglePref;

    private Preference mCustomDoubleTapHomeAppActivityPref;

    private int mKeyNumber = 1;

    private static final int REQUEST_PICK_SHORTCUT = 1;

    private static final int REQUEST_PICK_APPLICATION = 2;

    private static final int REQUEST_CREATE_SHORTCUT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.input_double_tap_home_key_title);
        addPreferencesFromResource(R.xml.input_double_tap_home_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mCustomDoubleTapHomeAppTogglePref = (CheckBoxPreference) prefSet
                .findPreference(INPUT_DOUBLE_TAP_HOME_APP_TOGGLE);
        mCustomDoubleTapHomeAppTogglePref.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.USE_CUSTOME_DOUBLE_TAP_KEY_TOGGLE, 0) == 1);
        mCustomDoubleTapHomeAppActivityPref = (Preference) prefSet
                .findPreference(INPUT_DOUBLE_TAP_HOME_APP_ACTIVITY);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCustomDoubleTapHomeAppActivityPref.setSummary(Settings.System.getString(getContentResolver(),
                Settings.System.USE_CUSTOME_DOUBLE_TAP_ACTIVITY));
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mCustomDoubleTapHomeAppTogglePref) {
            value = mCustomDoubleTapHomeAppTogglePref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.USE_CUSTOME_DOUBLE_TAP_KEY_TOGGLE, value ? 1 : 0);
            return true;
        } else if (preference == mCustomDoubleTapHomeAppActivityPref) {
            pickShortcut(2);
            return true;
        }

        return false;
    }

    private void pickShortcut(int keyNumber) {
        mKeyNumber = keyNumber;
        Bundle bundle = new Bundle();
        ArrayList<String> shortcutNames = new ArrayList<String>();
        shortcutNames.add(getString(R.string.group_applications));
        bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);
        ArrayList<ShortcutIconResource> shortcutIcons = new ArrayList<ShortcutIconResource>();
        shortcutIcons.add(ShortcutIconResource
                .fromContext(this, R.drawable.ic_launcher_application));
        bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIcons);
        Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        pickIntent.putExtra(Intent.EXTRA_TITLE, getText(R.string.select_custom_app_title));
        pickIntent.putExtras(bundle);
        startActivityForResult(pickIntent, REQUEST_PICK_SHORTCUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_APPLICATION:
                    completeSetCustomApp(data);
                    break;
                case REQUEST_CREATE_SHORTCUT:
                    completeSetCustomShortcut(data);
                    break;
                case REQUEST_PICK_SHORTCUT:
                    processShortcut(data, REQUEST_PICK_APPLICATION, REQUEST_CREATE_SHORTCUT);
                    break;
            }
        }
    }

    void processShortcut(Intent intent, int requestCodeApplication, int requestCodeShortcut) {
        // Handle case where user selected "Applications"
        String applicationName = getResources().getString(R.string.group_applications);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        if (applicationName != null && applicationName.equals(shortcutName)) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
            pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
            startActivityForResult(pickIntent, requestCodeApplication);
        } else {
            startActivityForResult(intent, requestCodeShortcut);
        }
    }

    void completeSetCustomShortcut(Intent data) {
        Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
        int keyNumber = mKeyNumber;
        if (keyNumber == 2) {
            if (Settings.System.putString(getContentResolver(),
                    Settings.System.USE_CUSTOME_DOUBLE_TAP_ACTIVITY, intent.toUri(0))) {
                mCustomDoubleTapHomeAppActivityPref.setSummary(intent.toUri(0));
            }
        }

    }

    void completeSetCustomApp(Intent data) {
        int keyNumber = mKeyNumber;
        if (keyNumber == 2) {
            if (Settings.System.putString(getContentResolver(),
                    Settings.System.USE_CUSTOME_DOUBLE_TAP_ACTIVITY, data.toUri(0))) {
                mCustomDoubleTapHomeAppActivityPref.setSummary(data.toUri(0));
            }
        }

    }
}
