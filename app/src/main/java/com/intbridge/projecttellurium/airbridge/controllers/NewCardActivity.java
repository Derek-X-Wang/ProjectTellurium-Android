package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Activity;
import android.os.Bundle;

import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.auth.CognitoHelper;

import butterknife.ButterKnife;

/**
 * Create new name card
 * Created by Derek on 7/12/2016.
 */
public class NewCardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcard);
        //ButterKnife.bind(this);


        initViews();

    }

    private void initViews() {
    }
}
