package com.example.prawnhub_v2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends BaseNavActivity {
    private DatabaseReference database;
    private TextView alertBanner;
    private TextView tempText;
    private TextView salText;
    private TextView turbText;
    private TextView waterText;
    private LinearLayout tempCard;
    private LinearLayout salCard;
    private LinearLayout turbCard;
    private LinearLayout waterCard;
    private Switch pumpSwitch;
    private Switch filterSwitch;
    private Switch aeratorSwitch;
    private float minTemp = 28f;
    private float maxTemp = 32f;
    private int minSal = 15;
    private int maxSal = 25;
    private float maxTurb = 45f;
    private float overflowLimit = 5f;
    private boolean updatingSwitches = false;
    private boolean ammoniaWasActive = false;
    private boolean overflowWasActive = false;
    private boolean tempWasActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setupBottomNav();

        database = FirebaseDatabase.getInstance().getReference();
        bindViews();
        requestNotificationPermission();
        listenToSettings();
        listenToAquarium();
        listenToControl();
        listenToAlerts();
        bindControls();
    }

    private void bindViews() {
        alertBanner = findViewById(R.id.alertBanner);
        tempText = findViewById(R.id.tempText);
        salText = findViewById(R.id.salText);
        turbText = findViewById(R.id.turbText);
        waterText = findViewById(R.id.waterText);
        tempCard = findViewById(R.id.tempCard);
        salCard = findViewById(R.id.salCard);
        turbCard = findViewById(R.id.turbCard);
        waterCard = findViewById(R.id.waterCard);
        pumpSwitch = findViewById(R.id.pumpSwitch);
        filterSwitch = findViewById(R.id.filterSwitch);
        aeratorSwitch = findViewById(R.id.aeratorSwitch);
        Button feedButton = findViewById(R.id.feedButton);
        feedButton.setOnClickListener(view -> database.child("control").child("motor_trig").setValue(true)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Feed command sent.", Toast.LENGTH_SHORT).show()));
    }

    private void bindControls() {
        CompoundButton.OnCheckedChangeListener listener = (button, checked) -> {
            if (updatingSwitches) {
                return;
            }
            if (button.getId() == R.id.pumpSwitch) {
                database.child("control").child("pump_stat").setValue(checked);
            } else if (button.getId() == R.id.filterSwitch) {
                database.child("control").child("filter_stat").setValue(checked);
            } else if (button.getId() == R.id.aeratorSwitch) {
                database.child("control").child("aerator_stat").setValue(checked);
            }
        };
        pumpSwitch.setOnCheckedChangeListener(listener);
        filterSwitch.setOnCheckedChangeListener(listener);
        aeratorSwitch.setOnCheckedChangeListener(listener);
    }

    private void listenToSettings() {
        database.child("settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                maxTemp = getFloat(snapshot, "max_temp", 32f);
                minTemp = getFloat(snapshot, "min_temp", 28f);
                minSal = getInt(snapshot, "min_sal", 15);
                maxSal = getInt(snapshot, "max_sal", 25);
                maxTurb = getFloat(snapshot, "max_turb", 45f);
                overflowLimit = getFloat(snapshot, "overflow_limit", 5f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Settings listener failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenToAquarium() {
        database.child("aquarium").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float temp = getFloat(snapshot, "temp_val", 0f);
                float salinity = getFloat(snapshot, "tds_val", 0f);
                float turbidity = getFloat(snapshot, "turb_val", 0f);
                float water = getFloat(snapshot, "water_lvl", 0f);
                tempText.setText(String.format("%.1f C", temp));
                salText.setText(String.format("%.1f ppm", salinity));
                turbText.setText(String.format("%.1f NTU", turbidity));
                waterText.setText(String.format("%.1f cm", water));
                setCardAlert(tempCard, temp < minTemp || temp > maxTemp);
                setCardAlert(salCard, salinity < minSal || salinity > maxSal);
                setCardAlert(turbCard, turbidity > maxTurb);
                setCardAlert(waterCard, water > overflowLimit);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Aquarium listener failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenToControl() {
        database.child("control").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updatingSwitches = true;
                pumpSwitch.setChecked(Boolean.TRUE.equals(snapshot.child("pump_stat").getValue(Boolean.class)));
                filterSwitch.setChecked(Boolean.TRUE.equals(snapshot.child("filter_stat").getValue(Boolean.class)));
                aeratorSwitch.setChecked(Boolean.TRUE.equals(snapshot.child("aerator_stat").getValue(Boolean.class)));
                updatingSwitches = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                updatingSwitches = false;
            }
        });
    }

    private void listenToAlerts() {
        database.child("alerts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean ammonia = Boolean.TRUE.equals(snapshot.child("ammonia_risk").getValue(Boolean.class));
                boolean overflow = Boolean.TRUE.equals(snapshot.child("overflow_risk").getValue(Boolean.class));
                boolean temp = Boolean.TRUE.equals(snapshot.child("temp_alert").getValue(Boolean.class));
                boolean sal = Boolean.TRUE.equals(snapshot.child("sal_alert").getValue(Boolean.class));
                boolean any = ammonia || overflow || temp || sal;
                alertBanner.setVisibility(any ? View.VISIBLE : View.GONE);
                alertBanner.setText(buildAlertText(ammonia, overflow, temp, sal));

                if (ammonia && !ammoniaWasActive) {
                    NotificationHelper.showAlert(DashboardActivity.this, "ShrimpHub Alert", "ShrimpHub: High Ammonia Risk! Filter activated.", 1001);
                }
                if (overflow && !overflowWasActive) {
                    NotificationHelper.showAlert(DashboardActivity.this, "ShrimpHub Alert", "ShrimpHub: Water level low! Pump activated.", 1002);
                }
                if (temp && !tempWasActive) {
                    NotificationHelper.showAlert(DashboardActivity.this, "ShrimpHub Alert", "ShrimpHub: Temperature out of safe range!", 1003);
                }
                ammoniaWasActive = ammonia;
                overflowWasActive = overflow;
                tempWasActive = temp;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Alert listener failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String buildAlertText(boolean ammonia, boolean overflow, boolean temp, boolean sal) {
        StringBuilder builder = new StringBuilder("Active alerts: ");
        if (ammonia) builder.append("ammonia risk ");
        if (overflow) builder.append("water level ");
        if (temp) builder.append("temperature ");
        if (sal) builder.append("salinity ");
        return builder.toString().trim();
    }

    private void setCardAlert(LinearLayout card, boolean alert) {
        card.setBackgroundResource(alert ? R.drawable.card_alert : R.drawable.card_background);
    }

    private float getFloat(DataSnapshot snapshot, String key, float fallback) {
        Number value = snapshot.child(key).getValue(Number.class);
        return value == null ? fallback : value.floatValue();
    }

    private int getInt(DataSnapshot snapshot, String key, int fallback) {
        Number value = snapshot.child(key).getValue(Number.class);
        return value == null ? fallback : value.intValue();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 44);
        }
    }
}
