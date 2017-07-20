package com.lionsquare.kenna.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.lionsquare.kenna.model.User;

import java.util.ArrayList;

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
            String token,
            Double lat,
            Double lng
    ) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DbHelper.NAME, name);//1
        contentValue.put(DbHelper.EMAIL, email);//2
        contentValue.put(DbHelper.PROFILE_PICK, profile_pick);//3
        contentValue.put(DbHelper.COVER, cover);//4
        contentValue.put(DbHelper.TYPE_ACCOUNT, type_account);//5
        contentValue.put(DbHelper.TOKEN_SOCIAL, token_social);//6
        contentValue.put(DbHelper.TOKEN, token);//7
        contentValue.put(DbHelper.LAT, lat);//8
        contentValue.put(DbHelper.LNG, lng);//9
        database.insert(DbHelper.TABLE_USER, null, contentValue);
    }

    public User getUser() {
        String selectQuery = "SELECT  * FROM " + DbHelper.TABLE_USER;
        SQLiteDatabase db = dbHelper.getContextlist();
        //Log.e("consulta", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        User user = null;
        if (cursor.moveToFirst()) {
            do {
                user = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getDouble(8),
                        cursor.getDouble(9));
            } while (cursor.moveToNext());
        }


        return user;
    }


    public void clearUser() {
        database.execSQL("delete from " + DbHelper.TABLE_USER);
    }


}
