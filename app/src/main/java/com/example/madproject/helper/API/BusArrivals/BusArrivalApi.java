package com.example.madproject.helper.API.BusArrivals;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface BusArrivalApi {
    @Headers({
            "AccountKey: 0x2W9VMATp6bX/9d3BdsyA==", // Replace with your actual key
            "accept: application/json"
    })
    @GET("v3/BusArrival")
    Call<BusArrivalResponse> getBusArrivals(@Query("BusStopCode") String busStopCode);
}

