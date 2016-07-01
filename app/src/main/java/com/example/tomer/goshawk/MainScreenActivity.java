package com.example.tomer.goshawk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

public class MainScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Connection {

    public static String whosHome;
    public static boolean protection;
    public static String[] events;
    public static boolean continueWait;
    Button activateButton;
    private String[] eventID;
    private boolean isCheckPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            whosHome = extras.getString("whosHome");
            events = extras.getStringArray("EventsList");
            protection = extras.getBoolean("Protection");
        } else {
            whosHome = getResources().getString(R.string.whosHomeDefault);
        }
        if (events == null) {
            events = new String[0];
        }

        activateButton = (Button)findViewById(R.id.activateButtonID);

        activateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                continueWait = true;
                HttpGoshawkClient client = new HttpGoshawkClient(MainScreenActivity.this);

                if (protection) {
                    client.Disarm();
                } else {
                    client.Arm();
                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.mainActivityID, new ChangeActivationFragment());
                ft.addToBackStack(null);
                ft.commit();

            }
        });
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, whosHome, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        ImageView homeButton = (ImageView)findViewById(R.id.homeButtonID);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, whosHome + " is home.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setParam();
        InitEventIDs();
    }

    public void setParam() {
        String activationString;

        if (protection) {
            activationString = getResources().getString(R.string.systemActivate);
            activateButton.setBackgroundResource(R.drawable.activate_button);
        } else {
            activationString = getResources().getString(R.string.systemDeactivate);
            activateButton.setBackgroundResource(R.drawable.deactivate_button);
        }
        TextView activationText = (TextView) findViewById(R.id.activateinTextID);
        activationText.setText(activationString);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        setParam();
    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
            if (eventID.length != 0) {
                Snapshot_Fragment snapshot_fragment = new Snapshot_Fragment();
                snapshot_fragment.SetEvents(eventID);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.mainActivityID, snapshot_fragment);
                ft.addToBackStack(null);
                ft.commit();
            } else {
                Toast.makeText(this, "No new events", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_gallery) {
            HttpGoshawkClient client = new HttpGoshawkClient(MainScreenActivity.this);
            isCheckPermission = true;
            client.GetPermission();
            //Intent i = new Intent(MainScreenActivity.this, AddUserActivity.class);
            //startActivity(i);
        } else if (id == R.id.nav_slideshow) {
            Intent i = new Intent(MainScreenActivity.this, EventActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(MainScreenActivity.this, SettingsActivity.class);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void InitEventIDs() {
        eventID = new String[events.length];
        int eventCounter = 1;
        for (int i = 0; i < events.length; i++) {
            try {
                JSONObject json = new JSONObject(events[i]);
                String id = eventCounter + "," + json.getString("id");
                eventCounter++;
                eventID[i] = id;
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void Respond(String respond) {
        //handle protection respond
        try {
            JSONObject jsonObj = new JSONObject(respond);
            if (!isCheckPermission) {
                String s = jsonObj.getString("Success");
                if (s.contentEquals("1")) {
                    protection = !protection;
                }
                continueWait = false;
            } else {
                String perm = jsonObj.getString("Permission");
                if (perm.contains("Admin")) {
                    Intent i = new Intent(MainScreenActivity.this, AddUserActivity.class);
                    startActivity(i);
                    return;
                }
                throw new Exception();
            }
        } catch (Exception e) {
            if (!isCheckPermission) {
                continueWait = false;
            } else {
                Toast.makeText(getApplicationContext(), "Only admin can add user", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }


    @Override
    public ActionBarActivity GetActivity() {

        return null;
    }

    @Override
    public void SetCookieSyncManager() {
        CookieSyncManager.createInstance(this);
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
}
