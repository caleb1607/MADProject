package com.example.madproject.pages.travel_routes;

public class LegPanel {
    private String fromLocation;
    private String mode;
    private String toLocation;
    private int duration;

    public LegPanel(String fromLocation, String mode, String toLocation, int duration) {
        this.fromLocation = fromLocation;
        this.mode = mode;
        this.toLocation = toLocation;
        this.duration = duration;
    }
    public String getFromLocation() { return fromLocation; }
    public String getMode() { return mode; }
    public String getToLocation() { return toLocation; }
    public int getDuration() { return duration; }
}
