package com.example.madproject.helper;

import android.content.Context;

import com.example.madproject.R;
import com.example.madproject.ui.bus_times.BusStopData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class JSONReader {
    public static List<String> bus_services(Context context) {
        try {
            InputStreamReader reader = new InputStreamReader(context.getResources().openRawResource(R.raw.bus_services));
            Gson gson = new Gson();
            return gson.fromJson(reader, List.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<BusStopData> bus_stops_complete(Context context) {
        try {
            InputStreamReader reader = new InputStreamReader(context.getResources().openRawResource(R.raw.bus_stops_complete));
            Gson gson = new Gson();
            Type listType = new TypeToken<List<BusStopData>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
