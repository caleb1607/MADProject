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
        public String LATITUDE;
        public String LONGITUDE;
        public String ADDRESS;

        public Result(String ADDRESS, String LATITUDE, String LONGITUDE) {
            this.ADDRESS = ADDRESS;
            this.LATITUDE = LATITUDE;
            this.LONGITUDE = LONGITUDE;
        }
    }
}
