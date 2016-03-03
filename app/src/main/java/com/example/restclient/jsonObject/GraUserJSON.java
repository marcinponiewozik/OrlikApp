package com.example.restclient.jsonObject;

import com.example.restclient.tabele.GraUser;

/**
 * Created by Marcin on 2016-01-17.
 */
public class GraUserJSON {
    private Long id;
    private UzytkownikJson uzytkownik;
    private GraJson gra;
    private int decyzja;

    public GraUserJSON() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UzytkownikJson getUzytkownik() {
        return uzytkownik;
    }

    public void setUzytkownik(UzytkownikJson uzytkownik) {
        this.uzytkownik = uzytkownik;
    }

    public GraJson getGra() {
        return gra;
    }

    public void setGra(GraJson gra) {
        this.gra = gra;
    }

    public int getDecyzja() {
        return decyzja;
    }

    public void setDecyzja(int decyzja) {
        this.decyzja = decyzja;
    }
}
