package com.example.madproject.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.madproject.R;
import com.example.madproject.ui.bookmarks.Bookmarks;
import com.example.madproject.ui.bus_times.BusTimes;
import com.example.madproject.ui.settings.Settings;
import com.example.madproject.ui.travel_now.TravelRoutes;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set default fragment
        loadFragment(new Bookmarks());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // find navbar clicked item
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemID = item.getItemId();
            if (itemID == R.id.homepage) {
                selectedFragment = new Bookmarks(); // fragment declaration
            } else if (itemID == R.id.busarrivaltimes) {
                selectedFragment = new BusTimes();
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