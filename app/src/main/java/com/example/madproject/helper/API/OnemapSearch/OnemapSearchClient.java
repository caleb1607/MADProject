package com.example.madproject.helper.API.OnemapSearch;

import com.example.madproject.helper.API.BusArrivals.BusArrivalApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnemapSearchClient {
    private static final String BASE_URL = "https://www.onemap.gov.sg/";
    private static Retrofit retrofit = null;

    public static OnemapSearchApi getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(OnemapSearchApi.class);
    }
}
