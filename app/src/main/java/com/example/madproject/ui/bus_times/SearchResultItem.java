package com.example.madproject.ui.bus_times;

public class SearchResultItem {
    private String type;
    private String header;
    private String subheader1;
    private String subheader2;

    public SearchResultItem(String type, String header, String subheader1, String subheader2) {
        this.type = type;
        this.header = header;
        this.subheader1 = subheader1;
        this.subheader2 = subheader2;
    }
    public String getType() { return type; }
    public String getHeader() { return header; }
    public String getSubheader1() { return subheader1; }
    public String getSubheader2() { return subheader2; }
}
