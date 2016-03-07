package com.example.tomer.goshawk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Created by Tomer on 31/12/2015.
 */
public class WakeLocker extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {

        Intent myService = new Intent(context, NotificationService.class);
        context.startService(myService);
    }
}