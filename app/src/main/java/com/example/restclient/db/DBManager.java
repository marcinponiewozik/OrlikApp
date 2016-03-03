package com.example.restclient.db;

import android.content.Context;

import com.example.restclient.Dane;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Marcin on 2015-12-10.
 */
public class DBManager {

    private static DBManager instance;

    static public void init(Context context) {
        if (null == instance) {
            instance = new DBManager(context);
        }
    }

    static public DBManager getInstance() {
        return instance;
    }

    public DBManager(Context context) {
        helper = new DBHelper(context);
    }

    private DBHelper helper;

    private DBHelper getHelper() {
        return helper;
    }

    public List<Znajomy> getAllWishLists() {
        List<Znajomy> wishLists = null;
        try {
            wishLists = getHelper().getPersonDao().queryForAll();

//            wishLists = getHelper().getPersonDao().queryBuilder().selectColumns("id","login").where().eq("id",1L).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishLists;
    }

    public void addPerson(Znajomy p) {
        try {
            getHelper().getPersonDao().create(p);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePerson(Znajomy z){
        try{
            getHelper().getPersonDao().delete(z);
        }catch (SQLException e){

        }
    }
    public void addWiadomosc(WiadomoscDB w) {
        try {
            getHelper().getWiadomoscDao().create(w);
        } catch (SQLException e) {
            e.printStackTrace();
            ;
        }
    }

    public List<WiadomoscDB> getOdbiorcza() {
        List<WiadomoscDB> odbiorcza = null;
        List<WiadomoscDB> temp = new ArrayList<>();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try {
            odbiorcza = getHelper().getWiadomoscDao().queryForAll();
            WiadomoscDB w = new WiadomoscDB();
            boolean flaga = true;
            for (int i = 0; i < odbiorcza.size(); i++) {
                w = odbiorcza.get(i);
                flaga = true;
                for (int j = 0; j < temp.size(); j++) {
                    if (w.getIdRozmowcy() == temp.get(j).getIdRozmowcy()) {
                        System.out.println(w.getData());
                        Date a = Dane.sdf.parse(w.getData());
                        Date b = Dane.sdf.parse(temp.get(j).getData());
                        if (a.after(b)||a.equals(b)) {
                            temp.set(j, w);
                        }
                        flaga = false;
                    }
                }
                if(flaga)
                    temp.add(w);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public List<WiadomoscDB> getWiadomosci(Long idRozmowcy) {
        List<WiadomoscDB> wiadomosci = null;
        try {
            wiadomosci = getHelper().getWiadomoscDao().queryBuilder().where().eq("idRozmowcy",idRozmowcy).query();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wiadomosci;
    }
}
