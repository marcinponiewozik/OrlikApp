package com.example.restclient.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Marcin on 2015-12-29.
 */
@DatabaseTable
public class WiadomoscDB {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String wiadomosc;
    @DatabaseField
    private Long idRozmowcy;
    @DatabaseField
    private String data;
    @DatabaseField
    private String nazwaRozmowcy;
    @DatabaseField
    private boolean wychodzaca;


    public WiadomoscDB() {
    }

    public WiadomoscDB(int id, String wiadomosc, Long idRozmowcy, String data) {
        this.id = id;
        this.wiadomosc = wiadomosc;
        this.idRozmowcy = idRozmowcy;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWiadomosc() {
        return wiadomosc;
    }

    public void setWiadomosc(String wiadomosc) {
        this.wiadomosc = wiadomosc;
    }

    public Long getIdRozmowcy() {
        return idRozmowcy;
    }

    public void setIdRozmowcy(Long idRozmowcy) {
        this.idRozmowcy = idRozmowcy;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isWychodzaca() {
        return wychodzaca;
    }

    public void setWychodzaca(boolean wychodzaca) {
        this.wychodzaca = wychodzaca;
    }

    public String getNazwaRozmowcy() {
        return nazwaRozmowcy;
    }

    public void setNazwaRozmowcy(String nazwaRozmowcy) {
        this.nazwaRozmowcy = nazwaRozmowcy;
    }

    @Override
    public String toString() {
        return "Wiadomosc{" +
                "id=" + id +
                ", wiadomosc='" + wiadomosc + '\'' +
                ", idRozmowcy=" + idRozmowcy +
                ", data='" + data + '\'' +
                ", nazwaRozmowcy='" + nazwaRozmowcy + '\'' +
                ", wychodzaca=" + wychodzaca +
                '}';
    }
}
