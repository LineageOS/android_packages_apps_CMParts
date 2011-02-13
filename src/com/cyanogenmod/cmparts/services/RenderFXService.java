/*
 * Copyright (C) 2011 The CyanogenMod Project
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

package com.cyanogenmod.cmparts.services;

import com.cyanogenmod.cmparts.R;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;

public class RenderFXService extends Service {

	public static final String MSG_TAG = "RenderFXService";
    private Notification mNotification;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			writeRenderEffect(intent.getIntExtra("widget_render_effect", 1));
		}
		
		mNotification = new Notification(R.drawable.notification_icon, getResources().getString(R.string.notify_render_effect),
                                System.currentTimeMillis());

        startForeground(0, mNotification);
		
		return START_STICKY;
	}
	
	public void onDestroy() {
	    writeRenderEffect(0);
		stopForeground(true);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void writeRenderEffect(int mRenderEffect) {
		try {
			IBinder flinger = ServiceManager.getService("SurfaceFlinger");
			if (flinger != null) {
				Parcel data = Parcel.obtain();
				data.writeInterfaceToken("android.ui.ISurfaceComposer");
				data.writeInt(mRenderEffect);
				flinger.transact(1014, data, null, 0);
				data.recycle();
			}
		} catch (RemoteException ex) {
		}
	}
}
