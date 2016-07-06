package com.intbridge.projecttellurium.airbridge;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.intbridge.projecttellurium.airbridge.controllers.CardFragment;
import com.intbridge.projecttellurium.airbridge.controllers.ContactFragment;
import com.intbridge.projecttellurium.airbridge.controllers.DiscoveryFragment;
import com.intbridge.projecttellurium.airbridge.controllers.SettingFragment;
import com.intbridge.projecttellurium.airbridge.views.IconWithTextView;
import com.zhy.autolayout.AutoLayoutActivity;


public class MainActivity extends AutoLayoutActivity implements View.OnClickListener {

    private ContactFragment contactFragment;
    private CardFragment cardFragment;
    private DiscoveryFragment discoveryFragment;
    private SettingFragment settingFragment;

    private int currentTab = 0;
    IconWithTextView tabContacts;
    IconWithTextView tabCards;
    IconWithTextView tabDiscovery;
    IconWithTextView tabSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Log.e("test","main");


        initView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private void initView(){

        tabContacts = (IconWithTextView)findViewById(R.id.tab_contacts);
        tabCards = (IconWithTextView)findViewById(R.id.tab_cards);
        tabDiscovery = (IconWithTextView)findViewById(R.id.tab_discovery);
        tabSettings = (IconWithTextView)findViewById(R.id.tab_settings);

        tabContacts.setOnClickListener(this);
        tabCards.setOnClickListener(this);
        tabDiscovery.setOnClickListener(this);
        tabSettings.setOnClickListener(this);

        resetOtherTabs();
        //initFragments();
        switch (currentTab) {
            case 0:
                tabContacts.setIconAlpha(1.0f);
//                getFragmentManager().beginTransaction()
//                        .show(contactFragment)
//                        .commit();
                break;
            case 1:
                tabCards.setIconAlpha(1.0f);
//                getFragmentManager().beginTransaction()
//                        .show(cardFragment)
//                        .commit();
                break;
            case 2:
                tabDiscovery.setIconAlpha(1.0f);
//                getFragmentManager().beginTransaction()
//                        .show(discoveryFragment)
//                        .commit();
                break;
            case 3:
                tabSettings.setIconAlpha(1.0f);
//                getFragmentManager().beginTransaction()
//                        .show(settingFragment)
//                        .commit();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();
        //ActionBar actionBar = getActionBar();
        switch (v.getId()){
            case R.id.tab_contacts:
                currentTab = 0;
                tabContacts.setIconAlpha(1.0f);
                //hideAllFragments();
//                getFragmentManager().beginTransaction()
//                        .show(contactFragment)
//                        .commit();
                break;
            case R.id.tab_cards:
                currentTab = 1;
                tabCards.setIconAlpha(1.0f);
                //hideAllFragments();
//                getFragmentManager().beginTransaction()
//                        .show(cardFragment)
//                        .commit();
                break;
            case R.id.tab_discovery:
                currentTab = 2;
                tabDiscovery.setIconAlpha(1.0f);
                //hideAllFragments();
//                getFragmentManager().beginTransaction()
//                        .show(discoveryFragment)
//                        .commit();
                break;
            case R.id.tab_settings:
                currentTab = 3;
                tabSettings.setIconAlpha(1.0f);
                //hideAllFragments();
//                getFragmentManager().beginTransaction()
//                        .show(settingFragment)
//                        .commit();
                break;
        }
    }

    private void resetOtherTabs(){
        tabContacts.setIconAlpha(0);
        tabCards.setIconAlpha(0);
        tabDiscovery.setIconAlpha(0);
        tabSettings.setIconAlpha(0);
    }
}
