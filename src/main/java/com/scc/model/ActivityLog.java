package com.scc.model;

import java.sql.Timestamp;

public record ActivityLog(
        int id,
        int userId,
        String actionType,
        String description,
        Timestamp timestamp
) {
    @Override
    public String toString() {
        return String.format("[ID: %d] User %d | %s | %s | %s", id, userId, actionType, description, timestamp);
    }
}