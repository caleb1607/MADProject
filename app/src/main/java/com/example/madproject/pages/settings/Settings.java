package com.example.madproject.pages.settings;

import android.content.Context;
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

public class Settings extends Fragment {

    View rootView;
    Context mainContext;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        Button toggleThemeButton = rootView.findViewById(R.id.ToggleThemeButton);
        toggleThemeButton.setOnClickListener(view -> toggleTheme());
        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Store the activity reference
        mainContext = context;
    }
    private void toggleTheme() {
        ThemeManager.toggleTheme(mainContext);
        Log.d("dark theme: ", Boolean.toString(ThemeManager.isDarkTheme()));
    }
}