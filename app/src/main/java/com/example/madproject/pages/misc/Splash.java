package com.example.madproject.pages.misc;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.madproject.R;
import com.example.madproject.helper.BusTimesBookmarksDB;
import com.example.madproject.helper.LocalStorageDB;
import com.example.madproject.pages.Main;
import com.example.madproject.pages.settings.ThemeManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Splash extends AppCompatActivity {

    LocalStorageDB localStorageDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
        // manageTheme
        manageTheme();
        ImageView NYOOM = findViewById(R.id.NYOOM);
        Glide.with(this)
                .load(R.drawable.nyoom_effect)
                .into((ImageView) findViewById(R.id.NyoomEffect));
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(NYOOM, "scaleX", 0f, 5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(NYOOM, "scaleY", 0f, 5f);
        scaleX.setDuration(4000);
        scaleY.setDuration(4000);
        scaleX.start();
        scaleY.start();
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.nyoom);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = (localStorageDB.getValue("LoginToken").equals("0")) ?
                    new Intent(Splash.this, Startup.class) :
                    new Intent(Splash.this, Main.class);
            handler.postDelayed(() -> {
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }, 600);
        }, 600);
    }
    private void manageTheme() {
        ConstraintLayout splashBackground = findViewById(R.id.SplashBackground);
        if (ThemeManager.isDarkTheme()) {
            splashBackground.setBackgroundResource(R.drawable.splash_dark);
        } else { // light
            splashBackground.setBackgroundResource(R.drawable.splash_light);
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

        BusTimesBookmarksDB bookmarksDB = new BusTimesBookmarksDB(this);
        bookmarksDB.syncBookmarksFromFirestore(() -> Log.d("Init", "Firestore sync complete"));

        ThemeManager.setTheme(
                null,
                localStorageDB.getValue("theme_preference").equals("dark")
        );
    }

}
