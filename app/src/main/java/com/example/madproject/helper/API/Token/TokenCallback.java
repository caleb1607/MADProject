package com.example.madproject.helper.API.Token;

public interface TokenCallback {
    void onTokenReceived(String token);
    void onError(String errorMessage);
}

