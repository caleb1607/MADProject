package com.example.madproject.helper;

import android.content.Context;

import com.example.madproject.datasets.BusStopsComplete;
import com.example.madproject.pages.travel_routes.LocationData;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public static String FormatSEARCHVAL(String input) {
        return TitleCase(input);
    }
    private static String TitleCase(String input) {
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
    public static void FormatSearchResultList(List<LocationData> list, String query) {
        BlkSuffix(list);
        ASCIISort(list);
        SmartSort(list, query);
    }
    private static void BlkSuffix(List<LocationData> list) {
        List<LocationData> copyList = new ArrayList<>(list);
        list.clear();
        Map<String, Integer> countMap = new HashMap<>();
        Set<String> duplicates = new HashSet<>();
        for (LocationData locationdata : copyList) {
            String name = locationdata.getName();
            countMap.put(name, countMap.getOrDefault(name, 0) + 1);
            if (countMap.get(name) > 1) {
                duplicates.add(name);
            }
        }
        for (LocationData locationdata : copyList) {
            String name = locationdata.getName();
            if (duplicates.contains(name)) {
                locationdata.name = name + " Blk " + locationdata.getBlk();
                list.add(locationdata);
            } else {
                list.add(locationdata);
            }
        }
    }
    public static void ASCIISort(List<LocationData> list) {
        list.sort(Comparator.comparing(LocationData::getName));
    }
    private static void SmartSort(List<LocationData> list, String query) {
        List<LocationData> copyList = new ArrayList<>(list);
        list.clear();
        for (LocationData locationdata : copyList) {
            if (locationdata.getName().toLowerCase().startsWith(query)) {
                list.add(locationdata);
            }
        }
        for (LocationData locationdata : copyList) {
            if (!locationdata.getName().toLowerCase().contains(query)) {
                list.add(locationdata);
            }
        }
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
        // Round down (if necessary) and take the absolute value
        long min = Math.abs(minutesDifference);
        // Return the result as a string without the decimal part
        return Long.toString(min);
    }
}
