package org.cyanogenmod.cmparts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import org.cyanogenmod.internal.cmparts.IPartChangedCallback;
import org.cyanogenmod.internal.cmparts.IPartsCatalog;
import org.cyanogenmod.internal.cmparts.PartInfo;

import java.util.concurrent.ExecutionException;

public class PartsCatalog extends Service {

    private static final String TAG = "PartsCatalog";

    private static final String FRAGMENT_BATTERY_LIGHTS = "battery_lights";
    private static final String FRAGMENT_NOTIFICATION_LIGHTS = "notification_lights";
    private static final String FRAGMENT_LIVEDISPLAY = "livedisplay";

    private static final BiMap<String, Class<? extends SettingsPreferenceFragment>> sParts =
            new ImmutableBiMap.Builder<String, Class<? extends SettingsPreferenceFragment>>()
            .put(FRAGMENT_BATTERY_LIGHTS,
                org.cyanogenmod.cmparts.notificationlight.BatteryLightSettings.class)
            .put(FRAGMENT_LIVEDISPLAY,
                org.cyanogenmod.cmparts.livedisplay.LiveDisplay.class)
            .put(FRAGMENT_NOTIFICATION_LIGHTS,
                org.cyanogenmod.cmparts.notificationlight.NotificationLightSettings.class)
            .build();

    private static final LoadingCache<Class<? extends SettingsPreferenceFragment>,
                SettingsPreferenceFragment> sFragments = CacheBuilder.newBuilder().weakValues().build(
            new CacheLoader<Class<? extends SettingsPreferenceFragment>, SettingsPreferenceFragment>() {
                @Override
                public SettingsPreferenceFragment load(
                        Class<? extends SettingsPreferenceFragment> aClass) throws Exception {
                    return aClass.newInstance();
                }
            });

    private static final LoadingCache<Class<? extends SettingsPreferenceFragment>, PartInfo> sInfos =
                CacheBuilder.newBuilder().build(
            new CacheLoader<Class<? extends SettingsPreferenceFragment>, PartInfo>() {
                @Override
                public PartInfo load(
                        Class<? extends SettingsPreferenceFragment> aClass) throws Exception {
                    final SettingsPreferenceFragment f = sFragments.get(aClass);
                    if (f != null) {
                        return new PartInfo(sParts.inverse().get(aClass),
                                f.getDashboardTitle(), f.getDashboardSummary());
                    }
                    return null;
                }
            });

    private static final LoadingCache<String, RemoteCallbackList<IPartChangedCallback>> sCallbacks =
            CacheBuilder.newBuilder().removalListener(
                    new RemovalListener<String, RemoteCallbackList<IPartChangedCallback>>() {
                        @Override
                        public void onRemoval(RemovalNotification<String,
                                RemoteCallbackList<IPartChangedCallback>> removalNotification) {
                            if (removalNotification.getValue() != null) {
                                removalNotification.getValue().kill();
                            }
                        }
                    }
            ).build(new CacheLoader<String, RemoteCallbackList<IPartChangedCallback>>() {
                @Override
                public RemoteCallbackList<IPartChangedCallback> load(String s) throws Exception {
                    return new RemoteCallbackList<>();
                }
            });

    private static synchronized SettingsPreferenceFragment getFragment(Class<? extends SettingsPreferenceFragment> part) {
        try {
            return sFragments.get(part);
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    static synchronized void destroyPart(String part) {
        Class<? extends SettingsPreferenceFragment> clazz = getPartClass(part);
        if (sFragments.getIfPresent(clazz) != null) {
            sFragments.invalidate(clazz);
        }
    }

    static synchronized SettingsPreferenceFragment getFragment(String part) {
        return getFragment(getPartClass(part));
    }

    private static Class<? extends SettingsPreferenceFragment> getPartClass(String part) {
        String[] keys = part.split(":");
        if (keys.length < 2) {
            return null;
        }
        return sParts.get(keys[1]);
    }

    private final IPartsCatalog.Stub mBinder = new IPartsCatalog.Stub() {

        @Override
        public boolean isPartAvailable(String key) throws RemoteException {
            SettingsPreferenceFragment f = getFragment(key);
            return f != null && f.isAvailable();
        }

        @Override
        public PartInfo getPartInfo(String key) throws RemoteException {
            return sInfos.getUnchecked(getPartClass(key));
        }

        @Override
        public void registerCallback(String key, IPartChangedCallback cb) throws RemoteException {
            if (sParts.containsKey(key)) {
                sCallbacks.getUnchecked(key).register(cb);
            }
        }

        @Override
        public void unregisterCallback(String key, IPartChangedCallback cb) throws RemoteException {
            if (sParts.containsKey(key)) {
                sCallbacks.getUnchecked(key).unregister(cb);
            }
        }

        @Override
        public String[] getPartsList() throws RemoteException {
            return sParts.keySet().toArray(new String[sParts.size()]);
        }
    };

    public void notifyPartChanged(String key) {
        if (sParts.containsKey(key)) {
            final RemoteCallbackList<IPartChangedCallback> cb = sCallbacks.getUnchecked(key);
            int i = cb.beginBroadcast();
            while (i > 0) {
                i--;
                try {
                    cb.getBroadcastItem(i).onPartChanged(sInfos.getUnchecked(getPartClass(key)));
                } catch (RemoteException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            cb.finishBroadcast();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        sCallbacks.invalidateAll();
    }
}
