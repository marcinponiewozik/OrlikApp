package com.example.restclient.tabele;

import com.example.restclient.jsonObject.NewsJson;

import java.util.Date;

/**
 * Created by Marcin on 2015-11-26.
 */

public class News {
    private Long id;

    private String tytul;
    private String tresc;
    private Uzytkownik autor;
    private Date data;

    public News() {
    }

    public News(NewsJson json) {
        this.id = json.getId();
        this.tytul = json.getTytul();
        this.tresc = json.getTresc();
        this.autor = json.getAutor();
        this.data = new Date(json.getData());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }

    public String getTresc() {
        return tresc;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }

    public Uzytkownik getAutor() {
        return autor;
    }

    public void setAutor(Uzytkownik autor) {
        this.autor = autor;
    }


    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
