package com.example.prawnhub_v2;

import android.content.Intent;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SettingsActivity extends BaseNavActivity {
    private DatabaseReference rootRef;
    private EditText feedTimeInput;
    private EditText feedIntervalInput;
    private RangeSlider temperatureRangeSlider;
    private RangeSlider salinityRangeSlider;
    private RangeSlider turbidityLimitSlider;
    private Slider overflowLimitSlider;
    private TextView temperatureRangeValue;
    private TextView salinityRangeValue;
    private TextView turbidityLimitValue;
    private TextView overflowLimitValue;
    private Switch soundSwitch;
    private Switch vibrateSwitch;
    private Switch pushSwitch;
    private Switch midnightFeedSwitch;
    private Button saveButton;
    private Button logoutButton;
    private Button aboutButton;
    private Button backButton;
    private TextView settingsTitle;
    private LinearLayout bottomNavContainer;
    private RadioGroup themeRadioGroup;
    private boolean isAdminMode;
    private boolean applyingThemeSelection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        isAdminMode = "admin".equalsIgnoreCase(SessionStore.getRole(this));
        if (!isAdminMode) {
            setupBottomNav();
        }
        rootRef = FirebaseDatabase.getInstance().getReference();
        bindViews();
        applyRoleMode();
        bindThresholdSliders();
        loadSettings();
    }

    private void bindViews() {
        feedTimeInput = findViewById(R.id.feedTimeInput);
        feedIntervalInput = findViewById(R.id.feedIntervalInput);
        temperatureRangeSlider = findViewById(R.id.temperatureRangeSlider);
        salinityRangeSlider = findViewById(R.id.salinityRangeSlider);
        turbidityLimitSlider = findViewById(R.id.turbidityLimitSlider);
        overflowLimitSlider = findViewById(R.id.overflowLimitSlider);
        temperatureRangeValue = findViewById(R.id.temperatureRangeValue);
        salinityRangeValue = findViewById(R.id.salinityRangeValue);
        turbidityLimitValue = findViewById(R.id.turbidityLimitValue);
        overflowLimitValue = findViewById(R.id.overflowLimitValue);
        soundSwitch = findViewById(R.id.soundSwitch);
        vibrateSwitch = findViewById(R.id.vibrateSwitch);
        pushSwitch = findViewById(R.id.pushSwitch);
        midnightFeedSwitch = findViewById(R.id.midnightFeedSwitch);
        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        settingsTitle = findViewById(R.id.settingsTitle);
        backButton = findViewById(R.id.backButton);
        bottomNavContainer = findViewById(R.id.bottomNavContainer);
        TextView accountEmailText = findViewById(R.id.accountEmailText);
        if (isAdminMode && FirebaseAuth.getInstance().getCurrentUser() != null) {
            accountEmailText.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        } else {
            accountEmailText.setText("Farmer");
        }
        saveButton = findViewById(R.id.saveSettingsButton);
        logoutButton = findViewById(R.id.logoutButton);
        aboutButton = findViewById(R.id.aboutButton);

        feedTimeInput.setOnClickListener(view -> showTimePicker());
        backButton.setOnClickListener(view -> finish());
        saveButton.setOnClickListener(view -> saveSettings());
        logoutButton.setOnClickListener(view -> logout());
        aboutButton.setOnClickListener(view -> startActivity(new Intent(this, AboutActivity.class)));
        pushSwitch.setOnCheckedChangeListener((button, checked) -> saveNotificationPreferencesLocally());
        vibrateSwitch.setOnCheckedChangeListener((button, checked) -> saveNotificationPreferencesLocally());
        soundSwitch.setOnCheckedChangeListener((button, checked) -> saveNotificationPreferencesLocally());
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (applyingThemeSelection) {
                return;
            }
            ThemePreference.saveThemeMode(this, themeModeFromRadioId(checkedId));
        });
    }

    private void applyRoleMode() {
        settingsTitle.setText(isAdminMode ? "System Settings" : "Monitoring Settings");
        backButton.setVisibility(isAdminMode ? View.VISIBLE : View.GONE);
        if (bottomNavContainer != null) {
            bottomNavContainer.setVisibility(isAdminMode ? View.GONE : View.VISIBLE);
        }
        saveButton.setVisibility(android.view.View.VISIBLE);
        logoutButton.setText(isAdminMode ? "Logout" : "Back to Roles");
        setEditable(feedTimeInput, true);
        setEditable(feedIntervalInput, true);
        temperatureRangeSlider.setEnabled(isAdminMode);
        salinityRangeSlider.setEnabled(isAdminMode);
        turbidityLimitSlider.setEnabled(isAdminMode);
        overflowLimitSlider.setEnabled(isAdminMode);
        soundSwitch.setEnabled(true);
        vibrateSwitch.setEnabled(true);
        pushSwitch.setEnabled(true);
        midnightFeedSwitch.setEnabled(true);
    }

    private void setEditable(EditText input, boolean enabled) {
        input.setEnabled(enabled);
        input.setFocusable(enabled);
        input.setFocusableInTouchMode(enabled);
        input.setClickable(enabled);
    }

    private void bindThresholdSliders() {
        temperatureRangeSlider.setValueFrom(0f);
        temperatureRangeSlider.setValueTo(50f);
        temperatureRangeSlider.setStepSize(0.5f);
        salinityRangeSlider.setValueFrom(0f);
        salinityRangeSlider.setValueTo(50f);
        salinityRangeSlider.setStepSize(1f);
        turbidityLimitSlider.setValueFrom(0f);
        turbidityLimitSlider.setValueTo(100f);
        turbidityLimitSlider.setStepSize(1f);
        overflowLimitSlider.setValueFrom(0f);
        overflowLimitSlider.setValueTo(20f);
        overflowLimitSlider.setStepSize(0.5f);

        temperatureRangeSlider.setValues(28f, 32f);
        salinityRangeSlider.setValues(15f, 25f);
        turbidityLimitSlider.setValues(0f, 45f);
        overflowLimitSlider.setValue(5f);

        temperatureRangeSlider.addOnChangeListener((slider, value, fromUser) -> updateTemperatureLabel());
        salinityRangeSlider.addOnChangeListener((slider, value, fromUser) -> updateSalinityLabel());
        turbidityLimitSlider.addOnChangeListener((slider, value, fromUser) -> updateTurbidityLabel());
        overflowLimitSlider.addOnChangeListener((slider, value, fromUser) -> updateOverflowLabel());

        updateThresholdLabels();
    }

    private void loadSettings() {
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot settings = snapshot.child("settings");
                DataSnapshot thresholds = snapshot.child("thresholds");

                float minTemperature = getFloat(thresholds, "minTemperature", getFloat(thresholds, "temperature/min", getFloat(settings, "min_temp", getNestedFloat(settings, "temperature", "min", 28f))));
                float maxTemperature = getFloat(thresholds, "maxTemperature", getFloat(thresholds, "temperature/max", getFloat(settings, "max_temp", getNestedFloat(settings, "temperature", "max", 32f))));
                float minSalinity = getFloat(thresholds, "minSalinity", getFloat(thresholds, "salinity/min", getFloat(settings, "min_sal", getNestedFloat(settings, "salinity", "min", 15f))));
                float maxSalinity = getFloat(thresholds, "maxSalinity", getFloat(thresholds, "salinity/max", getFloat(settings, "max_sal", getNestedFloat(settings, "salinity", "max", 25f))));
                float minTurbidity = getFloat(thresholds, "minTurbidity", getFloat(thresholds, "turbidity/min", getFloat(settings, "min_turb", getNestedFloat(settings, "turbidity", "min", 0f))));
                float maxTurbidity = getFloat(thresholds, "maxTurbidity", getFloat(thresholds, "turbidity/max", getFloat(settings, "max_turb", getNestedFloat(settings, "turbidity", "max", 45f))));
                float overflowLimit = getFloat(thresholds, "overflowLimit", getFloat(thresholds, "water_level/max", getFloat(settings, "overflow_limit", getNestedFloat(settings, "water_level", "max", 5f))));

                setRangeSliderValues(temperatureRangeSlider, minTemperature, maxTemperature);
                setRangeSliderValues(salinityRangeSlider, minSalinity, maxSalinity);
                setRangeSliderValues(turbidityLimitSlider, minTurbidity, maxTurbidity);
                overflowLimitSlider.setValue(clampToSlider(overflowLimitSlider, overflowLimit));
                updateThresholdLabels();

                setText(feedTimeInput, settings, "feed_time", "08:00");
                setText(feedIntervalInput, settings, "feed_interval_hours", "4");
                soundSwitch.setChecked(getBoolean(settings, "sound_alert", true));
                vibrateSwitch.setChecked(getBoolean(settings, "vibrate", true));
                pushSwitch.setChecked(getBoolean(settings, "push_notifications", true));
                midnightFeedSwitch.setChecked(getBoolean(settings, "midnight_feeding", false));
                saveNotificationPreferencesLocally();
                applySavedThemeSelection();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, "Could not load settings.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setText(EditText input, DataSnapshot snapshot, String key, String fallback) {
        Object value = snapshot.child(key).getValue();
        input.setText(value == null ? fallback : String.valueOf(value));
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

    private boolean getBoolean(DataSnapshot snapshot, String key, boolean fallback) {
        Boolean value = snapshot.child(key).getValue(Boolean.class);
        return value == null ? fallback : value;
    }

    private float getNestedFloat(DataSnapshot snapshot, String category, String child, float fallback) {
        Object nested = snapshot.child(category).child(child).getValue();
        if (nested instanceof Number) {
            return ((Number) nested).floatValue();
        }
        if (nested instanceof String) {
            try {
                return Float.parseFloat((String) nested);
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private void showTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> feedTimeInput.setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute)),
                8,
                0,
                true
        );
        dialog.show();
    }

    private void saveSettings() {
        if (!validateInputs()) {
            Toast.makeText(this, "Fill in feeding settings before saving.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Float> temperatureValues = temperatureRangeSlider.getValues();
        List<Float> salinityValues = salinityRangeSlider.getValues();
        float minTemperature = temperatureValues.get(0);
        float maxTemperature = temperatureValues.get(1);
        int minSalinity = Math.round(salinityValues.get(0));
        int maxSalinity = Math.round(salinityValues.get(1));
        List<Float> turbidityValues = turbidityLimitSlider.getValues();
        float minTurbidity = turbidityValues.get(0);
        float maxTurbidity = turbidityValues.get(1);
        float overflowLimit = overflowLimitSlider.getValue();

        Map<String, Object> updates = new HashMap<>();
        updates.put("settings/feed_time", feedTimeInput.getText().toString());
        updates.put("settings/feed_interval_hours", Integer.parseInt(feedIntervalInput.getText().toString()));
        updates.put("settings/sound_alert", soundSwitch.isChecked());
        updates.put("settings/vibrate", vibrateSwitch.isChecked());
        updates.put("settings/push_notifications", pushSwitch.isChecked());
        updates.put("settings/midnight_feeding", midnightFeedSwitch.isChecked());
        updates.put("settings/esp32_feeding_scheduler", midnightFeedSwitch.isChecked() ? "daily_midnight_enabled" : "manual_interval");

        if (isAdminMode) {
            updates.put("settings/max_temp", maxTemperature);
            updates.put("settings/min_temp", minTemperature);
            updates.put("settings/min_sal", minSalinity);
            updates.put("settings/max_sal", maxSalinity);
            updates.put("settings/min_turb", minTurbidity);
            updates.put("settings/max_turb", maxTurbidity);
            updates.put("settings/overflow_limit", overflowLimit);

            updates.put("thresholds/minTemperature", minTemperature);
            updates.put("thresholds/maxTemperature", maxTemperature);
            updates.put("thresholds/minSalinity", minSalinity);
            updates.put("thresholds/maxSalinity", maxSalinity);
            updates.put("thresholds/minTurbidity", minTurbidity);
            updates.put("thresholds/maxTurbidity", maxTurbidity);
            updates.put("thresholds/overflowLimit", overflowLimit);
            updates.put("thresholds/temperature/min", minTemperature);
            updates.put("thresholds/temperature/max", maxTemperature);
            updates.put("thresholds/salinity/min", minSalinity);
            updates.put("thresholds/salinity/max", maxSalinity);
            updates.put("thresholds/turbidity/min", minTurbidity);
            updates.put("thresholds/turbidity/max", maxTurbidity);
            updates.put("thresholds/water_level/max", overflowLimit);
        }

        rootRef.updateChildren(updates)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error -> Toast.makeText(this, "Save failed: " + error.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void saveNotificationPreferencesLocally() {
        NotificationSettings.save(this, pushSwitch.isChecked(), vibrateSwitch.isChecked(), soundSwitch.isChecked());
    }

    private void applySavedThemeSelection() {
        applyingThemeSelection = true;
        try {
            String mode = ThemePreference.getThemeMode(this);
            themeRadioGroup.check(radioIdForThemeMode(mode));
        } finally {
            applyingThemeSelection = false;
        }
    }

    private int radioIdForThemeMode(String mode) {
        if (ThemePreference.THEME_LIGHT.equals(mode)) {
            return R.id.themeLightRadio;
        }
        if (ThemePreference.THEME_DARK.equals(mode)) {
            return R.id.themeDarkRadio;
        }
        return R.id.themeSystemRadio;
    }

    private String themeModeFromRadioId(int checkedId) {
        if (checkedId == R.id.themeLightRadio) {
            return ThemePreference.THEME_LIGHT;
        }
        if (checkedId == R.id.themeDarkRadio) {
            return ThemePreference.THEME_DARK;
        }
        return ThemePreference.THEME_SYSTEM;
    }

    private boolean validateInputs() {
        return !TextUtils.isEmpty(feedTimeInput.getText())
                && !TextUtils.isEmpty(feedIntervalInput.getText())
                && TextUtils.isDigitsOnly(feedIntervalInput.getText());
    }

    private void setRangeSliderValues(RangeSlider slider, float rawMin, float rawMax) {
        float min = clampToSlider(slider, rawMin);
        float max = clampToSlider(slider, rawMax);
        if (min > max) {
            float swap = min;
            min = max;
            max = swap;
        }
        slider.setValues(min, max);
    }

    private float clampToSlider(Slider slider, float value) {
        return Math.max(slider.getValueFrom(), Math.min(slider.getValueTo(), value));
    }

    private float clampToSlider(RangeSlider slider, float value) {
        return Math.max(slider.getValueFrom(), Math.min(slider.getValueTo(), value));
    }

    private void updateThresholdLabels() {
        updateTemperatureLabel();
        updateSalinityLabel();
        updateTurbidityLabel();
        updateOverflowLabel();
    }

    private void updateTemperatureLabel() {
        List<Float> values = temperatureRangeSlider.getValues();
        temperatureRangeValue.setText(String.format(Locale.US, "Min: %.1f C        Max: %.1f C", values.get(0), values.get(1)));
    }

    private void updateSalinityLabel() {
        List<Float> values = salinityRangeSlider.getValues();
        salinityRangeValue.setText(String.format(Locale.US, "Min: %d ppt        Max: %d ppt", Math.round(values.get(0)), Math.round(values.get(1))));
    }

    private void updateTurbidityLabel() {
        List<Float> values = turbidityLimitSlider.getValues();
        turbidityLimitValue.setText(String.format(Locale.US, "Min: %d NTU        Max: %d NTU", Math.round(values.get(0)), Math.round(values.get(1))));
    }

    private void updateOverflowLabel() {
        overflowLimitValue.setText(String.format(Locale.US, "Current: %.1f ft", overflowLimitSlider.getValue()));
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        SessionStore.clear(this);
        Intent intent = new Intent(this, RoleSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
