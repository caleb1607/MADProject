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

        public Result(String SEARCHVAL, String LATITUDE, String LONGITUDE) {
            this.SEARCHVAL = SEARCHVAL;
            this.LATITUDE = LATITUDE;
            this.LONGITUDE = LONGITUDE;
        }
    }
}
