package com.example.tomer.goshawk;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;

/**
 * Created by Tomer on 22/05/2016.
 */
public interface Connection {
    void Respond(String respond);

    ActionBarActivity GetActivity();

    SharedPreferences GetSharedPreferences();

    String GetUrl();

    void SetCookieSyncManager();
}
