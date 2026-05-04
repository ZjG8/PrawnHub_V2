package com.example.prawnhub_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public abstract class BaseNavActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBottomNav() {
        bindNavButton(R.id.navDashboard, DashboardActivity.class);
        bindNavButton(R.id.navHistory, HistoryActivity.class);
        bindNavButton(R.id.navGrowth, GrowthActivity.class);
        bindNavButton(R.id.navSettings, SettingsActivity.class);
    }

    private void bindNavButton(int buttonId, Class<?> target) {
        Button button = findViewById(buttonId);
        if (button == null) {
            return;
        }
        boolean selected = getClass().equals(target);
        button.setSelected(selected);
        button.setTextColor(ContextCompat.getColor(this, selected ? R.color.brand_primary_dark : R.color.text_secondary));
        if (selected) {
            button.setBackgroundResource(R.drawable.nav_selected);
        }
        button.setOnClickListener(view -> {
            if (getClass().equals(target)) {
                return;
            }
            Intent intent = new Intent(this, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}
