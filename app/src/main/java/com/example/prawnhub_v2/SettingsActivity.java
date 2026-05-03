package com.example.prawnhub_v2;

import android.content.Intent;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText maxTempInput;
    private EditText minTempInput;
    private EditText minSalInput;
    private EditText maxSalInput;
    private EditText maxTurbInput;
    private EditText feedTimeInput;
    private EditText overflowInput;
    private EditText targetHarvestInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupBottomNav();

        settingsRef = FirebaseDatabase.getInstance().getReference().child("settings");
        bindViews();
        loadSettings();
    }

    private void bindViews() {
        maxTempInput = findViewById(R.id.maxTempInput);
        minTempInput = findViewById(R.id.minTempInput);
        minSalInput = findViewById(R.id.minSalInput);
        maxSalInput = findViewById(R.id.maxSalInput);
        maxTurbInput = findViewById(R.id.maxTurbInput);
        feedTimeInput = findViewById(R.id.feedTimeInput);
        overflowInput = findViewById(R.id.overflowInput);
        targetHarvestInput = findViewById(R.id.targetHarvestInput);
        Button saveButton = findViewById(R.id.saveSettingsButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        feedTimeInput.setOnClickListener(view -> showTimePicker());
        saveButton.setOnClickListener(view -> saveSettings());
        logoutButton.setOnClickListener(view -> logout());
    }

    private void loadSettings() {
        settingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setText(maxTempInput, snapshot, "max_temp", "32.0");
                setText(minTempInput, snapshot, "min_temp", "28.0");
                setText(minSalInput, snapshot, "min_sal", "15");
                setText(maxSalInput, snapshot, "max_sal", "25");
                setText(maxTurbInput, snapshot, "max_turb", "45.0");
                setText(feedTimeInput, snapshot, "feed_time", "08:00");
                setText(overflowInput, snapshot, "overflow_limit", "5.0");
                setText(targetHarvestInput, snapshot, "target_harvest_days", "75");
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
        updates.put("overflow_limit", Float.parseFloat(overflowInput.getText().toString()));
        updates.put("target_harvest_days", Integer.parseInt(targetHarvestInput.getText().toString()));

        settingsRef.updateChildren(updates)
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
                && !TextUtils.isEmpty(overflowInput.getText())
                && !TextUtils.isEmpty(targetHarvestInput.getText());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
