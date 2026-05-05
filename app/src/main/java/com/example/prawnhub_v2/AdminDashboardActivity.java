package com.example.prawnhub_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardActivity extends androidx.appcompat.app.AppCompatActivity {
    private TextView syncText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!"admin".equalsIgnoreCase(SessionStore.getRole(this))) {
            finish();
            return;
        }
        setContentView(R.layout.activity_admin_dashboard);

        syncText = findViewById(R.id.adminSyncText);
        bindButtons();
        listenToStatus();
    }

    private void bindButtons() {
        Button manageUsers = findViewById(R.id.manageUsersButton);
        Button diagnostics = findViewById(R.id.diagnosticsButton);
        Button reports = findViewById(R.id.reportsButton);
        Button thresholds = findViewById(R.id.thresholdsButton);
        Button systemSettings = findViewById(R.id.systemSettingsButton);
        Button logout = findViewById(R.id.logoutButton);

        manageUsers.setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));
        diagnostics.setOnClickListener(v -> startActivity(new Intent(this, DiagnosticsActivity.class)));
        reports.setOnClickListener(v -> startActivity(new Intent(this, ReportsActivity.class)));
        thresholds.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        systemSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        logout.setOnClickListener(v -> {
            SessionStore.clear(this);
            FirebaseDatabase.getInstance().getReference().child("auth").child("session").setValue("logged_out");
            startActivity(new Intent(this, RoleSelectionActivity.class));
            finish();
        });
    }

    private void listenToStatus() {
        FirebaseDatabase.getInstance().getReference().child("control").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean pump = Boolean.TRUE.equals(snapshot.child("pump_stat").getValue(Boolean.class));
                boolean filter = Boolean.TRUE.equals(snapshot.child("filter_stat").getValue(Boolean.class));
                boolean aerator = Boolean.TRUE.equals(snapshot.child("aerator_stat").getValue(Boolean.class));
                syncText.setText("Firebase Sync Active • ESP32 " + (pump || filter || aerator ? "Connected" : "Standby"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                syncText.setText("Firebase Sync Status unavailable");
            }
        });
    }
}
