package com.example.tomer.goshawk;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    //SharedPreferences sharedPref;

    Boolean isNotificationCheced;
    Boolean isSnapshotCheced;

    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);


        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.settingFile), Context.MODE_PRIVATE);
        CheckBox notificationSwitch = (CheckBox)findViewById(R.id.checkNotificationID);
        CheckBox snapshotSwitch = (CheckBox)findViewById(R.id.checkSnapshotID);
        t = (TextView)findViewById(R.id.testTextID);


        isNotificationCheced = sharedPref.getBoolean("notificationSwitch", true);
        isSnapshotCheced = sharedPref.getBoolean("snapshotSwitch", true);

        notificationSwitch.setChecked(isNotificationCheced);
        snapshotSwitch.setChecked(isSnapshotCheced);

        snapshotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    //do stuff when Switch is ON
                    isSnapshotCheced = isChecked;
                } else {
                    //do stuff when Switch if OFF
                    isSnapshotCheced = isChecked;
                }
                // editor.commit();
            }
        });
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Intent i = new Intent(SettingsActivity.this, NotificationService.class);
                    i.putExtra("keepGoing", true);
                    startService(i);

                    isNotificationCheced = isChecked;
                } else {


                    Intent i = new Intent(SettingsActivity.this, NotificationService.class);
                    i.putExtra("keepGoing", false);
                    startService(i);

                    isNotificationCheced = isChecked;
                }
            }
        });

    }

    @Override
    protected void onStop(){
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(getString(R.string.settingFile), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putBoolean("notificationSwitch", isNotificationCheced);
        editor.putBoolean("snapshotSwitch", isSnapshotCheced);

        // Commit the edits!
        editor.commit();
    }

    public void layoutClick(View v) {
        // does something very interesting
        LinearLayout lV = (LinearLayout)v;
        CheckBox c = (CheckBox) lV.getChildAt(2);
        c.performClick();
    }

}
