package com.example.madproject.pages.misc;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.madproject.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView NYOOM = findViewById(R.id.NYOOM);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent startup = new Intent(Splash.this, Startup.class);
            handler.postDelayed(() -> {
                startActivity(startup);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }, 400);
        }, 400);
    }
}
