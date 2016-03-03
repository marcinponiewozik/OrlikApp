package com.example.restclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.jsonObject.GraJson;
import com.example.restclient.jsonObject.GraUserJSON;
import com.example.restclient.jsonObject.UzytkownikJson;
import com.example.restclient.tabele.Gra;
import com.example.restclient.tabele.Uzytkownik;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Zagraj extends AppCompatActivity {

    private Context context = this;
    private TextView tvData;
    private Gra gra;
    private List<Uzytkownik> listaChetnych, listaNiezdecydowanych, listaNieobecnych;

    private Button btnBede, btnMoze, btnNie;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gra);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
        REST_GET_GRA get_gra = new REST_GET_GRA();
        get_gra.execute();
    }
    private void initComponents() {
        tvData = (TextView) findViewById(R.id.tvData);
        tvData.setText(ustawDate());

        btnBede = (Button) findViewById(R.id.btnBede);
        btnMoze = (Button) findViewById(R.id.btnMoze);
        btnNie = (Button) findViewById(R.id.btnNie);
        btnBede.setEnabled(true);
        btnMoze.setEnabled(true);
        btnNie.setEnabled(true);
        if (Dane.decyzja == 0)
            btnBede.setEnabled(false);
        if (Dane.decyzja == 1)
            btnMoze.setEnabled(false);
        if (Dane.decyzja == 2)
            btnNie.setEnabled(false);

    }
    public void btnBede(View v) {

        if (Dane.decyzja != -1) {
            ZmienDecyzje decyzja = new ZmienDecyzje();
            decyzja.execute(0);
        }else{

            ZapiszDecyzje decyzja = new ZapiszDecyzje();
            decyzja.execute(0);
        }

    }

    public void btnMoze(View v) {
        if (Dane.decyzja != -1) {
            ZmienDecyzje decyzja = new ZmienDecyzje();
            decyzja.execute(1);
        }
        else{
            ZapiszDecyzje decyzja = new ZapiszDecyzje();
            decyzja.execute(1);
        }

    }

    public void btnNie(View v) {
        if (Dane.decyzja != -1) {
            ZmienDecyzje decyzja = new ZmienDecyzje();
            decyzja.execute(2);
        }
        else{
            ZapiszDecyzje decyzja = new ZapiszDecyzje();
            decyzja.execute(2);
        }

    }

    public void komentarze(View v) {
        Intent intent = new Intent(context, KomentarzeGra.class);
        intent.putExtra("id", gra.getId());
        startActivity(intent);
    }

    public String ustawDate() {
        Date data = new Date();
        int dzien;
        int miesiąc;
        int rok;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data);

        dzien = calendar.get(Calendar.DAY_OF_MONTH);
        miesiąc = calendar.get(Calendar.MONTH);
        rok = calendar.get(Calendar.YEAR);
        String wynik;

        String miesiacPol = Dane.getPOLName(miesiąc);
        String dzienPol = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(data);

        wynik = Dane.getPOLNameDay(dzienPol) + " " + dzien + " " + miesiacPol + " " + rok;
        return wynik;

    }

    public void listaChetnych(View v){
        Intent intent = new Intent(context,ListaGraczy.class);
        intent.putExtra("title","Lista chętnych");
        intent.putExtra("case",0);
        startActivity(intent);
    }
    public void listaNiezdecydowanych(View v){
        Intent intent = new Intent(context,ListaGraczy.class);
        intent.putExtra("title","Lista niezdecydowanych");
        intent.putExtra("case",1);
        startActivity(intent);
    }
    public void listaNieobecnych(View v){
        Intent intent = new Intent(context,ListaGraczy.class);
        intent.putExtra("title","Lista nieobecnych");
        intent.putExtra("case",2);
        startActivity(intent);
    }

    private class REST_GET_GRA extends AsyncTask<Void, Void, Long> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Pobieranie danych z serwera. Czekaj...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Long doInBackground(Void... params) {
            String url = Dane.URL + "/entitys.gra/getgra";

// Set the Accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

// Add the Gson message converter
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

// Make the HTTP GET request, marshaling the response from JSON to an array of Events
            GraJson events = null;
            try {
                ResponseEntity<GraJson> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, GraJson.class);
                events = responseEntity.getBody();
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }


            return events.getId();
        }


        @Override
        protected void onPostExecute(Long id) {
            gra = new Gra();
            gra.setId(id);
            dialog.dismiss();
            REST_GET_ListaChetnych chetni = new REST_GET_ListaChetnych();
            chetni.execute();
        }
    }

    private class REST_GET_ListaChetnych extends AsyncTask<Void, Void, Uzytkownik[]> {

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Pobieranie danych z serwera. Czekaj...lista");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Uzytkownik[] doInBackground(Void... wybor) {
            String url = Dane.URL + "/grauser/listachetnych";

// Set the Accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

// Add the Gson message converter
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

// Make the HTTP GET request, marshaling the response from JSON to an array of Events
            UzytkownikJson[] lista = null;
            Uzytkownik[] users = null;
            try {
                ResponseEntity<UzytkownikJson[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, UzytkownikJson[].class);
                System.out.println(responseEntity.getBody().toString());
                lista = responseEntity.getBody();
                users = new Uzytkownik[lista.length];

                for (int i = 0; i < lista.length; i++) {
                    users[i] = new Uzytkownik(lista[i]);
                }
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }


            return users;
        }


        @Override
        protected void onPostExecute(Uzytkownik[] lista) {
            listaChetnych = Arrays.asList(lista);
            sprawdzListe(lista, 0);
            btnBede.setText(String.valueOf(listaChetnych.size()));
            REST_GET_ListaNiezdecydowanych nieobecni = new REST_GET_ListaNiezdecydowanych();
            nieobecni.execute();
        }
    }

    private class REST_GET_ListaNiezdecydowanych extends AsyncTask<Void, Void, Uzytkownik[]> {

        @Override
        protected Uzytkownik[] doInBackground(Void... wybor) {
            String url = Dane.URL + "/grauser/listaniezdecydowanych";

// Set the Accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

// Add the Gson message converter
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

// Make the HTTP GET request, marshaling the response from JSON to an array of Events
            UzytkownikJson[] lista = null;
            Uzytkownik[] users = null;
            try {
                ResponseEntity<UzytkownikJson[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, UzytkownikJson[].class);
                System.out.println(responseEntity.getBody().toString());
                lista = responseEntity.getBody();
                users = new Uzytkownik[lista.length];

                for (int i = 0; i < lista.length; i++) {
                    users[i] = new Uzytkownik(lista[i]);
                }
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }


            return users;
        }


        @Override
        protected void onPostExecute(Uzytkownik[] lista) {
            listaNiezdecydowanych = Arrays.asList(lista);
            sprawdzListe(lista, 1);
            btnMoze.setText(String.valueOf(listaNiezdecydowanych.size()));
            REST_GET_ListaNieobecni nieobecni = new REST_GET_ListaNieobecni();
            nieobecni.execute();
        }
    }

    private class REST_GET_ListaNieobecni extends AsyncTask<Void, Void, Uzytkownik[]> {

        @Override
        protected Uzytkownik[] doInBackground(Void... wybor) {
            String url = Dane.URL + "/grauser/listanieobecni";

// Set the Accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

// Add the Gson message converter
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

// Make the HTTP GET request, marshaling the response from JSON to an array of Events
            UzytkownikJson[] lista = null;
            Uzytkownik[] users = null;
            try {
                ResponseEntity<UzytkownikJson[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, UzytkownikJson[].class);
                System.out.println(responseEntity.getBody().toString());
                lista = responseEntity.getBody();
                users = new Uzytkownik[lista.length];

                for (int i = 0; i < lista.length; i++) {
                    users[i] = new Uzytkownik(lista[i]);
                }
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }


            return users;
        }


        @Override
        protected void onPostExecute(Uzytkownik[] lista) {
            listaNieobecnych = Arrays.asList(lista);
            sprawdzListe(lista, 2);
            btnNie.setText(String.valueOf(listaNieobecnych.size()));
            dialog.dismiss();

            initComponents();
        }
    }

    private class ZmienDecyzje extends AsyncTask<Integer, Void, Integer> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Przesyłanie danych do serwera. Proszę czekać...zapisz");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... wybor) {

            String url = Dane.URL + "/grauser/uzytkownik/"+Dane.uzytkownik.getId()+"/zapiszdecyzje/" + wybor[0];

            UzytkownikJson person = new UzytkownikJson(Dane.uzytkownik);

            System.out.println(person);
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
            dialog.dismiss();
            if (s != 200) {
                Toast.makeText(context, "Błąd po stronie serwera. Spróbuj ponownie", Toast.LENGTH_SHORT).show();
            } else {
                REST_GET_ListaChetnych chetni = new REST_GET_ListaChetnych();
                chetni.execute();
            }
        }
    }

    private class ZapiszDecyzje extends AsyncTask<Integer, Void, Integer> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Przesyłanie danych do serwera. Proszę czekać...zapisz");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... wybor) {

            String url = Dane.URL + "/grauser/zapiszgra/"+Dane.uzytkownik.getId();

            GraUserJSON graUserJSON = new GraUserJSON();
            graUserJSON.setDecyzja(wybor[0]);

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<GraUserJSON> requestEntity = new HttpEntity<GraUserJSON>(graUserJSON, requestHeaders);

            RestTemplate restTemplate = new RestTemplate();

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
            if (s != 201) {
                Toast.makeText(context, "Błąd po stronie serwera. Spróbuj ponownie", Toast.LENGTH_SHORT).show();
            } else {
                REST_GET_ListaChetnych chetni = new REST_GET_ListaChetnych();
                chetni.execute();
            }
        }
    }
    private class UsunDecyzje extends AsyncTask<Integer, Void, Integer> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Przesyłanie danych do serwera. Proszę czekać...usun");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... wybor) {

            String url = Dane.URL + "/grauser/wypiszuzytkownika/" + Dane.uzytkownik.getId();
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity;
            try {
                responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
            } catch (HttpClientErrorException e) {
                return e.getStatusCode().value();
            }

            return responseEntity.getStatusCode().value();
        }

        @Override
        protected void onPostExecute(Integer s) {
            dialog.dismiss();
            if (s != 200) {
                Toast.makeText(context, "Błąd po stronie serwera. Spróbuj ponownie", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void sprawdzListe(Uzytkownik[] lista, int wybor) {
        for (Uzytkownik u : lista)
            if (u.getId() == Dane.uzytkownik.getId()) {
                Dane.decyzja = wybor;
                break;
            }
    }
}
