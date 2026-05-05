package com.example.prawnhub_v2;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionStore {
    private static final String PREFS = "prawnhub_session";
    private static final String KEY_ROLE = "role";

    private SessionStore() {
    }

    public static void setRole(Context context, String role) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_ROLE, role == null ? "" : role).apply();
    }

    public static String getRole(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String role = prefs.getString(KEY_ROLE, "");
        return role == null ? "" : role;
    }

    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_ROLE).apply();
    }
}
