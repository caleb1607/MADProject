package com.example.madproject.helper.API.OnemapRoute;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnemapRouteClient {
    private static final String BASE_URL = "https://www.onemap.gov.sg/";
    private static Retrofit retrofit = null;

    public static OnemapRouteApi getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(OnemapRouteApi.class);
    }
}
