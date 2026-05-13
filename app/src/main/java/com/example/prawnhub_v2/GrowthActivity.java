package com.example.prawnhub_v2;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GrowthActivity extends BaseNavActivity {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private DatabaseReference database;
    private TextView daysText;
    private TextView targetText;
    private TextView scoreText;
    private TextView statusBadge;
    private TextView recommendationsText;
    private ProgressBar healthProgress;
    private LineChart growthChart;
    private int targetDays = 75;
    private int optimalDays = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth);
        setupBottomNav();

        database = FirebaseDatabase.getInstance().getReference();
        daysText = findViewById(R.id.daysText);
        targetText = findViewById(R.id.targetText);
        scoreText = findViewById(R.id.scoreText);
        statusBadge = findViewById(R.id.statusBadge);
        recommendationsText = findViewById(R.id.recommendationsText);
        healthProgress = findViewById(R.id.healthProgress);
        growthChart = findViewById(R.id.growthChart);
        Button setStartDateButton = findViewById(R.id.setStartDateButton);
        setStartDateButton.setOnClickListener(view -> showDatePicker());

        setupChart();
        listenToSettings();
        listenToGrowth();
        listenToSensorHealth();
        listenToTrend();
    }

    private void setupChart() {
        growthChart.setNoDataText("Waiting for Firebase growth trend data.");
        Description description = new Description();
        description.setText("Weekly growth health");
        growthChart.setDescription(description);
    }

    private void listenToSettings() {
        database.child("settings").child("target_harvest_days").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                targetDays = getInt(snapshot, 75);
                targetText.setText("Harvest readiness is calculated automatically from growth and water quality.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GrowthActivity.this, "Settings listener failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenToGrowth() {
        database.child("growth").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String startDate = snapshot.child("start_date").getValue(String.class);
                optimalDays = getInt(snapshot.child("days_in_optimal_temp"), 0);
                int cultureDays = calculateDays(startDate);
                database.child("growth").child("days_of_culture").setValue(cultureDays);
                renderGrowth(cultureDays);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GrowthActivity.this, "Growth listener failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int calculateDays(String startDate) {
        if (startDate == null || startDate.isEmpty()) {
            return 0;
        }
        try {
            Date start = dateFormat.parse(startDate);
            if (start == null) {
                return 0;
            }
            long diff = new Date().getTime() - start.getTime();
            return Math.max(0, (int) TimeUnit.MILLISECONDS.toDays(diff));
        } catch (ParseException error) {
            return 0;
        }
    }

    private void renderGrowth(int cultureDays) {
        daysText.setText(cultureDays + " days");
        targetText.setText("Target harvest: " + targetDays + " days");
        targetText.setText("Harvest readiness is calculated automatically from growth and water quality.");

        float score = 0f;
        if (cultureDays > 0) {
            score = (((float) optimalDays / cultureDays) * 0.6f + ((float) targetDays / cultureDays) * 0.4f) * 100f;
            score = Math.min(100f, Math.max(0f, score));
        }
        database.child("growth").child("performance_score").setValue(score);
        renderHealth(score);
    }

    private void listenToSensorHealth() {
        database.child("aquarium").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot aquarium) {
                float temp = getFloat(aquarium, "temp_val", 30f);
                float salinity = getFloat(aquarium, "tds_val", 20f);
                float turbidity = getFloat(aquarium, "turb_val", 0f);
                float oxygen = getFloat(aquarium, "oxygen_val", getFloat(aquarium, "do_val", 6f));
                float ph = getFloat(aquarium, "ph_val", 7.5f);
                renderSensorHealth(temp, salinity, turbidity, oxygen, ph);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GrowthActivity.this, "Sensor listener failed.", Toast.LENGTH_SHORT).show();
            }
        });

        database.child("ShrimpHub").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot shrimpHub) {
                if (!shrimpHub.exists()) {
                    return;
                }
                float temp = getFloat(shrimpHub, "temperature", 30f);
                float salinity = getFloat(shrimpHub, "tds", 20f);
                float turbidity = getFloat(shrimpHub, "turbidity", 0f);
                renderSensorHealth(temp, salinity, turbidity, 6f, 7.5f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GrowthActivity.this, "ShrimpHub sensor listener failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderSensorHealth(float temp, float salinity, float turbidity, float oxygen, float ph) {
        int issues = 0;
        if (temp < 28f || temp > 32f) issues++;
        if (salinity < 15f || salinity > 25f) issues++;
        if (turbidity > 45f) issues++;
        if (oxygen < 5f) issues++;
        if (ph < 6.5f || ph > 8.5f) issues++;
        float score = Math.max(0f, 100f - (issues * 18f));
        database.child("growth").child("water_quality_score").setValue(score);
        renderHealth(score);
        recommendationsText.setText(buildRecommendations(temp, salinity, turbidity, oxygen, ph));
    }

    private void listenToTrend() {
        database.child("growth_trend").limitToLast(7).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Entry> entries = new ArrayList<>();
                int index = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Float value = getNullableFloat(child.child("score"));
                    if (value == null) {
                        value = getNullableFloat(child);
                    }
                    if (value != null) {
                        entries.add(new Entry(index++, value));
                    }
                }
                LineDataSet dataSet = new LineDataSet(entries, "Growth health");
                dataSet.setColor(Color.rgb(0, 108, 103));
                dataSet.setCircleColor(Color.rgb(242, 140, 111));
                dataSet.setLineWidth(2f);
                dataSet.setCircleRadius(4f);
                growthChart.setData(new LineData(dataSet));
                growthChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GrowthActivity.this, "Trend listener failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderHealth(float score) {
        healthProgress.setProgress(Math.round(score));
        scoreText.setText(String.format(Locale.US, "%.0f%% health", score));

        if (score >= 80f) {
            statusBadge.setText("On Track - Excellent conditions!");
            statusBadge.setBackgroundResource(R.drawable.badge_green);
        } else if (score >= 60f) {
            statusBadge.setText("Good - Monitor water closely.");
            statusBadge.setBackgroundResource(R.drawable.badge_yellow);
        } else {
            statusBadge.setText("Needs Attention - Check parameters now.");
            statusBadge.setBackgroundResource(R.drawable.badge_red);
        }
    }

    private String buildRecommendations(float temp, float salinity, float turbidity, float oxygen, float ph) {
        StringBuilder builder = new StringBuilder();
        if (oxygen < 5f) builder.append("Maintain oxygen level. ");
        if (salinity < 15f || salinity > 25f) builder.append("Reduce salinity variation. ");
        if (turbidity > 45f) builder.append("Monitor turbidity and filtration. ");
        if (temp < 28f || temp > 32f) builder.append("Keep temperature within target range. ");
        if (ph < 6.5f || ph > 8.5f) builder.append("Correct pH balance. ");
        return builder.length() == 0 ? "Maintain oxygen level, stable salinity, and clear water conditions." : builder.toString().trim();
    }

    private float getFloat(DataSnapshot snapshot, String key, float fallback) {
        Float value = getNullableFloat(snapshot.child(key));
        return value == null ? fallback : value;
    }

    private Float getNullableFloat(DataSnapshot snapshot) {
        Object value = snapshot.getValue();
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        if (value instanceof String) {
            try {
                return Float.parseFloat((String) value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private int getInt(DataSnapshot snapshot, int fallback) {
        Object value = snapshot.getValue();
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

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    database.child("growth").child("start_date").setValue(dateFormat.format(selected.getTime()));
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }
}
