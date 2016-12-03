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
import android.content.Intent;
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
import android.util.SparseIntArray;

import cyanogenmod.hardware.CMHardwareManager;
import cyanogenmod.hardware.TouchscreenGesture;

import org.cyanogenmod.cmparts.gestures.GesturesConstants;
import org.cyanogenmod.cmparts.R;
import org.cyanogenmod.cmparts.SettingsPreferenceFragment;
import org.cyanogenmod.cmparts.utils.ResourceUtils;

import java.util.Map;

public class GesturesSettings extends SettingsPreferenceFragment {

    private static final String TAG = GesturesSettings.class.getSimpleName();

    private static final String KEY_TOUCHSCREEN_GESTURE = "touchscreen_gesture";
    private static final String TOUCHSCREEN_GESTURE_TITLE = KEY_TOUCHSCREEN_GESTURE + "_%s_title";

    public static final SummaryProvider SUMMARY_PROVIDER = new SummaryProvider() {
        @Override
        public String getSummary(final Context context, final String key) {
            return context.getString(R.string.gestures_settings_summary);
        }
    };

    private TouchscreenGesture[] mTouchscreenGestures;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.gestures_settings);

        if (isTouchscreenGesturesSupported(getContext())) {
            initTouchscreenGesturesCategory();
        }
    }

    private void initTouchscreenGesturesCategory() {
        final PreferenceCategory category =
                (PreferenceCategory) findPreference("touchscreen_gestures_category");
        final CMHardwareManager manager = CMHardwareManager.getInstance(getContext());
        mTouchscreenGestures = manager.getTouchscreenGestures();
        final int[] actions = getDefaultGestureActions(getContext(), mTouchscreenGestures);
        for (final TouchscreenGesture gesture : mTouchscreenGestures) {
            category.addPreference(new TouchscreenGesturePreference(
                    getContext(), gesture, actions[gesture.id]));
        }
    }

    private class TouchscreenGesturePreference extends ListPreference {
        private final Context mContext;
        private final TouchscreenGesture mGesture;

        public TouchscreenGesturePreference(final Context context,
                                            final TouchscreenGesture gesture,
                                            final int defaultAction) {
            super(context);
            mContext = context;
            mGesture = gesture;

            setKey(buildPreferenceKey(gesture));
            setEntries(R.array.touchscreen_gesture_action_entries);
            setEntryValues(R.array.touchscreen_gesture_action_values);
            setDefaultValue(String.valueOf(defaultAction));
            setIcon(getIconDrawableResourceForAction(defaultAction));
            setOrder(DEFAULT_ORDER - 1); // Place this above the haptic feedback pref

            setSummary("%s");
            setDialogTitle(R.string.touchscreen_gesture_action_dialog_title);
            setTitle(ResourceUtils.getLocalizedString(
                    context.getResources(), gesture.name, TOUCHSCREEN_GESTURE_TITLE));
        }

        @Override
        public boolean callChangeListener(final Object newValue) {
            final int action = Integer.parseInt(String.valueOf(newValue));
            final CMHardwareManager manager = CMHardwareManager.getInstance(mContext);
            if (!manager.setTouchscreenGestureEnabled(mGesture, action > 0)) {
                return false;
            }
            return super.callChangeListener(newValue);
        }

        @Override
        protected boolean persistString(String value) {
            if (!super.persistString(value)) {
                return false;
            }
            final int action = Integer.parseInt(String.valueOf(value));
            setIcon(getIconDrawableResourceForAction(action));
            sendUpdateBroadcast(mContext, mTouchscreenGestures);
            return true;
        }

        private int getIconDrawableResourceForAction(final int action) {
            switch (action) {
                case GesturesConstants.ACTION_CAMERA:
                    return R.drawable.ic_gesture_action_camera;
                case GesturesConstants.ACTION_FLASHLIGHT:
                    return R.drawable.ic_gesture_action_flashlight;
                case GesturesConstants.ACTION_BROWSER:
                    return R.drawable.ic_gesture_action_browser;
                case GesturesConstants.ACTION_DIALER:
                    return R.drawable.ic_gesture_action_dialer;
                case GesturesConstants.ACTION_EMAIL:
                    return R.drawable.ic_gesture_action_email;
                case GesturesConstants.ACTION_MESSAGES:
                    return R.drawable.ic_gesture_action_messages;
                case GesturesConstants.ACTION_PLAY_PAUSE_MUSIC:
                    return R.drawable.ic_gesture_action_play_pause;
                case GesturesConstants.ACTION_PREVIOUS_TRACK:
                    return R.drawable.ic_gesture_action_previous_track;
                case GesturesConstants.ACTION_NEXT_TRACK:
                    return R.drawable.ic_gesture_action_next_track;
                default:
                    // No gesture action
                    return R.drawable.ic_settings_touchscreen_gesture;
            }
        }
    }

    public static void restoreTouchscreenGestureStates(final Context context) {
        if (!isTouchscreenGesturesSupported(context)) {
            return;
        }

        final CMHardwareManager manager = CMHardwareManager.getInstance(context);
        final TouchscreenGesture[] gestures = manager.getTouchscreenGestures();
        final int[] actionList = buildActionList(context, gestures);
        for (final TouchscreenGesture gesture : gestures) {
            manager.setTouchscreenGestureEnabled(gesture, actionList[gesture.id] > 0);
        }

        sendUpdateBroadcast(context, gestures);
    }

    private static boolean isTouchscreenGesturesSupported(final Context context) {
        final CMHardwareManager manager = CMHardwareManager.getInstance(context);
        return manager.isSupported(CMHardwareManager.FEATURE_TOUCHSCREEN_GESTURES);
    }

    private static int[] getDefaultGestureActions(final Context context,
            final TouchscreenGesture[] gestures) {
        final int[] defaultActions = context.getResources().getIntArray(
                R.array.config_defaultTouchscreenGestureActions);
        // TODO: Make this more robust - only fill in the missing actions
        // Do not accept the config if there aren't enough default actions
        return defaultActions != null && defaultActions.length >= gestures.length
                ? defaultActions
                : new int[gestures.length];
    }

    private static int[] buildActionList(final Context context,
            final TouchscreenGesture[] gestures) {
        final int[] result = new int[gestures.length];
        final int[] defaultActions = getDefaultGestureActions(context, gestures);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        for (final TouchscreenGesture gesture : gestures) {
            final String key = buildPreferenceKey(gesture);
            final String defaultValue = String.valueOf(defaultActions[gesture.id]);
            result[gesture.id] = Integer.parseInt(prefs.getString(key, defaultValue));
        }
        return result;
    }

    private static String buildPreferenceKey(final TouchscreenGesture gesture) {
        return "touchscreen_gesture_" + gesture.id;
    }

    private static void sendUpdateBroadcast(final Context context,
            final TouchscreenGesture[] gestures) {
        final Intent intent = new Intent(GesturesConstants.UPDATE_PREFS_ACTION);
        final int[] keycodes = new int[gestures.length];
        final int[] actions = buildActionList(context, gestures);
        for (final TouchscreenGesture gesture : gestures) {
            keycodes[gesture.id] = gesture.keycode;
        }
        intent.putExtra(GesturesConstants.UPDATE_EXTRA_KEYCODE_MAPPING, keycodes);
        intent.putExtra(GesturesConstants.UPDATE_EXTRA_ACTION_MAPPING, actions);
        intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        context.sendBroadcast(intent);
    }
}
