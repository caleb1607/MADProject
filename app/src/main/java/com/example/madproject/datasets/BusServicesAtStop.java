package com.example.madproject.datasets;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusServicesAtStop {
    @SerializedName("BusStopCode") // gson annotation mapping
    private String busStopCode;
    @SerializedName("BusServices")
    private List<String> busServices;

    public BusServicesAtStop(String busStopCode, List<String> busServices) {
        this.busStopCode = busStopCode;
        this.busServices = busServices;
    } // cuz gson doesnt support this by default

    public String getBusStopCode() { return busStopCode; }
    public List<String> getBusServices() { return busServices; }
}
