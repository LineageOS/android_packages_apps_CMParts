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

package org.cyanogenmod.cmparts;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.cyanogenmod.cmparts.notificationlight.BatteryLightSettings;
import org.cyanogenmod.cmparts.notificationlight.NotificationLightSettings;

public class PartsActivity extends PreferenceActivity {

    public static final String EXTRA_PART = "part";

    public static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        String part = getIntent().getStringExtra(EXTRA_PART);
        if (part != null) {
            SettingsPreferenceFragment fragment = null;
            if (part.equals("NOTIFICATION_LIGHTS")) {
                fragment = new NotificationLightSettings();
            } else if (part.equals("BATTERY_LIGHTS")) {
                fragment = new BatteryLightSettings();
            }

            if (fragment != null) {
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commitAllowingStateLoss();
                getFragmentManager().executePendingTransactions();
            }
        }
    }


}

