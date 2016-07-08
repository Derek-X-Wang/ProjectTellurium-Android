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

import com.intbridge.projecttellurium.airbridge.MainActivity;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.utils.CardGridAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * My Card Fragment
 * Created by Derek on 7/5/2016.
 */
public class CardFragment extends Fragment {
    private MainActivity host;

    @BindView(R.id.fragment_card_gridview)
    protected GridView gridView;

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
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_fragment_card, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
