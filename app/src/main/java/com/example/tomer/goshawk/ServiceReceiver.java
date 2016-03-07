package com.example.tomer.goshawk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ServiceReceiver extends BroadcastReceiver {
    public ServiceReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences("Goshawk.settingFile.privteMode", Context.MODE_PRIVATE);
        boolean keepGoing = sharedPref.getBoolean("notificationSwitch", true);
        if(keepGoing) {
            Intent startServiceIntent = new Intent(context, NotificationService.class);
            startServiceIntent.putExtra("keepGoing", keepGoing);
            context.startService(startServiceIntent);
        }
    }
}
