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
public class LoginUserFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    EditText tmpUserName;
    EditText tmpPassword;

    public LoginUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_user, container, false);

        tmpUserName = (EditText)view.findViewById(R.id.EditUsernameTextID);
        tmpPassword = (EditText)view.findViewById(R.id.EditPassTextID);
        Button loginButton = (Button) view.findViewById(R.id.LoginButtonID);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onButtonPressed("tomer", "shalmon");
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

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String userName, String password);
    }

    public void onButtonPressed(String userName, String password) {
        if (mListener != null) {
            mListener.onFragmentInteraction(userName, password);
        }
    }


}
