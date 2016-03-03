package com.example.restclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.Services.WiadomosciService;
import com.example.restclient.jsonObject.UzytkownikJson;
import com.example.restclient.tabele.Uzytkownik;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeActivity extends AppCompatActivity {
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sprawdzCzyZapamietany();



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Dane.zalogowany) {
            setContentView(R.layout.activity_main_zalogowany);
            if (isOnline()) {
                PUT_ONLINE online = new PUT_ONLINE();
                online.execute();
                Intent intent = new Intent(context,WiadomosciService.class);

                stopService(intent);
                intent.putExtra("interval",21211);
                startService(intent);
            }
        } else
            setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        if (isOnline()&&Dane.zalogowany) {
            PUT_OFFLINE offline = new PUT_OFFLINE();
            offline.execute();
        }
        super.onDestroy();

    }

    public void logowanie(View v) {
        Intent intent = new Intent(context, Logowanie.class);
        startActivity(intent);
    }

    public void news(View v) {
        Intent intent = new Intent(context, Aktualnosci.class);
        startActivity(intent);
    }

    public void ustawienia(View v) {
        Intent intent = new Intent(context, Ustawienia.class);
        startActivity(intent);
    }
    public void wiadomosci(View v) {
        Intent intent = new Intent(context, Wiadomosci.class);
        startActivity(intent);
    }


    private void sprawdzCzyZapamietany() {
        SharedPreferences p = getSharedPreferences("daneUzytkownika", HomeActivity.MODE_PRIVATE);
        if (!p.getString("login", "").equals("")) {
            zalogujUzytkownika();
            Dane.zalogowany = true;
        }
    }

    private void zalogujUzytkownika() {
        SharedPreferences p = getSharedPreferences("daneUzytkownika", Logowanie.MODE_PRIVATE);

        SimpleDateFormat sdf = new SimpleDateFormat("");
        Uzytkownik user = new Uzytkownik();
        user.setId(p.getLong("id", 0L));
        user.setLogin(p.getString("login", ""));
        user.setPassword(p.getString("password", ""));
        user.setEmail(p.getString("email", ""));
        try {
            user.setDataZalozenia(Dane.simpleDateFormat.parse(p.getString("dataZalozenia", "")));
            user.setOstatnieLogowanie(Dane.simpleDateFormat.parse(p.getString("ostatnieLogowanie", "")));
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(context,"Logowanie nie powiodło się.",Toast.LENGTH_SHORT).show();
        }
        user.setAdministrator(p.getBoolean("administrator", false));
        user.setZalogowany(p.getBoolean("zalogowany", false));

        Dane.uzytkownik = new Uzytkownik();
        Dane.uzytkownik = user;
        Dane.powiadomienia = p.getBoolean("powiadomienia", false);
        Dane.wibracje = p.getBoolean("wibracje",true);
    }

    public void gra(View v) {
        if(isOnline()) {
            Intent intent = new Intent(context, Zagraj.class);
            startActivity(intent);
        }else
            Toast.makeText(context,"Brak połączenia z internetem",Toast.LENGTH_SHORT).show();
    }

    private class PUT_ONLINE extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... wybor) {

            String url = Dane.URL + "/uzytkownik/" + Dane.uzytkownik.getId();
            UzytkownikJson person = new UzytkownikJson(Dane.uzytkownik);
            person.setZalogowany(true);

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<UzytkownikJson> requestEntity = new HttpEntity<UzytkownikJson>(person, requestHeaders);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity;
            try {
                responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
            } catch (HttpClientErrorException e) {
                return e.getStatusCode().value();
            }

            return responseEntity.getStatusCode().value();
        }

        @Override
        protected void onPostExecute(Integer s) {
            if (s == 201) {
                Dane.uzytkownik.setZalogowany(true);
            } else {
                Dane.uzytkownik.setZalogowany(false);
                Toast.makeText(context, "Błąd po stronie serwera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class PUT_OFFLINE extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... wybor) {

            String url = Dane.URL + "/uzytkownik/" + Dane.uzytkownik.getId();
            UzytkownikJson person = new UzytkownikJson(Dane.uzytkownik);
            person.setZalogowany(false);

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<UzytkownikJson> requestEntity = new HttpEntity<UzytkownikJson>(person, requestHeaders);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity;
            try {
                responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
            } catch (HttpClientErrorException e) {
                return e.getStatusCode().value();
            }

            return responseEntity.getStatusCode().value();
        }

        @Override
        protected void onPostExecute(Integer s) {
            if (s == 201) {
                Dane.uzytkownik.setZalogowany(true);
            } else {
                Dane.uzytkownik.setZalogowany(false);
                Toast.makeText(context, "Błąd po stronie serwera", Toast.LENGTH_SHORT).show();
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
