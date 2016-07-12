package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.intbridge.projecttellurium.airbridge.MainActivity;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.auth.AWSUserPoolHelper;
import com.intbridge.projecttellurium.airbridge.auth.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Setting Fragment
 * Created by Derek on 7/5/2016.
 */
public class SettingFragment extends Fragment {
    private MainActivity host;

    @BindView(R.id.setting_signin)
    protected Button signinButton;

    public SettingFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = (MainActivity)getActivity();
        host.setActionBarTitle("Settings");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this,v);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(host, LoginActivity.class);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_fragment_setting, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
