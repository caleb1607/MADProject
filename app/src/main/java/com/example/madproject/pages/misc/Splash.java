package com.example.madproject.pages.misc;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
        Log.d("test1", "");
        init();
        // manage theme
        Log.d("test2", "");
        manageTheme();
        ImageView NYOOM = findViewById(R.id.NYOOM);
        Log.d("test3", "");
        Handler handler = new Handler();
        Log.d("test4", "");
        handler.postDelayed(() -> {
            Log.d("test5", "");
            Intent intent = (localStorageDB.getValue("LoginToken").equals("0")) ?
                    new Intent(Splash.this, Startup.class) :
                    new Intent(Splash.this, Main.class);
            Log.d("test6", "");
            handler.postDelayed(() -> {
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                Log.d("test7", "");
                finish();
                Log.d("test8", "");
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
