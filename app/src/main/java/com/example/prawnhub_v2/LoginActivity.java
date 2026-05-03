package com.example.prawnhub_v2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailInput;
    private EditText passwordInput;
    private TextView errorText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        errorText = findViewById(R.id.errorText);
        loginButton = findViewById(R.id.loginButton);

        if (auth.getCurrentUser() != null) {
            openDashboard();
            return;
        }

        loginButton.setOnClickListener(view -> login());
    }

    private void login() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showError("Enter your email and password.");
            return;
        }

        loginButton.setEnabled(false);
        errorText.setVisibility(View.GONE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> openDashboard())
                .addOnFailureListener(error -> {
                    loginButton.setEnabled(true);
                    showError("Login failed. Check your Firebase user and password.");
                });
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void openDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
