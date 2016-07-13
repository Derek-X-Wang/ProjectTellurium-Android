package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.intbridge.projecttellurium.airbridge.MainActivity;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.models.Card;
import com.intbridge.projecttellurium.airbridge.utils.CardGridAdapter;
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
 * My Card Fragment
 * Created by Derek on 7/5/2016.
 */
public class CardFragment extends Fragment {
    private MainActivity host;

    @BindView(R.id.fragment_card_gridview)
    protected GridView gridView;

    Subscription cardListSubscription;
    private final String TAG = "CardFragment";

    public CardFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = (MainActivity)getActivity();
        host.setActionBarTitle("My Cards");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_card, container, false);
        ButterKnife.bind(this,v);

        gridView.setAdapter(new CardGridAdapter(host));
        cardListSubscription = cardListObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PaginatedQueryList<Card>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(PaginatedQueryList<Card> cards) {
                        CardGridAdapter adapter = (CardGridAdapter)gridView.getAdapter();
                        adapter.setCards(cards);
                        adapter.notifyDataSetChanged();
                    }
                });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_fragment_card, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void notifyDataSetChanged(){
        CardGridAdapter adapter = (CardGridAdapter)gridView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    Observable<PaginatedQueryList<Card>> cardListObservable = Observable.fromCallable(new Callable<PaginatedQueryList<Card>>() {

        @Override
        public PaginatedQueryList<Card> call() {
            Log.e(TAG, "call: sss" );
            return new RemoteDataHelper(host).findMyCards(host.getUserId());
        }
    });

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cardListSubscription != null && !cardListSubscription.isUnsubscribed()) {
            cardListSubscription.unsubscribe();
        }
    }
}
