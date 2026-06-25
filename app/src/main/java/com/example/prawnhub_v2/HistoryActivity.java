package com.example.prawnhub_v2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends BaseNavActivity {
    private static final String HISTORY_TAG = "PRAWNHUB_HISTORY";
    private static final String HISTORY_DEBUG_TAG = "HISTORY_DEBUG";
    private static final String FIREBASE_HISTORY_TAG = "HISTORY";
    private static final String HISTORY_COUNT_TAG = "HISTORY_COUNT";
    private static final String FILTERED_COUNT_TAG = "FILTERED_COUNT";
    private static final String CHART_COUNT_TAG = "CHART_COUNT";
    private static final String HISTORY_PATH = "ShrimpHub/history";
    private final List<HistoryItem> allItems = new ArrayList<>();
    private final List<HistoryRecord> allRecords = new ArrayList<>();
    private final Calendar startDate = Calendar.getInstance();
    private final Calendar endDate = Calendar.getInstance();
    private final SimpleDateFormat dateButtonFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    private final SimpleDateFormat verificationFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private HistoryAdapter adapter;
    private RecyclerView recycler;
    private String selectedParameter = "all";
    private LineChart temperatureChart;
    private LineChart salinityChart;
    private LineChart turbidityChart;
    private LineChart waterLevelChart;
    private TextView temperatureChartLabel;
    private TextView salinityChartLabel;
    private TextView turbidityChartLabel;
    private TextView waterLevelChartLabel;
    private TextView verificationTimestampText;
    private TextView verificationTemperatureText;
    private TextView verificationSalinityText;
    private TextView verificationTurbidityText;
    private TextView verificationWaterLevelText;
    private TextView emptyStateText;
    private Button startDateButton;
    private Button endDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupBottomNav();

        adapter = new HistoryAdapter();
        recycler = findViewById(R.id.historyRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());
        startDateButton = findViewById(R.id.startDateButton);
        endDateButton = findViewById(R.id.endDateButton);
        temperatureChart = findViewById(R.id.temperatureChart);
        salinityChart = findViewById(R.id.salinityChart);
        turbidityChart = findViewById(R.id.turbidityChart);
        waterLevelChart = findViewById(R.id.waterLevelChart);
        temperatureChartLabel = findViewById(R.id.temperatureChartLabel);
        salinityChartLabel = findViewById(R.id.salinityChartLabel);
        turbidityChartLabel = findViewById(R.id.turbidityChartLabel);
        waterLevelChartLabel = findViewById(R.id.waterLevelChartLabel);
        verificationTimestampText = findViewById(R.id.verificationTimestampText);
        verificationTemperatureText = findViewById(R.id.verificationTemperatureText);
        verificationSalinityText = findViewById(R.id.verificationSalinityText);
        verificationTurbidityText = findViewById(R.id.verificationTurbidityText);
        verificationWaterLevelText = findViewById(R.id.verificationWaterLevelText);
        emptyStateText = findViewById(R.id.historyEmptyText);
        configureChart(temperatureChart);
        configureChart(salinityChart);
        configureChart(turbidityChart);
        configureChart(waterLevelChart);

        setupDateRangeFilter();
        setupSensorFilter();
        listenToHistory();
    }

    private void setupDateRangeFilter() {
        endDate.setTimeInMillis(System.currentTimeMillis());
        startDate.setTimeInMillis(endDate.getTimeInMillis());
        startDate.add(Calendar.DAY_OF_MONTH, -7);
        updateDateButtons();

        startDateButton.setOnClickListener(view -> showDateTimePicker(startDate, true));
        endDateButton.setOnClickListener(view -> showDateTimePicker(endDate, false));
        Button applyButton = findViewById(R.id.applyDateFilterButton);
        applyButton.setOnClickListener(view -> renderData());
    }

    private void setupSensorFilter() {
        Spinner sensorFilterSpinner = findViewById(R.id.sensorFilterSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"All Sensors", "Temperature", "Salinity", "Turbidity", "Water Level"});
        sensorFilterSpinner.setAdapter(adapter);
        sensorFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void listenToHistory() {
        Log.d(HISTORY_TAG, "History path used: " + HISTORY_PATH);
        Query query = FirebaseDatabase.getInstance()
                .getReference("ShrimpHub")
                .child("history")
                .orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(HISTORY_TAG, "Firebase listener status: onDataChange");
                Log.d(HISTORY_TAG, "History children count: " + snapshot.getChildrenCount());
                Log.d(HISTORY_DEBUG_TAG, "History children count=" + snapshot.getChildrenCount());
                Object snapshotValue = snapshot.getValue();
                Log.d(FIREBASE_HISTORY_TAG, snapshotValue == null ? "null" : snapshotValue.toString());
                List<HistoryItem> nextItems = new ArrayList<>();
                List<HistoryRecord> nextRecords = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        Log.d(HISTORY_DEBUG_TAG, String.valueOf(child.getValue()));
                        Log.d(HISTORY_DEBUG_TAG, "Timestamp=" + getString(child.child("timestamp")));
                        Log.d(HISTORY_DEBUG_TAG, "Temperature=" + getString(child.child("temperature")));
                        Log.d(HISTORY_DEBUG_TAG, "TDS=" + getString(child.child("tds")));
                        Log.d(HISTORY_DEBUG_TAG, "Turbidity=" + getString(child.child("turbidity")));
                        Log.d(HISTORY_DEBUG_TAG, "WaterLevel=" + getString(child.child("waterLevel")));
                        nextItems.addAll(readShrimpHubHistoryItems(child));
                        HistoryItem legacyItem = readHistoryItem(child);
                        if (legacyItem != null) {
                            nextItems.add(legacyItem);
                        }
                        HistoryRecord record = readHistoryRecord(child);
                        if (record != null) {
                            nextRecords.add(record);
                        }
                    } catch (RuntimeException ignored) {
                        // Skip malformed legacy rows instead of crashing the History screen.
                        Log.d(HISTORY_TAG, "Ignored malformed history record: " + child.getKey());
                    }
                }
                Collections.sort(nextItems, (left, right) -> Long.compare(
                        TextUtilsSafe.parseTime(right.timestamp),
                        TextUtilsSafe.parseTime(left.timestamp)
                ));
                Collections.sort(nextRecords, (left, right) -> Long.compare(
                        TextUtilsSafe.parseTime(left.timestamp),
                        TextUtilsSafe.parseTime(right.timestamp)
                ));
                allItems.clear();
                allItems.addAll(nextItems);
                allRecords.clear();
                allRecords.addAll(nextRecords);
                Log.d(HISTORY_TAG, "Firebase records loaded: " + allRecords.size());
                Log.d(HISTORY_COUNT_TAG, String.valueOf(allRecords.size()));
                Log.d(HISTORY_TAG, "Oldest timestamp: " + oldestTimestamp(allRecords));
                Log.d(HISTORY_TAG, "Newest timestamp: " + newestTimestamp(allRecords));
                updateFirebaseVerificationPanel(allRecords);
                renderData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(HISTORY_TAG, "Firebase listener status: cancelled " + error.getMessage());
                Toast.makeText(HistoryActivity.this, "History listener failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<HistoryItem> readShrimpHubHistoryItems(DataSnapshot child) {
        List<HistoryItem> items = new ArrayList<>();
        String timestamp = getTimestampString(child);
        addSensorItem(items, timestamp, "temp", child.child("temperature"), "Normal");
        addSensorItem(items, timestamp, "salinity", child.child("tds"), "Normal");
        addSensorItem(items, timestamp, "turbidity", child.child("turbidity"), "Normal");
        addSensorItem(items, timestamp, "water_level", child.child("waterLevel"), "Normal");
        return items;
    }

    private HistoryRecord readHistoryRecord(DataSnapshot child) {
        String timestamp = getTimestampString(child);
        Float temperature = getNullableFloat(child.child("temperature"));
        Float salinity = getNullableFloat(child.child("tds"));
        Float turbidity = getNullableFloat(child.child("turbidity"));
        Float waterLevel = getNullableFloat(child.child("waterLevel"));

        HistoryItem legacyItem = readHistoryItem(child);
        if (legacyItem != null) {
            timestamp = legacyItem.timestamp;
            if ("temp".equals(legacyItem.parameter)) {
                temperature = legacyItem.value;
            } else if ("salinity".equals(legacyItem.parameter)) {
                salinity = legacyItem.value;
            } else if ("turbidity".equals(legacyItem.parameter)) {
                turbidity = legacyItem.value;
            } else if ("water_level".equals(legacyItem.parameter)) {
                waterLevel = legacyItem.value;
            }
        }

        if (timestamp == null) {
            return null;
        }
        return new HistoryRecord(timestamp, temperature, salinity, turbidity, waterLevel);
    }

    private void addSensorItem(List<HistoryItem> items, String timestamp, String parameter, DataSnapshot valueSnapshot, String status) {
        Float value = getNullableFloat(valueSnapshot);
        if (timestamp != null && value != null) {
            items.add(new HistoryItem(timestamp, parameter, value, status));
        }
    }

    private String getTimestampString(DataSnapshot child) {
        String timestamp = getString(child.child("timestamp"));
        if (timestamp != null) {
            return timestamp;
        }
        timestamp = getString(child.child("time"));
        if (timestamp != null) {
            return timestamp;
        }
        String hour = getString(child.child("hour"));
        String minute = getString(child.child("minute"));
        if (hour != null && minute != null) {
            return String.format(Locale.US, "%02d:%02d", parseInt(hour), parseInt(minute));
        }
        return null;
    }

    private HistoryItem readHistoryItem(DataSnapshot child) {
        String timestamp = getString(child.child("timestamp"));
        String parameter = normalizeParameter(getString(child.child("param_type")));
        Float value = getNullableFloat(child.child("rec_val"));
        String status = getString(child.child("status"));

        if (timestamp == null) {
            timestamp = getString(child.child("time"));
        }
        if (parameter == null) {
            parameter = inferParameterFromKey(child.getKey());
        }
        if (value == null) {
            value = getNullableFloat(child.child("value"));
        }

        if (timestamp == null || parameter == null || value == null) {
            return null;
        }
        return new HistoryItem(timestamp, parameter, value, TextUtilsSafe.status(status));
    }

    private void renderData() {
        List<HistoryItem> filtered = new ArrayList<>();
        for (HistoryItem item : allItems) {
            if (isSelectedParameter(item.parameter) && isInSelectedDateRange(item.timestamp)) {
                filtered.add(item);
            }
        }
        adapter.submitList(filtered);
        emptyStateText.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        emptyStateText.setText(filtered.isEmpty()
                ? "No records found for selected range."
                : "");
        if (!filtered.isEmpty()) {
            recycler.scrollToPosition(0);
        }
        Log.d(HISTORY_TAG, "Records after filter: " + filtered.size());
        Log.d(FILTERED_COUNT_TAG, String.valueOf(filtered.size()));
        Log.d(HISTORY_TAG, "renderData received records: " + filtered.size());
        if (filtered.isEmpty()) {
            Log.d(HISTORY_DEBUG_TAG, "No records found for selected range.");
        }
        renderCharts();
    }

    private boolean isSelectedParameter(String parameter) {
        return "all".equals(selectedParameter) || selectedParameter.equals(parameter);
    }

    private String parameterKey(String label) {
        if ("Temperature".equals(label)) return "temp";
        if ("Salinity".equals(label)) return "salinity";
        if ("Turbidity".equals(label)) return "turbidity";
        if ("Water Level".equals(label)) return "water_level";
        return "all";
    }

    private void showDateTimePicker(Calendar target, boolean rangeStart) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(rangeStart ? "Start Date" : "End Date")
                .setSelection(target.getTimeInMillis())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(selection);
            target.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR));
            target.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH));
            target.set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH));
            showTimePicker(target, rangeStart);
        });
        datePicker.show(getSupportFragmentManager(), rangeStart ? "start_date_picker" : "end_date_picker");
    }

    private void showTimePicker(Calendar target, boolean rangeStart) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText(rangeStart ? "Start Time" : "End Time")
                .setHour(target.get(Calendar.HOUR_OF_DAY))
                .setMinute(target.get(Calendar.MINUTE))
                .build();
        timePicker.addOnPositiveButtonClickListener(view -> {
            target.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            target.set(Calendar.MINUTE, timePicker.getMinute());
            target.set(Calendar.SECOND, rangeStart ? 0 : 59);
            target.set(Calendar.MILLISECOND, rangeStart ? 0 : 999);
            updateDateButtons();
        });
        timePicker.show(getSupportFragmentManager(), rangeStart ? "start_time_picker" : "end_time_picker");
    }

    private void updateDateButtons() {
        startDateButton.setText(dateButtonFormat.format(startDate.getTime()));
        endDateButton.setText(dateButtonFormat.format(endDate.getTime()));
    }

    private boolean isInSelectedDateRange(String timestamp) {
        long itemTime = TextUtilsSafe.parseTime(timestamp);
        if (itemTime <= 0L) {
            return true;
        }
        return itemTime >= startDate.getTimeInMillis() && itemTime <= endDate.getTimeInMillis();
    }

    private String normalizeParameter(String raw) {
        if (raw == null) return null;
        String parameter = raw.trim();
        if ("temperature".equalsIgnoreCase(parameter)) return "temp";
        if ("tds".equalsIgnoreCase(parameter)) return "salinity";
        if ("waterLevel".equals(parameter) || "water".equalsIgnoreCase(parameter)) return "water_level";
        return parameter;
    }

    private String inferParameterFromKey(String key) {
        if (key == null) return null;
        if (key.contains("temp")) return "temp";
        if (key.contains("sal")) return "salinity";
        if (key.contains("turb")) return "turbidity";
        if (key.contains("water")) return "water_level";
        return null;
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

    private String getString(DataSnapshot snapshot) {
        Object value = snapshot.getValue();
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private void configureChart(LineChart chart) {
        chart.setNoDataText("No sensor data available.");
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setTextColor(getColor(R.color.text_secondary));
        chart.getAxisLeft().setTextColor(getColor(R.color.text_secondary));
        chart.getLegend().setTextColor(getColor(R.color.text_primary));
        chart.getLegend().setForm(Legend.LegendForm.LINE);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
    }

    private void renderCharts() {
        List<Entry> temperatureEntries = new ArrayList<>();
        List<Entry> salinityEntries = new ArrayList<>();
        List<Entry> turbidityEntries = new ArrayList<>();
        List<Entry> waterEntries = new ArrayList<>();
        for (HistoryRecord record : allRecords) {
            if (!isInSelectedDateRange(record.timestamp)) {
                continue;
            }
            long itemMillis = TextUtilsSafe.parseTime(record.timestamp);
            if (itemMillis <= 0L) {
                continue;
            }
            Log.d(HISTORY_TAG, "Timestamp parsed: " + itemMillis);
            float timestampSeconds = itemMillis / 1000f;
            addChartEntry(temperatureEntries, timestampSeconds, record.temperature);
            addChartEntry(salinityEntries, timestampSeconds, record.salinity);
            addChartEntry(turbidityEntries, timestampSeconds, record.turbidity);
            addChartEntry(waterEntries, timestampSeconds, record.waterLevel);
        }
        Log.d(CHART_COUNT_TAG, String.valueOf(temperatureEntries.size()));
        renderChartGroup(temperatureChartLabel, temperatureChart, temperatureEntries, "Temperature", "temp", R.color.alert_yellow);
        renderChartGroup(salinityChartLabel, salinityChart, salinityEntries, "Salinity", "salinity", R.color.brand_primary);
        renderChartGroup(turbidityChartLabel, turbidityChart, turbidityEntries, "Turbidity", "turbidity", R.color.alert_red);
        renderChartGroup(waterLevelChartLabel, waterLevelChart, waterEntries, "Water Level", "water_level", R.color.status_green);
    }

    private void addChartEntry(List<Entry> entries, float timestampSeconds, Float value) {
        if (value != null) {
            entries.add(new Entry(timestampSeconds, value));
            Log.d(HISTORY_TAG, "Chart entries created: " + entries.size());
        }
    }

    private void updateFirebaseVerificationPanel(List<HistoryRecord> records) {
        if (records.isEmpty()) {
            verificationTimestampText.setText("Latest timestamp: --");
            verificationTemperatureText.setText("Temperature: --");
            verificationSalinityText.setText("TDS: --");
            verificationTurbidityText.setText("Turbidity: --");
            verificationWaterLevelText.setText("Water Level: --");
            return;
        }
        HistoryRecord latest = records.get(records.size() - 1);
        String timestamp = latest.timestamp;
        verificationTimestampText.setText("Latest timestamp: " + formatVerificationTimestamp(timestamp));
        verificationTemperatureText.setText("Temperature: " + formatVerificationValue(latest.temperature, "C"));
        verificationSalinityText.setText("TDS: " + formatVerificationValue(latest.salinity, "ppt"));
        verificationTurbidityText.setText("Turbidity: " + formatVerificationValue(latest.turbidity, "NTU"));
        verificationWaterLevelText.setText("Water Level: " + formatVerificationValue(latest.waterLevel, "ft"));
    }

    private String formatVerificationTimestamp(String timestamp) {
        long millis = TextUtilsSafe.parseTime(timestamp);
        if (millis <= 0L) {
            return timestamp == null ? "--" : timestamp;
        }
        return verificationFormat.format(new Date(millis));
    }

    private String formatVerificationValue(Float value, String unit) {
        if (value == null) {
            return "--";
        }
        return String.format(Locale.US, "%.1f %s", value, unit);
    }

    private void renderChartGroup(TextView labelView, LineChart chart, List<Entry> entries, String label, String parameter, int colorRes) {
        boolean visible = "all".equals(selectedParameter) || parameter.equals(selectedParameter);
        labelView.setVisibility(visible ? View.VISIBLE : View.GONE);
        chart.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) {
            renderChart(chart, entries, label, colorRes);
        }
    }

    private void renderChart(LineChart chart, List<Entry> entries, String label, int colorRes) {
        if (entries.isEmpty()) {
            chart.clear();
            chart.setNoDataText("No sensor data available.");
            chart.invalidate();
            return;
        }
        int color = getColor(colorRes);
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setValueTextColor(getColor(R.color.text_secondary));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);
        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }

    private String timestampRange(List<HistoryRecord> records) {
        if (records.isEmpty()) {
            return "none";
        }
        String first = records.get(0).timestamp;
        String last = records.get(records.size() - 1).timestamp;
        return first + " -> " + last;
    }

    private String oldestTimestamp(List<HistoryRecord> records) {
        if (records.isEmpty()) {
            return "--";
        }
        return records.get(0).timestamp;
    }

    private String newestTimestamp(List<HistoryRecord> records) {
        if (records.isEmpty()) {
            return "--";
        }
        return records.get(records.size() - 1).timestamp;
    }

    private static class TextUtilsSafe {
        private static final SimpleDateFormat FIRMWARE_TIMESTAMP_FORMAT =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        private static final SimpleDateFormat DISPLAY_TIMESTAMP_FORMAT =
                new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
        private static final SimpleDateFormat DISPLAY_TIMESTAMP_WITH_SECONDS_FORMAT =
                new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);

        static String status(String status) {
            return status == null || status.trim().isEmpty() ? "Important alert" : status;
        }

        static long parseTime(String timestamp) {
            if (timestamp == null || timestamp.trim().isEmpty()) {
                return 0L;
            }
            try {
                long raw = Long.parseLong(timestamp);
                return raw < 100000000000L ? raw * 1000L : raw;
            } catch (NumberFormatException ignored) {
                return parseFormattedTime(timestamp);
            }
        }

        private static long parseFormattedTime(String timestamp) {
            SimpleDateFormat[] formats = new SimpleDateFormat[]{
                    FIRMWARE_TIMESTAMP_FORMAT,
                    DISPLAY_TIMESTAMP_WITH_SECONDS_FORMAT,
                    DISPLAY_TIMESTAMP_FORMAT
            };
            for (SimpleDateFormat format : formats) {
                try {
                    Date parsed = format.parse(timestamp);
                    if (parsed != null) {
                        return parsed.getTime();
                    }
                } catch (ParseException ignored) {
                }
            }
            return 0L;
        }
    }

    private static class HistoryRecord {
        final String timestamp;
        final Float temperature;
        final Float salinity;
        final Float turbidity;
        final Float waterLevel;

        HistoryRecord(String timestamp, Float temperature, Float salinity, Float turbidity, Float waterLevel) {
            this.timestamp = timestamp;
            this.temperature = temperature;
            this.salinity = salinity;
            this.turbidity = turbidity;
            this.waterLevel = waterLevel;
        }
    }
}
