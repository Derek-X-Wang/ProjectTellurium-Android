package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    private CardGridAdapter adapter;

    Subscription cardListSubscription;
    Observer<PaginatedQueryList<Card>> observer;
    Observable<PaginatedQueryList<Card>> cardListObservable;
    private final static String TAG = "CardFragment";

    public CardFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = (MainActivity)getActivity();
        host.setActionBarTitle("My Cards");
        cardListObservable = Observable.fromCallable(new Callable<PaginatedQueryList<Card>>() {

            @Override
            public PaginatedQueryList<Card> call() {
                Log.e(TAG, "call: sss" );
                return new RemoteDataHelper(host).findMyCards(host.getUserId());
            }
        });
        observer = new Observer<PaginatedQueryList<Card>>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(PaginatedQueryList<Card> cards) {
                adapter.setCards(cards);
                adapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_card, container, false);
        ButterKnife.bind(this,v);

        adapter = new CardGridAdapter(host);
        gridView.setAdapter(adapter);

        cardListSubscription = cardListObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Card card = adapter.getCards().get(position);
                createIntent(card);
            }
        });
        return v;
    }

    private void createIntent(Card card) {
        Intent i = new Intent(getActivity(), NewCardActivity.class);
        i.putExtra(MainActivity.USER_ID, card.getUserId());
        i.putExtra(MainActivity.CARD_NAME, card.getCardname());
        host.startActivityForResult(i,MainActivity.CODE_EDIT_EXIST_CARD);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_fragment_card, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void notifyDataSetChanged(String cardName){
        RemoteDataHelper helper = new RemoteDataHelper(host);
        helper.setCallback(new RemoteDataHelper.Callback() {
            @Override
            public void done(Card card) {
                CardGridAdapter cardGridAdapter = (CardGridAdapter)gridView.getAdapter();
                //PaginatedQueryList<Card> list = adapter.getCards();
                //list.add(card);
                cardGridAdapter.getCards().add(card);
                cardGridAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cardListSubscription != null && !cardListSubscription.isUnsubscribed()) {
            cardListSubscription.unsubscribe();
        }
    }

    public void updateData() {
        Log.e(TAG, "updateData: ");
        RemoteDataHelper helper = new RemoteDataHelper(host);
        helper.setMyCardsCallback(new RemoteDataHelper.MyCardsCallback() {
            @Override
            public void done(PaginatedQueryList<Card> cards) {
                adapter.setCards(cards);
                adapter.notifyDataSetChanged();
                //gridView.invalidateViews();
                Log.e(TAG, "updateData: here count = "+cards.size());
            }
        });
        helper.findMyCardsInBackground(host.getUserId());
    }
}
