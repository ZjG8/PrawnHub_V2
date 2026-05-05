package com.example.prawnhub_v2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DiagnosticsActivity extends AppCompatActivity {
    private TextView esp32Status;
    private TextView firebaseStatus;
    private TextView sensorStatus;
    private TextView syncStatus;
    private TextView lastUpdateStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!"admin".equalsIgnoreCase(SessionStore.getRole(this))) {
            finish();
            return;
        }
        setContentView(R.layout.activity_diagnostics);

        esp32Status = findViewById(R.id.esp32Status);
        firebaseStatus = findViewById(R.id.firebaseStatus);
        sensorStatus = findViewById(R.id.sensorStatus);
        syncStatus = findViewById(R.id.syncStatus);
        lastUpdateStatus = findViewById(R.id.lastUpdateStatus);
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        FirebaseDatabase.getInstance().getReference().child("control").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                esp32Status.setText("ESP32: Online");
                firebaseStatus.setText("Firebase: Connected");
                boolean pump = Boolean.TRUE.equals(snapshot.child("pump_stat").getValue(Boolean.class));
                boolean filter = Boolean.TRUE.equals(snapshot.child("filter_stat").getValue(Boolean.class));
                boolean aerator = Boolean.TRUE.equals(snapshot.child("aerator_stat").getValue(Boolean.class));
                syncStatus.setText("Database Sync: Active");
                lastUpdateStatus.setText("Pump: " + state(pump) + "  Filter: " + state(filter) + "  Aerator: " + state(aerator));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                esp32Status.setText("ESP32: Offline");
                firebaseStatus.setText("Firebase: Unavailable");
                syncStatus.setText("Database Sync: Error");
            }
        });

        FirebaseDatabase.getInstance().getReference().child("aquarium").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float temp = getFloat(snapshot, "temp_val", 0f);
                float oxygen = getFloat(snapshot, "oxygen_val", getFloat(snapshot, "do_val", 0f));
                float ph = getFloat(snapshot, "ph_val", 0f);
                sensorStatus.setText(String.format("Sensors: Temperature %.1f C, Oxygen %.1f mg/L, pH %.1f", temp, oxygen, ph));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                sensorStatus.setText("Sensors: Unavailable");
            }
        });
    }

    private float getFloat(DataSnapshot snapshot, String key, float fallback) {
        Number value = snapshot.child(key).getValue(Number.class);
        return value == null ? fallback : value.floatValue();
    }

    private String state(boolean value) {
        return value ? "On" : "Off";
    }
}
