package com.example.restclient.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.restclient.CustomListViewOdbiorcza.ItemRozmowa;
import com.example.restclient.CustomListViewOdbiorcza.ListViewAdapterOdbiorcza;
import com.example.restclient.CustomListViewUzytkownicy.ItemUzytkonik;
import com.example.restclient.CustomListViewUzytkownicy.ListViewAdapterU;
import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.db.DBManager;
import com.example.restclient.db.WiadomoscDB;
import com.example.restclient.db.Znajomy;
import com.example.restclient.jsonObject.UzytkownikJson;
import com.example.restclient.tabele.Uzytkownik;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Wiadomosci extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    public Context context = this;

    public SharedPreferences preferences;

    public List<Uzytkownik> listaUzytkownikow;

    Dialog dialog;
    Button szukaj;
    ListView lista;
    EditText etFragment;
    public static ListView lvZnajomi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiadomosci);

        preferences = getSharedPreferences("daneUzytkownika", Wiadomosci.MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.icon_odbiorcza);
        tabLayout.getTabAt(1).setIcon(R.drawable.icon_znajomi);

        initDialogSzukaj();
    }

    private void initDialogSzukaj() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.szukaj_znajomych);
        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);
        dialog.setTitle("Szukaj znajomych");

        etFragment = (EditText) dialog.findViewById(R.id.etFragment);
        szukaj = (Button) dialog.findViewById(R.id.btnSzukaj);
        lista = (ListView) dialog.findViewById(R.id.lvUser);
        etFragment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0)
                    szukaj.setEnabled(false);
                else
                    szukaj.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        szukaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                REST_GET_USERS wyszukaj = new REST_GET_USERS();
                wyszukaj.execute(etFragment.getText().toString());
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                System.out.println("Cancel!!");
                initLvZnajomi(context);
            }
        });
    }


    public void szukajZnajomych(View v) {
        dialog.show();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)

                return OdbiorczaFragment.newInstance(preferences);
            else
                return ZnajomiFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:

                    return "";
                case 1:
                    return "";
            }
            return null;
        }
    }

    public static class OdbiorczaFragment extends Fragment {

        static SharedPreferences preferences;

        public static OdbiorczaFragment newInstance(SharedPreferences p) {
            OdbiorczaFragment fragment = new OdbiorczaFragment();
            preferences = p;
            return fragment;
        }

        public OdbiorczaFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_wiadomosci_odbiorcza, container, false);
            ListView lvOdbiorcza = (ListView) rootView.findViewById(R.id.lvOdbiorcza);
            initLvOdbiorcza(lvOdbiorcza);
            return rootView;
        }

        private void initLvOdbiorcza(ListView lvOdbiorcza) {
            DBManager manager = new DBManager(getContext());
            List<WiadomoscDB> wiadomosci = new ArrayList<>();
            wiadomosci = manager.getOdbiorcza();
            ItemRozmowa[] items = new ItemRozmowa[wiadomosci.size()];
            ItemRozmowa item;

            for (int i = 0; i < wiadomosci.size(); i++) {
                item = new ItemRozmowa();
                item.setId(wiadomosci.get(i).getIdRozmowcy());
                if (wiadomosci.get(i).isWychodzaca()) {
                    item.setWiadomosc("Ja: " + wiadomosci.get(i).getWiadomosc());
                } else {
                    item.setWiadomosc(wiadomosci.get(i).getWiadomosc());
                }
                if (item.getWiadomosc().length() > 20)
                    item.setWiadomosc(item.getWiadomosc().substring(0, 20) + "...");
                item.setUzytkownik(wiadomosci.get(i).getNazwaRozmowcy());
                item.setLiczbaNieprzeczytanych(preferences.getInt(wiadomosci.get(i).getNazwaRozmowcy(), 0));
                items[i] = item;
            }

            final ListViewAdapterOdbiorcza adapter = new ListViewAdapterOdbiorcza(getContext(), R.layout.item_odbiorcza, items);
            lvOdbiorcza.setAdapter(adapter);


            lvOdbiorcza.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ItemRozmowa item = (ItemRozmowa) parent.getAdapter().getItem(position);
                    Intent intent = new Intent(getContext(), Rozmowa.class);
                    intent.putExtra("login", item.getUzytkownik());
                    intent.putExtra("idRozmowcy", item.getId());
                    startActivity(intent);
                }
            });
        }

    }

    public static class ZnajomiFragment extends Fragment {

        public static ZnajomiFragment newInstance() {
            ZnajomiFragment fragment = new ZnajomiFragment();
            return fragment;
        }

        public ZnajomiFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_wiadomosci_znajomi, container, false);

            lvZnajomi = (ListView) rootView.findViewById(R.id.lvListaGraczy);
            ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.show();
            initLvZnajomi(getContext());
            dialog.dismiss();
            return rootView;
        }
    }


    private static void initLvZnajomi(Context context) {
        DBManager manager = new DBManager(context);
        List<Znajomy> znajomi = new ArrayList<>();
        znajomi = manager.getAllWishLists();
        ItemUzytkonik[] items = new ItemUzytkonik[znajomi.size()];
        ItemUzytkonik item;
        for (int i = 0; i < znajomi.size(); i++) {
            item = new ItemUzytkonik();
            item.setLogin(znajomi.get(i).getName());
            item.setZalogowany(false);
            item.setId(znajomi.get(i).getIdPerson());
            item.setZnajomy(true);
            items[i] = item;
        }

        ListViewAdapterU adapter = new ListViewAdapterU(context, R.layout.item_uzytkownik, items, ((Wiadomosci) context).getLayoutInflater());
        lvZnajomi.setAdapter(adapter);
    }

    private class REST_GET_USERS extends AsyncTask<String, Void, Uzytkownik[]> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Uzytkownik[] doInBackground(String... params) {
            String url = Dane.URL;
            url += "/uzytkownik/wyszukaj/" + params[0];

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            UzytkownikJson[] events = null;
            Uzytkownik[] users = null;
            try {
                ResponseEntity<UzytkownikJson[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, UzytkownikJson[].class);
                events = responseEntity.getBody();
                users = new Uzytkownik[events.length];

                for (int i = 0; i < events.length; i++) {
                    users[i] = new Uzytkownik(events[i]);
                }
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }


            return users;
        }

        @Override
        protected void onPostExecute(Uzytkownik[] uzytkownik) {
            dialog.dismiss();

            listaUzytkownikow = new ArrayList<>(Arrays.asList(uzytkownik));
            ustawListe(listaUzytkownikow);
        }

        private void ustawListe(List<Uzytkownik> listaUzytkownik) {
            List<ItemUzytkonik> temp = new ArrayList<>();
            ItemUzytkonik item = new ItemUzytkonik();

            DBManager manager = new DBManager(context);
            List<Znajomy> znajomi = new ArrayList<>();
            znajomi = manager.getAllWishLists();
            boolean znajomy;
            for (int i = 0; i < listaUzytkownik.size(); i++) {
                znajomy = false;
                for (int j = 0; j < znajomi.size(); j++) {
                    if (listaUzytkownik.get(i).getId() == znajomi.get(j).getId()) {
                        znajomy = true;
                        break;
                    }
                }
                if (listaUzytkownik.get(i).getId() == Dane.uzytkownik.getId()) {
                    continue;
                }
                item = new ItemUzytkonik();
                item.setId(listaUzytkownik.get(i).getId());
                item.setLogin(listaUzytkownik.get(i).getLogin());
                item.setZalogowany(listaUzytkownik.get(i).isZalogowany());
                item.setZnajomy(znajomy);
                if (!item.getLogin().equals(Dane.uzytkownik.getLogin()))
                    temp.add(item);
            }
            ItemUzytkonik[] items = new ItemUzytkonik[temp.size()];
            items = temp.toArray(items);
            ListViewAdapterU adapter = new ListViewAdapterU(context, R.layout.item_uzytkownik, items, ((Wiadomosci) context).getLayoutInflater());

            lista.setAdapter(adapter);
        }

    }

}
