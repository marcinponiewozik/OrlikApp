package com.example.restclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.tabele.Uzytkownik;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class Rejestracja extends AppCompatActivity {

    private Context context = this;
    private EditText etLogin, etHaslo, etHaslo2, etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejestracja);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponent();
    }

    private void initComponent() {
        etLogin = (EditText) findViewById(R.id.etLogin);
        etHaslo = (EditText) findViewById(R.id.etHaslo);
        etHaslo2 = (EditText) findViewById(R.id.etHaslo2);
        etEmail = (EditText) findViewById(R.id.etEmail);
    }

    public void gotowe(View v) {
        if (!prawidlowy(etLogin, 5))
            Toast.makeText(context, "Login musi składać się przynajmniej z 5 znaków", Toast.LENGTH_SHORT).show();
        else if (!prawidlowy(etHaslo, 8))
            Toast.makeText(context, "Hasło musi składać się przynajmniej z 8 znaków", Toast.LENGTH_SHORT).show();
        else if (!identyczneHasla())
            Toast.makeText(context, "Hasła różnią się od siebie", Toast.LENGTH_SHORT).show();
        else if (!prawidlowyEmail(etEmail))
            Toast.makeText(context, "Nieprawidłowy adres E-mail", Toast.LENGTH_SHORT).show();
        else {
            if (isOnline()) {
                Uzytkownik uzytkownik = new Uzytkownik();
                uzytkownik.setLogin(etLogin.getText().toString());
                uzytkownik.setPassword(Dane.haszujMD5(etHaslo.getText().toString()));
                uzytkownik.setEmail(etEmail.getText().toString());
                uzytkownik.setAdministrator(false);
                uzytkownik.setZalogowany(false);

                DodajUzytkownika dodaj = new DodajUzytkownika();
                dodaj.execute(uzytkownik);
            } else {
                Toast.makeText(context, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean identyczneHasla() {
        if (etHaslo.getText().toString().equals(etHaslo2.getText().toString()))
            return true;
        else
            return false;
    }

    private boolean prawidlowy(EditText etText, int minLength) {
        if (etText.getText().toString().length() >= minLength) {
            return true;
        } else {
            return false;
        }
    }

    private boolean prawidlowyEmail(EditText email) {
        if (!prawidlowy(email, 6))
            return false;
        else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
        }

    }

    private class DodajUzytkownika extends AsyncTask<Uzytkownik, Void, Integer> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Przesyłanie danych do serwera. Proszę czekać...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Uzytkownik... u) {

            String url = Dane.URL + "/uzytkownik/dodaj";
            // Create and populate a simple object to be used in the request
            Uzytkownik person = new Uzytkownik();
            person = u[0];

// Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<Uzytkownik> requestEntity = new HttpEntity<Uzytkownik>(person, requestHeaders);

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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Odpowiedź");
                alertDialogBuilder.setMessage("Rejestracja przebiegła pomyślnie. Możesz się teraz zalogować");
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialogBuilder.show();
            } else if (s == 409) {
                Toast.makeText(context, "Użytkownik o nazwie " + etLogin.getText().toString() + " już istnieje", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Bład po stronie serwera:" + s, Toast.LENGTH_SHORT).show();
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
