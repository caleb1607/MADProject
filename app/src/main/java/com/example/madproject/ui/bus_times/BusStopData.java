package com.example.madproject.ui.bus_times;

import com.google.gson.annotations.SerializedName;

public class BusStopData {
    @SerializedName("BusStopCode") // gson annotation mapping
    private String busStopCode;
    @SerializedName("RoadName")
    private String roadName;
    @SerializedName("Description")
    private String description;
    @SerializedName("Latitude")
    private Double latitude;
    @SerializedName("Longitude")
    private Double longitude;

    public String getBusStopCode() { return busStopCode; }
    public String getRoadName() { return roadName; }
    public String getDescription() { return description; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
