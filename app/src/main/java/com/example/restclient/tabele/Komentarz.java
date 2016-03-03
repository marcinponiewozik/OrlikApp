package com.example.restclient.tabele;

import com.example.restclient.jsonObject.KomentarzJson;

import java.util.Date;

/**
 * Created by Marcin on 2015-11-26.
 */
public class Komentarz {
    private Long id;

    private String komentarz;
    private Uzytkownik uzytkownik;
    private Date data;
    private Dzial dzial;
    private Long idDzialu;

    public Komentarz() {
    }

    public Komentarz(KomentarzJson json) {
        this.id = json.getId();
        this.komentarz = json.getKomentarz();
        this.uzytkownik = new Uzytkownik(json.getUzytkownik());
        this.data = new Date(json.getData());
        this.dzial = json.getDzial();
        this.idDzialu = json.getIdDzialu();
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

    public Uzytkownik getUzytkownik() {
        return uzytkownik;
    }

    public void setUzytkownik(Uzytkownik uzytkownik) {
        this.uzytkownik = uzytkownik;
    }


    public Date getData() {
        return data;
    }

    public void setData(Date data) {
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
