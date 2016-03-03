package com.example.restclient.jsonObject;

import com.example.restclient.tabele.Uzytkownik;
import com.example.restclient.tabele.Wiadomosc;

import java.util.Date;

/**
 * Created by Marcin on 2016-01-17.
 */
public class WiadomoscJson {
    private Long id;
    private UzytkownikJson nadawca;
    private UzytkownikJson odbiorca;

    private String tresc;
    private Long czas;

    public WiadomoscJson(Wiadomosc wiadomosc) {
        this.id = wiadomosc.getId();
        this.nadawca = new UzytkownikJson(wiadomosc.getNadawca());
        if (wiadomosc.getOdbiorca() != null)
            this.odbiorca = new UzytkownikJson(wiadomosc.getOdbiorca());
        this.tresc = wiadomosc.getTresc();
        this.czas = wiadomosc.getCzas().getTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UzytkownikJson getNadawca() {
        return nadawca;
    }

    public void setNadawca(UzytkownikJson nadawca) {
        this.nadawca = nadawca;
    }

    public UzytkownikJson getOdbiorca() {
        return odbiorca;
    }

    public void setOdbiorca(UzytkownikJson odbiorca) {
        this.odbiorca = odbiorca;
    }

    public String getTresc() {
        return tresc;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }


    public Long getCzas() {
        return czas;
    }

    public void setCzas(Long czas) {
        this.czas = czas;
    }
}
