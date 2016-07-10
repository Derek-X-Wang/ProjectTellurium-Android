package com.intbridge.projecttellurium.airbridge.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.intbridge.projecttellurium.airbridge.R;

/**
 * ListView adapter for nearby people
 * Created by Derek on 7/9/2016.
 */
public class NearbyAdapter extends BaseAdapter {

    private Context host;
    private String[] stringList = {"Test1","Test2","Test3", "Test4", "Test5"};
    private LayoutInflater inflater;

    public NearbyAdapter(Context context) {
        host = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return stringList.length;
    }

    @Override
    public Object getItem(int position) {
        return stringList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.item_listview_nearby, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.list_item_textview);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        return view;
    }

    class ViewHolder {
        TextView name;
        TextView subtitle;
    }
}
