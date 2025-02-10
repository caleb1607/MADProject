package com.example.madproject.pages.settings;

import android.content.Context;

import com.example.madproject.pages.Main;

public class ThemeManager {
    private static boolean IsDarkTheme = true;
    public static boolean isDarkTheme() {
        return IsDarkTheme;
    }
    public static void toggleTheme(Context mainContext) {
        IsDarkTheme = !IsDarkTheme;
        ExplicitThemeChange(mainContext);
    }
    private static void ExplicitThemeChange(Context mainContext) {
        if (mainContext instanceof Main) { ((Main) mainContext).manageTheme(); }
    }
}
