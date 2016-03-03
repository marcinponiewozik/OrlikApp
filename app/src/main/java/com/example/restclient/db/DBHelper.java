package com.example.restclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 2015-12-10.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {
    private Context context;
    public static final String DB_NAME="test";
    private static final Integer DB_VERSION = 1;

    private Dao<Znajomy, Integer> personDao =null;
    private Dao<WiadomoscDB, Integer> wiadomoscDao =null;
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, Znajomy.class);
            TableUtils.createTable(connectionSource, WiadomoscDB.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            List<String> allSql = new ArrayList<String>();
            switch(oldVersion)
            {
                case 1:
                    //allSql.add("alter table AdData add column `new_col` VARCHAR");
                    //allSql.add("alter table AdData add column `new_col2` VARCHAR");
            }
            for (String sql : allSql) {
                db.execSQL(sql);
            }
        } catch (Exception e) {
            Log.e(DBHelper.class.getName(), "exception during onUpgrade", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Znajomy, Integer> getPersonDao(){
        if(personDao ==null){
            try{
                personDao = getDao(Znajomy.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return personDao;
    }
    public Dao<WiadomoscDB, Integer> getWiadomoscDao(){
        if(wiadomoscDao ==null){
            try{
                wiadomoscDao = getDao(WiadomoscDB.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return wiadomoscDao;
    }
}
