package com.example.restclient.CustomListViewRozmowa;

/**
 * Created by Marcin on 2015-12-29.
 */
public class ItemWiadomosc {
    private String wiadomosc;
    private String data;
    private boolean autor;

    public String getWiadomosc() {
        return wiadomosc;
    }

    public void setWiadomosc(String wiadomosc) {
        this.wiadomosc = wiadomosc;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isAutor() {
        return autor;
    }

    public void setAutor(boolean autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return "ItemWiadomosc{" +
                "wiadomosc='" + wiadomosc + '\'' +
                ", data='" + data + '\'' +
                ", autor=" + autor +
                '}';
    }
}
