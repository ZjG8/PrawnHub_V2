package com.example.prawnhub_v2;

import android.Manifest;
import android.content.Intent;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends BaseNavActivity {
    private DatabaseReference database;
    private DatabaseReference controlRef;
    private DatabaseReference feederRef;
    private TextView alertBanner;
    private TextView pondStatusText;
    private TextView tempText;
    private TextView tempStatusText;
    private TextView salText;
    private TextView salStatusText;
    private TextView turbText;
    private TextView turbStatusText;
    private TextView waterText;
    private TextView waterStatusText;
    private TextView growthPredictionText;
    private TextView recentAlertsText;
    private TextView feedingStatusText;
    private TextView lastFeedTimeText;
    private TextView historyPreviewText;
    private TextView userSummaryText;
    private TextView settingsPreviewText;
    private LinearLayout adminSection;
    private LinearLayout tempCard;
    private LinearLayout salCard;
    private LinearLayout turbCard;
    private LinearLayout waterCard;
    private Switch pump1Switch;
    private Switch pump2Switch;
    private Switch pump3Switch;
    private Button feedNowButton;
    private Button stopFeedButton;
    private Button manageUsersButton;
    private Button exportReportButton;
    private float minTemp = 28f;
    private float maxTemp = 32f;
    private int minSal = 15;
    private int maxSal = 25;
    private float maxTurb = 45f;
    private float overflowLimit = 5f;
    private boolean updatingSwitches = false;
    private boolean feederRunning = false;
    private boolean overflowWasActive = false;
    private boolean tempWasActive = false;
    private String currentRole = "";
    private String feederStatusLine = "Ready to Feed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setupBottomNav();

        database = FirebaseDatabase.getInstance().getReference();
        controlRef = database.child("ShrimpHub").child("control");
        feederRef = database.child("ShrimpHub").child("feeder");
        currentRole = SessionStore.getRole(this);
        bindViews();
        renderRoleUi();
        requestNotificationPermission();
        listenToSettings();
        listenToAquarium();
        listenToControl();
        listenToFeeder();
        listenToHistoryPreview();
        listenToAdminSummary();
        bindControls();
    }

    private void bindViews() {
        alertBanner = findViewById(R.id.alertBanner);
        pondStatusText = findViewById(R.id.pondStatusText);
        tempText = findViewById(R.id.tempText);
        tempStatusText = findViewById(R.id.tempStatusText);
        salText = findViewById(R.id.salText);
        salStatusText = findViewById(R.id.salStatusText);
        turbText = findViewById(R.id.turbText);
        turbStatusText = findViewById(R.id.turbStatusText);
        waterText = findViewById(R.id.waterText);
        waterStatusText = findViewById(R.id.waterStatusText);
        growthPredictionText = findViewById(R.id.growthPredictionText);
        recentAlertsText = findViewById(R.id.recentAlertsText);
        feedingStatusText = findViewById(R.id.feedingStatusText);
        lastFeedTimeText = findViewById(R.id.lastFeedTimeText);
        historyPreviewText = findViewById(R.id.historyPreviewText);
        userSummaryText = findViewById(R.id.userSummaryText);
        settingsPreviewText = findViewById(R.id.settingsPreviewText);
        adminSection = findViewById(R.id.adminSection);
        tempCard = findViewById(R.id.tempCard);
        salCard = findViewById(R.id.salCard);
        turbCard = findViewById(R.id.turbCard);
        waterCard = findViewById(R.id.waterCard);
        pump1Switch = findViewById(R.id.pump1Switch);
        pump2Switch = findViewById(R.id.pump2Switch);
        pump3Switch = findViewById(R.id.pump3Switch);
        feedNowButton = findViewById(R.id.feedNowButton);
        stopFeedButton = findViewById(R.id.stopFeedButton);
        manageUsersButton = findViewById(R.id.manageUsersButton);
        exportReportButton = findViewById(R.id.exportReportButton);
    }

    private void renderRoleUi() {
        boolean admin = "admin".equalsIgnoreCase(currentRole);
        adminSection.setVisibility(admin ? View.VISIBLE : View.GONE);
    }

    private void bindControls() {
        CompoundButton.OnCheckedChangeListener listener = (button, checked) -> {
            if (updatingSwitches) {
                return;
            }
            if (button.getId() == R.id.pump1Switch) {
                writePumpState("pump1", checked);
            } else if (button.getId() == R.id.pump2Switch) {
                writePumpState("pump2", checked);
            } else if (button.getId() == R.id.pump3Switch) {
                writePumpState("pump3", checked);
            }
        };
        pump1Switch.setOnCheckedChangeListener(listener);
        pump2Switch.setOnCheckedChangeListener(listener);
        pump3Switch.setOnCheckedChangeListener(listener);
        feedNowButton.setOnClickListener(view -> {
            if (feederRunning) {
                Toast.makeText(this, "Feeder is already running.", Toast.LENGTH_SHORT).show();
                return;
            }
            feedNowButton.setEnabled(false);
            writeFeederState(true, true, "Feed request failed.");
        });
        stopFeedButton.setOnClickListener(view -> {
            writeFeederState(false, false, "Stop feed failed.");
        });
        manageUsersButton.setOnClickListener(view -> startActivity(new Intent(this, ManageUsersActivity.class)));
        exportReportButton.setOnClickListener(view -> startActivity(new Intent(this, ReportsActivity.class)));
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
                NotificationSettings.syncFromSettings(DashboardActivity.this, snapshot);
                settingsPreviewText.setText(String.format(Locale.US,
                        "Temp %.1f-%.1f C  |  TDS %d-%d ppm  |  Turbidity max %.1f NTU  |  Overflow %.1f ft",
                        minTemp, maxTemp, minSal, maxSal, maxTurb, overflowLimit));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Settings listener failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenToAquarium() {
        database.child("ShrimpHub").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot shrimpHub) {
                if (!shrimpHub.hasChild("temperature")
                        && !shrimpHub.hasChild("tds")
                        && !shrimpHub.hasChild("turbidity")
                        && !shrimpHub.hasChild("waterLevel")) {
                    return;
                }
                float temp = getFloat(shrimpHub, "temperature", 0f);
                float salinity = getFloat(shrimpHub, "tds", 0f);
                float turbidity = getFloat(shrimpHub, "turbidity", 0f);
                float water = getFloat(shrimpHub, "waterLevel", 0f);
                renderSensorValues(temp, salinity, turbidity, water);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "ShrimpHub listener failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenToFeeder() {
        feederRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feederRunning = Boolean.TRUE.equals(snapshot.child("feeding").getValue(Boolean.class));
                String lastFeedTime = getString(snapshot, "lastFeedTime", "--");

                feederStatusLine = feederRunning ? "Feeding..." : "Ready to Feed";
                feedingStatusText.setText(feederStatusLine);
                feedingStatusText.setTextColor(ContextCompat.getColor(DashboardActivity.this,
                        feederRunning ? R.color.alert_yellow : R.color.status_green));
                lastFeedTimeText.setText("Last Feed Time: " + lastFeedTime);
                feedNowButton.setEnabled(!feederRunning);
                stopFeedButton.setEnabled(feederRunning);

                if (alertBanner.getVisibility() != View.VISIBLE) {
                    recentAlertsText.setText("No water quality alerts\n" + feederStatusLine);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                feedingStatusText.setText("Feed Status: unavailable");
                feedNowButton.setEnabled(false);
                stopFeedButton.setEnabled(false);
            }
        });
    }

    private void renderSensorValues(float temp, float salinity, float turbidity, float water) {
        tempText.setText(String.format("%.1f C", temp));
        salText.setText(String.format("%.1f ppm", salinity));
        turbText.setText(String.format("%.1f NTU", turbidity));
        float waterFeet = water / 30.48f;
        waterText.setText(String.format(Locale.US, "%.1f ft", waterFeet));

        boolean tempAlert = temp < minTemp || temp > maxTemp;
        boolean salAlert = salinity < minSal || salinity > maxSal;
        boolean turbAlert = turbidity > maxTurb;
        boolean waterAlert = waterFeet > overflowLimit;
        setCardAlert(tempCard, tempAlert);
        setCardAlert(salCard, salAlert);
        setCardAlert(turbCard, turbAlert);
        setCardAlert(waterCard, waterAlert);
        salStatusText.setText(salAlert ? "Warning" : "Normal");
        salStatusText.setTextColor(ContextCompat.getColor(this, salAlert ? R.color.alert_yellow : R.color.status_green));
        turbStatusText.setText(turbAlert ? "Critical" : "Normal");
        turbStatusText.setTextColor(ContextCompat.getColor(this, turbAlert ? R.color.alert_red : R.color.status_green));
        waterStatusText.setText(waterAlert ? "Warning" : "Normal");
        waterStatusText.setTextColor(ContextCompat.getColor(this, waterAlert ? R.color.alert_yellow : R.color.status_green));
        renderPondHealth(tempAlert, salAlert, turbAlert, waterAlert);
        renderAlertSummary(tempAlert, salAlert, turbAlert, waterAlert);
    }

    private void listenToControl() {
        controlRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updatingSwitches = true;
                try {
                    pump1Switch.setChecked(Boolean.TRUE.equals(snapshot.child("pump1").getValue(Boolean.class)));
                    pump2Switch.setChecked(Boolean.TRUE.equals(snapshot.child("pump2").getValue(Boolean.class)));
                    pump3Switch.setChecked(Boolean.TRUE.equals(snapshot.child("pump3").getValue(Boolean.class)));
                } finally {
                    updatingSwitches = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                updatingSwitches = false;
            }
        });
    }

    private void writePumpState(String pumpKey, boolean enabled) {
        controlRef.child(pumpKey).setValue(enabled)
                .addOnFailureListener(error -> {
                    FirebaseDatabase.getInstance().goOnline();
                    controlRef.child(pumpKey).setValue(enabled)
                            .addOnFailureListener(retryError -> Toast.makeText(
                                    DashboardActivity.this,
                                    "Pump update failed. It will sync when connection is restored.",
                                    Toast.LENGTH_SHORT).show());
                });
    }

    private void writeFeederState(boolean feedNow, boolean feeding, String failureMessage) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("feedNow", feedNow);
        updates.put("feeding", feeding);
        feederRef.updateChildren(updates)
                .addOnFailureListener(error -> {
                    FirebaseDatabase.getInstance().goOnline();
                    feederRef.updateChildren(updates)
                            .addOnFailureListener(retryError -> {
                                feedNowButton.setEnabled(!feederRunning);
                                Toast.makeText(DashboardActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
                            });
                });
    }

    private void listenToHistoryPreview() {
        Query query = database.child("ShrimpHub").child("history").limitToLast(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder builder = new StringBuilder();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String timestamp = getString(child, "timestamp", "--");
                    float temp = getFloat(child, "temperature", 0f);
                    float tds = getFloat(child, "tds", 0f);
                    float turbidity = getFloat(child, "turbidity", 0f);
                    float water = getFloat(child, "waterLevel", 0f);
                    builder.insert(0, String.format(Locale.US,
                            "%s  %.1f C  %.1f ppm  %.1f NTU  %.1f ft\n",
                            timestamp, temp, tds, turbidity, water / 30.48f));
                }
                historyPreviewText.setText(builder.length() == 0 ? "No history logs yet" : builder.toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                historyPreviewText.setText("History unavailable");
            }
        });
    }

    private void listenToAdminSummary() {
        if (!"admin".equalsIgnoreCase(currentRole)) {
            return;
        }
        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userSummaryText.setText(String.format(Locale.US, "%d registered users", snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userSummaryText.setText("User summary unavailable");
            }
        });
    }

    private void renderPondHealth(boolean temp, boolean sal, boolean turbidity, boolean water) {
        int alertCount = 0;
        if (temp) alertCount++;
        if (sal) alertCount++;
        if (turbidity) alertCount++;
        if (water) alertCount++;

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

    private void renderAlertSummary(boolean temp, boolean sal, boolean turbidity, boolean water) {
        boolean any = temp || sal || turbidity || water;
        alertBanner.setVisibility(any ? View.VISIBLE : View.GONE);
        String alertText = buildAlertText(temp, sal, turbidity, water);
        alertBanner.setText(alertText);
        recentAlertsText.setText(any ? alertText.replace("Active alerts: ", "")
                : "No water quality alerts\n" + feederStatusLine);

        if (water && !overflowWasActive) {
            NotificationHelper.showAlert(DashboardActivity.this, "PrawnHub Alert", "PrawnHub: Water level outside safe range.", 1002);
        }
        if (temp && !tempWasActive) {
            NotificationHelper.showAlert(DashboardActivity.this, "PrawnHub Alert", "PrawnHub: Temperature out of safe range.", 1003);
        }
        overflowWasActive = water;
        tempWasActive = temp;
    }

    private String buildAlertText(boolean temp, boolean sal, boolean turbidity, boolean water) {
        StringBuilder builder = new StringBuilder("Active alerts: ");
        if (temp) builder.append("temperature ");
        if (sal) builder.append("TDS ");
        if (turbidity) builder.append("turbidity danger ");
        if (water) builder.append("water level ");
        return builder.toString().trim();
    }

    private void setCardAlert(LinearLayout card, boolean alert) {
        card.setBackgroundResource(alert ? R.drawable.card_alert : R.drawable.card_background);
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

    private int getInt(DataSnapshot snapshot, String key, int fallback) {
        Object value = snapshot.child(key).getValue();
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
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

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 44);
        }
    }
}
