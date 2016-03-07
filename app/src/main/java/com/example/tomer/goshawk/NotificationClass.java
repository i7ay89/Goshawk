package com.example.tomer.goshawk;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by Tomer on 05/01/2016.
 */
public class NotificationClass {
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;
    TaskStackBuilder stackBuilder;

    public void setBuilder(Context context, int icon, String title, String text) {
        this.mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(text);
        this.stackBuilder = TaskStackBuilder.create(context);
    }
    public void setBuilder(NotificationCompat.Builder b, Context context) {
        this.mBuilder = b;
        this.stackBuilder = TaskStackBuilder.create(context);
    }
    public void setManager(NotificationManager nm) {
        this.mNotificationManager = nm;
    }
    public void notify(Intent intent) {


        this.stackBuilder.addParentStack(SettingsActivity.class);  /// FIXME: 05/01/2016
        this.stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent = this.stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        this.mBuilder.setContentIntent(resultPendingIntent);
        this.mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        this.mBuilder.setSound(alarmSound);
        this.mNotificationManager.notify(0, this.mBuilder.build());
    }
}
