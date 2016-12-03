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

import org.cyanogenmod.cmparts.gestures.GesturesUtils;
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

        initTouchscreenGesturesCategory();
    }

    private void initTouchscreenGesturesCategory() {
        if (!GesturesUtils.isTouchscreenGesturesSupported(getContext())) {
            return;
        }

        if (!sGestureActionsLoaded) {
            restoreTouchscreenGestureStates(getActivity());
        }

        final PreferenceCategory category =
                (PreferenceCategory) findPreference("touchscreen_gestures_category");
        final CMHardwareManager manager = CMHardwareManager.getInstance(getContext());
        final TouchscreenGesture[] gestures = manager.getTouchscreenGestures();
        final int[] actions = GesturesUtils.getDefaultActionsForGestures(getContext(), gestures);
        for (final TouchscreenGesture gesture : gestures) {
            category.addPreference(new TouchscreenGesturePreference(
                    getContext(), gesture, actions[gesture.id]));
        }
    }

    private class TouchscreenGesturePreference extends ListPreference
            implements Preference.OnPreferenceChangeListener {

        private final Context mContext;
        private final TouchscreenGesture mGesture;

        public TouchscreenGesturePreference(final Context context,
                                            final TouchscreenGesture gesture,
                                            final int defaultAction) {
            super(context);
            mContext = context;
            mGesture = gesture;

            setKey(String.valueOf(gesture.id));
            setEntries(R.array.touchscreen_gesture_action_entries);
            setEntryValues(R.array.touchscreen_gesture_action_values);
            setDefaultValue(String.valueOf(defaultAction));
            setOrder(DEFAULT_ORDER - 1); // Place this above the haptic feedback pref

            setSummary("%s");
            setDialogTitle(R.string.touchscreen_gesture_action_dialog_title);
            setTitle(ResourceUtils.getLocalizedString(
                    context.getResources(), gesture.name, TOUCHSCREEN_GESTURE_TITLE));
            setIcon(getIconDrawableResourceForAction(getPreferenceInt(
                    context, String.valueOf(gesture.id), String.valueOf(defaultAction))));

            setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(final Preference preference, final Object newValue) {
            final int action = Integer.parseInt(String.valueOf(newValue));
            final CMHardwareManager manager = CMHardwareManager.getInstance(mContext);
            sGestureActionMap.put(mGesture.keycode, action);
            setIcon(getIconDrawableResourceForAction(action));
            return manager.setTouchscreenGestureEnabled(mGesture, action > 0);
        }

        private int getIconDrawableResourceForAction(final int action) {
            switch (action) {
                case GesturesUtils.ACTION_CAMERA:
                    return R.drawable.ic_gesture_GesturesUtils.ACTION_camera;
                case GesturesUtils.ACTION_FLASHLIGHT:
                    return R.drawable.ic_gesture_GesturesUtils.ACTION_flashlight;
                case GesturesUtils.ACTION_BROWSER:
                    return R.drawable.ic_gesture_GesturesUtils.ACTION_browser;
                case GesturesUtils.ACTION_DIALER:
                    return R.drawable.ic_gesture_GesturesUtils.ACTION_dialer;
                case GesturesUtils.ACTION_EMAIL:
                    return R.drawable.ic_gesture_GesturesUtils.ACTION_email;
                case GesturesUtils.ACTION_MESSAGES:
                    return R.drawable.ic_gesture_GesturesUtils.ACTION_messages;
                case GesturesUtils.ACTION_PLAY_PAUSE_MUSIC:
                    return R.drawable.ic_gesture_GesturesUtils.ACTION_play_pause;
                case GesturesUtils.ACTION_PREVIOUS_TRACK:
                    return R.drawable.ic_gesture_GesturesUtils.ACTION_previous_track;
                case GesturesUtils.ACTION_NEXT_TRACK:
                    return R.drawable.ic_gesture_GesturesUtils.ACTION_next_track;
                default:
                    // No gesture action
                    return R.drawable.ic_settings_touchscreen_gesture;
            }
        }
    }

    public static void restoreTouchscreenGestureStates(final Context context) {
        if (!GesturesUtils.isTouchscreenGesturesSupported(context)) {
            return;
        }

        final CMHardwareManager manager = CMHardwareManager.getInstance(context);
        final TouchscreenGesture[] gestures = manager.getTouchscreenGestures();
        final int[] defaultActions = GesturesUtils.getDefaultActionsForGestures(context, gestures);
        for (final TouchscreenGesture gesture : gestures) {
            final int value = GesturesUtils.getPreferenceInt(context,
                    String.valueOf(gesture.id), String.valueOf(defaultActions[gesture.id]));
            sGestureActionMap.put(gesture.keycode, value);
            manager.setTouchscreenGestureEnabled(gesture, value > 0);
        }
        sGestureActionsLoaded = true;
    }

    public static Integer getActionForGestureKeyCode(final Context context, final int keyCode) {
        if (!sGestureActionsLoaded) {
            restoreTouchscreenGestureStates(context);
        }
        return sGestureActionMap.get(keyCode);
    }
}
