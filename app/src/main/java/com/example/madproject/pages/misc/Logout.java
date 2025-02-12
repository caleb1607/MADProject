package com.example.madproject.pages.misc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.madproject.R;
import com.example.madproject.pages.Main;
import com.google.firebase.auth.FirebaseAuth;

public class Logout extends AppCompatActivity {
    private Button logoutButton;
    private FirebaseAuth mAuth; // Initialize Firebase Auth



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);


        logoutButton = findViewById(R.id.LogOutButton);
        logoutButton.setOnClickListener(onLogout);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }







    private View.OnClickListener onLogout = view -> {
        logoutUser();
    };



    private void logoutUser() {
        Toast.makeText(Logout.this, "Logout successful" , Toast.LENGTH_SHORT).show();
        Log.d("Firebase", "User logged out" );
        mAuth.signOut();

        Intent logout = new Intent(Logout.this, Login.class);
        startActivity(logout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
