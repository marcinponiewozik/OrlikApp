package com.example.restclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.jsonObject.UzytkownikJson;
import com.example.restclient.tabele.Uzytkownik;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class Logowanie extends AppCompatActivity {

    private Context context = this;
    private EditText etLogin, etHaslo;
    private CheckBox zapamietaj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        etLogin.setText("");
        etHaslo.setText("");
    }

    private void initComponents() {
        etLogin = (EditText) findViewById(R.id.etLogin);
        etHaslo = (EditText) findViewById(R.id.etHaslo);
        zapamietaj = (CheckBox) findViewById(R.id.cBZapamietaj);
    }

    public void zaloguj(View v) {
        if(isOnline()){
            GetUzytkownik getUzytkownik = new GetUzytkownik();
            String login = etLogin.getText().toString();
            String pass = Dane.haszujMD5(etHaslo.getText().toString());
            getUzytkownik.execute(login,pass);
        }else{
            Toast.makeText(context,"Brak dostępu do internetu",Toast.LENGTH_SHORT).show();
        }


    }

    public void rejestracja(View v){
        Intent intent = new Intent(context,Rejestracja.class);
        startActivity(intent);
    }

    private class GetUzytkownik extends AsyncTask<String, Void, Uzytkownik> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Logowanie...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Uzytkownik doInBackground(String... params) {
            String url = Dane.URL+"/uzytkownik/get/"+params[0]+"/"+params[1];
// Set the Accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);


// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
// Make the HTTP GET request, marshaling the response from JSON to an array of Events
            Uzytkownik person = null;
            System.out.println("Cos tam 0");
            try {
                System.out.println("Cos tam 1");
                ResponseEntity<UzytkownikJson> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, UzytkownikJson.class);
                System.out.println("Cos tam 2");
                System.out.println(responseEntity.getBody().toString());

                person = new Uzytkownik(responseEntity.getBody());
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }

            return person;
        }

        @Override
        protected void onPostExecute(Uzytkownik person) {
            System.out.println(person);
            dialog.dismiss();
            if (person == null)
                Toast.makeText(context, "Niepoprawny login lub hasło", Toast.LENGTH_SHORT).show();
            else {
                if(zapamietaj.isChecked())
                    zapiszUzytkownika(person);

                Dane.uzytkownik=person;
                Dane.zalogowany=true;
                finish();
            }
        }
    }

    private void zapiszUzytkownika(Uzytkownik u){
        SharedPreferences preferences;
        preferences = getSharedPreferences("daneUzytkownika",Logowanie.MODE_PRIVATE);
        SharedPreferences.Editor zapis = preferences.edit();
        zapis.putLong("id", u.getId());
        zapis.putString("login", u.getLogin());
        zapis.putString("password", u.getPassword());
        zapis.putString("email", u.getEmail());
        zapis.putString("dataZalozenia", Dane.simpleDateFormat.format(u.getDataZalozenia()));
        zapis.putString("ostatnieLogowanie", Dane.simpleDateFormat.format(u.getOstatnieLogowanie()));
        zapis.putBoolean("zalogowany",u.isZalogowany());
        zapis.putBoolean("administrator",u.isAdministrator());

        zapis.commit();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
