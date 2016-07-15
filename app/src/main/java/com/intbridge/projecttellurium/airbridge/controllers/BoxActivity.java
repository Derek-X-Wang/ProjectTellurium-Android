package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.intbridge.projecttellurium.airbridge.MainActivity;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.auth.CognitoHelper;
import com.intbridge.projecttellurium.airbridge.models.Card;
import com.intbridge.projecttellurium.airbridge.models.CardBox;
import com.intbridge.projecttellurium.airbridge.models.Discover;
import com.intbridge.projecttellurium.airbridge.utils.BoxListAdapter;
import com.intbridge.projecttellurium.airbridge.utils.NearbyAdapter;
import com.intbridge.projecttellurium.airbridge.utils.RemoteDataHelper;
import com.intbridge.projecttellurium.airbridge.utils.SendCardAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.logger.Logger;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Card Box List
 * Created by Derek on 7/14/2016.
 */
public class BoxActivity extends AutoLayoutActivity {
    @BindView(R.id.cardbox_listview)
    protected ListView listView;
    private BoxListAdapter adapter;

    private RemoteDataHelper helper;
    private DialogPlus dialog;
    private BoxActivity host;


    private String userId;

    private Subscription cardListSubscription;
    private Observer<List<CardBox>> observer;
    private Observable<List<CardBox>> cardListObservable;

    private final static String TAG = "BoxActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);
        host = this;
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_cardbox);
        myToolbar.setTitleTextColor(Color.parseColor("#919192"));
        myToolbar.setTitle("Card Boxes");
        setSupportActionBar(myToolbar);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString(MainActivity.USER_ID);
        }

        helper = new RemoteDataHelper(this);
        adapter = new BoxListAdapter(this);
        listView.setAdapter(adapter);

        cardListObservable = Observable.fromCallable(new Callable<List<CardBox>>() {

            @Override
            public List<CardBox> call() {
                Log.e(TAG, "call: box "+userId );
                return helper.getAllCardBoxes(userId);
            }
        });
        observer = new Observer<List<CardBox>>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "find box nearby: complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "find box nearby: onError");
                e.printStackTrace();
            }

            @Override
            public void onNext(List<CardBox> boxes) {
                Log.e(TAG, "Find: boxes "+boxes.size() );
                adapter.setList(boxes);
                adapter.notifyDataSetChanged();
            }
        };
        cardListSubscription = cardListObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int boxListPosition = position;
                helper.setMyCardsCallback(new RemoteDataHelper.MyCardsCallback() {
                    @Override
                    public void done(final PaginatedQueryList<Card> cards) {
                        LayoutInflater inflater = LayoutInflater.from(host);
                        final View header = inflater.inflate(R.layout.layout_sendcard_box_header, null);
                        TextView sendToWho = (TextView) header.findViewById(R.id.sendcard_to_who_box);
                        sendToWho.setText(String.format("%s's CardBox", userId));
                        SendCardAdapter sendCardadapter = new SendCardAdapter(host,cards);
                        dialog = DialogPlus.newDialog(host)
                                .setAdapter(sendCardadapter)
                                .setGravity(Gravity.BOTTOM)
                                .setHeader(header)
                                .setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                        Logger.e("onItemClick is","sending card to box");
                                        helper.placeCardInBoxInBackground(adapter.getList().get(boxListPosition).getUserId(), cards.get(position).getImageRef());
                                        dialog.dismiss();
                                    }
                                })
                                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                                .setContentBackgroundResource(android.R.color.transparent)
                                .create();
                        View holder = dialog.getHolderView();
                        host.setBgDrawable(holder, R.drawable.dialog_content);
                        dialog.show();
                    }
                });
                helper.findMyCardsInBackground(userId);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_box, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_new_box) {
            helper.setMyCardsCallback(new RemoteDataHelper.MyCardsCallback() {
                @Override
                public void done(final PaginatedQueryList<Card> cards) {
                    LayoutInflater inflater = LayoutInflater.from(host);
                    final View header = inflater.inflate(R.layout.layout_sendcard_box_header, null);
                    TextView sendToWho = (TextView) header.findViewById(R.id.sendcard_to_who_box);
                    sendToWho.setText(String.format("%s's CardBox", userId));
                    SendCardAdapter sendCardadapter = new SendCardAdapter(host,cards);
                    dialog = DialogPlus.newDialog(host)
                            .setAdapter(sendCardadapter)
                            .setGravity(Gravity.BOTTOM)
                            .setHeader(header)
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                    Logger.e("onItemClick is","Create and sending card to box");
                                    helper.createCardBoxInBackground(userId, cards.get(position).getImageRef());
                                    dialog.dismiss();
                                }
                            })
                            .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                            .setContentBackgroundResource(android.R.color.transparent)
                            .create();
                    View holder = dialog.getHolderView();
                    host.setBgDrawable(holder, R.drawable.dialog_content);
                    dialog.show();
                }
            });
            helper.findMyCardsInBackground(userId);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cardListSubscription != null && !cardListSubscription.isUnsubscribed()) {
            cardListSubscription.unsubscribe();
        }
    }

    public void setBgDrawable(View v, int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            v.setBackground(getDrawable(id));
        } else {
            v.setBackgroundDrawable(getResources().getDrawable(id));
        }
    }
}
