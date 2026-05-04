package com.example.prawnhub_v2;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());
    }
}
