package com.intbridge.projecttellurium.airbridge.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.intbridge.projecttellurium.airbridge.R;

/**
 * Pop-up menu adapter
 * Created by Derek on 7/8/2016.
 */
public class SendCardAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private String[] stringList = {"Test1","Test2","Test3", "Test4", "Test5","Test6"};
    public SendCardAdapter(Context context) {
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
            view = inflater.inflate(R.layout.item_listview_sendcard, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.list_item_textview);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        return view;
    }

    class ViewHolder {
        TextView textView;
    }
}
