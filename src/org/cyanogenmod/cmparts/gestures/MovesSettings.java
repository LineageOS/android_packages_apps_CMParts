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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.ArrayMap;
import android.util.Log;

import cyanogenmod.hardware.CMHardwareManager;
import cyanogenmod.hardware.TouchscreenGesture;

import org.cyanogenmod.cmparts.R;
import org.cyanogenmod.cmparts.SettingsPreferenceFragment;

import java.util.Map;

public class MovesSettings extends SettingsPreferenceFragment {

    private static final String TAG = MovesSettings.class.getSimpleName();

    private static final String KEY_TOUCHSCREEN_GESTURE = "touchscreen_gesture";
    private static final String TOUCHSCREEN_GESTURE_TITLE = KEY_TOUCHSCREEN_GESTURE + "_%s_title";

    public static final Map<Integer, Integer> sGestureActionMap = new ArrayMap<>();

    private TouchscreenGesture[] mGestures;
    private CMHardwareManager mManager;

    public static final SummaryProvider SUMMARY_PROVIDER = new SummaryProvider() {
        @Override
        public String getSummary(final Context context, final String key) {
            return context.getString(R.string.moves_settings_summary);
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.moves_settings);

        final PreferenceCategory category =
                (PreferenceCategory) findPreference("touchscreen_gestures_category");
        initTouchscreenGesturesCategory(category);
    }

    private void initTouchscreenGesturesCategory(final PreferenceCategory category) {
        if (category == null) return;
        mManager = CMHardwareManager.getInstance(getContext());
        mGestures = mManager.getTouchscreenGestures();
        for (final TouchscreenGesture gesture : mGestures) {
            category.addPreference(new TouchscreenGesturePreference(getContext(), gesture));
        }
    }

    private class TouchscreenGesturePreference extends ListPreference
            implements Preference.OnPreferenceChangeListener {

        private final TouchscreenGesture mGesture;

        public TouchscreenGesturePreference(final Context context,
                                            final TouchscreenGesture gesture) {
            super(context);
            mGesture = gesture;

            setLayoutResource(R.layout.touchscreen_gesture_list_item);

            final Resources res = context.getResources();
            setTitle(getLocalizedGestureTitle(res, mGesture.name));
            setSummary("%s");
            // TODO: Set the icon to that of the respective action
            setIcon(R.drawable.ic_settings_touchscreen_gesture);
            setKey(String.valueOf(mGesture.id));
            setEntries(R.array.touchscreen_gesture_action_entries);
            setEntryValues(R.array.touchscreen_gesture_action_values);
            setDialogTitle(R.string.touchscreen_gesture_action_dialog_title);
            setOnPreferenceChangeListener(this);

            // TODO: Set sensible defaults
            if (getValue() == null) {
                setValueIndex(0);
            }
            final int value = Integer.parseInt(getValue());
            sGestureActionMap.put(mGesture.keycode, value);
            mManager.setTouchscreenGestureEnabled(mGesture, value > 0);
        }

        @Override
        public boolean onPreferenceChange(final Preference preference, final Object newValue) {
            final int value = Integer.parseInt(String.valueOf(newValue));
            sGestureActionMap.put(mGesture.keycode, value);
            return mManager.setTouchscreenGestureEnabled(mGesture, value > 0);
        }
    }

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

    private static boolean getPreferenceInt(final Context context, final String key) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // TODO: Set sensible defaults
        return prefs.getInt(key, 0);
    }

    public static void restoreTouchscreenGestureStates(final Context context) {
        final CMHardwareManager manager = CMHardwareManager.getInstance(context);
        final TouchscreenGesture[] gestures = manager.getTouchscreenGestures();
        for (final TouchscreenGesture gesture : gestures) {
            final int value = getPreferenceInt(context, String.valueOf(gesture.id));
            sGestureActionMap.put(gesture.keycode, value);
            manager.setTouchscreenGestureEnabled(gesture, value > 0);
        }
    }
}
