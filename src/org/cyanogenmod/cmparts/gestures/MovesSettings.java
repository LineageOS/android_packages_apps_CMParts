/**
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

package org.cyanogenmod.cmparts.gestures;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import cyanogenmod.hardware.CMHardwareManager;
import cyanogenmod.hardware.TouchscreenGesture;

import org.cyanogenmod.cmparts.R;
import org.cyanogenmod.cmparts.SettingsPreferenceFragment;

public class MovesSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "MovesSettings";

    private static final String KEY_TOUCHSCREEN_GESTURE = "touchscreen_gesture";
    private static final String TOUCHSCREEN_GESTURE_TITLE = KEY_TOUCHSCREEN_GESTURE + "_%s_title";
    private static final String TOUCHSCREEN_GESTURE_SUMMARY =
            KEY_TOUCHSCREEN_GESTURE + "_%s_summary";

    private TouchscreenGesture[] mGestures;
    private CMHardwareManager mManager;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.moves_settings);

        mManager = CMHardwareManager.getInstance(getContext());
        mGestures = mManager.getTouchscreenGestures();

        final PreferenceCategory category =
                (PreferenceCategory) findPreference("touchscreen_gestures_category");
        initTouchscreenGesturesCategory(category);

    }

    @Override
    public boolean onPreferenceChange(final Preference preference, final Object newValue) {
        final int id = Integer.parseInt(preference.getKey());
        for (TouchscreenGesture gesture : mGestures) {
            if (id == gesture.id) {
                return mManager.setTouchscreenGestureEnabled(gesture, (boolean) newValue);
            }
        }
        return false;
    }

    private void initTouchscreenGesturesCategory(final PreferenceCategory category) {
        for (TouchscreenGesture gesture : mGestures) {
            final Resources res = getContext().getResources();
            final SwitchPreference preference = new SwitchPreference(getContext());
            preference.setKey(String.valueOf(gesture.id));
            preference.setTitle(getLocalizedGestureTitle(res, gesture.name));
            preference.setSummary(getLocalizedGestureSummary(res, gesture.name));
            preference.setChecked(mManager.isTouchscreenGestureEnabled(gesture));
            preference.setOnPreferenceChangeListener(this);
            category.addPreference(preference);
        }
    }

    public static final SummaryProvider SUMMARY_PROVIDER = new SummaryProvider() {
        @Override
        public String getSummary(Context context, String key) {
            return context.getString(R.string.moves_settings_summary);
        }
    };

    private static String getStringForResourceName(final Resources res, final String resourceName,
                                                   final String defaultValue) {
        final int resId = res.getIdentifier(resourceName, "string", "org.cyanogenmod.cmparts");
        if (resId <= 0) {
            Log.e(TAG, "No resource found for " + resourceName);
            return defaultValue;
        } else {
            return res.getString(resId);
        }
    }

    private static String getLocalizedGestureTitle(final Resources res, final String title) {
        final String name = title.toLowerCase().replace(" ", "_");
        final String nameRes = String.format(TOUCHSCREEN_GESTURE_TITLE, name);
        return getStringForResourceName(res, nameRes, title);
    }

    private static String getLocalizedGestureSummary(final Resources res, final String title) {
        final String name = title.toLowerCase().replace(" ", "_");
        final String summaryRes = String.format(TOUCHSCREEN_GESTURE_SUMMARY, name);
        return getStringForResourceName(res, summaryRes, null);
    }
}
