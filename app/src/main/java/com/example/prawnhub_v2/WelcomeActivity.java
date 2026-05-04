package com.example.prawnhub_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button getStartedButton = findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener(view -> startActivity(new Intent(this, RoleSelectionActivity.class)));
    }
}
