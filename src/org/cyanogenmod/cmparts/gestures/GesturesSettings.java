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
import org.cyanogenmod.cmparts.utils.ResourceUtils;

import java.util.Map;

public class GesturesSettings extends SettingsPreferenceFragment {

    private static final String TAG = GesturesSettings.class.getSimpleName();

    private static final String KEY_TOUCHSCREEN_GESTURE = "touchscreen_gesture";
    private static final String TOUCHSCREEN_GESTURE_TITLE = KEY_TOUCHSCREEN_GESTURE + "_%s_title";

    private static final Map<Integer, Integer> sGestureActionMap = new ArrayMap<>();
    private static boolean sGestureActionsLoaded = false;

    private CMHardwareManager mManager;

    public static final SummaryProvider SUMMARY_PROVIDER = new SummaryProvider() {
        @Override
        public String getSummary(final Context context, final String key) {
            return context.getString(R.string.gestures_settings_summary);
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.gestures_settings);

        initTouchscreenGestures();
    }

    private void initTouchscreenGestures() {
        final PreferenceCategory category =
                (PreferenceCategory) findPreference("touchscreen_gestures_category");
        // Do we even support touchscreen gestures?
        if (category == null) {
            return;
        }

        if (!sGestureActionsLoaded) {
            restoreTouchscreenGestureStates(getActivity());
        }

        final TouchscreenGesture[] gestures = mManager.getTouchscreenGestures();
        int[] defaultActions = getContext().getResources()
                .getIntArray(R.array.config_defaultTouchscreenGestureActions);
        mManager = CMHardwareManager.getInstance(getContext());
        // Do not accept the config if there aren't enough default actions
        if (defaultActions.length < gestures.length) {
            defaultActions = new int[gestures.length];
        }

        for (final TouchscreenGesture gesture : gestures) {
            category.addPreference(new TouchscreenGesturePreference(
                    getContext(), gesture, defaultActions[gesture.id]));
        }
    }

    private class TouchscreenGesturePreference extends ListPreference
            implements Preference.OnPreferenceChangeListener {

        private final TouchscreenGesture mGesture;

        public TouchscreenGesturePreference(final Context context,
                                            final TouchscreenGesture gesture,
                                            final int defaultValue) {
            super(context);
            mGesture = gesture;

            setLayoutResource(R.layout.touchscreen_gesture_list_item);

            setTitle(ResourceUtils.getLocalizedString(
                    context.getResources(), mGesture.name, TOUCHSCREEN_GESTURE_TITLE));
            setSummary("%s");
            // TODO: Set the icon to that of the respective action
            setIcon(R.drawable.ic_settings_touchscreen_gesture);
            setKey(String.valueOf(mGesture.id));
            setEntries(R.array.touchscreen_gesture_action_entries);
            setEntryValues(R.array.touchscreen_gesture_action_values);
            setDialogTitle(R.string.touchscreen_gesture_action_dialog_title);
            setDefaultValue(String.valueOf(defaultValue));
            setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(final Preference preference, final Object newValue) {
            final int value = Integer.parseInt(String.valueOf(newValue));
            sGestureActionMap.put(mGesture.keycode, value);
            return mManager.setTouchscreenGestureEnabled(mGesture, value > 0);
        }
    }

    private static int getPreferenceInt(final Context context,
                                        final String key, final String defaultValue) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(key, defaultValue));
    }

    public static void restoreTouchscreenGestureStates(final Context context) {
        final CMHardwareManager manager = CMHardwareManager.getInstance(context);
        if (!manager.isSupported(CMHardwareManager.FEATURE_TOUCHSCREEN_GESTURES)) {
            return;
        }

        final TouchscreenGesture[] gestures = manager.getTouchscreenGestures();
        int[] defaultActions = context.getResources()
                .getIntArray(R.array.config_defaultTouchscreenGestureActions);
        // Do not accept the config if there aren't enough default actions
        if (defaultActions.length < gestures.length) {
            defaultActions = new int[gestures.length];
        }

        for (final TouchscreenGesture gesture : gestures) {
            final int value = getPreferenceInt(context,
                    String.valueOf(gesture.id), String.valueOf(defaultActions[gesture.id]));
            sGestureActionMap.put(gesture.keycode, value);
            manager.setTouchscreenGestureEnabled(gesture, value > 0);
        }
        sGestureActionsLoaded = true;
    }

    public static Integer getActionForGestureKeyCode(Context context, int keyCode) {
        if (!sGestureActionsLoaded) {
            restoreTouchscreenGestureStates(context);
        }
        return sGestureActionMap.get(keyCode);
    }
}
