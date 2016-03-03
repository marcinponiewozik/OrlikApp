package com.example.restclient.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Marcin on 2015-12-29.
 */
@DatabaseTable
public class Znajomy {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private Long idPerson;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String firstname) {
        this.name = firstname;
    }

    public Long getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(Long idPerson) {
        this.idPerson = idPerson;
    }

    @Override
    public String toString() {
        return "Znajomy{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", idPerson=" + idPerson +
                '}';
    }
}
