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

class GesturesUtils {

    // Touchscreen gesture actions
    static final int ACTION_CAMERA = 1;
    static final int ACTION_FLASHLIGHT = 2;
    static final int ACTION_BROWSER = 3;
    static final int ACTION_DIALER = 4;
    static final int ACTION_EMAIL = 5;
    static final int ACTION_MESSAGES = 6;
    static final int ACTION_PLAY_PAUSE_MUSIC = 7;
    static final int ACTION_PREVIOUS_TRACK = 8;
    static final int ACTION_NEXT_TRACK = 9;

    static boolean isTouchscreenGesturesSupported(final Context context) {
        final CMHardwareManager manager = CMHardwareManager.getInstance(context);
        return manager.isSupported(CMHardwareManager.FEATURE_TOUCHSCREEN_GESTURES);
    }

    static int getPreferenceInt(final Context context,
                                final String key, final String defaultValue) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(key, defaultValue));
    }

    static int[] getDefaultActionsForGestures(final Context context,
                                              final TouchscreenGesture[] gestures) {
        int[] defaultActions = context.getResources()
                .getIntArray(R.array.config_defaultTouchscreenGestureActions);
        // TODO: Make this more robust - only fill in the missing actions
        // Do not accept the config if there aren't enough default actions
        if (defaultActions == null || defaultActions.length < gestures.length) {
            defaultActions = new int[gestures.length];
        }
        return defaultActions;
    }
}
