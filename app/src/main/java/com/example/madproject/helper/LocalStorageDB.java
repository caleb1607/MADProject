package com.example.madproject.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LocalStorageDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "localstorage.db";
    private static final int SCHEMA_VERSION = 1;

    public LocalStorageDB(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE localstorage( " +
                "ls_key TEXT PRIMARY KEY, " +
                "ls_value TEXT);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS localstorage");
        onCreate(db);
    }

    public void insertOrUpdate(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ls_key", key);
        values.put("ls_value", value);

        int rowsUpdated = db.update("localstorage", values, "ls_key = ?", new String[]{key});
        if (rowsUpdated == 0) {
            db.insert("localstorage", null, values);
        }
        db.close();
    }

    public String getValue(String key) {
        try (SQLiteDatabase db = getReadableDatabase(); Cursor cursor = db.query("localstorage",
                new String[]{"ls_value"},
                "ls_key = ?", new String[]{key},
                null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("ls_value");

                if (columnIndex >= 0) { // Ensure column exists
                    return cursor.getString(columnIndex);
                }
            }
        }
        return null;
    }
    public void deleteKey(String key) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("localstorage", "ls_key = ?", new String[]{key});
        db.close();
    }

    public boolean keyExists(String key) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("localstorage",
                new String[]{"ls_key"},
                "ls_key = ?", new String[]{key},
                null, null, null);

        boolean exists = (cursor != null && cursor.moveToFirst());
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }
}