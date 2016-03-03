package com.example.restclient.CustomListViewOdbiorcza;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.restclient.Activity.Wiadomosci;
import com.example.restclient.R;

/**
 * Created by Marcin on 2015-11-26.
 */
public class ListViewAdapterOdbiorcza extends ArrayAdapter<ItemRozmowa> {

    Context context;
    int resource;
    ItemRozmowa objects[] = null;

    public ListViewAdapterOdbiorcza(Context context, int resource, ItemRozmowa[] objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Wiadomosci) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }
        ItemRozmowa item = objects[position];

        TextView tvUzytkownik = (TextView) convertView.findViewById(R.id.tvUzytkownik);
        TextView tvWiadomosc = (TextView) convertView.findViewById(R.id.tvWiadomosc);
        TextView tvLiczbaNieprzeczytanych = (TextView) convertView.findViewById(R.id.tvLiczbaWiadomosci);


        tvUzytkownik.setText(item.getUzytkownik().toString());
        tvWiadomosc.setText(item.getWiadomosc().toString());
        if(item.getLiczbaNieprzeczytanych()>0) {
            tvLiczbaNieprzeczytanych.setText(String.valueOf(item.getLiczbaNieprzeczytanych()));
        }
        else {
            tvLiczbaNieprzeczytanych.setVisibility(View.INVISIBLE);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.ivMessage);
            imageView.setImageResource(R.drawable.read_message);
        }

        return convertView;
    }
}
