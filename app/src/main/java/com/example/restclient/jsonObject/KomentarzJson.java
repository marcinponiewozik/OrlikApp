package com.example.restclient.jsonObject;

import com.example.restclient.tabele.Dzial;
import com.example.restclient.tabele.Komentarz;
import com.example.restclient.tabele.Uzytkownik;

import java.util.Date;

/**
 * Created by Marcin on 2016-01-17.
 */
public class KomentarzJson {
    private Long id;

    private String komentarz;
    private UzytkownikJson uzytkownik;
    private Long data;
    private Dzial dzial;
    private Long idDzialu;

    public KomentarzJson() {
    }

    public KomentarzJson(Komentarz komentarz) {
        this.id = komentarz.getId();
        this.komentarz = komentarz.getKomentarz();
        this.uzytkownik = new UzytkownikJson(komentarz.getUzytkownik());
        if (komentarz.getData() != null)
            this.data = komentarz.getData().getTime();
        this.dzial = komentarz.getDzial();
        this.idDzialu = komentarz.getIdDzialu();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKomentarz() {
        return komentarz;
    }

    public void setKomentarz(String komentarz) {
        this.komentarz = komentarz;
    }

    public UzytkownikJson getUzytkownik() {
        return uzytkownik;
    }

    public void setUzytkownik(UzytkownikJson uzytkownik) {
        this.uzytkownik = uzytkownik;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public Dzial getDzial() {
        return dzial;
    }

    public void setDzial(Dzial dzial) {
        this.dzial = dzial;
    }

    public Long getIdDzialu() {
        return idDzialu;
    }

    public void setIdDzialu(Long idDzialu) {
        this.idDzialu = idDzialu;
    }
}
