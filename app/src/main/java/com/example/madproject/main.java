package com.example.madproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set default fragment
        loadFragment(new Homepage());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // find navbar clicked item
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemID = item.getItemId();
            if (itemID == R.id.homepage) {
                selectedFragment = new Homepage(); // fragment declaration
            } else if (itemID == R.id.busarrivaltimes) {
                selectedFragment = new BusArrivalTimes();
            } else if (itemID == R.id.travelroutes) {
                selectedFragment = new TravelRoutes();
            } else if (itemID == R.id.settings) {
                selectedFragment = new Settings();
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    // Method to load fragments
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}