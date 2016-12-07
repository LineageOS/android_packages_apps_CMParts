/*
 * Copyright (c) 2015 The CyanogenMod Project
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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

class ProximitySensor implements SensorEventListener {

    private static final String TAG = ProximitySensor.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final int POCKET_DELTA_NS = 1000 * 1000 * 1000;

    private Context mContext;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private boolean mSawNear;
    private long mInPocketTime = 0;

    ProximitySensor(final Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        final boolean isNear = event.values[0] < mSensor.getMaximumRange();
        if (mSawNear && !isNear) {
            if (shouldPulse(event.timestamp)) {
                Utils.launchDozePulse(mContext);
            }
        } else {
            mInPocketTime = event.timestamp;
        }
        mSawNear = isNear;
    }

    private boolean shouldPulse(final long timestamp) {
        final long delta = timestamp - mInPocketTime;

        if (Utils.handwaveGestureEnabled(mContext)
                && Utils.pocketGestureEnabled(mContext)) {
            return true;
        }

        if (Utils.handwaveGestureEnabled(mContext)
                && !Utils.pocketGestureEnabled(mContext)) {
            return delta < POCKET_DELTA_NS;
        }

        if (!Utils.handwaveGestureEnabled(mContext)
                && Utils.pocketGestureEnabled(mContext)) {
            return delta >= POCKET_DELTA_NS;
        }

        return false;
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        // Do nothing
    }

    void enable() {
        if (DEBUG) Log.d(TAG, "enable()");
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    void disable() {
        if (DEBUG) Log.d(TAG, "disable()");
        mSensorManager.unregisterListener(this, mSensor);
    }
}
