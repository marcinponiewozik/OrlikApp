package com.example.restclient.tabele;

import com.example.restclient.jsonObject.WiadomoscJson;

import java.util.Date;

/**
 * Created by Marcin on 2015-12-30.
 */
public class Wiadomosc {
    private Long id;
    private Uzytkownik nadawca;
    private Uzytkownik odbiorca;

    private String tresc;
    private Date czas;

    public Wiadomosc() {
    }

    public Wiadomosc(WiadomoscJson json) {
        this.id = json.getId();
        this.nadawca = new Uzytkownik(json.getNadawca());
        this.odbiorca = new Uzytkownik(json.getOdbiorca());
        this.tresc = json.getTresc();
        this.czas = new Date(json.getCzas());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Uzytkownik getNadawca() {
        return nadawca;
    }

    public void setNadawca(Uzytkownik nadawca) {
        this.nadawca = nadawca;
    }

    public Uzytkownik getOdbiorca() {
        return odbiorca;
    }

    public void setOdbiorca(Uzytkownik odbiorca) {
        this.odbiorca = odbiorca;
    }

    public String getTresc() {
        return tresc;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }

    public Date getCzas() {
        return czas;
    }

    public void setCzas(Date czas) {
        this.czas = czas;
    }
}
