package com.example.prawnhub_v2;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RadioGroup;
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

        RadioGroup filterGroup = findViewById(R.id.filterGroup);
        filterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.filterSalinity) {
                selectedParameter = "salinity";
            } else if (checkedId == R.id.filterTurbidity) {
                selectedParameter = "turbidity";
            } else if (checkedId == R.id.filterWater) {
                selectedParameter = "water_level";
            } else {
                selectedParameter = "temp";
            }
            renderData();
        });

        setupChart();
        listenToHistory();
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
                    if (timestamp != null && parameter != null && value != null) {
                        nextItems.add(new HistoryItem(timestamp, parameter, value.floatValue()));
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
            if (selectedParameter.equals(item.parameter)) {
                filtered.add(item);
            }
        }
        adapter.submitList(filtered);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < filtered.size(); i++) {
            entries.add(new Entry(i, filtered.get(filtered.size() - 1 - i).value));
        }
        LineDataSet dataSet = new LineDataSet(entries, selectedParameter.replace("_", " "));
        dataSet.setColor(Color.rgb(0, 108, 103));
        dataSet.setCircleColor(Color.rgb(247, 179, 43));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }
}
