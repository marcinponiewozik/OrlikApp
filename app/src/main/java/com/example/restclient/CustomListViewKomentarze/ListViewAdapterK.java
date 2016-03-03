package com.example.restclient.CustomListViewKomentarze;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.restclient.Activity.KomentarzeArtykul;
import com.example.restclient.R;

/**
 * Created by Marcin on 2015-11-27.
 */
public class ListViewAdapterK extends ArrayAdapter<ItemKomentarz> {
    Context context;
    int resource;
    ItemKomentarz objects[] = null;
    Activity activity;
    public ListViewAdapterK(Context context, int resource, ItemKomentarz[] objects,Activity activity) {
        super(context, resource, objects);

        this.context=context;
        this.resource=resource;
        this.objects=objects;
        this.activity=activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }
        ItemKomentarz item = objects[position];

        TextView tvAutor = (TextView) convertView.findViewById(R.id.tvAutor);
        TextView tvData = (TextView) convertView.findViewById(R.id.tvData);
        TextView tvTresc = (TextView) convertView.findViewById(R.id.tvTresc);

        tvAutor.setText(item.getAutor());
        tvData.setText(item.getData());
        tvTresc.setText(item.getTresc());

        return convertView;
    }
}
