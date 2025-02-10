package com.example.madproject.pages.misc;

import androidx.appcompat.app.AppCompatActivity;

import com.example.madproject.R;
import com.example.madproject.pages.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login extends AppCompatActivity {

    private Button registerRedirect;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerRedirect = findViewById(R.id.RegisterRedirect);
        registerRedirect.setOnClickListener(onRegisterRedirect);
        loginButton = findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(onLogin);
    }
    private View.OnClickListener onRegisterRedirect = view -> {
        Intent redirect = new Intent(Login.this, Register.class);
        startActivity(redirect);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    };
    private View.OnClickListener onLogin = view -> {
        Intent login = new Intent(Login.this, Main.class);
        startActivity(login);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    };
}