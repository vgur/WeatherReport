package com.example.a16719756.weatherreport;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;

import android.database.Cursor;
import android.database.SQLException;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class WeatherProvider extends ContentProvider {
    static final String PROVIDER_NAME = "com.example.WeatherReport.WeatherProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/weather";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    public static final String DATE = "date";
    public static final String TEMPR = "tempr";
    public static final String DESCR = "desctiption";

    static final int WEATHER = 1;
    // Если понадобится погода на дату
    static final int WEATHER_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "weather", WEATHER);
        uriMatcher.addURI(PROVIDER_NAME, "weather_id/#", WEATHER_ID);
    }

    private static HashMap<String, String> WEATHER_PROJECTION_MAP;

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "Weather";
    static final String WEATHER_TABLE_NAME = "weather";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + WEATHER_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " date TEXT NOT NULL, " +
                    " tempr TEXT NOT NULL, " +
                    " desctiption TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  WEATHER_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(	WEATHER_TABLE_NAME, "", values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(WEATHER_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case WEATHER:
                qb.setProjectionMap(WEATHER_PROJECTION_MAP);
                break;
            default:
        }

        if (sortOrder == null || sortOrder == ""){
            sortOrder = DATE;
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

}
