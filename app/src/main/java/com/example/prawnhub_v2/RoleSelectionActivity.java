package com.example.prawnhub_v2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        LinearLayout adminRoleCard = findViewById(R.id.adminRoleCard);
        LinearLayout farmerRoleCard = findViewById(R.id.farmerRoleCard);
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());
        adminRoleCard.setOnClickListener(view -> openLogin());
        farmerRoleCard.setOnClickListener(view -> openFarmerDashboard());
    }

    private void openLogin() {
        startActivity(new android.content.Intent(this, LoginActivity.class));
    }

    private void openFarmerDashboard() {
        SessionStore.setRole(this, "farmer");
        Toast.makeText(this, "Opening farmer dashboard.", Toast.LENGTH_SHORT).show();
        startActivity(new android.content.Intent(this, DashboardActivity.class));
    }
}
