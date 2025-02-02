package com.example.madproject.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.madproject.datasets.BusStopsComplete;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Helper {
    private static List<BusStopsComplete> busStopsList;
    public static class GetBusStopInfo {
        private BusStopsComplete busStopInfo;

        public GetBusStopInfo(Context context, String busStopCode) {
            if (busStopsList == null) {
                busStopsList = JSONReader.bus_stops_complete(context);
            }
            for (BusStopsComplete item : busStopsList) {
                if (item.getBusStopCode().equals(busStopCode)) {
                    busStopInfo = item;
                    break;
                }
            }
        }
        public String getRoadName() { return busStopInfo.getRoadName(); }
        public String getDescription() { return busStopInfo.getDescription(); }
        public double getLatitude() { return busStopInfo.getLatitude(); }
        public double getLongitude() { return busStopInfo.getLongitude(); }
    }

    public static String[] fetchBusArrivals(String busStopCode, String busNumber) {
        try {
            BusArrivalApi apiService = RetrofitClient.getApiService();
            Call<BusArrivalResponse> call = apiService.getBusArrivals(busStopCode);
            Response<BusArrivalResponse> response = call.execute(); // Synchronous call

            if (response.isSuccessful() && response.body() != null && !response.body().Services.isEmpty()) {
                for (BusArrivalResponse.Service serviceData : response.body().Services) {
                    if (serviceData.ServiceNo.equals(busNumber)) {
                        return new String[]{
                                ISOToMinutes(serviceData.NextBus.EstimatedArrival),
                                ISOToMinutes(serviceData.NextBus2.EstimatedArrival),
                                ISOToMinutes(serviceData.NextBus3.EstimatedArrival)
                        };
                    }
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static String ISOToMinutes(String timestamp) {

        // Define the formatter with time zone info
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        // Parse the timestamp
        ZonedDateTime parsedTime = ZonedDateTime.parse(timestamp, formatter);

        // Get current time
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("Asia/Singapore"));

        // Calculate the difference in minutes
        long minutesDifference = Duration.between(parsedTime, currentTime).toMinutes();

        // Print the result
        return Long.toString(Math.abs(minutesDifference));
    }
}
