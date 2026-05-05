package com.example.prawnhub_v2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends BaseNavActivity {
    private final List<HistoryItem> allItems = new ArrayList<>();
    private HistoryAdapter adapter;
    private LineChart chart;
    private String selectedParameter = "temp";
    private String selectedRange = "Today";

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

        setupSpinners();

        setupChart();
        listenToHistory();
    }

    private void setupSpinners() {
        Spinner rangeSpinner = findViewById(R.id.rangeSpinner);
        Spinner sensorSpinner = findViewById(R.id.sensorSpinner);
        ArrayAdapter<String> rangeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Today", "Week", "Month"});
        ArrayAdapter<String> sensorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Temperature", "Salinity", "Turbidity", "Water Level", "Oxygen", "pH"});
        rangeSpinner.setAdapter(rangeAdapter);
        sensorSpinner.setAdapter(sensorAdapter);
        rangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRange = (String) parent.getItemAtPosition(position);
                renderData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedParameter = parameterKey((String) parent.getItemAtPosition(position));
                renderData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupChart() {
        chart.setNoDataText("Waiting for Firebase history data.");
        Description description = new Description();
        description.setText("Last 50 entries");
        chart.setDescription(description);
    }

    private void listenToHistory() {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("history")
                .orderByChild("timestamp")
                .limitToLast(50);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HistoryItem> nextItems = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String timestamp = child.child("timestamp").getValue(String.class);
                    String parameter = child.child("param_type").getValue(String.class);
                    Number value = child.child("rec_val").getValue(Number.class);
                    String status = child.child("status").getValue(String.class);
                    if (timestamp != null && parameter != null && value != null && isImportant(status, parameter, value.floatValue())) {
                        nextItems.add(new HistoryItem(timestamp, parameter, value.floatValue(), TextUtilsSafe.status(status)));
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
            if (selectedParameter.equals(item.parameter) && isInSelectedRange(item.timestamp)) {
                filtered.add(item);
            }
        }
        adapter.submitList(filtered);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < filtered.size(); i++) {
            entries.add(new Entry(i, filtered.get(filtered.size() - 1 - i).value));
        }
        LineDataSet dataSet = new LineDataSet(entries, selectedRange + " " + selectedParameter.replace("_", " "));
        dataSet.setColor(Color.rgb(0, 108, 103));
        dataSet.setCircleColor(Color.rgb(247, 179, 43));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }

    private String parameterKey(String label) {
        if ("Salinity".equals(label)) return "salinity";
        if ("Turbidity".equals(label)) return "turbidity";
        if ("Water Level".equals(label)) return "water_level";
        if ("Oxygen".equals(label)) return "oxygen";
        if ("pH".equals(label)) return "ph";
        return "temp";
    }

    private boolean isImportant(String status, String parameter, float value) {
        if (status != null && !status.trim().isEmpty() && !"normal".equalsIgnoreCase(status)) {
            return true;
        }
        if ("oxygen".equals(parameter)) return value < 5f;
        if ("salinity".equals(parameter)) return value < 15f || value > 25f;
        if ("water_level".equals(parameter)) return value > 5f;
        if ("turbidity".equals(parameter)) return value > 45f;
        if ("temp".equals(parameter)) return value < 28f || value > 32f;
        if ("ph".equals(parameter)) return value < 6.5f || value > 8.5f;
        return false;
    }

    private boolean isInSelectedRange(String timestamp) {
        long itemTime = TextUtilsSafe.parseTime(timestamp);
        if (itemTime <= 0L) {
            return true;
        }
        long ageMillis = System.currentTimeMillis() - itemTime;
        long day = 24L * 60L * 60L * 1000L;
        if ("Week".equals(selectedRange)) return ageMillis <= 7L * day;
        if ("Month".equals(selectedRange)) return ageMillis <= 31L * day;
        return ageMillis <= day;
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
