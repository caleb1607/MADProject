package com.example.madproject.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper that manages a local SQLite bookmarks table and mirrors changes to Firestore.
 * Now uses Firestore array fields instead of subcollections.
 */
public class BusTimesBookmarksDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bookmark.db";
    private static final int SCHEMA_VERSION = 1;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String userDocId;

    public BusTimesBookmarksDB(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
        SharedPreferences prefs = context.getSharedPreferences("UserIDpref", Context.MODE_PRIVATE);
        userDocId = prefs.getString("USER_DOC_ID", null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bookmarks( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bus_stop_name TEXT," +
                "bus_stop_code TEXT," +
                "bus_service TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void syncBookmarksFromFirestore(Runnable onComplete) {
        if (userDocId == null) return;

        firestore.collection("users")
                .document(userDocId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.contains("savedarrivaltimes")) {
                        List<Map<String, Object>> bookmarks =
                                (List<Map<String, Object>>) document.get("savedarrivaltimes");

                        SQLiteDatabase db = getWritableDatabase();
                        db.beginTransaction();
                        try {
                            db.delete("bookmarks", null, null); // clear local
                            for (Map<String, Object> bookmark : bookmarks) {
                                String name = (String) bookmark.get("busStopName");
                                String code = (String) bookmark.get("busStopCode");
                                String service = (String) bookmark.get("busService");

                                ContentValues cv = new ContentValues();
                                cv.put("bus_stop_name", name);
                                cv.put("bus_stop_code", code);
                                cv.put("bus_service", service);
                                db.insert("bookmarks", null, cv);
                            }
                            db.setTransactionSuccessful();
                        } finally {
                            db.endTransaction();
                            db.close();
                        }
                        Log.d("Sync", "Synced Firestore to local DB");
                    }
                    if (onComplete != null) onComplete.run();
                })
                .addOnFailureListener(e -> {
                    Log.e("Sync", "Failed to sync from Firestore", e);
                    if (onComplete != null) onComplete.run();
                });
    }


    /**
     * Insert a new bookmark locally and append to the Firestore array field savedarrivaltimes.
     */
    public void insert(String busStopName, String busStopCode, String busService) {
        // 1) local SQLite
        ContentValues cv = new ContentValues();
        cv.put("bus_stop_name", busStopName);
        cv.put("bus_stop_code", busStopCode);
        cv.put("bus_service", busService);
        getWritableDatabase().insert("bookmarks", null, cv);

        // 2) Firestore: update the array field on the user document
        if (userDocId != null) {
            Map<String, Object> element = new HashMap<>();
            element.put("busStopName", busStopName);
            element.put("busStopCode", busStopCode);
            element.put("busService", busService);
            firestore.collection("users")
                    .document(userDocId)
                    .update("savedarrivaltimes", FieldValue.arrayUnion(element))
                    .addOnSuccessListener(aVoid ->
                            Log.d("Firestore", "Appended to savedarrivaltimes array"))
                    .addOnFailureListener(e ->
                            Log.e("Firestore", "Failed to update array field", e));
        }
    }

    /**
     * Delete a single bookmark locally and remove from the Firestore array field.
     */
    public void deleteBookmark(String busStopName, String busStopCode, String busService) {
        // 1) local
        SQLiteDatabase db = getWritableDatabase();
        db.delete("bookmarks",
                "bus_stop_name=? AND bus_stop_code=? AND bus_service=?",
                new String[]{busStopName, busStopCode, busService});
        db.close();

        // 2) Firestore: remove the matching map from the array
        if (userDocId != null) {
            Map<String, Object> element = new HashMap<>();
            element.put("busStopName", busStopName);
            element.put("busStopCode", busStopCode);
            element.put("busService", busService);
            firestore.collection("users")
                    .document(userDocId)
                    .update("savedarrivaltimes", FieldValue.arrayRemove(element))
                    .addOnSuccessListener(aVoid ->
                            Log.d("Firestore", "Removed from savedarrivaltimes array"))
                    .addOnFailureListener(e ->
                            Log.e("Firestore", "Failed to remove from array field", e));
        }
    }

    public void deleteBookmarkByService(String busService) {
        // 1) local
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                "bookmarks",
                "bus_service=?",
                new String[]{ busService }
        );
        db.close();

        // 2) Firestore: remove any matching map entries from the array
        if (userDocId != null) {
            // Build a “template” map with only the busService field;
            // arrayRemove will match any element whose fields are a superset,
            // so this will remove all entries with that service.
            Map<String,Object> element = new HashMap<>();
            element.put("busService", busService);

            firestore.collection("users")
                    .document(userDocId)
                    .update("savedarrivaltimes", FieldValue.arrayRemove(element))
                    .addOnSuccessListener(aVoid ->
                            Log.d("Firestore", "Removed all entries with service=" + busService))
                    .addOnFailureListener(e ->
                            Log.e("Firestore", "Failed to remove by service", e));
        }
    }

    public void deleteBookmarksByBusStop(String busStopName, String busStopCode) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT bus_service FROM bookmarks WHERE bus_stop_name = ? AND bus_stop_code = ?",
                new String[]{busStopName, busStopCode});

        if (cursor.moveToFirst()) {
            do {
                String busService = cursor.getString(0);
                deleteBookmark(busStopName, busStopCode, busService);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }


    public void deleteBookmarksByBusService(String busService) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT bus_stop_name, bus_stop_code FROM bookmarks WHERE bus_service = ?",
                new String[]{busService});

        if (cursor.moveToFirst()) {
            do {
                String busStopName = cursor.getString(0);
                String busStopCode = cursor.getString(1);
                deleteBookmark(busStopName, busStopCode, busService);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }


    /**
     * Clear all bookmarks locally and in the Firestore array.
     */
    public void deleteAllBookmarks() {
        // local
        SQLiteDatabase db = getWritableDatabase();
        db.delete("bookmarks", null, null);
        db.close();

        // Firestore: reset the array to empty
        if (userDocId != null) {
            firestore.collection("users")
                    .document(userDocId)
                    .update("savedarrivaltimes", new ArrayList<>())
                    .addOnSuccessListener(aVoid ->
                            Log.d("Firestore", "Cleared savedarrivaltimes array"))
                    .addOnFailureListener(e ->
                            Log.e("Firestore", "Failed to clear array field", e));
        }
    }

    /**
     * Retrieve all bookmarks as a list of rows: [id, stopName, stopCode, service].
     */
    public List<List<String>> getAllBookmarks() {
        List<List<String>> bookmarks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT _id, bus_stop_name, bus_stop_code, bus_service FROM bookmarks", null);

        if (cursor.moveToFirst()) {
            do {
                List<String> row = new ArrayList<>();
                row.add(cursor.getString(0));
                row.add(cursor.getString(1));
                row.add(cursor.getString(2));
                row.add(cursor.getString(3));
                bookmarks.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Collections.reverse(bookmarks);
        return bookmarks;
    }

    public boolean doesBusStopCodeExist(String busStopCode) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM bookmarks WHERE bus_stop_code = ? LIMIT 1", new String[]{busStopCode});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean doesBusServiceExist(String busService) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM bookmarks WHERE bus_service = ? LIMIT 1", new String[]{busService});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean isBookmarked(String busStopCode, String busService) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM bookmarks WHERE bus_stop_code = ? AND bus_service = ?";
        Cursor cursor = db.rawQuery(query, new String[]{busStopCode, busService});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}
