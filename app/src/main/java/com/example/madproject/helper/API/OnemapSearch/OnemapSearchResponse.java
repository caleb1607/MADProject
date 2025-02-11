package com.example.madproject.helper.API.OnemapSearch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OnemapSearchResponse {
    @SerializedName("results")
    public List<Result> Results;

    public OnemapSearchResponse(List<Result> Results) {
        this.Results = Results;
    }
    public static class Result {
        public String SEARCHVAL;
        public String BLK_NO;
        public String LATITUDE;
        public String LONGITUDE;
        public String ADDRESS;

        public Result(String SEARCHVAL, String BLK_NO, String ADDRESS, String LATITUDE, String LONGITUDE) {
            this.SEARCHVAL = SEARCHVAL;
            this.BLK_NO = BLK_NO;
            this.LATITUDE = LATITUDE;
            this.LONGITUDE = LONGITUDE;
            this.ADDRESS = ADDRESS;
        }
    }
}
