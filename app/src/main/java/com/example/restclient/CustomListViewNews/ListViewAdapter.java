package com.example.restclient.CustomListViewNews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.restclient.Activity.Aktualnosci;
import com.example.restclient.R;

/**
 * Created by Marcin on 2015-11-26.
 */
public class ListViewAdapter extends ArrayAdapter<Item> {

    Context context;
    int resource;
    Item objects[] = null;
    public ListViewAdapter(Context context, int resource, Item[] objects) {
        super(context, resource, objects);

        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            LayoutInflater inflater = ((Aktualnosci) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }
        Item item = objects[position];

        TextView tvTytul = (TextView) convertView.findViewById(R.id.tvTytul);
        TextView tvSzczegoly = (TextView) convertView.findViewById(R.id.tvSzczegoly);
        TextView tvIdNews = (TextView) convertView.findViewById(R.id.tvIdNews);

        tvTytul.setText(item.getTytul());
        tvSzczegoly.setText(item.getSzcegoly());
        tvIdNews.setText(item.getIdNews());

        return convertView;
    }
}
