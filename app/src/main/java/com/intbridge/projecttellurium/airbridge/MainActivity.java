package com.intbridge.projecttellurium.airbridge;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AutoLayoutActivity {

    private ContactFragment contactFragment;
    private CardFragment cardFragment;
    private DiscoveryFragment discoveryFragment;
    private SettingFragment settingFragment;

    private int currentTab = R.id.tab_cards;
    @BindView(R.id.tab_contacts)
    protected IconWithTextView tabContacts;
    @BindView(R.id.tab_cards)
    protected IconWithTextView tabCards;
    @BindView(R.id.tab_discovery)
    protected IconWithTextView tabDiscovery;
    @BindView(R.id.tab_settings)
    protected IconWithTextView tabSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(Color.parseColor("#919192"));
        setSupportActionBar(myToolbar);
        Log.e("test","main1");


        initView();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

        ButterKnife.bind(this);
        contactFragment = new ContactFragment();
        cardFragment = new CardFragment();
        discoveryFragment = new DiscoveryFragment();
        settingFragment = new SettingFragment();

        resetOtherTabs();
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_content, getFragment(R.id.tab_cards))
                .commit();
        setIconTextAlpha(currentTab);
    }

    @OnClick({R.id.tab_contacts, R.id.tab_cards, R.id.tab_discovery, R.id.tab_settings})
    public void onTabsClick(View v) {
        resetOtherTabs();
        currentTab = v.getId();
        setIconTextAlpha(currentTab);
        replaceFragment(v.getId());
    }

    private void replaceFragment(int tab) {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, getFragment(tab))
                .commit();
    }

    private void setIconTextAlpha(int id) {
        switch (id){
            case R.id.tab_contacts:
                currentTab = 0;
                tabContacts.setIconAlpha(1.0f);
                break;
            case R.id.tab_cards:
                currentTab = 1;
                tabCards.setIconAlpha(1.0f);
                break;
            case R.id.tab_discovery:
                currentTab = 2;
                tabDiscovery.setIconAlpha(1.0f);
                break;
            case R.id.tab_settings:
                currentTab = 3;
                tabSettings.setIconAlpha(1.0f);
                break;
        }
    }

    private Fragment getFragment(int id) {
        switch (id){
            case R.id.tab_contacts:
                return contactFragment;
            case R.id.tab_cards:
                return cardFragment;
            case R.id.tab_discovery:
                return discoveryFragment;
            case R.id.tab_settings:
                return settingFragment;
            default:
                return contactFragment;
        }
    }

    private void resetOtherTabs(){
        tabContacts.setIconAlpha(0);
        tabCards.setIconAlpha(0);
        tabDiscovery.setIconAlpha(0);
        tabSettings.setIconAlpha(0);
    }
}
