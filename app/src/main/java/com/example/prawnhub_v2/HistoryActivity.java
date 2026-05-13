package com.example.prawnhub_v2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends BaseNavActivity {
    private final List<HistoryItem> allItems = new ArrayList<>();
    private final Calendar startDateTime = Calendar.getInstance();
    private final Calendar endDateTime = Calendar.getInstance();
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
    private HistoryAdapter adapter;
    private RecyclerView recycler;
    private Button startDateTimeButton;
    private Button endDateTimeButton;
    private String selectedParameter = "all";

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
        startDateTimeButton = findViewById(R.id.startDateTimeButton);
        endDateTimeButton = findViewById(R.id.endDateTimeButton);
        startDateTime.add(Calendar.DAY_OF_MONTH, -1);
        updateRangeText();
        startDateTimeButton.setOnClickListener(view -> showDateTimePicker(startDateTime, startDateTimeButton));
        endDateTimeButton.setOnClickListener(view -> showDateTimePicker(endDateTime, endDateTimeButton));

        setupSensorFilter();
        listenToHistory();
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
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("ShrimpHub")
                .child("history")
                .limitToLast(100);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HistoryItem> nextItems = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        nextItems.addAll(readShrimpHubHistoryItems(child));
                    } catch (RuntimeException ignored) {
                        // Skip malformed legacy rows instead of crashing the History screen.
                    }
                }
                Collections.sort(nextItems, (left, right) -> Long.compare(
                        TextUtilsSafe.parseTime(right.timestamp),
                        TextUtilsSafe.parseTime(left.timestamp)
                ));
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

    private List<HistoryItem> readShrimpHubHistoryItems(DataSnapshot child) {
        List<HistoryItem> items = new ArrayList<>();
        String timestamp = getTimestampString(child);
        addSensorItem(items, timestamp, "temp", child.child("temperature"), "Normal");
        addSensorItem(items, timestamp, "salinity", child.child("tds"), "Normal");
        addSensorItem(items, timestamp, "turbidity", child.child("turbidity"), "Normal");
        addSensorItem(items, timestamp, "water_level", child.child("waterLevel"), "Normal");
        return items;
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
            if (isInSelectedRange(item.timestamp) && isSelectedParameter(item.parameter)) {
                filtered.add(item);
            }
        }
        adapter.submitList(filtered);
        if (!filtered.isEmpty()) {
            recycler.scrollToPosition(0);
        }
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

    private String normalizeParameter(String raw) {
        if (raw == null) return null;
        String parameter = raw.trim();
        if ("temperature".equalsIgnoreCase(parameter)) return "temp";
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
                            false
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

    private static class TextUtilsSafe {
        private static final SimpleDateFormat FIRMWARE_TIMESTAMP_FORMAT =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        static String status(String status) {
            return status == null || status.trim().isEmpty() ? "Important alert" : status;
        }

        static long parseTime(String timestamp) {
            try {
                long raw = Long.parseLong(timestamp);
                return raw < 100000000000L ? raw * 1000L : raw;
            } catch (NumberFormatException ignored) {
                try {
                    Date parsed = FIRMWARE_TIMESTAMP_FORMAT.parse(timestamp);
                    return parsed == null ? 0L : parsed.getTime();
                } catch (ParseException error) {
                    return 0L;
                }
            }
        }
    }
}
