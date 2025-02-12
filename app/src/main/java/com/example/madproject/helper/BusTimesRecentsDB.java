package com.example.madproject.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BusTimesRecentsDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bustimesrecent.db";
    private static final int SCHEMA_VERSION = 1;

    public BusTimesRecentsDB(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bustimes_recent( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT," +
                "value TEXT," +
                "header TEXT," +
                "subheader1 TEXT," +
                "subheader2 TEXT);"
        );
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor getAll() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM bustimes_recent ORDER BY header", null);
    }

    public Cursor getAllByType(String type) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM bustimes_recent WHERE type = ?",
                new String[]{type}
        );
    }

    public Cursor getAllByValue(String value) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM bustimes_recent WHERE value = ?",
                new String[]{value}
        );
    }

    public void insert(String type, String value, String header, String subheader1, String subheader2) {
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("value", value);
        cv.put("header", header);
        cv.put("subheader1", subheader1);
        cv.put("subheader2", subheader2);

        getWritableDatabase().insert("bustimes_recent", null, cv);
    }

    public void deleteRecordById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("bustimes_recent", "_id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteRecordByHeader(String header) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("bustimes_recent", "header=?", new String[]{header});
        db.close();
    }

    public void deleteAllRecords() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("bustimes_recent", null, null);
        db.close();
    }

    public String getType(Cursor c) {
        return c.getString(c.getColumnIndexOrThrow("type"));
    }

    public String getValue(Cursor c) {
        return c.getString(c.getColumnIndexOrThrow("value"));
    }

    public String getHeader(Cursor c) {
        return c.getString(c.getColumnIndexOrThrow("header"));
    }

    public String getSubheader1(Cursor c) {
        return c.getString(c.getColumnIndexOrThrow("subheader1"));
    }

    public String getSubheader2(Cursor c) {
        return c.getString(c.getColumnIndexOrThrow("subheader2"));
    }

    public boolean doesTypeValueExist(String type, String value) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM bustimes_recent WHERE type = ? AND value = ?",
                new String[]{type, value}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public void deleteRecordByTypeValue(String type, String value) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("bustimes_recent", "type=? AND value=?", new String[]{type, value});
        db.close();
    }

    public void sliceRecordsByNumber(int number) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT _id FROM bustimes_recent ORDER BY _id DESC LIMIT 1 OFFSET ?",
                new String[]{String.valueOf(number)}  // Pass 'number' as a parameter
        );

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            db.delete("bustimes_recent", "_id=?", new String[]{String.valueOf(id)});
        }

        cursor.close();
        db.close();
    }


    public List<List<String>> getAllRecords() {
        List<List<String>> bustimes_recent = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bustimes_recent", null);

        if (cursor.moveToFirst()) {
            do {
                List<String> row = new ArrayList<>();
                row.add(cursor.getString(0)); // ID
                row.add(cursor.getString(1)); // Type
                row.add(cursor.getString(2)); // Value
                row.add(cursor.getString(3)); // Header
                row.add(cursor.getString(4)); // Subheader1
                row.add(cursor.getString(5)); // Subheader2
                bustimes_recent.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Collections.reverse(bustimes_recent);
        return bustimes_recent;
    }
}
