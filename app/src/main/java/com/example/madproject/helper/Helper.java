package com.example.madproject.helper;

import android.content.Context;

import com.example.madproject.datasets.BusStopsComplete;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    public static String TitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        boolean withinBracket = false;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (c == '(') {
                withinBracket = true;
                result.append(c);
            } else if (c == ')') {
                withinBracket = false;
                result.append(c);
            } else if (capitalizeNext || withinBracket) {
                if (Character.isAlphabetic(c)) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else
                    result.append(c);
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }
    public static String ISOToMinutes(String timestamp) {

        if (timestamp == null || timestamp.trim().isEmpty()) {
            return timestamp;
        }
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
