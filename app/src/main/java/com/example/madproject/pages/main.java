package com.example.madproject.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.madproject.R;
import com.example.madproject.pages.bookmarks.Bookmarks;
import com.example.madproject.pages.bus_times.BusTimes;
import com.example.madproject.pages.settings.Settings;
import com.example.madproject.pages.travel_now.TravelRoutes;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class main extends AppCompatActivity {

    int currentFragmentId;
    int fragmentPosition;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set default fragment
        loadFragment(new Bookmarks(), null);
        currentFragmentId = R.id.bookmarks;
        fragmentPosition = 0;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // find navbar clicked item
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            Boolean animationGoesRight = null;
            int itemID = item.getItemId();
            if (itemID != currentFragmentId) {
                if (itemID == R.id.bookmarks) {
                    selectedFragment = new Bookmarks();
                    currentFragmentId = R.id.bookmarks;
                    animationGoesRight = (fragmentPosition < 0) ? true : false;
                    fragmentPosition = 0;
                } else if (itemID == R.id.busarrivaltimes) {
                    selectedFragment = new BusTimes();
                    currentFragmentId = R.id.busarrivaltimes;
                    animationGoesRight = (fragmentPosition < 1) ? true : false;
                    fragmentPosition = 1;
                } else if (itemID == R.id.travelroutes) {
                    selectedFragment = new TravelRoutes();
                    currentFragmentId = R.id.travelroutes;
                    animationGoesRight = (fragmentPosition < 2) ? true : false;
                    fragmentPosition = 2;
                } else if (itemID == R.id.settings) {
                    selectedFragment = new Settings();
                    currentFragmentId = R.id.settings;
                    animationGoesRight = (fragmentPosition < 3) ? true : false;
                    fragmentPosition = 3;
                }
                if (selectedFragment != null) {
                    loadFragment(selectedFragment, animationGoesRight);
                }
            }
            return true;
        });
    }

    // Method to load fragments
    private void loadFragment(Fragment fragment, Boolean animationGoesRight) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (animationGoesRight != null) {
            if (animationGoesRight == false) { // goes left
                fragmentTransaction.setCustomAnimations(
                        R.anim.slidefade_in_left,  // Enter animation for the new fragment
                        R.anim.slidefade_out_right // Exit animation for the current fragment
                );
            } else { // goes right
                fragmentTransaction.setCustomAnimations(
                        R.anim.slidefade_in_right,  // Enter animation for the new fragment
                        R.anim.slidefade_out_left // Exit animation for the current fragment
                );
            }
        }
        fragmentTransaction
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            Fragment previousFragment = fragmentManager.findFragmentById(R.id.fragment_container);
            if (previousFragment != null) {
                if (previousFragment instanceof Bookmarks) {
                    bottomNavigationView.setSelectedItemId(R.id.bookmarks);
                } else if (previousFragment instanceof BusTimes) {
                    bottomNavigationView.setSelectedItemId(R.id.busarrivaltimes);
                } else if (previousFragment instanceof TravelRoutes) {
                    bottomNavigationView.setSelectedItemId(R.id.travelroutes);
                } else if (previousFragment instanceof Settings) {
                    bottomNavigationView.setSelectedItemId(R.id.settings);
                }
            }
        } else {
            super.onBackPressed();
        }
    }
}