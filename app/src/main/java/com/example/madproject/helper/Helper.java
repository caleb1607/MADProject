package com.example.madproject.helper;

import android.content.Context;

import com.example.madproject.datasets.BusStopsComplete;

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
}
