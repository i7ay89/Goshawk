package com.example.tomer.goshawk;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeActivationFragment extends Fragment {


    public ChangeActivationFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_activation, container, false);
        // Inflate the layout for this fragment
        FrameLayout layout = (FrameLayout) view.findViewById(R.id.fragmentChangeActivationID);
        //   HttpGoshawkClient client = new HttpGoshawkClient((Connection)getActivity());
        //   client.Arm();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        try { //server connection
                            while (MainScreenActivity.continueWait) {
                                Thread.sleep(3000);
                            }
                            FragmentManager fm = getFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            fm.popBackStack();
                            ft.commit();
                            // MainScreenActivity.protection = !MainScreenActivity.protection;
                            MainScreenActivity mainAct = (MainScreenActivity) getActivity();
                            mainAct.setParam();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, 3000);

        return view;
    }


}
