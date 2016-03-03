package com.example.restclient.CustomListViewOdbiorcza;

/**
 * Created by Marcin on 2015-12-28.
 */
public class ItemRozmowa {
    String uzytkownik;
    String wiadomosc;
    Long id;
    int liczbaNieprzeczytanych;

    public ItemRozmowa() {
    }

    public ItemRozmowa(String uzytkownik, String wiadomosc, Long id) {
        this.uzytkownik = uzytkownik;
        this.wiadomosc = wiadomosc;
        this.id = id;
    }

    public String getUzytkownik() {
        return uzytkownik;
    }

    public void setUzytkownik(String uzytkownik) {
        this.uzytkownik = uzytkownik;
    }


    public String getWiadomosc() {
        return wiadomosc;
    }

    public void setWiadomosc(String wiadomosc) {
        this.wiadomosc = wiadomosc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLiczbaNieprzeczytanych() {
        return liczbaNieprzeczytanych;
    }

    public void setLiczbaNieprzeczytanych(int liczbaNieprzeczytanych) {
        this.liczbaNieprzeczytanych = liczbaNieprzeczytanych;
    }

    @Override
    public String toString() {
        return "ItemRozmowa{" +
                "uzytkownik='" + uzytkownik + '\'' +
                ", liczabWiadomosci='" + wiadomosc + '\'' +
                ", id=" + id +
                '}';
    }
}
