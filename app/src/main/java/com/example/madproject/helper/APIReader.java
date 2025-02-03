package com.example.madproject.helper;

import android.util.Log;

import com.example.madproject.helper.API.BusArrivals.BusArrivalApi;
import com.example.madproject.helper.API.BusArrivals.BusArrivalResponse;
import com.example.madproject.helper.API.BusArrivals.RetrofitClient;
import com.example.madproject.BuildConfig;
import com.example.madproject.helper.API.Token.TokenClient;
import com.example.madproject.helper.API.Token.TokenCallback;

import retrofit2.Call;
import retrofit2.Response;

public class APIReader {
    static String API_KEY;
    public static String[] fetchBusArrivals(String busStopCode, String busNumber) {
        try {
            BusArrivalApi apiService = RetrofitClient.getApiService();
            Call<BusArrivalResponse> call = apiService.getBusArrivals(busStopCode);
            Response<BusArrivalResponse> response = call.execute(); // Synchronous call

            if (response.isSuccessful() && response.body() != null && !response.body().Services.isEmpty()) {
                for (BusArrivalResponse.Service serviceData : response.body().Services) {
                    if (serviceData.ServiceNo.equals(busNumber)) {
                        if (serviceData.NextBus.EstimatedArrival != null) {
                            String[] arrivals = new String[3];
                            arrivals[0] = Helper.ISOToMinutes(serviceData.NextBus.EstimatedArrival);
                            arrivals[1] = (serviceData.NextBus2.EstimatedArrival != null) ? Helper.ISOToMinutes(serviceData.NextBus2.EstimatedArrival) : null;
                            arrivals[2] = (serviceData.NextBus3.EstimatedArrival != null) ? Helper.ISOToMinutes(serviceData.NextBus3.EstimatedArrival) : null;
                            return arrivals;
                        } else {
                            return null;
                        }
                    }
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    public static void setAPIKey() {
        String ONEMAP_EMAIL = BuildConfig.ONEMAP_EMAIL;
        String ONEMAP_PASSWORD = BuildConfig.ONEMAP_PASSWORD;
        TokenClient.getToken(new TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                // Example: store it in a variable
                API_KEY = token;
            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error
                Log.e("API", errorMessage);
            }
        });
    }
    public static String getAPIKey() {
        return API_KEY;
    }
}
