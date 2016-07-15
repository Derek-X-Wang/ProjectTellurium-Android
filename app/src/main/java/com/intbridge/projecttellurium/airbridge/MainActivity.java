package com.intbridge.projecttellurium.airbridge;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.intbridge.projecttellurium.airbridge.controllers.BoxActivity;
import com.intbridge.projecttellurium.airbridge.controllers.CardFragment;
import com.intbridge.projecttellurium.airbridge.controllers.ContactFragment;
import com.intbridge.projecttellurium.airbridge.controllers.DiscoveryFragment;
import com.intbridge.projecttellurium.airbridge.controllers.NewCardActivity;
import com.intbridge.projecttellurium.airbridge.controllers.SettingFragment;
import com.intbridge.projecttellurium.airbridge.utils.RemoteDataHelper;
import com.intbridge.projecttellurium.airbridge.utils.SendCardAdapter;
import com.intbridge.projecttellurium.airbridge.views.IconWithTextView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.zip.Inflater;

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

    private LayoutInflater inflater;

    private String userId;
    public static final String USER_ID = "USER_ID";
    public static final String CARD_NAME = "CARD_NAME";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String PHONE = "PHONE";
    public static final String POSITION = "POSITION";
    public static final String EMAIL = "EMAIL";
    public static final String ADDRESS = "ADDRESS";
    public static final String WEBSITE = "WEBSITE";
    public static final String IMAGE_REF = "IMAGE_REF";
    public static final String MODE_VIEW = "VIEW";

    public static final int CODE_CONFIRM_SIGNUP = 10;
    public static final int CODE_SELECT_PICTURE = 11;
    public static final int CODE_CREATE_NEW_CARD = 12;
    public static final int CODE_EDIT_EXIST_CARD = 13;
    public static final int CODE_DISPLAY_CARD_DETAIL = 14;
    public static final int CODE_CARD_BOX_LIST = 15;

    private static final String TAG = "MainActivity";

    private RemoteDataHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(Color.parseColor("#919192"));
        setSupportActionBar(myToolbar);

        inflater = LayoutInflater.from(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString(USER_ID);
        }

        helper = new RemoteDataHelper(this);
        helper.setDiscoverPresence(userId);

        Log.e("Login as",userId);
        //new RemoteDataHelper(this).findMyCardsInBackground("derek");
        initView();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public String getUserId() {
        return userId;
    }

    public LayoutInflater getInflater() {
        return inflater;
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

        if(id == R.id.action_inbox) {
            Intent i = new Intent(MainActivity.this, BoxActivity.class);
            i.putExtra(USER_ID, userId);
            startActivityForResult(i,CODE_CARD_BOX_LIST);
        } else if(id == R.id.action_addnew) {
            Intent i = new Intent(MainActivity.this, NewCardActivity.class);
            startActivityForResult(i,CODE_CREATE_NEW_CARD);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.removeDiscoverPresence(userId);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: ??");
        if (requestCode == CODE_CREATE_NEW_CARD) {
            if(resultCode == RESULT_OK){
                Log.e(TAG, "onActivityResult: 12");
                cardFragment.updateData();
            }
        } else if (requestCode == CODE_EDIT_EXIST_CARD) {
            if(resultCode == RESULT_OK){
                Log.e(TAG, "onActivityResult: 13");
                cardFragment.updateData();
            }
        } else if (requestCode == CODE_DISPLAY_CARD_DETAIL) {
            if(resultCode == RESULT_OK){
                Log.e(TAG, "onActivityResult: 14");
                contactFragment.updateData();
            }
        }
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

    public void setBgDrawable(View v, int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            v.setBackground(getDrawable(id));
        } else {
            v.setBackgroundDrawable(getResources().getDrawable(id));
        }
    }
}
