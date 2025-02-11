package com.example.madproject.pages.misc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.madproject.R;
import com.example.madproject.pages.Main;

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
    }
    private void goToSignInEnter() {
        Intent signin = new Intent(Startup.this, Login.class);
        startActivity(signin);
        finish();
    }
    private void onEnter() {
        Intent enter = new Intent(Startup.this, Main.class);
        startActivity(enter);
        finish();
    }
}