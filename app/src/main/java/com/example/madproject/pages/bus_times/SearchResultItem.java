package com.example.madproject.pages.bus_times;

public class SearchResultItem {
    private String type;
    private String value;
    private String header;
    private String subheader1;
    private String subheader2;

    public SearchResultItem(String type, String value, String header, String subheader1, String subheader2) {
        this.type = type;
        this.value = value;
        this.header = header;
        this.subheader1 = subheader1;
        this.subheader2 = subheader2;
    }
    public String getType() { return type; }
    public String getValue() { return value; }
    public String getHeader() { return header; }
    public String getSubheader1() { return subheader1; }
    public String getSubheader2() { return subheader2; }
}
