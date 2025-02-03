package com.example.madproject.datasets;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusServicesAtStop {
    private String busStopCode;
    private List<String> busServices;

    public BusServicesAtStop(String busStopCode, List<String> busServices) {
        this.busStopCode = busStopCode;
        this.busServices = busServices;
    }

    public String getBusStopCode() { return busStopCode; }
    public List<String> getBusServices() { return busServices; }
}
