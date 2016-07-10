package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.intbridge.projecttellurium.airbridge.MainActivity;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.utils.NearbyAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Discovery Fragment
 * Created by Derek on 7/5/2016.
 */
public class DiscoveryFragment extends Fragment {
    private MainActivity host;

    @BindView(R.id.fragment_discovery_listview)
    protected ListView listView;

    public DiscoveryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = (MainActivity)getActivity();
        host.setActionBarTitle("Discover");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_discovery, container, false);
        ButterKnife.bind(this,v);

        listView.setAdapter(new NearbyAdapter(host));
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_fragment_discovery, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
