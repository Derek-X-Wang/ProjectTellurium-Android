package com.intbridge.projecttellurium.airbridge;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.badoo.mobile.util.WeakHandler;
import com.intbridge.projecttellurium.airbridge.auth.LoginActivity;
import com.intbridge.projecttellurium.airbridge.utils.Fader;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * Splash Screen
 * Created by Derek on 7/4/2016.
 */
public class SplashActivity extends AutoLayoutActivity {
    private final static String LOG_TAG = "SplashActivity";
    private static int SPLASH_TIME_OUT = 3000;

    private WeakHandler handler = new WeakHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Fader.runAlphaAnimation(this, R.id.splash_icon);
        //final ImageView logo = (ImageView)findViewById(R.id.splash_icon);
        if(isNetworkConnected()){
            Log.d(LOG_TAG, "Network Connected");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }else{
            Log.d(LOG_TAG, "Network Disconnected");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }
}
