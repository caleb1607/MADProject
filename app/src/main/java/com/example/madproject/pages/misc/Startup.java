package com.example.madproject.pages.misc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.madproject.R;
import com.example.madproject.pages.Main;
import com.example.madproject.pages.settings.ThemeManager;

public class Startup extends AppCompatActivity {

    Button SignInButton, GuestButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        SignInButton = findViewById(R.id.SignInButton);
        SignInButton.setOnClickListener(view -> goToSignInEnter());
        GuestButton = findViewById(R.id.GuestButton);
        GuestButton.setOnClickListener(view -> onEnter());
        // manage theme
        manageTheme();
    }
    private void manageTheme() {
        if (ThemeManager.isDarkTheme()) {
            SignInButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            SignInButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else { // light
            SignInButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
            SignInButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        }

    }
    private void goToSignInEnter() {
        Intent signin = new Intent(Startup.this, Login.class);
        startActivity(signin);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
    private void onEnter() {
        Intent enter = new Intent(Startup.this, Main.class);
        enter.putExtra("guest", "1");
        startActivity(enter);
        finish();
    }
}