package com.intbridge.projecttellurium.airbridge.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.models.Card;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Card GridView Adapter
 * Created by Derek on 7/6/2016.
 */
public class CardGridAdapter extends BaseAdapter {

    private Context host;
    private LayoutInflater inflater;
    private PaginatedQueryList<Card> cards;
    private String dirtyCard;
    private static final String TAG = "CardGridAdapter";

    public CardGridAdapter(Context h) {
        host = h;
        inflater = (LayoutInflater) host.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public PaginatedQueryList<Card> getCards() {
        return cards;
    }

    public void setDirtyCard(String dirtyCard) {
        this.dirtyCard = dirtyCard;
    }

    public String getDirtyCard() {
        return dirtyCard;
    }

    public void setCards(PaginatedQueryList<Card> cards) {
        this.cards = cards;
    }

    @Override
    public int getCount() {
        if(cards == null) return 0;
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        if(cards == null) return null;
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        RemoteDataHelper helper = new RemoteDataHelper(host);
        Card card = cards.get(position);

        if (row == null) {
            row = inflater.inflate(R.layout.item_gridview, parent, false);
            holder = new ViewHolder(row, position);
            row.setTag(holder);
            //helper.setMyCardItem(row, card.getImageRef());
            //Log.e(TAG, "getView: "+"create");
        } else {
            holder = (ViewHolder) row.getTag();
            //Log.e(TAG, "getView: "+"recycle");
        }
        holder.cardName.setText(card.getCardname());
        if(dirtyCard == null || !dirtyCard.equals(card.getCardname())) {
            helper.setMyCardItemAutoOption(row, card.getImageRef());
        } else {
            helper.setMyCardItem(row, card.getImageRef());
        }


        return row;
    }

    class ViewHolder {
        TextView cardName;
        CircleImageView image;

        ViewHolder(View v, int position) {
            cardName = (TextView)v.findViewById(R.id.my_card_gridview_cardname);
            image = (CircleImageView)v.findViewById(R.id.my_card_gridview_image);
        }
    }
}
