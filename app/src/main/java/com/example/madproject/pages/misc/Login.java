package com.example.madproject.pages.misc;

import androidx.appcompat.app.AppCompatActivity;

import com.example.madproject.R;
import com.example.madproject.pages.Main;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        registerRedirect.setOnClickListener(onRegisterRedirect);
        loginButton = findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(onLogin);
        emailEditText = findViewById(R.id.Email_InputField);
        passwordEditText = findViewById(R.id.Password_InputField);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
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



    private View.OnClickListener onRegisterRedirect = view -> {
        Intent redirect = new Intent(Login.this, Register.class);
        startActivity(redirect);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    };
    private View.OnClickListener onLogin = view -> {
        //Intent login = new Intent(Login.this, main.class);
        //startActivity(login);
        loginUser();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    };



    private void loginUser() {
        // Get input from the EditText fields
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in the user with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Login.this, "Login successful: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        Log.d("Firebase", "User logged in: " + user.getEmail());

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