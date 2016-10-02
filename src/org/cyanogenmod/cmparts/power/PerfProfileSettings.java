/*
 * Copyright (C) 2016 The CyanogenMod Project
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

package org.cyanogenmod.cmparts.power;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings.Global;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

import cyanogenmod.power.PerformanceManager;
import cyanogenmod.providers.CMSettings;

import org.cyanogenmod.cmparts.R;
import org.cyanogenmod.cmparts.SettingsPreferenceFragment;

public class PerfProfileSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_PERF_PROFILE = "pref_perf_profile";
    private static final String KEY_AUTO_POWER_SAVE = "auto_power_save";
    private static final String KEY_PER_APP_PROFILES = "app_perf_profiles_enabled";

    private ListPreference mPerfProfilePref;
    private ListPreference mAutoPowerSavePref;
    private SwitchPreference mPerAppProfiles;

    private String[] mPerfProfileEntries;
    private String[] mPerfProfileValues;
    private int mNumPerfProfiles = 0;
    private PerformanceProfileObserver mPerformanceProfileObserver = null;

    private PowerManager mPowerManager;
    private PerformanceManager mPerf;

    private class PerformanceProfileObserver extends ContentObserver {
        public PerformanceProfileObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            updatePerformanceValue();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.perf_profile_settings);

        mPerfProfilePref = (ListPreference) findPreference(KEY_PERF_PROFILE);
        mAutoPowerSavePref = (ListPreference) findPreference(KEY_AUTO_POWER_SAVE);
        mPerAppProfiles = (SwitchPreference) findPreference(KEY_PER_APP_PROFILES);

        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mPerf = PerformanceManager.getInstance(getActivity());
        mNumPerfProfiles = mPerf.getNumberOfProfiles();

        if (mNumPerfProfiles < 1) {
            removePreference(KEY_PERF_PROFILE);
            removePreference(KEY_AUTO_POWER_SAVE);
            removePreference(KEY_PER_APP_PROFILES);
            mPerfProfilePref = null;
            mAutoPowerSavePref = null;
            mPerAppProfiles = null;
        } else {
            mPerfProfileEntries = new String[mNumPerfProfiles];
            mPerfProfileValues = new String[mNumPerfProfiles];

            // Filter out the unsupported profiles
            final String[] entries = getResources().getStringArray(
                    org.cyanogenmod.platform.internal.R.array.perf_profile_entries);
            final int[] values = getResources().getIntArray(
                    org.cyanogenmod.platform.internal.R.array.perf_profile_values);
            int i = 0;
            for (int j = 0; j < values.length; j++) {
                if (values[j] < mNumPerfProfiles) {
                    mPerfProfileEntries[i] = entries[j];
                    mPerfProfileValues[i] = String.valueOf(values[j]);
                    i++;
                }
            }
            mPerfProfilePref.setEntries(mPerfProfileEntries);
            mPerfProfilePref.setEntryValues(mPerfProfileValues);
            updatePerformanceValue();
            mPerfProfilePref.setOnPreferenceChangeListener(this);

            mAutoPowerSavePref.setEntries(R.array.auto_power_save_entries);
            mAutoPowerSavePref.setEntryValues(R.array.auto_power_save_values);
            updateAutoPowerSaveValue();
            mAutoPowerSavePref.setOnPreferenceChangeListener(this);
        }
        mPerformanceProfileObserver = new PerformanceProfileObserver(new Handler());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPerfProfilePref != null) {
            updatePerformanceValue();
            ContentResolver resolver = getActivity().getContentResolver();
            resolver.registerContentObserver(CMSettings.Secure.getUriFor(
                    CMSettings.Secure.PERFORMANCE_PROFILE), false, mPerformanceProfileObserver);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mPerfProfilePref != null) {
            ContentResolver resolver = getActivity().getContentResolver();
            resolver.unregisterContentObserver(mPerformanceProfileObserver);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (newValue == null) return false;

        if (preference == mPerfProfilePref) {
            int value = Integer.parseInt((String) newValue);
            boolean powerProfileUpdated = mPerf.setPowerProfile(value);
            if (powerProfileUpdated) {
                updatePerformanceSummary();
            }
            return powerProfileUpdated;
        } else if (preference == mAutoPowerSavePref) {
            int value = Integer.parseInt((String) newValue);
            Global.putInt(getContentResolver(), Global.LOW_POWER_MODE_TRIGGER_LEVEL, value);
            updateAutoPowerSaveSummary();
        }
        return true;
    }

    private void updatePerformanceSummary() {
        int value = mPerf.getPowerProfile();
        String summary = "";
        int count = mPerfProfileValues.length;
        for (int i = 0; i < count; i++) {
            try {
                if (mPerfProfileValues[i].equals(String.valueOf(value))) {
                    summary = mPerfProfileEntries[i];
                }
            } catch (IndexOutOfBoundsException ex) {
                // Ignore
            }
        }
        mPerfProfilePref.setSummary(String.format("%s", summary));
    }

    private void updatePerformanceValue() {
        if (mPerfProfilePref == null) {
            return;
        }

        mPerfProfilePref.setValue(String.valueOf(mPerf.getPowerProfile()));
        mPerAppProfiles.setEnabled(
            mPerf.getProfileHasAppProfiles(mPerf.getPowerProfile()));
        updatePerformanceSummary();
    }

    private void updateAutoPowerSaveValue() {
        if (mAutoPowerSavePref == null) {
            return;
        }
        String value = String.valueOf(
                Global.getInt(getContentResolver(), Global.LOW_POWER_MODE_TRIGGER_LEVEL, 0));
        mAutoPowerSavePref.setValue(value);
        updateAutoPowerSaveSummary();
    }

    private void updateAutoPowerSaveSummary() {
        int value = Global.getInt(getContentResolver(), Global.LOW_POWER_MODE_TRIGGER_LEVEL, 0);
        String summary;
        if (value > 0 && value < 100) {
            summary = getResources().getString(R.string.auto_power_save_summary_on, value);
        } else {
            summary = getResources().getString(R.string.auto_power_save_summary_off);
        }
        mAutoPowerSavePref.setSummary(summary);
    }
}
