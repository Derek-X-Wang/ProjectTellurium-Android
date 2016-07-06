package com.intbridge.projecttellurium.airbridge.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intbridge.projecttellurium.airbridge.MainActivity;
import com.intbridge.projecttellurium.airbridge.R;

/**
 * Contact Fragment
 * Created by Derek on 7/5/2016.
 */
public class ContactFragment extends Fragment {
    private MainActivity host;

    public ContactFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = (MainActivity)getActivity();
        host.setActionBarTitle("Contacts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_fragment_contact, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
