package com.example.madproject;

import android.app.Application;

import com.example.madproject.helper.APIReader;

public class Init extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        APIReader.setAPIKey(); // is only run once
    }
}
