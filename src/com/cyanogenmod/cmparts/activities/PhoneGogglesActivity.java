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
import java.util.List;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.cyanogenmod.cmparts.R;

public class PhoneGogglesActivity extends PreferenceActivity {

    public static final String ACTION_PHONE_GOGGLES_COMMUNICATION =
        "android.intent.action.PHONE_GOGGLES_COMMUNICATION";

    private ArrayList<Preference> mPhoneGogglesApps;
    private CheckBoxPreference mPhoneGogglesEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_phone_goggles);

        addPreferencesFromResource(R.xml.phone_goggles_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        PreferenceCategory appCategory = (PreferenceCategory)prefSet.
        findPreference("phone_goggles_apps");

        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(ACTION_PHONE_GOGGLES_COMMUNICATION);
        List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, 0);

        mPhoneGogglesEnabled = (CheckBoxPreference)prefSet.findPreference(
                Settings.System.PHONE_GOGGLES_ENABLED);

        mPhoneGogglesApps = new ArrayList<Preference>();

        for (ResolveInfo currentInfo : infos) {
            ApplicationInfo appInfos = currentInfo.activityInfo.applicationInfo;
            CharSequence label = appInfos.loadLabel(getPackageManager());
            CharSequence summary = appInfos.loadDescription(packageManager);
            String packageName = appInfos.packageName;

            Preference pref = new Preference(this);
            pref.setKey(packageName);
            pref.setTitle(label);
            pref.setSummary(summary);
            pref.setEnabled(mPhoneGogglesEnabled.isChecked());
            appCategory.addPreference(pref);
            mPhoneGogglesApps.add(pref);
        }

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {

        if (preference == mPhoneGogglesEnabled) {
            boolean isChecked = mPhoneGogglesEnabled.isChecked();

            for (Preference pref : mPhoneGogglesApps) {
                pref.setEnabled(isChecked);
            }

            return Settings.System.putInt(getContentResolver(),
                    Settings.System.PHONE_GOGGLES_ENABLED,
                    isChecked ? 1 : 0);
        }
        else if (mPhoneGogglesApps.contains(preference)) {

            Intent intent = new Intent(this, PhoneGogglesAppActivity.class);
            intent.putExtra("appName", preference.getTitle());
            intent.putExtra("appId", preference.getKey());
            startActivity(intent);
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
