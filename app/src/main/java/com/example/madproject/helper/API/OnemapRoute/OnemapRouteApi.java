package com.example.madproject.helper.API.OnemapRoute;

import com.example.madproject.helper.API.OnemapRoute.OnemapRouteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface OnemapRouteApi {
    @GET("api/public/routingsvc/route")
    Call<OnemapRouteResponse> getRouteResults(
            @Header("Authorization") String authorization,
            @Query(value = "start", encoded = true) String start,
            @Query(value = "end", encoded = true) String end,
            @Query("routeType") String routeType,
            @Query("date") String date,
            @Query("time") String time,
            @Query("mode") String mode,
            @Query("numItineraries") String numItineraries
    );
}