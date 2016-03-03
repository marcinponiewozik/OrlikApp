package com.example.restclient.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.restclient.CustomListViewKomentarze.ItemKomentarz;
import com.example.restclient.CustomListViewKomentarze.ListViewAdapterK;
import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.Services.KomentarzService;
import com.example.restclient.jsonObject.KomentarzJson;
import com.example.restclient.tabele.Komentarz;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KomentarzeGra extends AppCompatActivity {

    private Context context = this;
    private ListView lvKomentarze;

    public static Long idGra;
    public static final String ACTION_KOMENTARZ="com.example.restclient.Activity.Komentarz";
    public static final String ETYKIETA="lista";
    private MyReceiver myReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komentarze_gra);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idGra = getIntent().getLongExtra("id",0L);
        initComponents();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pobierzKomentarze(idGra);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initService();
        initReceiver();
    }

    @Override
    protected void onStop() {
        finishService();
        finishReceiver();
        super.onStop();
    }

    private void initComponents() {
        lvKomentarze = (ListView) findViewById(R.id.lvKomentarz);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.input_komentrz, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.etKomentarz);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Komentarz komentarz = new Komentarz();
                                        komentarz.setKomentarz(userInput.getText().toString());
                                        komentarz.setUzytkownik(Dane.uzytkownik);
                                        DodajKomentarz dodajKomentarz = new DodajKomentarz();
                                        dodajKomentarz.execute(komentarz);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }
    private void initReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(ACTION_KOMENTARZ);
        registerReceiver(myReceiver, filter);
    }


    private void initService() {
        Intent intent = new Intent(this, KomentarzService.class);
        startService(intent);
    }


    private void finishService() {
        Intent intent = new Intent(this, KomentarzService.class);
        stopService(intent);
    }

    private void finishReceiver() {
        unregisterReceiver(myReceiver);
    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_KOMENTARZ)) {
                wypelnijListe(context, intent);
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if(Dane.wibracje)
                    v.vibrate(100);
            }
        }

        private void wypelnijListe(Context context, Intent intent) {
            ArrayList<ItemKomentarz> temp = new ArrayList<>();
            temp = (ArrayList<ItemKomentarz>) intent.getSerializableExtra(ETYKIETA);
            ItemKomentarz[] komentarz = new ItemKomentarz[temp.size()];
            komentarz = temp.toArray(komentarz);
            ListViewAdapterK adapter = new ListViewAdapterK(context, R.layout.item_komentarz,komentarz,KomentarzeGra.this);
            lvKomentarze.setAdapter(adapter);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void pobierzKomentarze(Long idGra) {
        REST_GET_Komentarze pobierz = new REST_GET_Komentarze();
        pobierz.execute(idGra);
    }


    private class REST_GET_Komentarze extends AsyncTask<Long, Void, Komentarz[]> {

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Pobieranie danych z serwera. Czekaj...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Komentarz[] doInBackground(Long... params) {
            String url = Dane.URL+"/komentarz/findAll/GRA/"+params[0];

// Set the Accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

// Add the Gson message converter
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

// Make the HTTP GET request, marshaling the response from JSON to an array of Events
            KomentarzJson[] events = null;
            Komentarz[] list = null;
            try {
                ResponseEntity<KomentarzJson[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, KomentarzJson[].class);
                events = responseEntity.getBody();
                list = new Komentarz[events.length];

                for (int i = 0; i < events.length; i++) {
                    list[i]= new Komentarz(events[i]);
                }
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }


            return list;
        }

        @Override
        protected void onPostExecute(Komentarz[] komentarze) {
            System.out.println("XXXXX" + komentarze.length);
            dialog.dismiss();

            List<Komentarz> listaKomentarzy = new ArrayList<>(Arrays.asList(komentarze));
            wypelnijListe(listaKomentarzy);
        }

        private void wypelnijListe(List<Komentarz> lista) {
            List<ItemKomentarz> temp = new ArrayList<>();
            ItemKomentarz item = new ItemKomentarz();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            for (int i = 0; i < lista.size() ; i++) {
                item = new ItemKomentarz();
                item.setAutor(lista.get(i).getUzytkownik().getLogin());
                item.setData(sdf.format(lista.get(i).getData()));
                item.setTresc(lista.get(i).getKomentarz());

                temp.add(item);
            }
            ItemKomentarz[] items = new ItemKomentarz[temp.size()];
            items = temp.toArray(items);
            ListViewAdapterK adapter = new ListViewAdapterK(context, R.layout.item_komentarz, items,KomentarzeGra.this);

            lvKomentarze.setAdapter(adapter);

        }
    }


    private class DodajKomentarz extends AsyncTask<Komentarz, Void, Integer> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Przesyłanie danych do serwera. Proszę czekać...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Komentarz... k) {

            String url = Dane.URL+"/komentarz/dodajKomentarz/GRA/"+ idGra;
            // Create and populate a simple object to be used in the request

// Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            KomentarzJson komentarzJson = new KomentarzJson(k[0]);
            HttpEntity<KomentarzJson> requestEntity = new HttpEntity<KomentarzJson>(komentarzJson, requestHeaders);

// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

// Make the HTTP POST request, marshaling the request to JSON, and the response to a String

            ResponseEntity<String> responseEntity;
            try{
                responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            }catch (HttpClientErrorException e){
                return e.getStatusCode().value();
            }

            return responseEntity.getStatusCode().value();
        }

        @Override
        protected void onPostExecute(Integer s) {
            dialog.dismiss();
            if(s==201){
                REST_GET_Komentarze get_komentarze = new REST_GET_Komentarze();
                get_komentarze.execute(getIntent().getLongExtra("id",0L));
            }else{
                Toast.makeText(context, "Bład po stronie serwera:" + s, Toast.LENGTH_SHORT).show();
            }
        }
    }



}
