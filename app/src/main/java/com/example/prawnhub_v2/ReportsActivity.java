package com.example.prawnhub_v2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {
    private final Calendar startDate = Calendar.getInstance();
    private final Calendar endDate = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private EditText startDateInput;
    private EditText endDateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!"admin".equalsIgnoreCase(SessionStore.getRole(this))) {
            finish();
            return;
        }
        setContentView(R.layout.activity_reports);

        Button backButton = findViewById(R.id.backButton);
        startDateInput = findViewById(R.id.startDateInput);
        endDateInput = findViewById(R.id.endDateInput);
        Button exportPdf = findViewById(R.id.exportPdfButton);
        Button exportExcel = findViewById(R.id.exportExcelButton);

        backButton.setOnClickListener(v -> finish());
        startDateInput.setOnClickListener(v -> showDatePicker(startDate, startDateInput));
        endDateInput.setOnClickListener(v -> showDatePicker(endDate, endDateInput));
        exportPdf.setOnClickListener(v -> exportReport("PDF"));
        exportExcel.setOnClickListener(v -> exportReport("Excel"));

        startDate.add(Calendar.DAY_OF_MONTH, -7);
        updateDateInputs();
    }

    private void showDatePicker(Calendar selectedDate, EditText targetInput) {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    targetInput.setText(dateFormat.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void updateDateInputs() {
        startDateInput.setText(dateFormat.format(startDate.getTime()));
        endDateInput.setText(dateFormat.format(endDate.getTime()));
    }

    private void exportReport(String format) {
        Toast.makeText(
                this,
                format + " report export can be wired to Firebase data next.",
                Toast.LENGTH_SHORT
        ).show();
    }
}
