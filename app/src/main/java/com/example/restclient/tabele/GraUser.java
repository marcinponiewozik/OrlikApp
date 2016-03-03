package com.example.restclient.tabele;

/**
 * Created by Marcin on 2016-01-17.
 */
public class GraUser {
    private Long id;
    private Uzytkownik uzytkownik;
    private Gra gra;
    private int decyzja;

    public GraUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Uzytkownik getUzytkownik() {
        return uzytkownik;
    }

    public void setUzytkownik(Uzytkownik uzytkownik) {
        this.uzytkownik = uzytkownik;
    }

    public Gra getGra() {
        return gra;
    }

    public void setGra(Gra gra) {
        this.gra = gra;
    }

    public int getDecyzja() {
        return decyzja;
    }

    public void setDecyzja(int decyzja) {
        this.decyzja = decyzja;
    }
}
