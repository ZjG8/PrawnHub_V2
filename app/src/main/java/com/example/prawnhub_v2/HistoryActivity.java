package com.example.prawnhub_v2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends BaseNavActivity {
    private final List<HistoryItem> allItems = new ArrayList<>();
    private final Calendar startDateTime = Calendar.getInstance();
    private final Calendar endDateTime = Calendar.getInstance();
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
    private HistoryAdapter adapter;
    private LineChart chart;
    private Button startDateTimeButton;
    private Button endDateTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupBottomNav();

        chart = findViewById(R.id.historyChart);
        adapter = new HistoryAdapter();
        RecyclerView recycler = findViewById(R.id.historyRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());
        startDateTimeButton = findViewById(R.id.startDateTimeButton);
        endDateTimeButton = findViewById(R.id.endDateTimeButton);
        startDateTime.add(Calendar.DAY_OF_MONTH, -1);
        updateRangeText();
        startDateTimeButton.setOnClickListener(view -> showDateTimePicker(startDateTime, startDateTimeButton));
        endDateTimeButton.setOnClickListener(view -> showDateTimePicker(endDateTime, endDateTimeButton));

        setupChart();
        listenToHistory();
    }

    private void setupChart() {
        chart.setNoDataText("Waiting for Firebase history data.");
        Description description = new Description();
        description.setText("Selected history range");
        chart.setDescription(description);
    }

    private void listenToHistory() {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("history")
                .orderByChild("timestamp")
                .limitToLast(200);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HistoryItem> nextItems = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String timestamp = child.child("timestamp").getValue(String.class);
                    String parameter = child.child("param_type").getValue(String.class);
                    Float value = getNullableFloat(child.child("rec_val"));
                    String status = child.child("status").getValue(String.class);
                    if (timestamp != null && parameter != null && value != null && isImportant(status, parameter, value.floatValue())) {
                        nextItems.add(new HistoryItem(timestamp, parameter, value, TextUtilsSafe.status(status)));
                    }
                }
                Collections.reverse(nextItems);
                allItems.clear();
                allItems.addAll(nextItems);
                renderData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, "History listener failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderData() {
        List<HistoryItem> filtered = new ArrayList<>();
        for (HistoryItem item : allItems) {
            if (isInSelectedRange(item.timestamp)) {
                filtered.add(item);
            }
        }
        adapter.submitList(filtered);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < filtered.size(); i++) {
            entries.add(new Entry(i, filtered.get(filtered.size() - 1 - i).value));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Important events");
        dataSet.setColor(Color.rgb(0, 108, 103));
        dataSet.setCircleColor(Color.rgb(247, 179, 43));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }

    private boolean isImportant(String status, String parameter, float value) {
        if (status != null && !status.trim().isEmpty() && !"normal".equalsIgnoreCase(status)) {
            return true;
        }
        if ("salinity".equals(parameter)) return value < 15f || value > 25f;
        if ("water_level".equals(parameter)) return value > 5f;
        if ("turbidity".equals(parameter)) return value > 45f;
        if ("temp".equals(parameter)) return value < 28f || value > 32f;
        return false;
    }

    private boolean isInSelectedRange(String timestamp) {
        long itemTime = TextUtilsSafe.parseTime(timestamp);
        if (itemTime <= 0L) {
            return true;
        }
        return itemTime >= startDateTime.getTimeInMillis() && itemTime <= endDateTime.getTimeInMillis();
    }

    private void showDateTimePicker(Calendar target, Button targetButton) {
        DatePickerDialog dateDialog = new DatePickerDialog(
                this,
                (dateView, year, month, dayOfMonth) -> {
                    target.set(Calendar.YEAR, year);
                    target.set(Calendar.MONTH, month);
                    target.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    TimePickerDialog timeDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                target.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                target.set(Calendar.MINUTE, minute);
                                target.set(Calendar.SECOND, 0);
                                target.set(Calendar.MILLISECOND, 0);
                                updateRangeText();
                                renderData();
                            },
                            target.get(Calendar.HOUR_OF_DAY),
                            target.get(Calendar.MINUTE),
                            true
                    );
                    timeDialog.show();
                },
                target.get(Calendar.YEAR),
                target.get(Calendar.MONTH),
                target.get(Calendar.DAY_OF_MONTH)
        );
        dateDialog.show();
    }

    private void updateRangeText() {
        startDateTimeButton.setText("Start: " + displayDateFormat.format(startDateTime.getTime()));
        endDateTimeButton.setText("End: " + displayDateFormat.format(endDateTime.getTime()));
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

    private static class TextUtilsSafe {
        static String status(String status) {
            return status == null || status.trim().isEmpty() ? "Important alert" : status;
        }

        static long parseTime(String timestamp) {
            try {
                return Long.parseLong(timestamp);
            } catch (NumberFormatException ignored) {
                return 0L;
            }
        }
    }
}
