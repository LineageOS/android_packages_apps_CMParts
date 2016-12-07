/**
 * Copyright (C) 2015-2016 The CyanogenMod project
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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

class AmbientDisplayService extends Service {

    private static final String TAG = AmbientDisplayService.class.getSimpleName();
    private static final boolean DEBUG = false;

    private ProximitySensor mProximitySensor;

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_ON:
                    onDisplayOn();
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    onDisplayOff();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "onCreate()");

        mProximitySensor = new ProximitySensor(this);

        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, screenStateFilter);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (DEBUG) Log.d(TAG, "onStartCommand()");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");
        super.onDestroy();
        unregisterReceiver(mScreenStateReceiver);
        mProximitySensor.disable();
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    private void onDisplayOn() {
        if (DEBUG) Log.d(TAG, "onDisplayOn()");
        if (Utils.handwaveGestureEnabled(this) ||
                Utils.pocketGestureEnabled(this)) {
            mProximitySensor.disable();
        }
    }

    private void onDisplayOff() {
        if (DEBUG) Log.d(TAG, "onDisplayOff()");
        if (Utils.handwaveGestureEnabled(this) ||
                Utils.pocketGestureEnabled(this)) {
            mProximitySensor.enable();
        }
    }
}
