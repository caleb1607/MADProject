package com.example.madproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Register extends AppCompatActivity {

    private Button loginRedirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginRedirect = findViewById(R.id.LoginRedirect);
        loginRedirect.setOnClickListener(onLoginRedirect);
    }
    private View.OnClickListener onLoginRedirect = view -> {
        Intent redirect = new Intent(Register.this, Login.class);
        startActivity(redirect);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    };
}