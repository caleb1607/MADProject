package com.example.madproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login extends AppCompatActivity {

    private Button registerRedirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerRedirect = findViewById(R.id.RegisterRedirect);
        registerRedirect.setOnClickListener(onRegisterRedirect);
    }
    private View.OnClickListener onRegisterRedirect = view -> {
        Intent redirect = new Intent(Login.this, Register.class);
        startActivity(redirect);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    };
}