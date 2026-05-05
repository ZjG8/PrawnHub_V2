package com.example.prawnhub_v2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageUsersActivity extends AppCompatActivity {
    private final List<AdminUserRecord> allUsers = new ArrayList<>();
    private EditText searchInput;
    private LinearLayout usersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!"admin".equalsIgnoreCase(SessionStore.getRole(this))) {
            finish();
            return;
        }
        setContentView(R.layout.activity_manage_users);

        searchInput = findViewById(R.id.searchInput);
        usersContainer = findViewById(R.id.usersContainer);
        Button backButton = findViewById(R.id.backButton);
        Button addUserButton = findViewById(R.id.addUserButton);
        backButton.setOnClickListener(v -> finish());
        addUserButton.setOnClickListener(v -> showAddUserDialog());
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderUsers(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        listenToUsers();
    }

    private void listenToUsers() {
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUsers.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    allUsers.add(new AdminUserRecord(
                            child.getKey(),
                            String.valueOf(child.child("email").getValue(String.class)),
                            String.valueOf(child.child("role").getValue(String.class)),
                            String.valueOf(child.child("status").getValue(String.class)),
                            String.valueOf(child.child("registeredAt").getValue(String.class))
                    ));
                }
                renderUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageUsersActivity.this, "Could not load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderUsers() {
        usersContainer.removeAllViews();
        String query = searchInput.getText().toString().trim().toLowerCase();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (AdminUserRecord user : allUsers) {
            String haystack = (user.email + " " + user.role + " " + user.status + " " + user.registeredAt).toLowerCase();
            if (!query.isEmpty() && !haystack.contains(query)) {
                continue;
            }
            View item = inflater.inflate(R.layout.item_admin_user, usersContainer, false);
            ((TextView) item.findViewById(R.id.userEmail)).setText(user.email);
            ((TextView) item.findViewById(R.id.userMeta)).setText(
                    "Role: " + safe(user.role) + "  •  Status: " + safe(user.status) + "  •  Registered: " + safe(user.registeredAt));

            item.findViewById(R.id.editButton).setOnClickListener(v -> editUser(user));
            item.findViewById(R.id.toggleButton).setOnClickListener(v -> toggleStatus(user));
            item.findViewById(R.id.deleteButton).setOnClickListener(v -> deleteUser(user));
            usersContainer.addView(item);
        }
    }

    private void editUser(AdminUserRecord user) {
        String[] roles = {"admin", "farmer"};
        new AlertDialog.Builder(this)
                .setTitle("Assign Role")
                .setItems(roles, (dialog, which) -> FirebaseDatabase.getInstance().getReference()
                        .child("users").child(user.uid).child("role").setValue(roles[which]))
                .show();
    }

    private void toggleStatus(AdminUserRecord user) {
        String next = "active".equalsIgnoreCase(user.status) ? "disabled" : "active";
        FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).child("status").setValue(next);
    }

    private void deleteUser(AdminUserRecord user) {
        FirebaseDatabase.getInstance().getReference().child("users").child(user.uid).removeValue();
    }

    private void showAddUserDialog() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(32, 16, 32, 0);

        EditText emailInput = new EditText(this);
        emailInput.setHint("Email");
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        form.addView(emailInput);

        EditText dateInput = new EditText(this);
        dateInput.setHint("Registered At");
        dateInput.setText("now");
        form.addView(dateInput);

        Spinner roleSpinner = new Spinner(this);
        roleSpinner.setAdapter(new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"admin", "farmer"}));
        form.addView(roleSpinner);

        Spinner statusSpinner = new Spinner(this);
        statusSpinner.setAdapter(new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"active", "disabled"}));
        form.addView(statusSpinner);

        new AlertDialog.Builder(this)
                .setTitle("Add User")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    String email = emailInput.getText().toString().trim();
                    if (email.isEmpty()) {
                        Toast.makeText(this, "Email is required.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").push();
                    AdminUserRecord record = new AdminUserRecord(
                            usersRef.getKey(),
                            email,
                            roleSpinner.getSelectedItem().toString(),
                            statusSpinner.getSelectedItem().toString(),
                            dateInput.getText().toString().equals("now") ? String.valueOf(System.currentTimeMillis()) : dateInput.getText().toString()
                    );
                    usersRef.setValue(record);
                })
                .show();
    }

    private String safe(String value) {
        return value == null || value.equals("null") ? "-" : value;
    }
}
