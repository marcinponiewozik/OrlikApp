package com.example.restclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ZmianaHasla extends AppCompatActivity {

    private EditText etStareHaslo, etNoweHaslo, etPowtorz;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zmiana_hasla);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
    }

    private void initComponents() {
        etStareHaslo = (EditText) findViewById(R.id.etStareHaslo);
        etNoweHaslo = (EditText) findViewById(R.id.etNoweHaslo);
        etPowtorz = (EditText) findViewById(R.id.etPowtorz);
    }

    public void btnGotowe(View v) {
        String stare = etStareHaslo.getText().toString();
        String nowe = etNoweHaslo.getText().toString();
        String powtorz = etPowtorz.getText().toString();
        if (Dane.haszujMD5(stare).equals(Dane.uzytkownik.getPassword())) {
            if (nowe.length() >= 8 && powtorz.length() >= 8) {
                if (nowe.equals(powtorz)) {
                    PUT_Zmaiana zmianaHasla = new PUT_Zmaiana();
                    zmianaHasla.execute(Dane.haszujMD5(nowe));
                } else
                    Toast.makeText(context, "Halo różnią się od siebie", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, "Halo musi miec min 8 znaków", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(context, "Stare haslo jest nieprawidlowe", Toast.LENGTH_SHORT).show();

    }

    private class PUT_Zmaiana extends AsyncTask<String, Void, Integer> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Przesyłanie danych do serwera. Proszę czekać...zapisz");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... wybor) {

            String url = Dane.URL + "/uzytkownik/" + Dane.uzytkownik.getId();
            // Create and populate a simple object to be used in the request
            UzytkownikJson person = new UzytkownikJson(Dane.uzytkownik);
            person.setPassword(wybor[0]);
// Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<UzytkownikJson> requestEntity = new HttpEntity<UzytkownikJson>(person, requestHeaders);

// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

// Make the HTTP POST request, marshaling the request to JSON, and the response to a String

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
            dialog.dismiss();
            if (s == 201) {
                wyczyscDaneAplikacji();
                Toast.makeText(context, "Zmiana została zapisana. Zaloguj się ponownie", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,Logowanie.class);
                finish();
                startActivity(intent);
            } else {
                Toast.makeText(context, "Błąd po stronie serwera. Spróbuj ponownie", Toast.LENGTH_SHORT).show();
            }
        }
        private void wyczyscDaneAplikacji() {
            SharedPreferences preferences;
            preferences = getSharedPreferences("daneUzytkownika", Ustawienia.MODE_PRIVATE);
            SharedPreferences.Editor edytor = preferences.edit();

            edytor.clear();
            edytor.commit();
            Dane.zalogowany=false;
        }
    }
}
