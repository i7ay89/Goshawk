package com.example.tomer.goshawk;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginUserFragment extends Fragment {

    EditText tmpUserName;
    EditText tmpPassword;
    EditText tmpIP;
    String serverIp;
    SharedPreferences settings;
    private OnFragmentInteractionListener mListener;

    public LoginUserFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_user, container, false);

        settings = this.getActivity().getSharedPreferences(getString(R.string.settingFile), Context.MODE_PRIVATE);
        serverIp = settings.getString("serverIP", getString(R.string.DefaultServerIP));

        tmpUserName = (EditText)view.findViewById(R.id.EditUsernameTextID);
        tmpPassword = (EditText)view.findViewById(R.id.EditPassTextID);
        tmpIP = (EditText) view.findViewById(R.id.EditIPTextID);

        tmpIP.setText(serverIp);

        Button loginButton = (Button) view.findViewById(R.id.LoginButtonID);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onButtonPressed("tomer", "shalmon");
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.putString("serverIP", tmpIP.getText().toString());
                // Commit the edits!
                editor.commit();
                LoginActivity.userName = tmpUserName.getText().toString();
                LoginActivity.password = tmpPassword.getText().toString();
                FragmentManager mgr = getFragmentManager();
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void onButtonPressed(String userName, String password) {

        if (mListener != null) {
            mListener.onFragmentInteraction(userName, password);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String userName, String password);
    }



}
