package com.example.madproject.ui.bus_times;

import java.util.List;

public class BusServicePanel {
    private String busNumber;
    private String[] AT;
    private boolean isBookmarked;

    public BusServicePanel(String busNumber, String[] AT, boolean isBookmarked) {
        this.busNumber = busNumber;
        this.AT = AT;
        this.isBookmarked = isBookmarked;
    }
    public String getBusNumber() { return busNumber; }
    public String[] getAT() { return AT; }
    public boolean getIsBookmarked() { return isBookmarked; }
}
