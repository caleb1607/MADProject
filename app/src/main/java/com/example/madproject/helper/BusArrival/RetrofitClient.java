package com.example.madproject.helper.BusArrival;

import com.example.madproject.helper.BusArrival.BusArrivalApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://datamall2.mytransport.sg/ltaodataservice/";
    private static Retrofit retrofit = null;

    public static BusArrivalApi getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(BusArrivalApi.class);
    }
}
