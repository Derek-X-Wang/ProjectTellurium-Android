package com.intbridge.projecttellurium.airbridge.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.amazonaws.auth.policy.Resource;
import com.intbridge.projecttellurium.airbridge.R;

import java.util.List;


/**
 * Card GridView Adapter
 * Created by Derek on 7/6/2016.
 */
public class CardGridAdapter extends BaseAdapter {

    private Context host;
    private String[] stringList = {"Test1","Test2","Test3", "Test4", "Test5"};
    private LayoutInflater inflater;

    public CardGridAdapter(Context h) {
        host = h;
        inflater = (LayoutInflater) host.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            row = inflater.inflate(R.layout.gridview_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        return row;
    }

    class ViewHolder {
        String cardName;
        ImageView image;
        ViewHolder(View v) {
            BlurHelper.with(host).sampling(16).radius(25).image(R.drawable.test1).async().setBackground(v);
            //Blurry.with(host).radius(25).sampling(2).onto((ViewGroup) v);
        }
    }
}
