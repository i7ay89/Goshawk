package com.example.tomer.goshawk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventActivity extends AppCompatActivity implements Connection {

    public static String[] eventID;
    String[] eventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HttpGoshawkClient client = new HttpGoshawkClient(EventActivity.this);
        client.GetLastEvent();

        // SetContent();
        TextView content = (TextView) findViewById(R.id.EventContent_ID);
        content.setText("Loding...");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "show snapshots", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();
                Snapshot_Fragment snapshot_fragment = new Snapshot_Fragment();
                snapshot_fragment.SetEvents(eventID);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.event_activity_ID, snapshot_fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void SetContent() {
        TextView content = (TextView) findViewById(R.id.EventContent_ID);
        String fullText = "";
        String[] events = eventsList;
        int eventCounter = 1;
        for (int i = 0; i < events.length; i++) {
            try {
                JSONObject json = new JSONObject(events[i]);

                String id = eventCounter + "," + json.getString("id");
                eventID[i] = id;

                JSONObject time = json.getJSONObject("timestamp");
                String min = time.getString("minute");
                if (min.length() == 1) {
                    min = "0" + min;
                }
                String counter = "Event number: " + eventCounter + "\n\n";
                String event_id = "Event ID:\n" + json.getString("id") + "\n\n";
                String timeFormat = "Timestamp:\n" + time.getString("hour") + ":" + min + ":" + time.getString("second")
                        + "\t\t" + time.getString("day") + "." + time.getString("month") + "." + time.getString("year") + "\n\n";
                String severity = "Severity:\n" + json.getString("severity") + "\n\n";
                String type = "Type:\n" + json.getString("type") + "\n\n";
                String description = "Description:\n" + json.getString("description") + "\n\n";
                fullText += counter + event_id + timeFormat + severity + type + description + "\n\n";
                fullText += "-------------------\n\n";
            } catch (Exception e) {
                eventID[i] = "corrupt";
                fullText += "Corrupt Event!\n\n";
                fullText += "-------------------\n\n";
            }
            eventCounter++;
        }
        content.setText(fullText);


    }

    @Override
    public void Respond(String respond) {
        try {
            JSONObject jsonObj = new JSONObject(respond);
            JSONArray eventList = jsonObj.getJSONArray("events");
            eventsList = new String[eventList.length()];
            eventID = new String[eventList.length()];
            for (int i = 0; i < eventList.length(); i++) {
                JSONObject event = eventList.getJSONObject(i);
                eventsList[i] = event.toString();
            }
            SetContent();
        } catch (JSONException e) {
            TextView content = (TextView) findViewById(R.id.EventContent_ID);
            eventID = new String[1];
            eventID[0] = "curropt";
            content.setText("Connection problem");
            return;
        }

    }

    @Override
    public ActionBarActivity GetActivity() {
        return null;
    }

    @Override
    public SharedPreferences GetSharedPreferences() {
        return getSharedPreferences(getString(R.string.settingFile), Context.MODE_PRIVATE);
    }

    @Override
    public String GetUrl() {
        SharedPreferences settings = getSharedPreferences(getString(R.string.settingFile), Context.MODE_PRIVATE);
        return settings.getString("serverIP", getString(R.string.DefaultServerIP));
    }

    @Override
    public void SetCookieSyncManager() {
        CookieSyncManager.createInstance(this);
    }
}
