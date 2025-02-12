package com.example.madproject.pages.misc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.madproject.R;
import com.example.madproject.pages.settings.ThemeManager;

public class Register extends AppCompatActivity {

    private Button loginRedirect;
    private Button registerButton;
    private EditText usernameEditText, emailEditText, passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginRedirect = findViewById(R.id.LoginRedirect);
        loginRedirect.setOnClickListener(view -> onLoginRedirect());
        registerButton = findViewById(R.id.RegisterButton);
        //registerButton.setOnClickListener(onRegister);
        usernameEditText = findViewById(R.id.Username_InputField);
        emailEditText = findViewById(R.id.Email_InputField2);
        passwordEditText = findViewById(R.id.Password_InputField2);
        ImageView DownButton = findViewById(R.id.DownButton2);
        DownButton.setOnClickListener(view -> goBack());
        // manage theme
        manageTheme();
    }

    private void manageTheme() {
        FrameLayout RegisterBG = findViewById(R.id.RegisterBG);
        View UsernameBG = findViewById(R.id.UsernameBG2);
        TextView USERNAME = findViewById(R.id.USERNAME);
        View EmailBG = findViewById(R.id.EmailBG2);
        TextView EMAIL = findViewById(R.id.EMAIL2);
        View PasswordBG = findViewById(R.id.PasswordBG2);
        TextView PASSWORD = findViewById(R.id.PASSWORD2);
        if (ThemeManager.isDarkTheme()) {
            RegisterBG.setBackgroundResource(R.drawable.slide1blurdark);
            UsernameBG.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            USERNAME.setTextColor(ContextCompat.getColor(this, R.color.hintGray));
            usernameEditText.setTextColor(ContextCompat.getColor(this, R.color.nyoomLightYellow));
            EmailBG.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            EMAIL.setTextColor(ContextCompat.getColor(this, R.color.hintGray));
            emailEditText.setTextColor(ContextCompat.getColor(this, R.color.nyoomLightYellow));
            PasswordBG.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            PASSWORD.setTextColor(ContextCompat.getColor(this, R.color.hintGray));
            passwordEditText.setTextColor(ContextCompat.getColor(this, R.color.nyoomLightYellow));
            loginRedirect.setTextColor(ContextCompat.getColor(this, R.color.hintGray));
        } else { // light
            RegisterBG.setBackgroundResource(R.drawable.slide1blurlight);
            UsernameBG.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
            USERNAME.setTextColor(ContextCompat.getColor(this, R.color.LhintGray));
            usernameEditText.setTextColor(ContextCompat.getColor(this, R.color.nyoomDarkYellow));
            EmailBG.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
            EMAIL.setTextColor(ContextCompat.getColor(this, R.color.LhintGray));
            emailEditText.setTextColor(ContextCompat.getColor(this, R.color.nyoomDarkYellow));
            PasswordBG.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
            PASSWORD.setTextColor(ContextCompat.getColor(this, R.color.LhintGray));
            passwordEditText.setTextColor(ContextCompat.getColor(this, R.color.nyoomDarkYellow));
            loginRedirect.setTextColor(ContextCompat.getColor(this, R.color.LhintGray));
        }
    }

    private void goBack() {
        Intent goback = new Intent(Register.this, Startup.class);
        startActivity(goback);
        overridePendingTransition(R.anim.slidefade_in_top, R.anim.slidefade_out_bottom);
    }

    private void onLoginRedirect() {
        Intent redirect = new Intent(Register.this, Login.class);
        startActivity(redirect);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    };
}