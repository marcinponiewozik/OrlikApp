package com.example.restclient.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;

import com.example.restclient.Activity.Rozmowa;
import com.example.restclient.Activity.Wiadomosci;
import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.db.DBManager;
import com.example.restclient.db.WiadomoscDB;
import com.example.restclient.jsonObject.WiadomoscJson;
import com.example.restclient.tabele.Wiadomosc;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Marcin on 2016-01-04.
 */
public class RozmowaService extends Service {

    public static boolean jednaOsoba = true;
    public Long idUzytkownik;
    private Timer timer;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            System.out.println("Rozmowa!!!!!");
            if (isOnline()) {
                REST_GET_Wiadomosci getWiadomosci = new REST_GET_Wiadomosci();
                getWiadomosci.execute();
            }
        }
    };


    public void sendMessageToActivity() {
        Intent intent = new Intent(Rozmowa.ACTION_ROZMOWA);
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences p = getSharedPreferences("daneUzytkownika", WiadomosciService.MODE_PRIVATE);
        idUzytkownik = p.getLong("id", 0L);
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 5000);
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

    private class REST_GET_Wiadomosci extends AsyncTask<Void, Void, ArrayList<WiadomoscDB>> {

        @Override
        protected ArrayList<WiadomoscDB> doInBackground(Void... params) {
            String url = Dane.URL + "/wiadomosc/WiadomosciUzytkownika/" + idUzytkownik;

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            WiadomoscJson[] events = null;
            Wiadomosc[] wiadomosci = null;

            try {
                ResponseEntity<WiadomoscJson[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, WiadomoscJson[].class);
                events = responseEntity.getBody();
                wiadomosci = new Wiadomosc[events.length];

                System.out.println("xxxxx"+events.length);
                for (int i = 0; i < events.length; i++) {
                    wiadomosci[i] = new Wiadomosc(events[i]);
                }
            } catch (Exception e) {
                System.out.println("XXXX" + e.getMessage());
            }


            List<Wiadomosc> listaWiadomosci = Arrays.asList(wiadomosci);
            ArrayList<WiadomoscDB> temp = new ArrayList<>();
            WiadomoscDB item = new WiadomoscDB();
            for (int i = 0; i < listaWiadomosci.size(); i++) {
                item = new WiadomoscDB();
                item.setWychodzaca(false);
                item.setData(Dane.simpleDateFormat.format(listaWiadomosci.get(i).getCzas()));
                item.setWiadomosc(listaWiadomosci.get(i).getTresc());
                item.setIdRozmowcy(listaWiadomosci.get(i).getNadawca().getId());
                item.setNazwaRozmowcy(listaWiadomosci.get(i).getNadawca().getLogin());
                temp.add(item);
            }
            return temp;
        }

        @Override
        protected void onPostExecute(ArrayList<WiadomoscDB> wiadomosci) {
            if (wiadomosci.size() > 0) {
                dodajWiadomosciDoBazy(wiadomosci);
                UsunNwiadomosci usunNwiadomosci = new UsunNwiadomosci();
                usunNwiadomosci.execute(wiadomosci.size());
                sendMessageToActivity();
                pokazPowiadomienie(wiadomosci.size(),wiadomosci.get(wiadomosci.size()-1));
            }
        }

        private void dodajWiadomosciDoBazy(ArrayList<WiadomoscDB> wiadomosci) {
            SharedPreferences preferences = getSharedPreferences("daneUzytkownika",RozmowaService.MODE_PRIVATE);
            SharedPreferences.Editor zapis = preferences.edit();
            DBManager manager = new DBManager(getApplicationContext());
            jednaOsoba =true;
            for (int i = 0; i < wiadomosci.size(); i++) {
                manager.addWiadomosc(wiadomosci.get(i));
                if (i > 0 && jednaOsoba) {
                    if (wiadomosci.get(i).getIdRozmowcy() != wiadomosci.get(i - 1).getIdRozmowcy())
                        jednaOsoba = false;
                }
                String key =wiadomosci.get(i).getNazwaRozmowcy();
                int value = preferences.getInt(key,0) + 1;
                zapis.putInt(key, value);
            }
        }

    }

    private class UsunNwiadomosci extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... N) {

            String url = Dane.URL + "/wiadomosc/usunWiadomosciUzytkownika/" + idUzytkownik + "/" + N[0];
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity;
            try {
                responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
            } catch (HttpClientErrorException e) {
                return e.getStatusCode().value();
            }

            return responseEntity.getStatusCode().value();
        }
    }

    private void pokazPowiadomienie(int ilosc, WiadomoscDB wiadomoscDB) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, Wiadomosci.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Notification n;
        String wiadomosc = wiadomoscDB.getWiadomosc();
        if (jednaOsoba) {
            Intent intent1 = new Intent(this, Rozmowa.class);
            intent1.putExtra("login",wiadomoscDB.getNazwaRozmowcy());
            intent1.putExtra("idRozmowcy",wiadomoscDB.getIdRozmowcy());
            PendingIntent pendingIntent1 = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent1, 0);
            if (ilosc > 1) {
                n = new Notification.Builder(this)
                        .setContentTitle("Masz " + ilosc + " wiadomosci od " + wiadomoscDB.getNazwaRozmowcy())
                        .setContentText(wiadomosc)
                        .setSmallIcon(R.drawable.icon)
                        .setContentIntent(pendingIntent1)
                        .setAutoCancel(true).build();
            } else {
                n = new Notification.Builder(this)
                        .setContentTitle("Masz nową wiadomość od " + wiadomoscDB.getNazwaRozmowcy())
                        .setContentText(wiadomosc)
                        .setSmallIcon(R.drawable.icon)
                        .setContentIntent(pendingIntent1)
                        .setAutoCancel(true).build();
            }
        } else {
            n = new Notification.Builder(this)
                    .setContentTitle("Masz "+ilosc+" nowe wiadomosci")
                    .setContentText(wiadomoscDB.getNazwaRozmowcy()+":"+wiadomosc)
                    .setSmallIcon(R.drawable.icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true).build();
        }

        notificationManager.notify(0, n);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
