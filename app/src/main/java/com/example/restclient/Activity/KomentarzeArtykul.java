package com.example.restclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class KomentarzeArtykul extends AppCompatActivity {

    private Context context = this;
    private ListView lvKomentarze;

    private Long idArtykulu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komentarze_artykul);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idArtykulu = getIntent().getLongExtra("id",0L);
        initComponents();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pobierzKomentarze(idArtykulu);
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
                                    public void onClick(DialogInterface dialog,int id) {
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
                                    public void onClick(DialogInterface dialog,int id) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void pobierzKomentarze(Long idArtykulu) {
        REST_GET_Komentarze pobierz = new REST_GET_Komentarze();
        pobierz.execute(idArtykulu);
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
            String url = Dane.URL+"/komentarz/findAll/NEWS/"+params[0];

// Set the Accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

// Add the Gson message converter
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

// Make the HTTP GET request, marshaling the response from JSON to an array of Events
            Komentarz[] events = null;
            try {
                ResponseEntity<Komentarz[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Komentarz[].class);
                events = responseEntity.getBody();
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }


            return events;
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
            ListViewAdapterK adapter = new ListViewAdapterK(context, R.layout.item_komentarz, items,KomentarzeArtykul.this);

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

            String url = Dane.URL+"/komentarz/dodajKomentarz/NEWS/"+idArtykulu;
            // Create and populate a simple object to be used in the request

// Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<Komentarz> requestEntity = new HttpEntity<Komentarz>(k[0], requestHeaders);

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
                Toast.makeText(context,"Bład po stronie serwera:"+s,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
