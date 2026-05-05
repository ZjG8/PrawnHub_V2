package com.example.prawnhub_v2;

public class AdminUserRecord {
    public String uid;
    public String email;
    public String role;
    public String status;
    public String registeredAt;

    public AdminUserRecord() {
    }

    public AdminUserRecord(String uid, String email, String role, String status, String registeredAt) {
        this.uid = uid;
        this.email = email;
        this.role = role;
        this.status = status;
        this.registeredAt = registeredAt;
    }
}
