package com.example.prawnhub_v2;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;

public final class NotificationSettings {
    private static final String PREFS = "prawnhub_notifications";
    private static final String KEY_PUSH = "push_notifications";
    private static final String KEY_VIBRATE = "vibrate";
    private static final String KEY_SOUND = "sound_alert";

    private NotificationSettings() {
    }

    public static void save(Context context, boolean pushEnabled, boolean vibrateEnabled, boolean soundEnabled) {
        preferences(context).edit()
                .putBoolean(KEY_PUSH, pushEnabled)
                .putBoolean(KEY_VIBRATE, vibrateEnabled)
                .putBoolean(KEY_SOUND, soundEnabled)
                .apply();
    }

    public static void syncFromSettings(Context context, DataSnapshot settings) {
        save(
                context,
                getBoolean(settings, KEY_PUSH, true),
                getBoolean(settings, KEY_VIBRATE, true),
                getBoolean(settings, KEY_SOUND, true)
        );
    }

    public static boolean pushEnabled(Context context) {
        return preferences(context).getBoolean(KEY_PUSH, true);
    }

    public static boolean vibrateEnabled(Context context) {
        return preferences(context).getBoolean(KEY_VIBRATE, true);
    }

    public static boolean soundEnabled(Context context) {
        return preferences(context).getBoolean(KEY_SOUND, true);
    }

    private static SharedPreferences preferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static boolean getBoolean(DataSnapshot snapshot, String key, boolean fallback) {
        Boolean value = snapshot.child(key).getValue(Boolean.class);
        return value == null ? fallback : value;
    }
}
