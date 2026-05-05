package com.example.prawnhub_v2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ReportsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!"admin".equalsIgnoreCase(SessionStore.getRole(this))) {
            finish();
            return;
        }
        setContentView(R.layout.activity_reports);

        Button backButton = findViewById(R.id.backButton);
        Spinner rangeSpinner = findViewById(R.id.rangeSpinner);
        Spinner formatSpinner = findViewById(R.id.formatSpinner);
        Button exportPdf = findViewById(R.id.exportPdfButton);
        Button exportExcel = findViewById(R.id.exportExcelButton);

        backButton.setOnClickListener(v -> finish());
        rangeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Today", "Week", "Month", "Custom Range"}));
        formatSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"PDF", "Excel (.xlsx)"}));
        exportPdf.setOnClickListener(v -> Toast.makeText(this, "PDF export can be wired to Firebase data next.", Toast.LENGTH_SHORT).show());
        exportExcel.setOnClickListener(v -> Toast.makeText(this, "Excel export can be wired to Firebase data next.", Toast.LENGTH_SHORT).show());
    }
}
