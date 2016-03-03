package com.example.restclient.Services;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.restclient.Activity.KomentarzeGra;
import com.example.restclient.Activity.Zagraj;
import com.example.restclient.CustomListViewKomentarze.ItemKomentarz;
import com.example.restclient.CustomListViewKomentarze.ListViewAdapterK;
import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.jsonObject.KomentarzJson;
import com.example.restclient.tabele.Komentarz;

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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Marcin on 2015-12-09.
 */
public class KomentarzService extends Service {

    ArrayList<ItemKomentarz> komentarze;
    ArrayList<ItemKomentarz> komentarzeTemp;
    int count = 0;

    private Timer timer;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (isOnline()) {
                REST_GET_Komentarze getKomentarze = new REST_GET_Komentarze();
                Long idGra = KomentarzeGra.idGra;
                if (idGra != null) {
                    getKomentarze.execute(KomentarzeGra.idGra);
                    sendMessageToActivity();
                }
            }
        }
    };

    private void sendMessageToActivity() {

        if (komentarze.size() != komentarzeTemp.size()) {
            komentarze = komentarzeTemp;
            if (count > 1) {
                Intent intent = new Intent(KomentarzeGra.ACTION_KOMENTARZ);
                intent.putExtra(KomentarzeGra.ETYKIETA, komentarze);
                sendBroadcast(intent);
            }
        }
        if (count < 2)
            count++;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        komentarzeTemp = new ArrayList<>();
        komentarze = new ArrayList<>();
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 4000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
        timer.purge();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class REST_GET_Komentarze extends AsyncTask<Long, Void, ArrayList<ItemKomentarz>> {


        @Override
        protected ArrayList<ItemKomentarz> doInBackground(Long... params) {
            String url = Dane.URL + "/komentarz/findAll/GRA/" + params[0];

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            KomentarzJson[] events = null;
            Komentarz[] list = null;
            try {
                ResponseEntity<KomentarzJson[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, KomentarzJson[].class);
                events = responseEntity.getBody();
                list = new Komentarz[events.length];

                for (int i = 0; i < events.length; i++) {
                    list[i] = new Komentarz(events[i]);
                }
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }


            List<Komentarz> listaKomentarzy = new ArrayList<>(Arrays.asList(list));
            ArrayList<ItemKomentarz> temp = new ArrayList<>();
            ItemKomentarz item = new ItemKomentarz();
            for (int i = 0; i < listaKomentarzy.size(); i++) {
                item = new ItemKomentarz();
                item.setAutor(listaKomentarzy.get(i).getUzytkownik().getLogin());
                item.setData(Dane.sdf.format(listaKomentarzy.get(i).getData()));
                item.setTresc(listaKomentarzy.get(i).getKomentarz());

                if (!item.getAutor().equals(Dane.uzytkownik.getLogin()))
                    temp.add(item);
            }
            return temp;
        }

        @Override
        protected void onPostExecute(ArrayList<ItemKomentarz> komentarze) {
            komentarzeTemp = komentarze;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
