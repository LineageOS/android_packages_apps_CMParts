package com.cyanogenmod.cmparts.intents;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;

public class CPUReceiver extends BroadcastReceiver {

    private static final String CPU_SETTINGS_PROP = "sys.cpufreq.restored";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (SystemProperties.getBoolean(CPU_SETTINGS_PROP, false) == false &&
            intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
            SystemProperties.set(CPU_SETTINGS_PROP, "true");
            ComponentName cmp = new ComponentName(ctx.getPackageName(),
                    com.cyanogenmod.cmparts.services.CPUService.class.getName());
            ctx.startService(new Intent().setComponent(cmp));
        } else {
            SystemProperties.set(CPU_SETTINGS_PROP,"false");
        }
    }
}
