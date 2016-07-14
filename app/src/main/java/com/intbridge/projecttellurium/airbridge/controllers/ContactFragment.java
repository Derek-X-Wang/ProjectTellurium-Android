package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.SubscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.intbridge.projecttellurium.airbridge.MainActivity;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.models.Card;
import com.intbridge.projecttellurium.airbridge.models.Contact;
import com.intbridge.projecttellurium.airbridge.models.Discover;
import com.intbridge.projecttellurium.airbridge.utils.ContactsAdapter;
import com.intbridge.projecttellurium.airbridge.utils.RemoteDataHelper;
import com.squareup.haha.perflib.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Contact Fragment
 * Created by Derek on 7/5/2016.
 */
public class ContactFragment extends Fragment {

    private MainActivity host;

    @BindView(R.id.fragment_contact_listview)
    protected ListView listView;
    private ContactsAdapter adapter;

    private RemoteDataHelper helper;

    private Subscription cardListSubscription;
    private Observer<List<Card>> observer;
    private Observable<List<Card>> cardListObservable;
    private static final String TAG = "ContactFragment";

    public ContactFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = (MainActivity) getActivity();
        host.setActionBarTitle("Contacts");

        adapter = new ContactsAdapter(host);
        helper = new RemoteDataHelper(host);
        cardListObservable = Observable.fromCallable(new Callable<List<Card>>() {

            @Override
            public List<Card> call() {
                Log.e(TAG, "call: ddd" );
                Contact contactBook = helper.getMyContact(host.getUserId());
                List<String> list = contactBook.getContacts();
                List<Card> cardList = new ArrayList<Card>();
                if (list != null) {
                    for (String key : list) {
                        // TODO: prevent has card name include "_"
                        String[] set = key.split("_");
                        Card card = helper.getMyCard(set[0],set[1]);
                        cardList.add(card);
                    }
                }
                Log.e(TAG, "call: contact"+cardList.size() );
                return cardList;
            }
        });
        observer = new Observer<List<Card>>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(List<Card> cards) {
                adapter.setList(cards);
                adapter.notifyDataSetChanged();
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.bind(this,v);

        listView.setAdapter(adapter);
        cardListSubscription = cardListObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Card> list = adapter.getList();
                Card selectedCard = list.get(position);
                Intent i = new Intent(host, NewCardActivity.class);
                i.putExtra(MainActivity.MODE_VIEW, true);
                i.putExtra(MainActivity.USER_ID, selectedCard.getUserId());
                i.putExtra(MainActivity.CARD_NAME,selectedCard.getCardname());
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_fragment_contact, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
