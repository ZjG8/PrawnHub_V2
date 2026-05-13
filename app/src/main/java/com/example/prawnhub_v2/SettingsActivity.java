package com.example.prawnhub_v2;

import android.content.Intent;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SettingsActivity extends BaseNavActivity {
    private DatabaseReference settingsRef;
    private DatabaseReference thresholdsRef;
    private EditText maxTempInput;
    private EditText minTempInput;
    private EditText minSalInput;
    private EditText maxSalInput;
    private EditText maxTurbInput;
    private EditText feedTimeInput;
    private EditText feedIntervalInput;
    private EditText overflowInput;
    private Switch soundSwitch;
    private Switch vibrateSwitch;
    private Switch pushSwitch;
    private Switch midnightFeedSwitch;
    private Button saveButton;
    private Button logoutButton;
    private Button aboutButton;
    private Button backButton;
    private TextView settingsTitle;
    private LinearLayout bottomNavContainer;
    private boolean isAdminMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        isAdminMode = "admin".equalsIgnoreCase(SessionStore.getRole(this));
        if (!isAdminMode) {
            setupBottomNav();
        }
        settingsRef = FirebaseDatabase.getInstance().getReference().child("settings");
        thresholdsRef = FirebaseDatabase.getInstance().getReference().child("thresholds");
        bindViews();
        applyRoleMode();
        loadSettings();
    }

    private void bindViews() {
        maxTempInput = findViewById(R.id.maxTempInput);
        minTempInput = findViewById(R.id.minTempInput);
        minSalInput = findViewById(R.id.minSalInput);
        maxSalInput = findViewById(R.id.maxSalInput);
        maxTurbInput = findViewById(R.id.maxTurbInput);
        feedTimeInput = findViewById(R.id.feedTimeInput);
        feedIntervalInput = findViewById(R.id.feedIntervalInput);
        overflowInput = findViewById(R.id.overflowInput);
        soundSwitch = findViewById(R.id.soundSwitch);
        vibrateSwitch = findViewById(R.id.vibrateSwitch);
        pushSwitch = findViewById(R.id.pushSwitch);
        midnightFeedSwitch = findViewById(R.id.midnightFeedSwitch);
        settingsTitle = findViewById(R.id.settingsTitle);
        backButton = findViewById(R.id.backButton);
        bottomNavContainer = findViewById(R.id.bottomNavContainer);
        TextView accountEmailText = findViewById(R.id.accountEmailText);
        if (isAdminMode && FirebaseAuth.getInstance().getCurrentUser() != null) {
            accountEmailText.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        } else {
            accountEmailText.setText("Farmer");
        }
        saveButton = findViewById(R.id.saveSettingsButton);
        logoutButton = findViewById(R.id.logoutButton);
        aboutButton = findViewById(R.id.aboutButton);

        feedTimeInput.setOnClickListener(view -> showTimePicker());
        backButton.setOnClickListener(view -> finish());
        saveButton.setOnClickListener(view -> saveSettings());
        logoutButton.setOnClickListener(view -> logout());
        aboutButton.setOnClickListener(view -> startActivity(new Intent(this, AboutActivity.class)));
    }

    private void applyRoleMode() {
        settingsTitle.setText(isAdminMode ? "System Settings" : "Monitoring Settings");
        backButton.setVisibility(isAdminMode ? View.VISIBLE : View.GONE);
        if (bottomNavContainer != null) {
            bottomNavContainer.setVisibility(isAdminMode ? View.GONE : View.VISIBLE);
        }
        saveButton.setVisibility(isAdminMode ? android.view.View.VISIBLE : android.view.View.GONE);
        logoutButton.setText(isAdminMode ? "Logout" : "Back to Roles");
        setEditable(maxTempInput, isAdminMode);
        setEditable(minTempInput, isAdminMode);
        setEditable(minSalInput, isAdminMode);
        setEditable(maxSalInput, isAdminMode);
        setEditable(maxTurbInput, isAdminMode);
        setEditable(feedTimeInput, isAdminMode);
        setEditable(feedIntervalInput, isAdminMode);
        setEditable(overflowInput, isAdminMode);
        soundSwitch.setEnabled(isAdminMode);
        vibrateSwitch.setEnabled(isAdminMode);
        pushSwitch.setEnabled(isAdminMode);
        midnightFeedSwitch.setEnabled(isAdminMode);
    }

    private void setEditable(EditText input, boolean enabled) {
        input.setEnabled(enabled);
        input.setFocusable(enabled);
        input.setFocusableInTouchMode(enabled);
        input.setClickable(enabled);
    }

    private void loadSettings() {
        settingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setText(maxTempInput, snapshot, "max_temp", getNestedValue(snapshot, "temperature", "max", "32.0"));
                setText(minTempInput, snapshot, "min_temp", getNestedValue(snapshot, "temperature", "min", "28.0"));
                setText(minSalInput, snapshot, "min_sal", getNestedValue(snapshot, "salinity", "min", "15"));
                setText(maxSalInput, snapshot, "max_sal", getNestedValue(snapshot, "salinity", "max", "25"));
                setText(maxTurbInput, snapshot, "max_turb", getNestedValue(snapshot, "turbidity", "max", "45.0"));
                setText(feedTimeInput, snapshot, "feed_time", "08:00");
                setText(feedIntervalInput, snapshot, "feed_interval_hours", "4");
                setText(overflowInput, snapshot, "overflow_limit", getNestedValue(snapshot, "water_level", "max", "5.0"));
                soundSwitch.setChecked(getBoolean(snapshot, "sound_alert", true));
                vibrateSwitch.setChecked(getBoolean(snapshot, "vibrate", true));
                pushSwitch.setChecked(getBoolean(snapshot, "push_notifications", true));
                midnightFeedSwitch.setChecked(getBoolean(snapshot, "midnight_feeding", false));

                thresholdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot thresholdSnapshot) {
                        setText(minTempInput, thresholdSnapshot, "temperature/min", String.valueOf(minTempInput.getText()));
                        setText(maxTempInput, thresholdSnapshot, "temperature/max", String.valueOf(maxTempInput.getText()));
                        setText(minSalInput, thresholdSnapshot, "salinity/min", String.valueOf(minSalInput.getText()));
                        setText(maxSalInput, thresholdSnapshot, "salinity/max", String.valueOf(maxSalInput.getText()));
                        setText(maxTurbInput, thresholdSnapshot, "turbidity/max", String.valueOf(maxTurbInput.getText()));
                        setText(overflowInput, thresholdSnapshot, "water_level/max", String.valueOf(overflowInput.getText()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, "Could not load settings.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setText(EditText input, DataSnapshot snapshot, String key, String fallback) {
        Object value = snapshot.child(key).getValue();
        input.setText(value == null ? fallback : String.valueOf(value));
    }

    private boolean getBoolean(DataSnapshot snapshot, String key, boolean fallback) {
        Boolean value = snapshot.child(key).getValue(Boolean.class);
        return value == null ? fallback : value;
    }

    private String getNestedValue(DataSnapshot snapshot, String category, String child, String fallback) {
        Object nested = snapshot.child(category).child(child).getValue();
        return nested == null ? fallback : String.valueOf(nested);
    }

    private void showTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> feedTimeInput.setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute)),
                8,
                0,
                true
        );
        dialog.show();
    }

    private void saveSettings() {
        if (!validateInputs()) {
            Toast.makeText(this, "Fill in every setting before saving.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("max_temp", Float.parseFloat(maxTempInput.getText().toString()));
        updates.put("min_temp", Float.parseFloat(minTempInput.getText().toString()));
        updates.put("min_sal", Integer.parseInt(minSalInput.getText().toString()));
        updates.put("max_sal", Integer.parseInt(maxSalInput.getText().toString()));
        updates.put("max_turb", Float.parseFloat(maxTurbInput.getText().toString()));
        updates.put("feed_time", feedTimeInput.getText().toString());
        updates.put("feed_interval_hours", Integer.parseInt(feedIntervalInput.getText().toString()));
        updates.put("overflow_limit", Float.parseFloat(overflowInput.getText().toString()));
        updates.put("sound_alert", soundSwitch.isChecked());
        updates.put("vibrate", vibrateSwitch.isChecked());
        updates.put("push_notifications", pushSwitch.isChecked());
        updates.put("midnight_feeding", midnightFeedSwitch.isChecked());
        updates.put("esp32_feeding_scheduler", midnightFeedSwitch.isChecked() ? "daily_midnight_enabled" : "manual_interval");

        Map<String, Object> thresholdUpdates = new HashMap<>();
        thresholdUpdates.put("temperature/min", Float.parseFloat(minTempInput.getText().toString()));
        thresholdUpdates.put("temperature/max", Float.parseFloat(maxTempInput.getText().toString()));
        thresholdUpdates.put("salinity/min", Integer.parseInt(minSalInput.getText().toString()));
        thresholdUpdates.put("salinity/max", Integer.parseInt(maxSalInput.getText().toString()));
        thresholdUpdates.put("turbidity/max", Float.parseFloat(maxTurbInput.getText().toString()));
        thresholdUpdates.put("water_level/max", Float.parseFloat(overflowInput.getText().toString()));

        settingsRef.updateChildren(updates);
        thresholdsRef.updateChildren(thresholdUpdates)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error -> Toast.makeText(this, "Save failed: " + error.getMessage(), Toast.LENGTH_LONG).show());
    }

    private boolean validateInputs() {
        return !TextUtils.isEmpty(maxTempInput.getText())
                && !TextUtils.isEmpty(minTempInput.getText())
                && !TextUtils.isEmpty(minSalInput.getText())
                && !TextUtils.isEmpty(maxSalInput.getText())
                && !TextUtils.isEmpty(maxTurbInput.getText())
                && !TextUtils.isEmpty(feedTimeInput.getText())
                && !TextUtils.isEmpty(feedIntervalInput.getText())
                && !TextUtils.isEmpty(overflowInput.getText());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        SessionStore.clear(this);
        Intent intent = new Intent(this, RoleSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
