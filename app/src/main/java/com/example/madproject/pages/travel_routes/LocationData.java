package com.example.madproject.pages.travel_routes;

public class LocationData {
    public String name;
    public String lat;
    public String lon;

    public LocationData(String name, String lat, String lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }
    public String getName() { return name; }
    public String getLat() { return lat; }
    public String getLon() { return lon; }
}
