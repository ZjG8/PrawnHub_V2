package com.example.prawnhub_v2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GrowthActivity extends BaseNavActivity {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private DatabaseReference database;
    private TextView daysText;
    private TextView targetText;
    private TextView scoreText;
    private TextView statusBadge;
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
        Button setStartDateButton = findViewById(R.id.setStartDateButton);
        setStartDateButton.setOnClickListener(view -> showDatePicker());

        listenToSettings();
        listenToGrowth();
    }

    private void listenToSettings() {
        database.child("settings").child("target_harvest_days").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Number value = snapshot.getValue(Number.class);
                targetDays = value == null ? 75 : value.intValue();
                targetText.setText("Target harvest: " + targetDays + " days");
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
                Number optimal = snapshot.child("days_in_optimal_temp").getValue(Number.class);
                optimalDays = optimal == null ? 0 : optimal.intValue();
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

        float score = 0f;
        if (cultureDays > 0) {
            score = (((float) optimalDays / cultureDays) * 0.6f + ((float) targetDays / cultureDays) * 0.4f) * 100f;
            score = Math.min(100f, Math.max(0f, score));
        }
        database.child("growth").child("performance_score").setValue(score);
        scoreText.setText(String.format("Performance score: %.0f%%", score));

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
