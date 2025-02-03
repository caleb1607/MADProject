package com.example.madproject.pages.travel_now;

public class TRSearchResultItem {
    private String name;
    private String lat;
    private String lon;

    public TRSearchResultItem(String name, String lat, String lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }
    public String getName() { return name; }
    public String getLat() { return lat; }
    public String getLon() { return lon; }
}
