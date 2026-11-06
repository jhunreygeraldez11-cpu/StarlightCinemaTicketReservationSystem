package com.scc.dao;

import com.scc.model.ActivityLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    /**
     * Inserts a new activity log entry into the activity_logs table.
     */
    public boolean logAction(int userId, String actionType, String description) {
        String sql = "INSERT INTO activity_logs (user_id, action_type, description) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, actionType);
            pstmt.setString(3, description);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("Failed to log activity: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all recorded logs ordered by newest first.
     */
    public List<ActivityLog> getAllLogs() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT id, user_id, action_type, description, timestamp FROM activity_logs ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                logs.add(new ActivityLog(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("action_type"),
                        rs.getString("description"),
                        rs.getTimestamp("timestamp")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all activity logs: " + e.getMessage());
        }

        return logs;
    }

    /**
     * Retrieves logs filtered by a specific User ID.
     */
    public List<ActivityLog> getLogsByUserId(int userId) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT id, user_id, action_type, description, timestamp FROM activity_logs WHERE user_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(new ActivityLog(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("action_type"),
                            rs.getString("description"),
                            rs.getTimestamp("timestamp")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching logs for user " + userId + ": " + e.getMessage());
        }

        return logs;
    }

    /**
     * Clears all activity logs safely.
     */
    public boolean clearLogs() {
        String sql = "DELETE FROM activity_logs";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error clearing activity logs: " + e.getMessage());
            return false;
        }
    }
}