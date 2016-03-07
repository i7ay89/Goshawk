package com.example.tomer.goshawk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class NotificationService  extends Service {
    public static boolean keepListen = true;
    Thread t;
    NotificationClass notify = new NotificationClass();

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
        setNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            keepListen = intent.getBooleanExtra("keepGoing", true);
        }
        t = new Thread(new Runnable() {
            public void run() {
                //listen to server
                while(NotificationService.keepListen) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Toast.makeText(getApplicationContext(), "service is running", Toast.LENGTH_SHORT).show();
                    if(NotificationService.keepListen) {
                        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                        notify.notify(intent);
                    }
                }
            }
        });
        if(keepListen) {
            t.start();
        } else {
            stopSelf();
        }

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();


    }

    private void setNotification() {

        NotificationCompat.Builder b = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.goshawk_icon)
                        .setContentTitle("ALARM!!!")
                        .setContentText("Goshawk is Amazing!!!");
        this.notify.setBuilder(b, this);
        this.notify.setManager((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));

// Creates an explicit intent for an Activity in your app
        /*
        Intent resultIntent = new Intent(this, SettingsActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SettingsActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());*/
    }
}