package com.example.madproject.helper;

import android.content.Context;

import com.example.madproject.R;
import com.example.madproject.datasets.BusServicesAtStop;
import com.example.madproject.datasets.BusStopsComplete;
import com.example.madproject.datasets.BusStopsMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public static List<BusStopsComplete> bus_stops_complete(Context context) {
        try {
            InputStreamReader reader = new InputStreamReader(context.getResources().openRawResource(R.raw.bus_stops_complete));
            Gson gson = new Gson();
            Type listType = new TypeToken<List<BusStopsComplete>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<BusServicesAtStop> bus_services_at_stop(Context context) {
        try {
            InputStreamReader reader = new InputStreamReader(context.getResources().openRawResource(R.raw.bus_services_at_stop));
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, List<String>>>() {}.getType();

            Map<String, List<String>> rawData = gson.fromJson(reader, mapType);

            List<BusServicesAtStop> result = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : rawData.entrySet()) {
                BusServicesAtStop item = new BusServicesAtStop(entry.getKey(), entry.getValue());
                result.add(item);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<BusStopsMap> busstops_map(Context context) {
        try {
            // Reading the raw JSON file (busstop_map.json)
            InputStreamReader reader = new InputStreamReader(context.getResources().openRawResource(R.raw.busstops_map));
            Gson gson = new Gson();

            // Parse the raw JSON into a Map where the key is a String (bus service code) and the value is a BusStopsMap
            Type mapType = new TypeToken<Map<String, BusStopsMap>>() {}.getType();
            Map<String, BusStopsMap> rawData = gson.fromJson(reader, mapType);

            // Return the list of BusStopsMap objects
            return new ArrayList<>(rawData.values());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
