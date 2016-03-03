package com.example.restclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.restclient.CustomListViewNews.Item;
import com.example.restclient.CustomListViewNews.ListViewAdapter;
import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.jsonObject.NewsJson;
import com.example.restclient.tabele.News;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Aktualnosci extends AppCompatActivity {

    private Context context = this;
    private ListView lvNews;
    private List<News> listaNews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktualnosci);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvNews = (ListView)findViewById(R.id.lvNews);
        initLvNews();
    }

    private void initLvNews() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            REST_GET_NEWS restGET = new REST_GET_NEWS();
            restGET.execute();
        } else
            Toast.makeText(context, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();

        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, Artykul.class);
                intent.putExtra("tytul", listaNews.get(listaNews.size() - 1 - position).getTytul());
                intent.putExtra("tresc", listaNews.get(listaNews.size() - 1 - position).getTresc());
                intent.putExtra("id", listaNews.get(listaNews.size() - 1 - position).getId());
                startActivity(intent);
            }
        });
    }


    private class REST_GET_NEWS extends AsyncTask<Void, Void, News[]> {

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Pobieranie danych z serwera. Czekaj...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected News[] doInBackground(Void... params) {
            String url = Dane.URL+"/news";

// Set the Accept header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

// Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

// Add the Gson message converter
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

// Make the HTTP GET request, marshaling the response from JSON to an array of Events
            NewsJson[] events = null;
            News[] news = null;
            try {
                ResponseEntity<NewsJson[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, NewsJson[].class);
                events = responseEntity.getBody();

                news = new News[events.length];
                for (int i = 0; i < events.length; i++) {
                    news[i] = new News(events[i]);
                }
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }


            return news;
        }

        @Override
        protected void onPostExecute(News[] newses) {
            System.out.println("XXXXX"+newses.length);
            dialog.dismiss();
            listaNews = new ArrayList<>(Arrays.asList(newses));
            wypelnijListe();
        }
    }

    private void wypelnijListe() {
        List<Item> temp = new ArrayList<>();
        Item item = new Item();
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < listaNews.size() ; i++) {
            item.setIdNews(String.valueOf(listaNews.get(i).getId()));
            item.setTytul(listaNews.get(i).getTytul());
            c.setTime(listaNews.get(i).getData());
            item.setSzcegoly(listaNews.get(i).getAutor().getLogin() + "|" + c.get(Calendar.YEAR)+"/"+c.get(Calendar.MONTH)+"/"+c.get(Calendar.DAY_OF_MONTH));

            temp.add(item);
        }
        Item[] items = new Item[temp.size()];
        items = temp.toArray(items);
        ListViewAdapter adapter = new ListViewAdapter(context, R.layout.item_lv_news, items);

        lvNews.setAdapter(adapter);

    }

}
