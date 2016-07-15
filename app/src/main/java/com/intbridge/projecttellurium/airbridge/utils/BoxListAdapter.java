package com.intbridge.projecttellurium.airbridge.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.models.CardBox;
import com.intbridge.projecttellurium.airbridge.models.Contact;

import java.util.List;

/**
 *
 * Created by Derek on 7/14/2016.
 */
public class BoxListAdapter extends BaseAdapter {

    private Context host;
    private LayoutInflater inflater;
    private List<CardBox> list;

    private static final String TAG = "BoxListAdapter";

    public BoxListAdapter(Context context) {
        host = context;
        inflater = LayoutInflater.from(context);
    }

    public List<CardBox> getList() {
        return list;
    }

    public void setList(List<CardBox> list) {
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
            view = inflater.inflate(R.layout.item_listview_box, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.cardName = (TextView) view.findViewById(R.id.list_item_box_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Log.e(TAG, "getView: "+position);
        CardBox box = list.get(position);
        String word = String.format("CardBox by %s %s", box.getFirstName(), box.getLastName());
        viewHolder.cardName.setText(word);
        return view;
    }

    class ViewHolder {
        TextView cardName;
    }
}
