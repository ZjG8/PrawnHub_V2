package com.example.prawnhub_v2;

import android.app.Application;

public class PrawnHubApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ThemePreference.applySavedTheme(this);
    }
}
