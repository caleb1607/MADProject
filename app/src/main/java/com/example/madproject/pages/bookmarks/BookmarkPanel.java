package com.example.madproject.pages.bookmarks;

public class BookmarkPanel {
    private String busNumber;
    private String busStopName;
    private String busStopCode;
    private String[] AT;
    private boolean isBookmarked;

    public BookmarkPanel(String busNumber, String busStopName, String busStopCode, String[] AT, boolean isBookmarked) {
        this.busNumber = busNumber;
        this.busStopName = busStopName;
        this.busStopCode = busStopCode;
        this.AT = AT;
        this.isBookmarked = isBookmarked;
    }
    public void setAT(String[] AT) { this.AT = AT; }
    public void setIsBookmarked(boolean isBookmarked) { this.isBookmarked = isBookmarked; }
    public String getBusNumber() { return busNumber; }
    public String getBusStopName() { return busStopName; }
    public String getBusStopCode() { return busStopCode; }
    public String[] getAT() { return AT; }
    public boolean getIsBookmarked() { return isBookmarked; }

    public void toggleIsBookmarked() {
    }
}
