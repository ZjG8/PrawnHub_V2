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
    private TextView pondStatusText;
    private TextView tempText;
    private TextView tempStatusText;
    private TextView salText;
    private TextView turbText;
    private TextView waterText;
    private TextView oxygenText;
    private TextView phText;
    private TextView growthPredictionText;
    private TextView recentAlertsText;
    private LinearLayout tempCard;
    private LinearLayout salCard;
    private LinearLayout turbCard;
    private LinearLayout waterCard;
    private LinearLayout oxygenCard;
    private LinearLayout phCard;
    private Switch pumpSwitch;
    private Switch filterSwitch;
    private Switch aeratorSwitch;
    private float minTemp = 28f;
    private float maxTemp = 32f;
    private int minSal = 15;
    private int maxSal = 25;
    private float maxTurb = 45f;
    private float overflowLimit = 5f;
    private float minOxygen = 5f;
    private float minPh = 6.5f;
    private float maxPh = 8.5f;
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
        pondStatusText = findViewById(R.id.pondStatusText);
        tempText = findViewById(R.id.tempText);
        tempStatusText = findViewById(R.id.tempStatusText);
        salText = findViewById(R.id.salText);
        turbText = findViewById(R.id.turbText);
        waterText = findViewById(R.id.waterText);
        oxygenText = findViewById(R.id.oxygenText);
        phText = findViewById(R.id.phText);
        growthPredictionText = findViewById(R.id.growthPredictionText);
        recentAlertsText = findViewById(R.id.recentAlertsText);
        tempCard = findViewById(R.id.tempCard);
        salCard = findViewById(R.id.salCard);
        turbCard = findViewById(R.id.turbCard);
        waterCard = findViewById(R.id.waterCard);
        oxygenCard = findViewById(R.id.oxygenCard);
        phCard = findViewById(R.id.phCard);
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
                minOxygen = getFloat(snapshot, "min_oxygen", 5f);
                minPh = getFloat(snapshot, "min_ph", 6.5f);
                maxPh = getFloat(snapshot, "max_ph", 8.5f);
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
                float oxygen = getFloat(snapshot, "oxygen_val", getFloat(snapshot, "do_val", 0f));
                float ph = getFloat(snapshot, "ph_val", 0f);
                tempText.setText(String.format("%.1f C", temp));
                salText.setText(String.format("%.1f ppm", salinity));
                turbText.setText(String.format("%.1f NTU", turbidity));
                waterText.setText(String.format("%.1f cm", water));
                oxygenText.setText(String.format("%.1f mg/L", oxygen));
                phText.setText(String.format("%.1f", ph));

                boolean tempAlert = temp < minTemp || temp > maxTemp;
                boolean salAlert = salinity < minSal || salinity > maxSal;
                boolean turbAlert = turbidity > maxTurb;
                boolean waterAlert = water > overflowLimit;
                boolean oxygenAlert = oxygen > 0f && oxygen < minOxygen;
                boolean phAlert = ph > 0f && (ph < minPh || ph > maxPh);
                setCardAlert(tempCard, tempAlert);
                setCardAlert(salCard, salAlert);
                setCardAlert(turbCard, turbAlert);
                setCardAlert(waterCard, waterAlert);
                setCardAlert(oxygenCard, oxygenAlert);
                setCardAlert(phCard, phAlert);
                renderPondHealth(tempAlert, salAlert, turbAlert, waterAlert, oxygenAlert, phAlert);
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
                boolean oxygen = Boolean.TRUE.equals(snapshot.child("oxygen_alert").getValue(Boolean.class));
                boolean turbidity = Boolean.TRUE.equals(snapshot.child("turbidity_alert").getValue(Boolean.class));
                boolean any = ammonia || overflow || temp || sal || oxygen || turbidity;
                alertBanner.setVisibility(any ? View.VISIBLE : View.GONE);
                String alertText = buildAlertText(ammonia, overflow, temp, sal, oxygen, turbidity);
                alertBanner.setText(alertText);
                recentAlertsText.setText(any ? alertText.replace("Active alerts: ", "") : "No important alerts");

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

    private void renderPondHealth(boolean temp, boolean sal, boolean turbidity, boolean water, boolean oxygen, boolean ph) {
        int alertCount = 0;
        if (temp) alertCount++;
        if (sal) alertCount++;
        if (turbidity) alertCount++;
        if (water) alertCount++;
        if (oxygen) alertCount++;
        if (ph) alertCount++;

        int score = Math.max(0, 100 - (alertCount * 18));
        growthPredictionText.setText(String.format("Estimated prawn health: %d%%\n%s",
                score,
                score >= 80 ? "Healthy growth expected" : score >= 60 ? "Monitor pond conditions" : "Critical water quality needs action"));

        if (alertCount == 0) {
            pondStatusText.setText("Healthy");
            pondStatusText.setTextColor(ContextCompat.getColor(this, R.color.status_green));
            tempStatusText.setText("Stable");
            tempStatusText.setTextColor(ContextCompat.getColor(this, R.color.status_green));
        } else if (alertCount <= 2) {
            pondStatusText.setText("Warning");
            pondStatusText.setTextColor(ContextCompat.getColor(this, R.color.alert_yellow));
            tempStatusText.setText(temp ? "Critical" : "Stable");
            tempStatusText.setTextColor(ContextCompat.getColor(this, temp ? R.color.alert_red : R.color.status_green));
        } else {
            pondStatusText.setText("Danger");
            pondStatusText.setTextColor(ContextCompat.getColor(this, R.color.alert_red));
            tempStatusText.setText(temp ? "Critical" : "Check Sensors");
            tempStatusText.setTextColor(ContextCompat.getColor(this, R.color.alert_red));
        }
    }

    private String buildAlertText(boolean ammonia, boolean overflow, boolean temp, boolean sal, boolean oxygen, boolean turbidity) {
        StringBuilder builder = new StringBuilder("Active alerts: ");
        if (ammonia) builder.append("ammonia risk ");
        if (overflow) builder.append("water level low ");
        if (temp) builder.append("temperature ");
        if (sal) builder.append("salinity ");
        if (oxygen) builder.append("oxygen critical ");
        if (turbidity) builder.append("turbidity danger ");
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
