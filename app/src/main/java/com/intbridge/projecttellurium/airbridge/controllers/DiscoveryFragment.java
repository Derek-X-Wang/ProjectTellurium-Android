package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.intbridge.projecttellurium.airbridge.MainActivity;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.models.Card;
import com.intbridge.projecttellurium.airbridge.models.Discover;
import com.intbridge.projecttellurium.airbridge.utils.NearbyAdapter;
import com.intbridge.projecttellurium.airbridge.utils.RemoteDataHelper;

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
    Observer<PaginatedScanList<Discover>> observer;
    Observable<PaginatedScanList<Discover>> cardListObservable;

    private final static String TAG = "CardFragment";

    public DiscoveryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = (MainActivity)getActivity();
        host.setActionBarTitle("Discover");
        helper = new RemoteDataHelper(host);
        helper.setDiscoverPresence(host.getUserId());
        adapter = new NearbyAdapter(host);

        cardListObservable = Observable.fromCallable(new Callable<PaginatedScanList<Discover>>() {

            @Override
            public PaginatedScanList<Discover> call() {
                Log.e(TAG, "call: sss" );
                return helper.findPeopleNearby(host.getUserId());
            }
        });
        observer = new Observer<PaginatedScanList<Discover>>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(PaginatedScanList<Discover> cards) {
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
                //Card card = adapter.getCards().get(position);
                //createIntent(card);
                Log.e(TAG, "onItemClick: update intent" );
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_fragment_discovery, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        helper.removeDiscoverPresence(host.getUserId());
        if (cardListSubscription != null && !cardListSubscription.isUnsubscribed()) {
            cardListSubscription.unsubscribe();
        }
    }
}
