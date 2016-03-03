package com.example.restclient.jsonObject;

import com.example.restclient.tabele.Gra;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Marcin on 2016-01-17.
 */
public class GraJson {
    private Long id;
    private String data;
    private String dodatkoweInformacje;

    public GraJson() {
    }

    public GraJson(Gra gra) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.id = gra.getId();
        this.data = simpleDateFormat.format(gra.getData());
        this.dodatkoweInformacje = gra.getDodatkoweInformacje();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDodatkoweInformacje() {
        return dodatkoweInformacje;
    }

    public void setDodatkoweInformacje(String dodatkoweInformacje) {
        this.dodatkoweInformacje = dodatkoweInformacje;
    }
}
