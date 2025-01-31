package com.example.madproject.datasets;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusStopsMap {
    private String busService;
    @SerializedName("1")
    private List<BusStopInfo> direction1List;
    @SerializedName("2")
    private List<BusStopInfo> direction2List;
    @SerializedName("startend1")
    private List<String> startend1;
    @SerializedName("startend2")
    private List<String> startend2;

    public BusStopsMap(String busService, List<BusStopInfo> direction1List, List<BusStopInfo> direction2List, List<String> startend1, List<String> startend2) {
        this.busService = busService;
        this.direction1List = direction1List;
        this.direction2List = direction2List;
        this.startend1 = startend1;
        this.startend2 = startend2;
    }
    public String getBusService() { return busService; }
    public List<BusStopInfo> getDirection1List() { return direction1List; }
    public List<BusStopInfo> getDirection2List() { return direction2List; }
    public List<String> getStartend1() { return startend1; }
    public List<String> getStartend2() { return startend2; }


    public static class BusStopInfo { // inner class
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
