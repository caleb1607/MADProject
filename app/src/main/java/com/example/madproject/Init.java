package com.example.madproject;

import android.app.Application;

import com.example.madproject.helper.APIReader;
import com.google.firebase.FirebaseApp;

public class Init extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        APIReader.setAPIKey(); // is only run once

        // Initialize Firebase globally
        FirebaseApp.initializeApp(this);
    }
}
