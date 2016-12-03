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

    public static final int ACTION_CAMERA = 1;
    public static final int ACTION_FLASHLIGHT = 2;
    public static final int ACTION_BROWSER = 3;
    public static final int ACTION_DIALER = 4;
    public static final int ACTION_EMAIL = 5;
    public static final int ACTION_MESSAGES = 6;
    public static final int ACTION_PLAY_PAUSE_MUSIC = 7;
    public static final int ACTION_PREVIOUS_TRACK = 8;
    public static final int ACTION_NEXT_TRACK = 9;

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

        mManager = CMHardwareManager.getInstance(getContext());
        final TouchscreenGesture[] gestures = mManager.getTouchscreenGestures();
        int[] defaultActions = getContext().getResources()
                .getIntArray(R.array.config_defaultTouchscreenGestureActions);
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
                                            final int defaultAction) {
            super(context);
            mGesture = gesture;

            setKey(String.valueOf(gesture.id));
            setEntries(R.array.touchscreen_gesture_action_entries);
            setEntryValues(R.array.touchscreen_gesture_action_values);
            setDefaultValue(String.valueOf(defaultAction));

            setSummary("%s");
            setDialogTitle(R.string.touchscreen_gesture_action_dialog_title);
            setTitle(ResourceUtils.getLocalizedString(
                    context.getResources(), gesture.name, TOUCHSCREEN_GESTURE_TITLE));
            setIcon(getIconDrawableResourceForAction(
                    getPreferenceInt(context, gesture.id, defaultAction)));

            setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(final Preference preference, final Object newValue) {
            final int action = Integer.parseInt(String.valueOf(newValue));
            sGestureActionMap.put(mGesture.keycode, action);
            setIcon(getIconDrawableResourceForAction(action));
            return mManager.setTouchscreenGestureEnabled(mGesture, action > 0);
        }

        private int getIconDrawableResourceForAction(final int action) {
            switch (action) {
                case ACTION_CAMERA:
                    return R.drawable.ic_gesture_action_camera;
                case ACTION_FLASHLIGHT:
                    return R.drawable.ic_gesture_action_flashlight;
                case ACTION_BROWSER:
                    return R.drawable.ic_gesture_action_browser;
                case ACTION_DIALER:
                    return R.drawable.ic_gesture_action_dialer;
                case ACTION_EMAIL:
                    return R.drawable.ic_gesture_action_email;
                case ACTION_MESSAGES:
                    return R.drawable.ic_gesture_action_messages;
                case ACTION_PLAY_PAUSE_MUSIC:
                    return R.drawable.ic_gesture_action_play_pause;
                case ACTION_PREVIOUS_TRACK:
                    return R.drawable.ic_gesture_action_previous_track;
                case ACTION_NEXT_TRACK:
                    return R.drawable.ic_gesture_action_next_track;
                default:
                    // No gesture action
                    return R.drawable.ic_settings_touchscreen_gesture;
            }
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
