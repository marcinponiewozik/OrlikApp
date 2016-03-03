package com.example.restclient.tabele;

import com.example.restclient.jsonObject.UzytkownikJson;

import java.util.Date;

/**
 * Created by Marcin on 2015-11-26.
 */
public class Uzytkownik {
    private Long id;
    private String login;
    private String password;
    private String email;
    private boolean administrator;
    private Date dataZalozenia;
    private Date ostatnieLogowanie;
    private boolean zalogowany;

    public Uzytkownik() {
    }

    public Uzytkownik(UzytkownikJson json) {
        this.id = json.getId();
        this.login = json.getLogin();
        this.password = json.getPassword();
        this.email = json.getEmail();
        this.administrator = json.isAdministrator();
        this.dataZalozenia = new Date(json.getDataZalozenia());
        this.ostatnieLogowanie = new Date(json.getOstatnieLogowanie());
        this.zalogowany = json.isZalogowany();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

//    public String getDataZalozenia() {
//        return dataZalozenia;
//    }
//
//    public void setDataZalozenia(String dataZalozenia) {
//        this.dataZalozenia = dataZalozenia;
//    }
//
//    public String getOstatnieLogowanie() {
//        return ostatnieLogowanie;
//    }
//
//    public void setOstatnieLogowanie(String ostatnieLogowanie) {
//        this.ostatnieLogowanie = ostatnieLogowanie;
//    }

    public Date getDataZalozenia() {
        return dataZalozenia;
    }

    public void setDataZalozenia(Date dataZalozenia) {
        this.dataZalozenia = dataZalozenia;
    }

    public Date getOstatnieLogowanie() {
        return ostatnieLogowanie;
    }

    public void setOstatnieLogowanie(Date ostatnieLogowanie) {
        this.ostatnieLogowanie = ostatnieLogowanie;
    }

    public boolean isZalogowany() {
        return zalogowany;
    }

    public void setZalogowany(boolean zalogowany) {
        this.zalogowany = zalogowany;
    }

    @Override
    public String toString() {
        return "Uzytkownik{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", administrator=" + administrator +
                ", dataZalozenia=" + dataZalozenia +
                ", ostatnieLogowanie=" + ostatnieLogowanie +
                ", zalogowany=" + zalogowany +
                '}';
    }
}