package com.example.restclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restclient.Dane;
import com.example.restclient.R;
import com.example.restclient.db.DBHelper;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Ustawienia extends AppCompatActivity {

    private Context context = this;
    private Switch switchPowiadomienia, switchWibracje;
    private TextView tvWiecej, tvLogin, tvEmail, tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustawienia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initComponents();
    }


    public void initComponents() {
        switchPowiadomienia = (Switch) findViewById(R.id.switchPowiadomienia);
        tvWiecej = (TextView) findViewById(R.id.tvWiecej);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvLogin.setText("Login:" + Dane.uzytkownik.getLogin());
        tvEmail.setText("Email:" + Dane.uzytkownik.getEmail());
        switchPowiadomienia.setChecked(Dane.powiadomienia);
        tvWiecej.setEnabled(Dane.powiadomienia);
        switchPowiadomienia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                zapisz();
                if (!isChecked)
                    System.out.println("Wyłącz powiadomienia");
                tvWiecej.setEnabled(isChecked);
                Dane.powiadomienia = isChecked;
            }
        });
        switchWibracje = (Switch) findViewById(R.id.swWibracje);
        switchWibracje.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                zapisz();
                Dane.wibracje = isChecked;
            }
        });

        tvData = (TextView) findViewById(R.id.tvDataRejestracji);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date data = Dane.uzytkownik.getDataZalozenia();
        String dataString = sdf.format(data);
        tvData.setText("Rejestracja:" + dataString);
//        tvData.setText("Rejestracja:" + Dane.uzytkownik.getDataZalozenia());
    }

    private void zapisz() {
        SharedPreferences preferences = getSharedPreferences("daneUzytkownika", Ustawienia.MODE_PRIVATE);
        SharedPreferences.Editor zapis = preferences.edit();
        zapis.putBoolean("powiadomienia", switchPowiadomienia.isChecked());
        zapis.putBoolean("wibracje", switchWibracje.isChecked());

        zapis.commit();
    }

    public void wyloguj(View v) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Informacja");
        alertDialog.setIcon(R.drawable.info);
        alertDialog.setMessage("Po wylogowaniu skasowane zostaną wszystkie dane aplikacji. \nKontynuować?");
        alertDialog.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wyczyscDaneAplikacji();

                Intent intent = new Intent(context, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void usunKonto(View v) {
        if (isOnline()) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Usuwanie konta");
            alertDialog.setIcon(R.drawable.info);
            alertDialog.setMessage("Wybierz TAK aby potwierdzić");
            alertDialog.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UsunUzytkownika usun = new UsunUzytkownika();
                    usun.execute(Dane.uzytkownik.getId());
                    dialog.cancel();
                }
            });
            alertDialog.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        } else
            Toast.makeText(context, "Musisz być online", Toast.LENGTH_SHORT).show();

    }

    public void zmienHaslo(View v) {
        if (isOnline()) {
            Intent intent = new Intent(context, ZmianaHasla.class);
            startActivity(intent);
        } else
            Toast.makeText(context, "Musisz być online", Toast.LENGTH_SHORT).show();

    }

    public void zmienEmial(View v) {
        if (isOnline()) {
            Intent intent = new Intent(context, ZmianaEmail.class);
            startActivity(intent);
        } else
            Toast.makeText(context, "Musisz być online", Toast.LENGTH_SHORT).show();

    }

    private class UsunUzytkownika extends AsyncTask<Long, Void, Integer> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Usuwanie uzytkownika " + Dane.uzytkownik.getLogin());
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Long... id) {

            String url = Dane.URL + "/uzytkownik/" + id[0];
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
            wyczyscDaneAplikacji();
            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent);

        }
    }

    private void wyczyscDaneAplikacji() {
        context.deleteDatabase(DBHelper.DB_NAME);
        SharedPreferences preferences;
        preferences = getSharedPreferences("daneUzytkownika", Ustawienia.MODE_PRIVATE);
        SharedPreferences.Editor edytor = preferences.edit();

        edytor.clear();
        edytor.commit();
        Dane.zalogowany = false;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
