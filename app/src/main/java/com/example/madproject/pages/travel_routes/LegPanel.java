package com.example.madproject.pages.travel_routes;

public class LegPanel {
    private String fromLocation;
    private String mode;
    private String toLocation;
    private int duration;
    private String route;

    public LegPanel(String fromLocation, String mode, String toLocation, int duration, String route) {
        this.fromLocation = fromLocation;
        this.mode = mode;
        this.toLocation = toLocation;
        this.duration = duration;
        this.route = route;
    }
    public String getFromLocation() { return fromLocation; }
    public String getMode() { return mode; }
    public String getToLocation() { return toLocation; }
    public int getDuration() { return duration; }
    public String getRoute() { return route; }
}
