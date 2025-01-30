package com.example.madproject.ui.bus_times;

public class BusServicePanel {
    private String busNumber;
    private String AT1;
    private String AT2;
    private String AT3;
    private boolean isBookmarked;

    public BusServicePanel(String busNumber, String AT1, String AT2, String AT3, boolean isBookmarked) {
        this.busNumber = busNumber;
        this.AT1 = AT1;
        this.AT2 = AT2;
        this.AT3 = AT3;
        this.isBookmarked = isBookmarked;
    }
    public String getBusNumber() { return busNumber; }
    public String getAT1() { return AT1; }
    public String getAT2() { return AT2; }
    public String getAT3() { return AT3; }
    public boolean getIsBookmarked() { return isBookmarked; }
}
