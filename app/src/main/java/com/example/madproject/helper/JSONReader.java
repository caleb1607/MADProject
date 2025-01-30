package com.example.madproject.helper;

import android.content.Context;

import com.example.madproject.R;
import com.example.madproject.datasets.BusServicesAtStop;
import com.example.madproject.datasets.BusStopsComplete;
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
}
