package com.example.restclient.jsonObject;

import com.example.restclient.tabele.Uzytkownik;

import java.util.Date;

/**
 * Created by Marcin on 2016-01-17.
 */
public class UzytkownikJson {
    private Long id;
    private String login;
    private String password;
    private String email;
    private boolean administrator;
    private Long dataZalozenia;
    private Long ostatnieLogowanie;
    private boolean zalogowany;

    public UzytkownikJson(Uzytkownik uzytkownik) {
        this.id = uzytkownik.getId();
        this.login = uzytkownik.getLogin();
        this.password = uzytkownik.getPassword();
        this.email = uzytkownik.getEmail();
        this.administrator = uzytkownik.isAdministrator();
        this.dataZalozenia = uzytkownik.getDataZalozenia().getTime();
        this.ostatnieLogowanie = uzytkownik.getOstatnieLogowanie().getTime();
        this.zalogowany = uzytkownik.isZalogowany();

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

    public Long getDataZalozenia() {
        return dataZalozenia;
    }

    public void setDataZalozenia(Long dataZalozenia) {
        this.dataZalozenia = dataZalozenia;
    }

    public Long getOstatnieLogowanie() {
        return ostatnieLogowanie;
    }

    public void setOstatnieLogowanie(Long ostatnieLogowanie) {
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
