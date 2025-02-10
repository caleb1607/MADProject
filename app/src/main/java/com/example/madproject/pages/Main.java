package com.example.madproject.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.madproject.R;
import com.example.madproject.pages.bookmarks.Bookmarks;
import com.example.madproject.pages.bus_times.BusTimes;
import com.example.madproject.pages.settings.Settings;
import com.example.madproject.pages.settings.ThemeManager;
import com.example.madproject.pages.travel_routes.TRSearch;
import com.example.madproject.pages.travel_routes.TravelRoutes;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main extends AppCompatActivity {

    public void manageTheme() {
        View rootView = findViewById(android.R.id.content);
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LmainBackground));
        }
    }

    int currentFragmentId;
    int fragmentPosition;
    BottomNavigationView bottomNavigationView;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // manage theme
        manageTheme();
        // set default fragment
        loadFragment(new Bookmarks(), null);
        currentFragmentId = R.id.bookmarks;
        fragmentPosition = 0;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        menu = bottomNavigationView.getMenu();
        // set the icon tint for each individual item
        ColorStateList bookmarksColor = ContextCompat.getColorStateList(this, R.color.navbar_bookmarks_icon_tint);
        ColorStateList busarrivaltimesColor = ContextCompat.getColorStateList(this, R.color.navbar_bustimes_icon_tint);
        ColorStateList travelroutesColor = ContextCompat.getColorStateList(this, R.color.navbar_travelroutes_icon_tint);
        ColorStateList settingsColor = ContextCompat.getColorStateList(this, R.color.navbar_settings_icon_tint);
        Menu menu = bottomNavigationView.getMenu();
        // default
        bottomNavigationView.setItemTextColor(bookmarksColor);
        bottomNavigationView.setItemIconTintList(bookmarksColor);
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
                    // Apply the selected item colors (text and icon)
                    bottomNavigationView.setItemTextColor(bookmarksColor);
                    bottomNavigationView.setItemIconTintList(bookmarksColor);
                } else if (itemID == R.id.busarrivaltimes) {
                    selectedFragment = new BusTimes();
                    currentFragmentId = R.id.busarrivaltimes;
                    animationGoesRight = (fragmentPosition < 1) ? true : false;
                    fragmentPosition = 1;
                    // Apply the selected item colors (text and icon)
                    bottomNavigationView.setItemTextColor(busarrivaltimesColor);
                    bottomNavigationView.setItemIconTintList(busarrivaltimesColor);
                } else if (itemID == R.id.travelroutes) {
                    selectedFragment = new TravelRoutes();
                    currentFragmentId = R.id.travelroutes;
                    animationGoesRight = (fragmentPosition < 2) ? true : false;
                    fragmentPosition = 2;
                    // Apply the selected item colors (text and icon)
                    bottomNavigationView.setItemTextColor(travelroutesColor);
                    bottomNavigationView.setItemIconTintList(travelroutesColor);
                } else if (itemID == R.id.settings) {
                    selectedFragment = new Settings();
                    currentFragmentId = R.id.settings;
                    animationGoesRight = (fragmentPosition < 3) ? true : false;
                    fragmentPosition = 3;
                    // Apply the selected item colors (text and icon)
                    bottomNavigationView.setItemTextColor(settingsColor);
                    bottomNavigationView.setItemIconTintList(settingsColor);
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
                } else if (previousFragment instanceof TRSearch) {
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