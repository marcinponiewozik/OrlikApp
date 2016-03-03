package com.example.restclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.restclient.CustomListViewKomentarze.ItemKomentarz;
import com.example.restclient.CustomListViewKomentarze.ListViewAdapterK;
import com.example.restclient.CustomListViewUzytkownicy.ItemUzytkonik;
import com.example.restclient.CustomListViewUzytkownicy.ListViewAdapterU;
import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.db.DBManager;
import com.example.restclient.db.Znajomy;
import com.example.restclient.jsonObject.UzytkownikJson;
import com.example.restclient.tabele.Komentarz;
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

public class ListaGraczy extends AppCompatActivity {

    private ListView lvListaGraczy;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_graczy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String tytul = getIntent().getStringExtra("title");
        setTitle(tytul);

        Integer wybor = getIntent().getIntExtra("case", 0);
        initList(wybor);
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

    private void initList(int wybor) {
        lvListaGraczy = (ListView) findViewById(R.id.lvListaGraczy);

        REST_GET_Gracze gracze = new REST_GET_Gracze();
        gracze.execute(wybor);
    }


    private class REST_GET_Gracze extends AsyncTask<Integer, Void, Uzytkownik[]> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Pobieranie danych z serwera. Czekaj...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Uzytkownik[] doInBackground(Integer... params) {
            String url = Dane.URL;
            if (params[0] == 0)
                url += "/grauser/listachetnych";
            else if (params[0] == 1)
                url += "/grauser/listaniezdecydowanych";
            else
                url += "/grauser/listanieobecni";

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            UzytkownikJson[] events = null;
            Uzytkownik[] list = null;
            try {
                ResponseEntity<UzytkownikJson[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, UzytkownikJson[].class);
                events = responseEntity.getBody();

                list = new Uzytkownik[events.length];
                for (int i = 0; i < events.length; i++) {
                    list[i] = new Uzytkownik(events[i]);
                }
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }


            return list;
        }

        @Override
        protected void onPostExecute(Uzytkownik[] uzytkownik) {
            dialog.dismiss();

            List<Uzytkownik> listaGraczy = new ArrayList<>(Arrays.asList(uzytkownik));
            wypelnijListe(listaGraczy);
        }

        private void wypelnijListe(List<Uzytkownik> lista) {
            List<ItemUzytkonik> temp = new ArrayList<>();
            ItemUzytkonik item = new ItemUzytkonik();

            DBManager manager = new DBManager(context);
            List<Znajomy> znajomi = new ArrayList<>();
            znajomi = manager.getAllWishLists();
            boolean znajomy = false;
            for (int i = 0; i < lista.size(); i++) {
                znajomy = false;
                for (int j = 0; j < znajomi.size(); j++) {
                    if (lista.get(i).getId() == znajomi.get(j).getId()) {
                        znajomy = true;
                        break;
                    }
                }

                item = new ItemUzytkonik();
                item.setId(lista.get(i).getId());
                item.setLogin(lista.get(i).getLogin());
                item.setZalogowany(lista.get(i).isZalogowany());
                item.setZnajomy(znajomy);
                if (!item.getLogin().equals(Dane.uzytkownik.getLogin()))
                    temp.add(item);
            }
            ItemUzytkonik[] items = new ItemUzytkonik[temp.size()];
            items = temp.toArray(items);
            ListViewAdapterU adapter = new ListViewAdapterU(context, R.layout.item_uzytkownik, items, ((ListaGraczy) context).getLayoutInflater());

            lvListaGraczy.setAdapter(adapter);

        }
    }
}
