/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.cyanogenmod.cmparts.utils;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public class SurfaceFlingerUtils {
    private static final String TAG = "SurfaceFlingerUtils";

    private SurfaceFlingerUtils() {
    }

    public static int getActiveRenderEffect(Context context) {
        int effectId = 0;

        // Taken from DevelopmentSettings
        // magic communication with surface flinger.
        try {
            IBinder flinger = ServiceManager.getService("SurfaceFlinger");
            if (flinger != null) {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken("android.ui.ISurfaceComposer");
                flinger.transact(1010, data, reply, 0);
                // boolean: show CPU load
                reply.readInt();
                // boolean: enable GL ES
                reply.readInt();
                // boolean: show updates
                reply.readInt();
                // boolean: show background
                reply.readInt();
                // int: render effect id
                effectId = reply.readInt();
                reply.recycle();
                data.recycle();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Could not get active render effect", e);
        }

        return effectId;
    }

    public static void setRenderEffect(Context context, int effectId) {
        try {
            IBinder flinger = ServiceManager.getService("SurfaceFlinger");
            if (flinger != null) {
                Parcel data = Parcel.obtain();
                data.writeInterfaceToken("android.ui.ISurfaceComposer");
                data.writeInt(effectId);
                flinger.transact(1014, data, null, 0);
                data.recycle();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Setting render effect failed", e);
        }
    }
}
