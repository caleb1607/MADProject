package com.example.madproject.pages.misc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.madproject.R;
import com.example.madproject.helper.BusTimesBookmarksDB;
import com.example.madproject.pages.Main;
import com.google.firebase.auth.FirebaseAuth;

public class Logout extends AppCompatActivity {
    private Button logoutButton;
    private FirebaseAuth mAuth; // Initialize Firebase Auth
    private SharedPreferences usernamepref, emailpref, userdocumentidpref; //Initialize Pref

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        logoutButton = findViewById(R.id.LogOutButton);
        logoutButton.setOnClickListener(onLogout);

        // Initialize Pref
        usernamepref = getApplicationContext().getSharedPreferences("Usernamepref", MODE_PRIVATE);
        emailpref = getApplicationContext().getSharedPreferences("Emailpref", MODE_PRIVATE);
        userdocumentidpref = getApplicationContext().getSharedPreferences("UserIDpref", MODE_PRIVATE);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private View.OnClickListener onLogout = view -> {
        logoutUser();
    };

    private void logoutUser() {
        usernamepref.edit().clear().apply();
        emailpref.edit().clear().apply();
        userdocumentidpref.edit().clear().apply();
        BusTimesBookmarksDB busTimesBookmarksDB = new BusTimesBookmarksDB(this);
        busTimesBookmarksDB.deleteAllBookmarks();
        Log.d("sex", "sex");

        mAuth.signOut();

        Toast.makeText(Logout.this, "Logout successful" , Toast.LENGTH_SHORT).show();
        Log.d("Firebase", "User logged out" );

        Intent logout = new Intent(Logout.this, Login.class);
        startActivity(logout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
