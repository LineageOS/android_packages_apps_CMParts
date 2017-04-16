/*
 * Copyright (C) 2012 The CyanogenMod Project
 *               2017 The LineageOS Project
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

package org.cyanogenmod.cmparts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import org.cyanogenmod.cmparts.contributors.ContributorsCloudFragment;
import org.cyanogenmod.cmparts.doze.Utils;
import org.cyanogenmod.cmparts.gestures.TouchscreenGestureSettings;
import org.cyanogenmod.cmparts.input.ButtonSettings;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";
    private static final String ONE_TIME_TUNABLE_RESTORE = "hardware_tunable_restored";

    private static final boolean DEBUG = false;

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (!hasRestoredTunable(ctx)) {
            /* Restore the hardware tunable values */
            ButtonSettings.restoreKeyDisabler(ctx);
            setRestoredTunable(ctx);
        }

        TouchscreenGestureSettings.restoreTouchscreenGestureStates(ctx);

        // Extract the contributors database
        ContributorsCloudFragment.extractContributorsCloudDatabase(ctx);

        /* Start doze service only if enabled system-wide and at least one of the
           additional Ambient Display's features is turned on */
        if (Utils.isDozeEnabled(ctx) && Utils.sensorsEnabled(ctx)) {
            if (DEBUG) Log.d(TAG, "Starting doze service");
            Utils.startService(ctx);
        }
    }

    private boolean hasRestoredTunable(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(ONE_TIME_TUNABLE_RESTORE, false);
    }

    private void setRestoredTunable(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(ONE_TIME_TUNABLE_RESTORE, true).apply();
    }
}
