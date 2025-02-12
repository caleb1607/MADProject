package com.example.madproject.pages.misc;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.madproject.R;
import com.example.madproject.helper.LocalStorageDB;
import com.example.madproject.pages.Main;
import com.example.madproject.pages.settings.ThemeManager;

public class Splash extends AppCompatActivity {

    LocalStorageDB localStorageDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
        // manage theme
        manageTheme();
        ImageView NYOOM = findViewById(R.id.NYOOM);
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            Intent intent = (localStorageDB.getValue("LoginToken").equals("0")) ?
                    new Intent(Splash.this, Startup.class) :
                    new Intent(Splash.this, Main.class);
            handler.postDelayed(() -> {
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }, 400);
        }, 400);
    }
    private void manageTheme() {
        ConstraintLayout layout = findViewById(R.id.SplashBackground);
        if (ThemeManager.isDarkTheme()) {
            layout.setBackgroundResource(R.drawable.splash_dark);
        } else {
            layout.setBackgroundResource(R.drawable.splash_light);
        }
    }
    private void init() {
        localStorageDB = new LocalStorageDB(this);
        if (!localStorageDB.keyExists("theme_preference")) {
            localStorageDB.insertOrUpdate("theme_preference", "light");
        }
        if (!localStorageDB.keyExists("LoginToken")) {
            localStorageDB.insertOrUpdate("LoginToken", "0");
        }
        ThemeManager.setTheme(
                null,
                localStorageDB.getValue("theme_preference").equals("dark")
        );
    }
}
