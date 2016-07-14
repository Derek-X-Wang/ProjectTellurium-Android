package com.intbridge.projecttellurium.airbridge.utils;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.intbridge.projecttellurium.airbridge.R;
import com.intbridge.projecttellurium.airbridge.models.Card;
import com.intbridge.projecttellurium.airbridge.models.Discover;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * ListView adapter for nearby people
 * Created by Derek on 7/9/2016.
 */
public class NearbyAdapter extends BaseAdapter {

    private Context host;
    private String[] stringList = {"Test1","Test2","Test3", "Test4", "Test5"};
    private LayoutInflater inflater;

    PaginatedScanList<Discover> list;

    public NearbyAdapter(Context context) {
        host = context;
        inflater = LayoutInflater.from(context);
    }

    public void setList(PaginatedScanList<Discover> list) {
        this.list = list;
    }

    public PaginatedScanList<Discover> getList() {
        return list;
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
        RemoteDataHelper helper = new RemoteDataHelper(host);
        Discover person = list.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.item_listview_nearby, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.listview_nearby_name);
            viewHolder.position = (TextView) view.findViewById(R.id.listview_nearby_position);
            viewHolder.imageRef = person.getImageRef();
            viewHolder.profileImage = (ImageView) view.findViewById(R.id.profile_image_nearby);
            viewHolder.backgroundImage = (ImageView) view.findViewById(R.id.item_listview_nearby_bg);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(String.format("%s %s", person.getFirstName(), person.getLastName()));
        viewHolder.position.setText(person.getPosition());
        helper.setImage(viewHolder.profileImage, viewHolder.imageRef);
        BlurHelper.BlurFactor factor = new BlurHelper.BlurFactor();
        factor.width = 280;
        factor.height = 60;
        factor.radius = 25;
        helper.setBlurImage(viewHolder.backgroundImage, viewHolder.imageRef, factor);
        return view;
    }


    class ViewHolder {
        TextView name;
        TextView position;
        String imageRef;
        ImageView profileImage;
        ImageView backgroundImage;
    }
}
