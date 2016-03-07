package com.example.tomer.goshawk;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class LoginActivity extends ActionBarActivity {

    public static String userName = "tom"; //fix to ""
    public static String password = "";

    private String whosHome;
    private boolean isActivate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        boolean loginSuccess = false;
                        //Temp
                        if(userName.length() != 0) { //// FIXME: 25/12/2015
                            loginSuccess = true;
                            whosHome = userName;
                            isActivate = true;
                        }
                        try {
                            Thread.sleep(100); //login to server -> update loginSuccess
                            if(!loginSuccess) {
                                FragmentManager fm = getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.add(R.id.LayoutLoginID, new LoginUserFragment());
                                ft.commit();
                            } else {
                                login();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, 3000);

    }

    public void login() {
        this.finish();
        Intent i = new Intent(LoginActivity.this, MainScreenActivity.class);
        i.putExtra("whosHome", whosHome);
        i.putExtra("isActivate", isActivate);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }
}
