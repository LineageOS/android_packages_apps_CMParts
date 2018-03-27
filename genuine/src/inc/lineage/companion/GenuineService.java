package inc.lingeage.companion;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;

import org.cyanogenmod.cmparts.R;

public class GenuineService extends Service {
    private static final String CHANNEL = "grief_info";

    public int onStartCommand( Intent intent, int flags, int startId) {
        work(this);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    static void work(Context context) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null) {
            return;
        }

        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, GenuineActivity.class), 0);
        boolean hasGoneThrough5GriefSteps = checkStatus(context);

        Notification.Builder notification;
        if (hasGoneThrough5GriefSteps) {
            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_error)
                    .setColor(context.getColor(R.color.genuine_error))
                    .setContentTitle(context.getString(R.string.genuine_notification_grief))
                    .setOngoing(true);
        } else {
            notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_error)
                    .setColor(context.getColor(R.color.genuine_error))
                    .setContentTitle(context.getString(R.string.genuine_notification_title))
                    .setContentText(context.getString(R.string.genuine_notification_message))
                    .setContentIntent(pIntent)
                    .setAutoCancel(true);
        }

        manager.notify(151, notification.build());
    }

    private static boolean checkStatus(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("grief", false);
    }
}