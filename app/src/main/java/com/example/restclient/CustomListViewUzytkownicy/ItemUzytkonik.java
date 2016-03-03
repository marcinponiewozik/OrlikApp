package com.example.restclient.CustomListViewUzytkownicy;

/**
 * Created by Marcin on 2015-11-26.
 */
public class ItemUzytkonik {
    String login;
    Long id;
    Boolean zalogowany;
    Boolean znajomy;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Boolean getZalogowany() {
        return zalogowany;
    }

    public void setZalogowany(Boolean zalogowany) {
        this.zalogowany = zalogowany;
    }

    public Boolean getZnajomy() {
        return znajomy;
    }

    public void setZnajomy(Boolean znajomy) {
        this.znajomy = znajomy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
