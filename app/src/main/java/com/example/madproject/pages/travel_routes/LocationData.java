package com.example.madproject.pages.travel_routes;

public class LocationData {
    public String name;
    public String blk;
    public String lat;
    public String lon;
    public String savedQuery;

    public LocationData(String name, String blk, String lat, String lon, String savedQuery) {
        this.name = name;
        this.blk = blk;
        this.lat = lat;
        this.lon = lon;
        this.savedQuery = savedQuery;
    }
    public String getName() { return name; }
    public String getBlk() { return blk; }
    public String getLat() { return lat; }
    public String getLon() { return lon; }
    public String getSavedQuery() { return savedQuery; }
}
