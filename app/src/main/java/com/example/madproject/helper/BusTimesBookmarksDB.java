package com.example.madproject.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BusTimesBookmarksDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookmark.db";
    private static final int SCHEMA_VERSION = 1;

    public BusTimesBookmarksDB(Context context) {
        super(context, DATABASE_NAME,null,SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bookmarks( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bus_stop_name TEXT," +
                "bus_stop_code TEXT," +
                "bus_service TEXT);");
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //read from each row use cursor c
    // use get functions (below) to get each column
    public Cursor getAll() {
        return (getReadableDatabase().rawQuery(
                "SELECT _id, bus_stop_name, bus_stop_code, " +
                        "bus_service FROM bookmarks ORDER BY bus_stop_name", null));
    }


    public Cursor getAllByBusStopName(String busStopName){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM bookmarks WHERE bus_stop_name = ?",
                new String[]{busStopName}
        );
    }

    public Cursor getAllByBusStopCode(String busStopCode){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM bookmarks WHERE bus_stop_code = ?",
                new String[]{busStopCode}
        );
    }

    public Cursor getAllByBusService(String busService){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM bookmarks WHERE bus_service = ?",
                new String[]{busService}
        );
    }

    public void insert(String bus_stop_name, String bus_stop_code,
                       String bus_service) {
        ContentValues cv = new ContentValues();

        cv.put("bus_stop_name", bus_stop_name);
        cv.put("bus_stop_code", bus_stop_code);
        cv.put("bus_service", bus_service);

        getWritableDatabase().insert("bookmarks", null, cv);

}



    public void deleteBookmarkById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("bookmarks", "_id=?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void deleteBookmarkByName(String busStopName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("bookmarks", "bus_stop_name=?", new String[]{String.valueOf(busStopName)});
        db.close();
    }

    public void deleteBookmarkByCode(String busStopCode) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("bookmarks", "bus_stop_code=?", new String[]{String.valueOf(busStopCode)});
        db.close();
    }

    public void deleteBookmarkByService(String busStopService) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("bookmarks", "bus_stop_code=?", new String[]{String.valueOf(busStopService)});
        db.close();
    }



    public String getBusStopName(Cursor c) {
        return c.getString(c.getColumnIndexOrThrow("bus_stop_name"));
    }

    public String getBusStopCode(Cursor c) {
        return c.getString(c.getColumnIndexOrThrow("bus_stop_code"));
    }

    public String getBusService(Cursor c) {
        return c.getString(c.getColumnIndexOrThrow("bus_service"));
    }
}

/* USE CASE EXAMPLE:

Bookmark dbHelper = new Bookmark(context);
Cursor cursor = dbHelper.getAllByBusStopName("Downtown Station");

if (cursor.moveToFirst()) {
    String busStopName = dbHelper.getBusStopName(cursor);
    String busStopCode = dbHelper.getBusStopCode(cursor);
    String busService = dbHelper.getBusService(cursor);

    System.out.println("Bus Stop Name: " + busStopName);
    System.out.println("Bus Stop Code: " + busStopCode);
    System.out.println("Bus Service: " + busService);

 */