package com.example.restclient.CustomListViewRozmowa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.restclient.Activity.Rozmowa;
import com.example.restclient.CustomListViewUzytkownicy.ItemUzytkonik;
import com.example.restclient.R;
import com.example.restclient.db.DBManager;
import com.example.restclient.db.Znajomy;

/**
 * Created by Marcin on 2015-11-26.
 */
public class ListViewAdapterRozmowa extends ArrayAdapter<ItemWiadomosc> {

    Context context;
    int resource;
    int resourceAutor;
    ItemWiadomosc objects[] = null;
    public ListViewAdapterRozmowa(Context context, int resource,int resourceAutor, ItemWiadomosc[] objects) {
        super(context, resource, objects);

        this.context=context;
        this.resource=resource;
        this.resourceAutor = resourceAutor;
        this.objects=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemWiadomosc item = objects[position];
        if(convertView==null&& item.isAutor()) {
            LayoutInflater inflater = ((Rozmowa)context).getLayoutInflater();
            convertView = inflater.inflate(resourceAutor, parent, false);
        }else if(convertView==null&& !item.isAutor()){
            LayoutInflater inflater = ((Rozmowa)context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }else if(item.isAutor()){
            LayoutInflater inflater = ((Rozmowa)context).getLayoutInflater();
            convertView = inflater.inflate(resourceAutor, parent, false);
        }else if(!item.isAutor()){
            LayoutInflater inflater = ((Rozmowa)context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }

        System.out.println(item);
        TextView tvwiadomosc = (TextView) convertView.findViewById(R.id.tvWiadomosc);
        TextView tvdata = (TextView) convertView.findViewById(R.id.tvData);
        tvwiadomosc.setText(item.getWiadomosc());
        tvdata.setText(item.getData());
        return convertView;
    }
}
