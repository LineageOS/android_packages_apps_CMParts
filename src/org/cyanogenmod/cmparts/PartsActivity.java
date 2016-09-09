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

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.cyanogenmod.internal.cmparts.IPartsCatalog;

public class PartsActivity extends Activity {

    private static final String TAG = "PartsActivity";

    public static final String EXTRA_PART = "part";
    public static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";

    public static final String FRAGMENT_PREFIX = "cmparts:";

    public static final String FRAGMENT_BATTERY_LIGHTS = "battery_lights";
    public static final String FRAGMENT_NOTIFICATION_LIGHTS = "notification_lights";
    public static final String FRAGMENT_LIVEDISPLAY = "livedisplay";

    private String mPart;
    private IPartsCatalog mCatalog;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        connectCatalog();

        String part = getIntent().getStringExtra(EXTRA_PART);
        if (part != null && part.startsWith(FRAGMENT_PREFIX)) {
            Log.d(TAG, "Launching fragment: " + part);

            final SettingsPreferenceFragment fragment = PartsCatalog.getFragment(part);
            if (fragment == null) {
                throw new UnsupportedOperationException("Unable to get fragment: " + part);
            }

            mPart = part;

            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }

            getFragmentManager().beginTransaction().replace(android.R.id.content, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commitAllowingStateLoss();
            getFragmentManager().executePendingTransactions();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPart != null) {
            PartsCatalog.destroyPart(mPart);
        }
        disconnectCatalog();
    }

    private void connectCatalog() {
        Intent i = new Intent(this, PartsCatalog.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void disconnectCatalog() {
        unbindService(mConnection);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mCatalog = IPartsCatalog.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mCatalog = null;
        }
    };
}

