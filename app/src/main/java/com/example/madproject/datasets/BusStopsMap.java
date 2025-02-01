package com.example.madproject.datasets;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.stream.Collectors;

public class BusStopsMap {
    private String busService;
    @SerializedName("1")
    private List<List<Object>> direction1Raw;
    @SerializedName("2")
    private List<List<Object>> direction2Raw;
    @SerializedName("startend1")
    private List<String> startend1;
    @SerializedName("startend2")
    private List<String> startend2;

    public void setBusService(String busService) {
        this.busService = busService;
    }

    public String getBusService() {
        return busService;
    }
    public List<BusStopInfo> getDirection1List() {
        return convertToBusStopInfo(direction1Raw);
    }

    public List<BusStopInfo> getDirection2List() {
        return convertToBusStopInfo(direction2Raw);
    }

    public List<String> getStartend1() { return startend1; }
    public List<String> getStartend2() { return startend2; }

    private List<BusStopInfo> convertToBusStopInfo(List<List<Object>> rawList) {
        if (rawList == null) return null;
        return rawList.stream()
                .map(item -> new BusStopInfo((String) item.get(0), ((Number) item.get(1)).floatValue()))
                .collect(Collectors.toList());
    }

    public static class BusStopInfo {
        private String busStopCode;
        private float roadDistance;

        public BusStopInfo(String busStopCode, float roadDistance) {
            this.busStopCode = busStopCode;
            this.roadDistance = roadDistance;
        }

        public String getBusStopCode() { return busStopCode; }
        public float getRoadDistance() { return roadDistance; }
    }
}