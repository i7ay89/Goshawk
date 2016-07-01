package com.example.tomer.goshawk;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends ActionBarActivity implements Connection {

    public static String userName = "";
    public static String password = "";
    public static boolean loginSuccess = false;
    private static boolean waitForAnswar = false;
    public boolean Protection = true;
    String URL;
    private String whosHome = "";
    private String loginState = "START";
    private String[] eventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences settings = getSharedPreferences(getString(R.string.settingFile), Context.MODE_PRIVATE);
        URL = settings.getString("serverIP", getString(R.string.DefaultServerIP));

        if (URL.startsWith("http")) {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            HttpGoshawkClient client = new HttpGoshawkClient(LoginActivity.this);
                            try {

                                if (!waitForAnswar) {
                                    loginSuccess = true;
                                    loginState = "SYNC";
                                    client.Sync();
                                    //client.GetLastEvent(); //FIXME
                                } else if (!loginSuccess) {
                                    loginSuccess = true;
                                    waitForAnswar = false;
                                    loginState = "LOGIN";
                                    client.ConnectToServer(userName, password);
                                }
                            } catch (Exception e) {

                            }
                        }
                    }, 3000);
        } else {
            Respond("");
        }

    }

    public void login() {
        this.finish();
        Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
        intent.putExtra("EventsList", eventsList);
        intent.putExtra("Protection", Protection);
        intent.putExtra("whosHome", whosHome);
        startActivity(intent);
    }

    public void Respond(String respond) {
        HttpGoshawkClient client = new HttpGoshawkClient(LoginActivity.this);
        if (respond.contains("org.apache.http.conn.HttpHostConnectException") ||
                respond.contains("org.apache.http.conn.ConnectTimeoutException")) {
            Toast.makeText(this, "Offline", Toast.LENGTH_LONG).show();
            login();

            return;
        }
        try {
            JSONObject jsonObj = new JSONObject(respond);
            if (loginSuccess) {
                //handle sync
                if (ReturnValueHandle(jsonObj)) {
                    return;
                }
                login();
            } else {
                loginSuccess = true;
                client.SendMacAddress();
            }
        } catch (Exception e) {
            waitForAnswar = true;
            loginSuccess = false;
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.LayoutLoginID, new LoginUserFragment());
            ft.commit();
        }

    }

    public ActionBarActivity GetActivity() {
        return LoginActivity.this;
    }

    public SharedPreferences GetSharedPreferences() {
        return getSharedPreferences(getString(R.string.settingFile), Context.MODE_PRIVATE);
    }


    private boolean ReturnValueHandle(JSONObject jsonObj) {
        HttpGoshawkClient client = new HttpGoshawkClient(LoginActivity.this);
        if (loginState.equals("LOGIN")) {
            //send MAC answer
            try {
                //parse sync answer
                loginState = "MAC";
                client.SendMacAddress();
                return true;
            } catch (Exception e) {
                //do somthing
                loginState = "LOGIN";
                client.Sync();
                return true;
            }
        } else if (loginState.equals("MAC")) {
            loginState = "SYNC";
            client.Sync();
            return true;
        } else if (loginState.equals("SYNC")) {
            //parse SYNC answer
            try {
                JSONArray eventList = jsonObj.getJSONArray("events");
                eventsList = new String[eventList.length()];
                for (int i = 0; i < eventList.length(); i++) {
                    JSONObject event = eventList.getJSONObject(i);
                    eventsList[i] = event.toString();
                }
                loginState = "PROTECTION";
                client.CheckProtectionStatus();
                return true;
            } catch (Exception e) {
                loginState = "PROTECTION";
                client.CheckProtectionStatus();
                return true;
            }
        } else if (loginState.equals("PROTECTION")) {
            try {
                //parse Protection answer
                Protection = true;
                String status = jsonObj.getString("status");
                if (status.contains("Unarmed")) {
                    Protection = false;
                }
                loginState = "HOME";
                client.WhosHome();
                return true;
            } catch (Exception e) {
                loginState = "HOME";
                client.WhosHome();
                return true;
            }
        } else if (loginState.equals("HOME")) {
            try {
                //parse Protection answer
                JSONArray nameList = jsonObj.getJSONArray("Users");
                whosHome = "";
                for (int i = 0; i < nameList.length(); i++) {
                    JSONObject name = nameList.getJSONObject(i);
                    whosHome += name.getString("Name") + ", ";
                }
                if (whosHome.length() == 0) {
                    whosHome = getString(R.string.whosHomeDefault);
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public String GetUrl() {
        return URL;
    }

    @Override
    public void SetCookieSyncManager() {
        CookieSyncManager.createInstance(this);
    }
}
