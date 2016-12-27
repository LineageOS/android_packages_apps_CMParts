/*
 * Copyright (C) 2017 The LineageOS Project
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

package org.cyanogenmod.cmparts.input;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import cyanogenmod.providers.CMSettings;

import org.cyanogenmod.cmparts.input.ButtonSettings;

public class ButtonSettingsUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = "ButtonSettingsUpdateReceiver";

    private static final String FP_HOME_INTENT = "com.cyanogenmod.settings.device.FP_HOME_TOGGLE";
    private static final String FP_HOME_INTENT_EXTRA = "fingerprint_home";

    private static final int KEY_ACTION_NOTHING = 0;

    private static Integer mHomeLongPressUserSetAction = -1;

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (intent.getAction().equals(FP_HOME_INTENT)) {
            ButtonSettings.mFingerprintHomeButtonEnabled = intent.getBooleanExtra(
                    FP_HOME_INTENT_EXTRA, false);
            toggleHomeLongPressAction(ctx.getContentResolver());
        }
    }

    private void toggleHomeLongPressAction(ContentResolver resolver) {
        if (mHomeLongPressUserSetAction.intValue() == -1) {
            mHomeLongPressUserSetAction = CMSettings.System.getInt(resolver,
                    CMSettings.System.KEY_HOME_LONG_PRESS_ACTION, KEY_ACTION_NOTHING);
        }
        if (ButtonSettings.mFingerprintHomeButtonEnabled) {
            CMSettings.System.putInt(resolver, CMSettings.System.KEY_HOME_LONG_PRESS_ACTION,
                    mHomeLongPressUserSetAction);
        } else {
            mHomeLongPressUserSetAction = CMSettings.System.getInt(resolver,
                    CMSettings.System.KEY_HOME_LONG_PRESS_ACTION, mHomeLongPressUserSetAction);
            CMSettings.System.putInt(resolver, CMSettings.System.KEY_HOME_LONG_PRESS_ACTION,
                    KEY_ACTION_NOTHING);
        }
    }
}
