package com.example.madproject.pages.bus_times;

public class BusServicePanel {
    private String busNumber;
    private String[] AT;
    private boolean isBookmarked;

    public BusServicePanel(String busNumber, String[] AT, boolean isBookmarked) {
        this.busNumber = busNumber;
        this.AT = AT;
        this.isBookmarked = isBookmarked;
    }
    public void setAT(String[] AT) {
        this.AT = AT;
    }
    public void setIsBookmarked(boolean isBookmarked) { this.isBookmarked = isBookmarked; }
    public String getBusNumber() { return busNumber; }
    public String[] getAT() { return AT; }
    public boolean getIsBookmarked() { return isBookmarked; }
}
