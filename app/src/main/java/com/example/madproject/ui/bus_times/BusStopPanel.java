package com.example.madproject.ui.bus_times;

public class BusStopPanel {
    private String busStopName;
    private String busStopCode;
    private String streetName;
    private String[] AT;
    private boolean isBookmarked;

    public BusStopPanel(String busStopName, String busStopCode, String streetName, String[] AT, boolean isBookmarked) {
        this.busStopName = busStopName;
        this.busStopCode = busStopCode;
        this.streetName = streetName;
        this.AT = AT;
        this.isBookmarked = isBookmarked;
    }
    public String getBusStopName() { return busStopName; }
    public String getBusStopCode() { return busStopCode; }
    public String getStreetName() { return streetName; }
    public String[] getAT() { return AT; }
    public boolean getIsBookmarked() { return isBookmarked; }
}
