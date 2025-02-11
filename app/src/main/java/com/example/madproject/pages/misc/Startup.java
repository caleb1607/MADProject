package com.example.madproject.pages.misc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.madproject.R;
import com.example.madproject.pages.Main;

public class Startup extends AppCompatActivity {

    Button justLemmeInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        justLemmeInButton = findViewById(R.id.justLemmeIn);
        justLemmeInButton.setOnClickListener(view -> onEnter());
    }
    private void onEnter() {
        Intent login = new Intent(Startup.this, Main.class);
        startActivity(login);
        finish();
    }
}