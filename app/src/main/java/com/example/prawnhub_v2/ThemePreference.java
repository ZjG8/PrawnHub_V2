package com.example.prawnhub_v2;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public final class ThemePreference {
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_SYSTEM = "system";
    private static final String PREFS = "prawnhub_theme";
    private static final String KEY_THEME = "theme_mode";

    private ThemePreference() {
    }

    public static void applySavedTheme(Context context) {
        applyThemeMode(getThemeMode(context));
    }

    public static String getThemeMode(Context context) {
        return preferences(context).getString(KEY_THEME, THEME_SYSTEM);
    }

    public static void saveThemeMode(Context context, String mode) {
        String normalizedMode = normalizeMode(mode);
        preferences(context).edit().putString(KEY_THEME, normalizedMode).apply();
        applyThemeMode(normalizedMode);
    }

    private static SharedPreferences preferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String normalizeMode(String mode) {
        if (THEME_LIGHT.equals(mode) || THEME_DARK.equals(mode)) {
            return mode;
        }
        return THEME_SYSTEM;
    }

    private static void applyThemeMode(String mode) {
        if (THEME_LIGHT.equals(mode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (THEME_DARK.equals(mode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
