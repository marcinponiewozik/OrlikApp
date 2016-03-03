package com.example.restclient.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.restclient.R;

public class Artykul extends AppCompatActivity {

    private Context context = this;
    private TextView tvTytul, tvTresc;
    private String tytul, tresc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artykul);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tytul = getIntent().getStringExtra("tytul");
        tresc = getIntent().getStringExtra("tresc");

        initComponents();
    }

    private void initComponents(){
        tvTresc = (TextView) findViewById(R.id.tvTresc);
        tvTytul = (TextView) findViewById(R.id.tvTytul);

        tvTytul.setText(tytul);
        tvTresc.setText(Html.fromHtml(tresc));
    }
    public void komentarze(View v) {
        Intent intent = new Intent(context, KomentarzeArtykul.class);
        intent.putExtra("id", getIntent().getLongExtra("id", 0L));
        startActivity(intent);
    }

}
