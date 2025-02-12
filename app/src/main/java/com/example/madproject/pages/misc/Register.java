package com.example.madproject.pages.misc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

import com.example.madproject.R;
import com.example.madproject.helper.LocalStorageDB;
import com.example.madproject.pages.Main;
import com.example.madproject.pages.settings.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private Button loginRedirect, registerButton;
    private EditText usernameEditText, emailEditText, passwordEditText;
    private FirebaseAuth mAuth; // Initialize Firebase Auth
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginRedirect = findViewById(R.id.LoginRedirect);
        loginRedirect.setOnClickListener(view -> onLoginRedirect());
        registerButton = findViewById(R.id.RegisterButton);
        registerButton.setOnClickListener(onRegister);
        usernameEditText = findViewById(R.id.Username_InputField);
        emailEditText = findViewById(R.id.Email_InputField2);
        passwordEditText = findViewById(R.id.Password_InputField2);
        ImageView DownButton = findViewById(R.id.DownButton2);
        DownButton.setOnClickListener(view -> goBack());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void onLoginRedirect() {
        Intent redirect = new Intent(Register.this, Login.class);
        startActivity(redirect);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    };

    private View.OnClickListener onRegister = view -> {
        registerUser();
    };



    private void registerUser() {
        // Get input from the EditText fields
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            // Sign in the user with Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success
                            LocalStorageDB localStorageDB = new LocalStorageDB(this);
                            localStorageDB.insertOrUpdate("LoginToken", "1");

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Store the username in Firestore
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("uid", user.getUid());  // Store UID
                                userData.put("email", email);
                                userData.put("username", username);

                                db.collection("users").document(user.getUid())
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data added"))
                                        .addOnFailureListener(e -> Log.w("Firestore", "Error adding user", e));
                            }

                            // Navigate to the next activity (e.g., HomeActivity)
                            // startActivity(new Intent(Login.this, HomeActivity.class));
                            Intent Signup = new Intent(Register.this, Main.class);
                            startActivity(Signup);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();

                        } else {
                            // Sign in failure
                            Toast.makeText(Register.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            Log.e("Firebase", "Authentication failed", task.getException());
                        }
                    });
        }
    }
}