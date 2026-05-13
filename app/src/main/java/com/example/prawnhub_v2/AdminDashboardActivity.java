package com.example.prawnhub_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class AdminDashboardActivity extends androidx.appcompat.app.AppCompatActivity {
    private TextView onlineStatus;
    private TextView syncStatus;
    private TextView temperatureValue;
    private TextView salinityValue;
    private TextView waterLevelValue;
    private TextView turbidityValue;
    private TextView feedTimeValue;
    private TextView feedIntervalValue;
    private TextView midnightFeedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!"admin".equalsIgnoreCase(SessionStore.getRole(this))) {
            finish();
            return;
        }
        setContentView(R.layout.activity_admin_dashboard);

        bindViews();
        bindActions();
        listenToSensorSummary();
        listenToFeedingSettings();
    }

    private void bindViews() {
        onlineStatus = findViewById(R.id.onlineStatus);
        syncStatus = findViewById(R.id.syncStatus);
        temperatureValue = findViewById(R.id.temperatureValue);
        salinityValue = findViewById(R.id.salinityValue);
        waterLevelValue = findViewById(R.id.waterLevelValue);
        turbidityValue = findViewById(R.id.turbidityValue);
        feedTimeValue = findViewById(R.id.feedTimeValue);
        feedIntervalValue = findViewById(R.id.feedIntervalValue);
        midnightFeedValue = findViewById(R.id.midnightFeedValue);
    }

    private void bindActions() {
        View reportsCard = findViewById(R.id.reportsCard);
        View settingsCard = findViewById(R.id.settingsCard);
        View recentAlertsCard = findViewById(R.id.recentAlertsCard);
        View logoutCard = findViewById(R.id.logoutCard);

        reportsCard.setOnClickListener(v -> startActivity(new Intent(this, ReportsActivity.class)));
        settingsCard.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        recentAlertsCard.setOnClickListener(v ->
                Toast.makeText(this, "Recent alerts summary can be wired to Firebase alerts next.", Toast.LENGTH_SHORT).show());
        logoutCard.setOnClickListener(v -> logout());
    }

    private void listenToSensorSummary() {
        FirebaseDatabase.getInstance().getReference().child("aquarium").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot aquarium) {
                float temperature = getFloat(aquarium, "temp_val", 0f);
                float salinity = getFloat(aquarium, "salinity_val", getFloat(aquarium, "tds_val", 0f));
                float waterLevel = getFloat(aquarium, "water_lvl", 0f);
                float turbidity = getFloat(aquarium, "turb_val", 0f);
                renderSensorSummary(temperature, salinity, waterLevel, turbidity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onlineStatus.setText("OFFLINE");
                syncStatus.setText("SYNC ERROR");
                Toast.makeText(AdminDashboardActivity.this, "Sensor summary unavailable.", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("ShrimpHub").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot shrimpHub) {
                if (!shrimpHub.exists()) {
                    return;
                }
                float temperature = getFloat(shrimpHub, "temperature", 0f);
                float salinity = getFloat(shrimpHub, "tds", 0f);
                float waterLevel = getFloat(shrimpHub, "waterLevel", 0f);
                float turbidity = getFloat(shrimpHub, "turbidity", 0f);
                renderSensorSummary(temperature, salinity, waterLevel, turbidity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onlineStatus.setText("OFFLINE");
                syncStatus.setText("SYNC ERROR");
            }
        });
    }

    private void renderSensorSummary(float temperature, float salinity, float waterLevel, float turbidity) {
        temperatureValue.setText(String.format(Locale.US, "%.1f C", temperature));
        salinityValue.setText(String.format(Locale.US, "%.0f ppt", salinity));
        waterLevelValue.setText(String.format(Locale.US, "%.1f cm", waterLevel));
        turbidityValue.setText(String.format(Locale.US, "%.0f NTU", turbidity));
        onlineStatus.setText("ONLINE");
        syncStatus.setText("SYNCHRONIZED");
    }

    private void listenToFeedingSettings() {
        FirebaseDatabase.getInstance().getReference().child("settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String feedTime = getString(snapshot, "feed_time", "08:00");
                int interval = Math.round(getFloat(snapshot, "feed_interval_hours", 4f));
                boolean midnightFeed = Boolean.TRUE.equals(snapshot.child("midnight_feeding").getValue(Boolean.class));

                feedTimeValue.setText("Feed Time: " + feedTime);
                feedIntervalValue.setText(String.format(Locale.US, "Feeding Interval: Every %d Hours", interval));
                midnightFeedValue.setText("Midnight Auto Feed: " + (midnightFeed ? "Enabled" : "Disabled"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                feedTimeValue.setText("Feed Time: Unavailable");
                feedIntervalValue.setText("Feeding Interval: Unavailable");
                midnightFeedValue.setText("Midnight Auto Feed: Unavailable");
            }
        });
    }

    private float getFloat(DataSnapshot snapshot, String key, float fallback) {
        Object value = snapshot.child(key).getValue();
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        if (value instanceof String) {
            try {
                return Float.parseFloat((String) value);
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private String getString(DataSnapshot snapshot, String key, String fallback) {
        Object value = snapshot.child(key).getValue();
        return value == null ? fallback : String.valueOf(value);
    }

    private void logout() {
        SessionStore.clear(this);
        FirebaseAuth.getInstance().signOut();
        FirebaseDatabase.getInstance().getReference().child("auth").child("session").setValue("logged_out");
        startActivity(new Intent(this, RoleSelectionActivity.class));
        finish();
    }
}
