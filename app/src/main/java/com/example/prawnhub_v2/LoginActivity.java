package com.example.prawnhub_v2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailInput;
    private EditText passwordInput;
    private TextView errorText;
    private Button loginButton;
    private Button showPasswordButton;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        errorText = findViewById(R.id.errorText);
        loginButton = findViewById(R.id.loginButton);
        showPasswordButton = findViewById(R.id.showPasswordButton);
        TextView roleText = findViewById(R.id.roleText);
        Button backButton = findViewById(R.id.backButton);
        Button forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        Button signUpButton = findViewById(R.id.signUpButton);
        roleText.setText("Admin Login");
        backButton.setOnClickListener(view -> finish());
        signUpButton.setVisibility(View.GONE);

        if (auth.getCurrentUser() != null) {
            verifyAdminAndOpen(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail());
            return;
        }

        loginButton.setOnClickListener(view -> login());
        showPasswordButton.setOnClickListener(view -> togglePasswordVisibility());
        forgotPasswordButton.setOnClickListener(view -> sendPasswordReset());
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
                .addOnSuccessListener(result -> {
                    verifyAdminAndOpen(result.getUser().getUid(), email);
                })
                .addOnFailureListener(error -> {
                    loginButton.setEnabled(true);
                    showError("Login failed. Check your Firebase user and password.");
                });
    }

    private void sendPasswordReset() {
        String email = emailInput.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            showError("Enter your email first.");
            return;
        }
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error -> showError("Reset failed: " + error.getMessage()));
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        int selection = passwordInput.getSelectionStart();
        passwordInput.setInputType(passwordVisible
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setSelection(Math.max(0, selection));
        showPasswordButton.setText(passwordVisible ? "Hide Password" : "Show Password");
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void verifyAdminAndOpen(String uid, String email) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                if ("admin".equalsIgnoreCase(role)) {
                    SessionStore.setRole(LoginActivity.this, "admin");
                    openAdminDashboard();
                    return;
                }
                verifyAdminByEmail(usersRef, uid, email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                verifyAdminByEmail(usersRef, uid, email);
            }
        });
    }

    private void verifyAdminByEmail(DatabaseReference usersRef, String uid, String email) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String role = child.child("role").getValue(String.class);
                    if ("admin".equalsIgnoreCase(role)) {
                        usersRef.child(uid).child("email").setValue(email);
                        usersRef.child(uid).child("role").setValue("admin");
                        SessionStore.setRole(LoginActivity.this, "admin");
                        openAdminDashboard();
                        return;
                    }
                }
                auth.signOut();
                loginButton.setEnabled(true);
                showError("This account is not registered as an admin.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                auth.signOut();
                loginButton.setEnabled(true);
                showError("Could not verify admin role.");
            }
        });
    }

    private void openAdminDashboard() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
