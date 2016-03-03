package com.example.restclient.CustomListViewKomentarze;

import java.io.Serializable;

/**
 * Created by Marcin on 2015-11-27.
 */
public class ItemKomentarz implements Serializable{
    String autor;
    String tresc;
    String data;

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTresc() {
        return tresc;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
