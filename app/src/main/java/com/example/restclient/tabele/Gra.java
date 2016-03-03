package com.example.restclient.tabele;

import com.example.restclient.jsonObject.GraJson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Marcin on 2015-11-26.
 */
public class Gra {
    private Long id;
    private Date data;
    private String dodatkoweInformacje;

    public Gra() {
    }

    public Gra(GraJson json) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.id = json.getId();
        try {
            this.data = sdf.parse(json.getData());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.dodatkoweInformacje = json.getDodatkoweInformacje();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getDodatkoweInformacje() {
        return dodatkoweInformacje;
    }

    public void setDodatkoweInformacje(String dodatkoweInformacje) {
        this.dodatkoweInformacje = dodatkoweInformacje;
    }
}
