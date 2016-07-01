package com.example.tomer.goshawk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class NotificationService extends Service implements Connection {
    public static boolean keepListen = true;
    Thread t;
    boolean keepRuning = true;
    NotificationClass notify = new NotificationClass();

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        // Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
        // setNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            keepListen = intent.getBooleanExtra("keepGoing", true);
        }


        t = new Thread(new Runnable() {
            public void run() {
                while(NotificationService.keepListen) {
                    //listen to server
                    HttpGoshawkClient client = new HttpGoshawkClient(NotificationService.this);
                    client.Sync();
                    keepRuning = true;
                    while (NotificationService.keepListen && keepRuning) {
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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

        // Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();


    }

    private void setNotification(String title, String content) {

        NotificationCompat.Builder b = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.goshawk_icon)
                .setContentTitle(title)
                .setContentText(content);
        this.notify.setBuilder(b, this);
        this.notify.setManager((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
    }

    @Override
    public void Respond(String respond) {
        try {
            JSONObject jsonObj = new JSONObject(respond);
            JSONArray eventList = jsonObj.getJSONArray("events");
            JSONObject event = eventList.getJSONObject(0);
            String title = eventList.length() + ", " + event.getString("severity") + " - " + event.getString("type");
            String content = event.getString("description");
            setNotification(title, content);
            if (NotificationService.keepListen) {
                Intent intent = ParseJson(jsonObj);
                if (intent != null) {
                    notify.notify(intent);
                }
                keepRuning = false;
            }
        } catch (Exception e) {
            keepRuning = false;
        }

    }

    private Intent ParseJson(JSONObject json) {
        Intent intent = new Intent(getBaseContext(), MainScreenActivity.class);
        //parse the answer is wrong return null
        //put exstra in the intent by he incoming data

        try {
            JSONArray eventList = json.getJSONArray("events");
            String[] eventsArray = new String[eventList.length()];
            for (int i = 0; i < eventList.length(); i++) {
                JSONObject event = eventList.getJSONObject(i);
                eventsArray[i] = event.toString();
            }
            intent.putExtra("EventsList", eventsArray);
            intent.putExtra("Protection", true);
            intent.putExtra("whosHome", "no body");
            return intent;
        } catch (Exception e) {
            return intent;
        }

    }

    @Override
    public ActionBarActivity GetActivity() {
        return new LoginActivity();
    }

    @Override
    public SharedPreferences GetSharedPreferences() {
        return getSharedPreferences(getString(R.string.settingFile), Context.MODE_PRIVATE);
    }

    @Override
    public String GetUrl() {
        SharedPreferences settings = getSharedPreferences(getString(R.string.settingFile), Context.MODE_PRIVATE);
        String URL = settings.getString("serverIP", getString(R.string.DefaultServerIP));
        return URL;
    }

    @Override
    public void SetCookieSyncManager() {
        CookieSyncManager.createInstance(this);
    }
}