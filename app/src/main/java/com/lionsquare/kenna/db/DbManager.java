package com.lionsquare.kenna.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by EDGAR ARANA on 19/07/2017.
 */

public class DbManager {

    private DbHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DbManager(Context c) {
        context = c;
    }

    public DbManager open() throws SQLException {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public void insertUser(
            String name,
            String email,
            String profile_pick,
            String cover,
            int type_account,
            String token_social,
            String token) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DbHelper.NAME, name);//1
        contentValue.put(DbHelper.NAME, email);//2
        contentValue.put(DbHelper.NAME, profile_pick);//3
        contentValue.put(DbHelper.NAME, cover);//4
        contentValue.put(DbHelper.NAME, type_account);//5
        contentValue.put(DbHelper.NAME, token_social);//6
        contentValue.put(DbHelper.NAME, token);//7
        database.insert(DbHelper.TABLE_USER, null, contentValue);
    }


}
