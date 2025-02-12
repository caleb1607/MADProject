package com.example.madproject.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.madproject.R;
import com.example.madproject.pages.bookmarks.Bookmarks;
import com.example.madproject.pages.bus_times.BusTimes;
import com.example.madproject.pages.settings.Settings;
import com.example.madproject.pages.settings.ThemeManager;
import com.example.madproject.pages.travel_routes.TRSearch;
import com.example.madproject.pages.travel_routes.TravelRoutes;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Main extends AppCompatActivity {
    private FirebaseAuth mAuth; // Initialize Firebase Auth
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private SharedPreferences usernamepref, emailpref; //Initialize Pref
    int currentFragmentId;
    int fragmentPosition;
    BottomNavigationView bottomNavigationView;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Pref
        usernamepref = getSharedPreferences("Usernamepref", MODE_PRIVATE);
        emailpref = getSharedPreferences("Emailpref", MODE_PRIVATE);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        // set default fragment
        loadFragment(new Bookmarks(), null);
        currentFragmentId = R.id.bookmarks;
        fragmentPosition = 0;
        bottomNavigationView = findViewById(R.id.BottomNavigationView);
        menu = bottomNavigationView.getMenu();
        // manage theme
        manageTheme();
        // update font family
        Typeface typeface = ResourcesCompat.getFont(this, R.font.albertsans5_medium);
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem item = bottomNavigationView.getMenu().getItem(i);
            SpannableString spanString = new SpannableString(item.getTitle());
            spanString.setSpan(new CustomTypefaceSpan("", typeface), 0, spanString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            item.setTitle(spanString);
        }
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
                updateNavbarColors();

                if (selectedFragment != null) {
                    loadFragment(selectedFragment, animationGoesRight);
                }
            }
            return true;
        });
    }



    @Override
    public void onStart() {
        super.onStart();

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");

                        usernamepref.edit().putString("username", username).apply();
                        emailpref.edit().putString("email", email).apply();
                        SharedPreferences emailpref2 = getSharedPreferences("Emailpref", MODE_PRIVATE);
                        String userEmail = emailpref2.getString("email", "default@gmail.com"); // Default if not found
                        Log.d("email",userEmail);


                        //Log.d("UserInfo", "Username: " + username + "Email: " + email);
                    }
                });
    }



    public void manageTheme() {
        View rootView = findViewById(android.R.id.content);
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            bottomNavigationView.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.navBarPanel));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LmainBackground));
            bottomNavigationView.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.LnavBarPanel));
        }
        // defaults
        ColorStateList bookmarksColor = ThemeManager.isDarkTheme() ?
                ContextCompat.getColorStateList(this, R.color.navbar_bookmarks_icon_tint) :
                ContextCompat.getColorStateList(this, R.color.light_navbar_bookmarks_icon_tint);
        bottomNavigationView.setItemTextColor(bookmarksColor);
        bottomNavigationView.setItemIconTintList(bookmarksColor);
        updateNavbarColors();
    }

    private void updateNavbarColors() {
        // set the icon tint for each individual item
        ColorStateList bookmarksColor = ThemeManager.isDarkTheme() ?
                ContextCompat.getColorStateList(this, R.color.navbar_bookmarks_icon_tint) :
                ContextCompat.getColorStateList(this, R.color.light_navbar_bookmarks_icon_tint);
        ColorStateList busarrivaltimesColor = ThemeManager.isDarkTheme() ?
                ContextCompat.getColorStateList(this, R.color.navbar_bustimes_icon_tint) :
                ContextCompat.getColorStateList(this, R.color.light_navbar_bustimes_icon_tint);
        ColorStateList travelroutesColor = ThemeManager.isDarkTheme() ?
                ContextCompat.getColorStateList(this, R.color.navbar_travelroutes_icon_tint) :
                ContextCompat.getColorStateList(this, R.color.light_navbar_travelroutes_icon_tint);
        ColorStateList settingsColor = ThemeManager.isDarkTheme() ?
                ContextCompat.getColorStateList(this, R.color.navbar_settings_icon_tint):
                ContextCompat.getColorStateList(this, R.color.light_navbar_settings_icon_tint);
        // set based on position
        if (currentFragmentId == R.id.bookmarks) {
            bottomNavigationView.setItemTextColor(bookmarksColor);
            bottomNavigationView.setItemIconTintList(bookmarksColor);
        } else if (currentFragmentId == R.id.busarrivaltimes) {
            bottomNavigationView.setItemTextColor(busarrivaltimesColor);
            bottomNavigationView.setItemIconTintList(busarrivaltimesColor);
        } else if (currentFragmentId == R.id.travelroutes) {
            bottomNavigationView.setItemTextColor(travelroutesColor);
            bottomNavigationView.setItemIconTintList(travelroutesColor);
        } else if (currentFragmentId == R.id.settings) {
            bottomNavigationView.setItemTextColor(settingsColor);
            bottomNavigationView.setItemIconTintList(settingsColor);
        }
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

    public class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private void applyCustomTypeFace(Paint paint, Typeface tf) {
            paint.setTypeface(tf);
        }
    }
}