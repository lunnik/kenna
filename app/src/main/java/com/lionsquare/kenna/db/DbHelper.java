package com.lionsquare.kenna.db;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by EDGAR ARANA on 19/07/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    // TODO: 19/07/2017 Nobre de las tablas
    public static final String TABLE_USER = "User";

    // TODO: 19/07/2017 columnas de la tabla

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PROFILE_PICK = "profile_pick";
    public static final String COVER = "cover";
    public static final String TYPE_ACCOUNT = "type_account";
    public static final String TOKEN_SOCIAL = "token_social";
    public static final String TOKEN = "token";
    public static final String LAT = "lat";
    public static final String LNG = "lng";


    // TODO: 19/07/2017 datos de la base de datos
    static final String DB_NAME = "USER.DB";
    static final int DB_VERSION = 1;

    // TODO: 19/07/2017  sentencias para crear la tablas
    private static final String CREATE_TABLE_USER = "create table "
            + TABLE_USER + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT, "
            + EMAIL + " TEXT,"
            + PROFILE_PICK + " TEXT,"
            + COVER + " TEXT ,"
            + TYPE_ACCOUNT + " INTEGER ,"
            + TOKEN_SOCIAL + " TEXT, "
            + TOKEN + " TEXT, "
            + LAT + " REAL, "
            + LNG + " REAL );";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CLIENTES);
        onCreate(db);
    }

    public SQLiteDatabase getContextlist() {
        SQLiteDatabase context = this.getWritableDatabase();
        return context;
    }

}
