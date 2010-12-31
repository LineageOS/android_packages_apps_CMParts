package com.cyanogenmod.cmparts.intents;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class CPUReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ComponentName cmp = new ComponentName(ctx.getPackageName(),
                    com.cyanogenmod.cmparts.services.CPUService.class.getName());
            ctx.startService(new Intent().setComponent(cmp));
        }
    }
}
