package com.example.restclient.CustomListViewUzytkownicy;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.restclient.Activity.Aktualnosci;
import com.example.restclient.Activity.ListaGraczy;
import com.example.restclient.Activity.Rozmowa;
import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.db.DBManager;
import com.example.restclient.db.Znajomy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 2015-11-26.
 */
public class ListViewAdapterU extends ArrayAdapter<ItemUzytkonik> {

    Context context;
    int resource;
    ItemUzytkonik objects[] = null;
    public LayoutInflater inflater;

    public ListViewAdapterU(Context context, int resource, ItemUzytkonik[] objects, LayoutInflater inflater) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.inflater = inflater;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = getInflater();
            convertView = inflater.inflate(resource, parent, false);
        }
        final ItemUzytkonik item = objects[position];

        TextView tvLogin = (TextView) convertView.findViewById(R.id.tvLogin);
        RadioButton rbOnline = (RadioButton) convertView.findViewById(R.id.rbOnline);

        tvLogin.setText(item.getLogin());
        rbOnline.setChecked(item.getZalogowany());
        if (!item.getZalogowany())
            rbOnline.setVisibility(View.INVISIBLE);

        final Button dodajZnajomego = (Button) convertView.findViewById(R.id.btnZnajomy);
        final Button wyslijWiadomosc = (Button) convertView.findViewById(R.id.btnWiadomosc);
        DBManager manager = new DBManager(context);
        List<Znajomy> lista = new ArrayList<>();
        lista = manager.getAllWishLists();

        boolean znajomy = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getName().equals(item.getLogin())) {
                znajomy = true;
                break;
            }
        }

        if (znajomy) {
            Drawable d = context.getResources().getDrawable(R.drawable.icon_remove_user);
            dodajZnajomego.setBackground(d);
        } else {
            Drawable d = context.getResources().getDrawable(R.drawable.icon_add_user);
            dodajZnajomego.setBackground(d);
        }
        dodajZnajomego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBManager manager = new DBManager(context);

                List<Znajomy> lista = new ArrayList<>();
                lista = manager.getAllWishLists();

                boolean znajomy = false;
                for (int i = 0; i < lista.size(); i++) {
                    if (lista.get(i).getName().equals(item.getLogin())) {
                        manager.removePerson(lista.get(i));
                        znajomy = true;
                        Drawable d = context.getResources().getDrawable(R.drawable.icon_add_user);
                        dodajZnajomego.setBackground(d);
                        break;
                    }
                }
                if (!znajomy) {
                    Znajomy z = new Znajomy();
                    z.setIdPerson(item.getId());
                    z.setName(item.getLogin());
                    manager.addPerson(z);
                    dodajZnajomego.setEnabled(false);
                    Drawable d = context.getResources().getDrawable(R.drawable.icon_remove_user);
                    dodajZnajomego.setBackground(d);
                }
            }
        });
        wyslijWiadomosc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Rozmowa.class);
                intent.putExtra("idRozmowcy", item.getId());
                intent.putExtra("login", item.getLogin());
                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
