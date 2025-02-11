package com.example.madproject.pages.misc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.madproject.R;
import com.example.madproject.helper.LocalStorageDB;
import com.example.madproject.pages.Main;
import com.example.madproject.pages.settings.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private Button registerRedirect;
    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth; // Initialize Firebase Auth



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerRedirect = findViewById(R.id.RegisterRedirect);
        registerRedirect.setOnClickListener(view -> onRegisterRedirect());
        loginButton = findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(onLogin);
        emailEditText = findViewById(R.id.Email_InputField);
        passwordEditText = findViewById(R.id.Password_InputField);
        ImageView DownButton = findViewById(R.id.DownButton);
        DownButton.setOnClickListener(view -> goBack());
        // manage theme
        manageTheme();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void manageTheme() {
        FrameLayout LoginBG = findViewById(R.id.LoginBG);
        View EmailBG = findViewById(R.id.EmailBG);
        TextView EMAIL = findViewById(R.id.EMAIL);
        View PasswordBG = findViewById(R.id.PasswordBG);
        TextView PASSWORD = findViewById(R.id.PASSWORD);
        if (ThemeManager.isDarkTheme()) {
            LoginBG.setBackgroundResource(R.drawable.slide1blurdark);
            EmailBG.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            EMAIL.setTextColor(ContextCompat.getColor(this, R.color.hintGray));
            emailEditText.setTextColor(ContextCompat.getColor(this, R.color.nyoomLightYellow));
            PasswordBG.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.black));
            PASSWORD.setTextColor(ContextCompat.getColor(this, R.color.hintGray));
            passwordEditText.setTextColor(ContextCompat.getColor(this, R.color.nyoomLightYellow));
            registerRedirect.setTextColor(ContextCompat.getColor(this, R.color.hintGray));
        } else { // light
            LoginBG.setBackgroundResource(R.drawable.slide1blurlight);
            EmailBG.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
            EMAIL.setTextColor(ContextCompat.getColor(this, R.color.LhintGray));
            emailEditText.setTextColor(ContextCompat.getColor(this, R.color.nyoomDarkYellow));
            PasswordBG.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
            PASSWORD.setTextColor(ContextCompat.getColor(this, R.color.LhintGray));
            passwordEditText.setTextColor(ContextCompat.getColor(this, R.color.nyoomDarkYellow));
            registerRedirect.setTextColor(ContextCompat.getColor(this, R.color.LhintGray));
        }
    }

    private void goBack() {
        Intent goback = new Intent(Login.this, Startup.class);
        startActivity(goback);
        overridePendingTransition(R.anim.slidefade_in_top, R.anim.slidefade_out_bottom);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    //private void updateUI(FirebaseUser currentUser) {
    //}



    private void onRegisterRedirect() {
        Intent redirect = new Intent(Login.this, Register.class);
        startActivity(redirect);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    };
    private View.OnClickListener onLogin = view -> {
        loginUser();
    };



    private void loginUser() {
        // Get input from the EditText fields
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Sign in the user with Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success
                            LocalStorageDB localStorageDB = new LocalStorageDB(this);
                            localStorageDB.insertOrUpdate("LoginToken", "1");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this, "Login successful: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            Log.d("Firebase", "User logged in: " + user.getEmail());
                            Intent login = new Intent(Login.this, Main.class);
                            startActivity(login);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                            // Navigate to the next activity (e.g., HomeActivity)
                            // startActivity(new Intent(Login.this, HomeActivity.class));
                        } else {
                            // Sign in failure
                            Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            Log.e("Firebase", "Authentication failed", task.getException());
                        }
                    });
        }


    }
}