package com.scc.model;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String role;
    private String status;
    private String lastLoginDate;

    // Constructor para sa existing user gikan sa database (naa nay ID ug uban pa)
    public User(int userId, String username, String passwordHash, String role, String status, String lastLoginDate) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.lastLoginDate = lastLoginDate;
    }

    // Constructor para sa pag-register og bag-ong user (default status: PENDING)
    public User(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = "PENDING";
    }

    // Getters ug Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
}