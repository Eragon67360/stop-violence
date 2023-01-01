package com.example.applicationdm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ContactListAdapter extends ArrayAdapter<NewContact> {

    //Variables declaration

    private final Context mContext;
    private final int mResource;

    public ContactListAdapter(Context context, int resource, ArrayList<NewContact> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //get the person information
        String name = getItem(position).getNom();
        String phone = getItem(position).getTelephone();


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.adapter_textview_name);
        TextView tvPhone = (TextView) convertView.findViewById(R.id.adapter_textview_phone);

        tvName.setText(name);
        tvPhone.setText(phone);

        return convertView;
    }
}
