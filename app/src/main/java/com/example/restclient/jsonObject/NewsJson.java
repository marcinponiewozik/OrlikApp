package com.example.restclient.jsonObject;

import com.example.restclient.tabele.News;
import com.example.restclient.tabele.Uzytkownik;

import java.util.Date;

/**
 * Created by Marcin on 2016-01-17.
 */
public class NewsJson {
    private Long id;

    private String tytul;
    private String tresc;
    private Uzytkownik autor;
    private Long data;

    public NewsJson() {
    }

    public NewsJson(News news) {
        this.id = news.getId();
        this.tytul = news.getTytul();
        this.tresc = news.getTresc();
        this.autor = news.getAutor();
        this.data = news.getData().getTime();
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

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }
}
