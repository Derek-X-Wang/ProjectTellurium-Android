package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.intbridge.projecttellurium.airbridge.MainActivity;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.models.Card;
import com.intbridge.projecttellurium.airbridge.models.Discover;
import com.intbridge.projecttellurium.airbridge.utils.NearbyAdapter;
import com.intbridge.projecttellurium.airbridge.utils.RemoteDataHelper;
import com.intbridge.projecttellurium.airbridge.utils.SendCardAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.logger.Logger;

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
 * Discovery Fragment
 * Created by Derek on 7/5/2016.
 */
public class DiscoveryFragment extends Fragment {
    private MainActivity host;

    @BindView(R.id.fragment_discovery_listview)
    protected ListView listView;
    private NearbyAdapter adapter;

    private RemoteDataHelper helper;

    Subscription cardListSubscription;
    Observer<List<Discover>> observer;
    Observable<List<Discover>> cardListObservable;

    DialogPlus dialog;

    private final static String TAG = "DiscoveryFragment";

    public DiscoveryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = (MainActivity)getActivity();
        host.setActionBarTitle("Discover");
        helper = new RemoteDataHelper(host);
//        helper.setDiscoverPresence(host.getUserId());
        adapter = new NearbyAdapter(host);

        cardListObservable = Observable.fromCallable(new Callable<List<Discover>>() {

            @Override
            public List<Discover> call() {
                Log.e(TAG, "call: sss "+host.getUserId() );
                return helper.findPeopleNearby(host.getUserId());
            }
        });
        observer = new Observer<List<Discover>>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "find People nearby: complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "find People nearby: onError");
                e.printStackTrace();
            }

            @Override
            public void onNext(List<Discover> cards) {
                Log.e(TAG, "Find: nearby "+cards.size() );
                adapter.setList(cards);
                adapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_discovery, container, false);
        ButterKnife.bind(this,v);

        listView.setAdapter(adapter);
        cardListSubscription = cardListObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showSendCardDialog(adapter.getList().get(position));
                Log.e(TAG, "onItemClick: update intent" );
            }
        });
        return v;
    }

    private void showSendCardDialog(final Discover person) {
        helper.setMyCardsCallback(new RemoteDataHelper.MyCardsCallback() {
            @Override
            public void done(final PaginatedQueryList<Card> cards) {
                final View header = host.getInflater().inflate(R.layout.layout_sendcard_header, null);
                TextView sendToWho = (TextView) header.findViewById(R.id.sendcard_to_who);
                ImageView imageView = (ImageView)header.findViewById(R.id.profile_image_sendcard);
                helper.setImage(imageView, person.getImageRef());
                sendToWho.setText(String.format("%s %s", person.getFirstName(), person.getLastName()));
                SendCardAdapter sendCardadapter = new SendCardAdapter(host,cards);
                dialog = DialogPlus.newDialog(host)
                        .setAdapter(sendCardadapter)
                        .setGravity(Gravity.BOTTOM)
                        .setHeader(header)
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                Logger.e("onItemClick is","sending card");
                                helper.addToContactInBackground(person.getUserId(),cards.get(position).getImageRef());
                                dialog.dismiss();
                            }
                        })
                        .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();
                View holder = dialog.getHolderView();
                // TODO: implement a new dialog for cleaner code, screen size independent and re-usability
                // using a trick to get header transparent and content semi-transparent
                //holder.setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_content));
                host.setBgDrawable(holder, R.drawable.dialog_content);
                dialog.show();
            }
        });
        helper.findMyCardsInBackground(host.getUserId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_fragment_discovery, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        helper.removeDiscoverPresence(host.getUserId());
        if (cardListSubscription != null && !cardListSubscription.isUnsubscribed()) {
            cardListSubscription.unsubscribe();
        }
    }
}
