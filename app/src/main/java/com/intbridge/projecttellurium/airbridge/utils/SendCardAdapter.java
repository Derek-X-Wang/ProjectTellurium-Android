package com.intbridge.projecttellurium.airbridge.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.models.Card;

/**
 * Pop-up menu adapter
 * Created by Derek on 7/8/2016.
 */
public class SendCardAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private PaginatedQueryList<Card> list;

    public SendCardAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public SendCardAdapter(Context context, PaginatedQueryList<Card> data) {
        inflater = LayoutInflater.from(context);
        list = data;
    }

    public PaginatedQueryList<Card> getList() {
        return list;
    }

    public void setList(PaginatedQueryList<Card> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if(list == null) return 0;
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if(list == null) return null;
        return list.get(position);
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
            viewHolder.cardName = (TextView) view.findViewById(R.id.list_item_textview);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.cardName.setText(list.get(position).getCardname());
        return view;
    }

    class ViewHolder {
        TextView cardName;
    }
}
