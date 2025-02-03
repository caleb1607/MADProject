package com.example.madproject.helper.API.Token;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface ApiService {
    @POST("api/auth/post/getToken")
    Call<JsonObject> getToken(@Body Map<String, String> authRequest);
}
