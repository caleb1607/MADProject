package com.example.madproject.pages.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.madproject.R;
import com.example.madproject.helper.LocalStorageDB;
import com.example.madproject.pages.Main;
import com.example.madproject.pages.misc.Login;
import com.example.madproject.pages.misc.Startup;
import com.github.chrisbanes.photoview.PhotoView;

public class Settings extends Fragment {

    View rootView;
    Context mainContext;
    // views
    Button toggleThemeButton;
    Button logoutButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        toggleThemeButton = rootView.findViewById(R.id.ToggleThemeButton);
        toggleThemeButton.setOnClickListener(view -> toggleTheme());
        logoutButton = rootView.findViewById(R.id.LogOutButton);
        logoutButton.setOnClickListener(view -> onLogout());
        PhotoView photoView = rootView.findViewById(R.id.photoView);
        photoView.setImageResource(R.drawable.your_high_res_image);
        // manage theme
        manageTheme();
        return rootView;
    }
    public void manageTheme() {
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LmainBackground));
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Store the activity reference
        mainContext = context;
    }
    private void toggleTheme() {
        LocalStorageDB localStorageDB = new LocalStorageDB(getContext());
        if (localStorageDB.getValue("theme_preference").equals("light")) {
            localStorageDB.insertOrUpdate("theme_preference", "dark");
        } else {
            localStorageDB.insertOrUpdate("theme_preference", "light");
        }
        ThemeManager.setTheme(mainContext, !ThemeManager.isDarkTheme());
        manageTheme();
    }
    private void onLogout() {
        LocalStorageDB localStorageDB = new LocalStorageDB(getContext());
        localStorageDB.insertOrUpdate("LoginToken", "0");
        Intent logout = new Intent(mainContext, Login.class);
        startActivity(logout);
        if (mainContext instanceof Activity) {
            Activity activity = (Activity) mainContext;
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            activity.finish();
        }
    }
}