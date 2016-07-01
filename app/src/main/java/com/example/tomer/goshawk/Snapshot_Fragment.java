package com.example.tomer.goshawk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Snapshot_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Snapshot_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Snapshot_Fragment extends Fragment implements Connection {

    View view;
    String URL;
    InputStream instream = null;
    HttpGoshawkClient client;
    TextView textView;
    String eventID;
    String eventCounter;
    int counter = 0;
    String[] eventsList = new String[0];

    public void SetEvents(String[] events) {
        this.eventsList = events;
    }

    private void LoadNext() {
        client = new HttpGoshawkClient(Snapshot_Fragment.this);
        if (eventsList == null || eventsList.length <= counter) {
            textView.setText("Curropt");
            return;

        } else {
            eventID = eventsList[counter].substring(2);
            eventCounter = eventsList[counter].substring(0, 1);
        }
        if (eventsList[counter] == "curropt") {
            //handle
            textView.setText("No Image");
        } else {
            client.GetSnapshot(eventID);
            textView.setText("Event number: " + eventCounter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_snapshot, container, false);


        Button next = (Button) view.findViewById(R.id.next_button_ID);
        Button back = (Button) view.findViewById(R.id.back_button_ID);
        textView = (TextView) view.findViewById(R.id.event_title_ID);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventsList != null && counter < eventsList.length - 1) {
                    counter++;
                    LoadNext();
                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventsList != null && counter > 0) {
                    counter--;
                    LoadNext();
                }

            }
        });
        LoadNext();
        return view;
    }

    private void LoadImages() {
        try {
            File imgFile = new File("/storage/sdcard0/Goshawk/" + eventID + ".jpg");
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ImageView imageView = (ImageView) view.findViewById(R.id.snapshot_image_ID);
                imageView.setImageBitmap(myBitmap);
            }


        } catch (Exception e) {
            e.toString();
        }
    }


    @Override
    public void Respond(String respond) {
        //instream = client.GetInputStream();
        if (respond.contains("Success"))
            LoadImages();
    }

    @Override
    public ActionBarActivity GetActivity() {
        return null;
    }

    @Override
    public SharedPreferences GetSharedPreferences() {
        return getActivity().getSharedPreferences(getString(R.string.settingFile), Context.MODE_PRIVATE);
    }

    @Override
    public String GetUrl() {
        SharedPreferences settings = getActivity().getSharedPreferences(getString(R.string.settingFile), Context.MODE_PRIVATE);
        return settings.getString("serverIP", getString(R.string.DefaultServerIP));
    }

    @Override
    public void SetCookieSyncManager() {
        CookieSyncManager.createInstance(getActivity());
    }

}
