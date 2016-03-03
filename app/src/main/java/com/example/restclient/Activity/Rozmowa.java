package com.example.restclient.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.restclient.CustomListViewRozmowa.ItemWiadomosc;
import com.example.restclient.CustomListViewRozmowa.ListViewAdapterRozmowa;
import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.Services.KomentarzService;
import com.example.restclient.Services.RozmowaService;
import com.example.restclient.Services.WiadomosciService;
import com.example.restclient.db.DBManager;
import com.example.restclient.db.WiadomoscDB;
import com.example.restclient.jsonObject.WiadomoscJson;
import com.example.restclient.tabele.Wiadomosc;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Rozmowa extends AppCompatActivity {

    public static final String ACTION_ROZMOWA = "Rozmowa";
    public static Long idRozmowcy;
    String login;
    Button btnWyslij;
    EditText etWiadomosc;
    ListView lvRozmowa;
    private Context context = this;
    private MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rozmowa);

        login = getIntent().getExtras().getString("login");
        idRozmowcy = getIntent().getExtras().getLong("idRozmowcy");
        setTitle("Rozmowa z " + login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initControls();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("OnResume - Rozmowa");
        zaznaczPrzeczytane();
        startService(new Intent(context, RozmowaService.class));
        initReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("OnPause - Rozmowa");
        stopService(new Intent(context, RozmowaService.class));
        finishReceiver();
    }

    private void initReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(ACTION_ROZMOWA);
        registerReceiver(myReceiver, filter);
    }

    private void finishReceiver() {
        unregisterReceiver(myReceiver);
    }

    private void zaznaczPrzeczytane() {
        SharedPreferences preferences = getSharedPreferences("daneUzytkownika", RozmowaService.MODE_PRIVATE);
        SharedPreferences.Editor zapis = preferences.edit();

        zapis.putInt(login, 0);
        zapis.commit();
    }

    private void initControls() {
        btnWyslij = (Button) findViewById(R.id.btnWyslij);
        btnWyslij.setEnabled(false);
        etWiadomosc = (EditText) findViewById(R.id.etWiadomosc);
        etWiadomosc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0)
                    btnWyslij.setEnabled(false);
                else
                    btnWyslij.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lvRozmowa = (ListView) findViewById(R.id.lvRozmowa);
        initLv();
    }

    public void wyslij(View v) {
        Wiadomosc w = new Wiadomosc();
        Date data = new Date();
        w.setCzas(data);
        w.setOdbiorca(null);
        w.setTresc(etWiadomosc.getText().toString());
        w.setNadawca(Dane.uzytkownik);

        if (isOnline()) {
            WyslijWiadomosc wyslijWiadomosc = new WyslijWiadomosc();
            wyslijWiadomosc.execute(w);
        } else
            Toast.makeText(context, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();

    }

    public void initLv() {

        DBManager manager = new DBManager(context);
        List<WiadomoscDB> wiadomosci = new ArrayList<>();
        wiadomosci = manager.getWiadomosci(idRozmowcy);
        ItemWiadomosc[] items = new ItemWiadomosc[wiadomosci.size()];
        ItemWiadomosc item = new ItemWiadomosc();
        for (int i = 0; i < wiadomosci.size(); i++) {
            item = new ItemWiadomosc();
            item.setData(wiadomosci.get(i).getData());
            item.setWiadomosc(wiadomosci.get(i).getWiadomosc());
            item.setAutor(wiadomosci.get(i).isWychodzaca());
            items[i] = item;
        }

        ListViewAdapterRozmowa adapter = new ListViewAdapterRozmowa(context, R.layout.item_wiadomosc, R.layout.item_wiadomosc_wlasna, items);
        lvRozmowa.setAdapter(adapter);
        lvRozmowa.setSelection(adapter.getCount() - 1);
        zaznaczPrzeczytane();
    }

    private class WyslijWiadomosc extends AsyncTask<Wiadomosc, Void, Integer> {
        ProgressDialog dialog;
        String data;
        String wiadomosc;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Przesyłanie danych do serwera. Proszę czekać...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Wiadomosc... k) {
            data = Dane.sdf.format(k[0].getCzas());
            wiadomosc = k[0].getTresc();
            String url = Dane.URL + "/wiadomosc/wyslijWiadomoscDoUzytkownika/" + idRozmowcy + "/" + Dane.uzytkownik.getId();
            // Create and populate a simple object to be used in the request

// Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            WiadomoscJson json = new WiadomoscJson(k[0]);
            HttpEntity<WiadomoscJson> requestEntity = new HttpEntity<WiadomoscJson>(json, requestHeaders);

// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

// Make the HTTP POST request, marshaling the request to JSON, and the response to a String

            ResponseEntity<String> responseEntity;
            try {
                responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            } catch (HttpClientErrorException e) {
                return e.getStatusCode().value();
            }

            return responseEntity.getStatusCode().value();
        }

        @Override
        protected void onPostExecute(Integer s) {
            dialog.dismiss();
            if (s == 201) {
                Toast.makeText(context, "Wysłano" + s, Toast.LENGTH_SHORT).show();
                DBManager manager = new DBManager(context);
                WiadomoscDB wiadomoscDB = new WiadomoscDB();
                wiadomoscDB.setData(data);
                wiadomoscDB.setNazwaRozmowcy(login);
                wiadomoscDB.setIdRozmowcy(idRozmowcy);
                wiadomoscDB.setWychodzaca(true);
                wiadomoscDB.setWiadomosc(wiadomosc);
                manager.addWiadomosc(wiadomoscDB);

                initLv();
                etWiadomosc.setText("");
            } else {
                Toast.makeText(context, "Bład po stronie serwera:" + s, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_ROZMOWA)) {
                initLv();
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (Dane.wibracje)
                    v.vibrate(100);
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
