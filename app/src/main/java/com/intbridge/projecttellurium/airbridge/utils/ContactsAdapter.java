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
import com.intbridge.projecttellurium.airbridge.models.Contact;

import java.util.List;

/**
 *
 * Created by Derek on 7/14/2016.
 */
public class ContactsAdapter extends BaseAdapter {

    private Context host;
    private LayoutInflater inflater;
    private List<Card> list;

    public ContactsAdapter(Context context) {
        host = context;
        inflater = LayoutInflater.from(context);
    }

    public List<Card> getList() {
        return list;
    }

    public void setList(List<Card> list) {
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
            view = inflater.inflate(R.layout.item_listview_contacts, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.cardName = (TextView) view.findViewById(R.id.list_item_contact_name);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.profile_image_contact);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Card person = list.get(position);
        String name = String.format("%s %s", person.getFirstName(), person.getLastName());
        viewHolder.cardName.setText(name);
        new RemoteDataHelper(host).setImage(viewHolder.imageView, list.get(position).getImageRef());
        return view;
    }
    class ViewHolder {
        TextView cardName;
        ImageView imageView;
    }
}
