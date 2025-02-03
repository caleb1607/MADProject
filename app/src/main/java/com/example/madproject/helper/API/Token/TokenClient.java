package com.example.madproject.helper.API.Token;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.madproject.BuildConfig;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class TokenClient {

    private static final String BASE_URL = "https://www.onemap.gov.sg/";

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Use Gson converter for JSON
            .build();

    public static ApiService apiService = retrofit.create(ApiService.class);

    public static void getToken(final TokenCallback callback) {
        Map<String, String> authRequest = new HashMap<>();
        authRequest.put("email", BuildConfig.ONEMAP_EMAIL);
        authRequest.put("password", BuildConfig.ONEMAP_PASSWORD);

        Call<JsonObject> call = apiService.getToken(authRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseBody = response.body();
                    String token = responseBody.get("access_token").getAsString();
                    callback.onTokenReceived(token);  // Pass the token to the callback
                } else {
                    callback.onError("Error: " + response.message());  // Handle error response
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());  // Handle failure
            }
        });
    }

}
