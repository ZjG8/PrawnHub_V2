package com.example.prawnhub_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {
    public static final String EXTRA_ROLE = "com.example.prawnhub_v2.ROLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        LinearLayout adminRoleCard = findViewById(R.id.adminRoleCard);
        LinearLayout farmerRoleCard = findViewById(R.id.farmerRoleCard);
        adminRoleCard.setOnClickListener(view -> openLogin("Admin"));
        farmerRoleCard.setOnClickListener(view -> openLogin("Farmer"));
    }

    private void openLogin(String role) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(EXTRA_ROLE, role);
        startActivity(intent);
    }
}
