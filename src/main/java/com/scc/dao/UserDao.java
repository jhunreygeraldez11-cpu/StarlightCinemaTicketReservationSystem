package com.scc.dao;

import com.scc.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public boolean updateUserRoleAndStatus(int userId, String newRole, String newStatus) {
        String sql = "UPDATE users SET roles = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRole.toUpperCase());
            pstmt.setString(2, newStatus.toUpperCase());
            pstmt.setInt(3, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Database error during update: " + e.getMessage());
            return false;
        }
    }

    public void defaultAdminGenerator(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM Users WHERE roles = 'ADMIN'";
        String insertSql = "INSERT INTO users(username, password, roles, status) VALUES('admin','admin123','ADMIN','APPROVED')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate(insertSql);
                System.out.println(" -> Default Admin created <-");
            }
        } catch (SQLException e) {
            System.err.println("Error seeding default admin: " + e.getMessage());
        }
    }

    public boolean registerUser(String username, String password, String role) {
        String normalizedRole = role.toUpperCase();
        String status = normalizedRole.equals("ADMIN") ? "APPROVED" : "PENDING";

        String sql = "INSERT INTO users(username, password, roles, status) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, normalizedRole);
            pstmt.setString(4, status);
            pstmt.executeUpdate();

            if (status.equals("APPROVED")) {
                System.out.println("SUCCESSFUL REGISTRATION. Admin account automatically approved.");
            } else {
                System.out.println("SUCCESSFUL REGISTRATION, waiting for approval.");
            }
            return true;
        } catch (SQLException e) {
            System.err.println("FAILED: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>();
        String sql = "SELECT id, username, password, roles, status FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usersList.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }

        return usersList;
    }

    public List<User> getPendingUser() {
        List<User> pendingList = new ArrayList<>();
        String sql = "SELECT id, username, password, roles, status FROM users WHERE status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pendingList.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error Fetching User: " + e.getMessage());
        }
        return pendingList;
    }

    public boolean approveUser(int userID){
        String sql = "UPDATE users SET status = 'APPROVED' WHERE id = ?";

        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, userID);
            int rowsUpdated = pstmt.executeUpdate();

            if(rowsUpdated > 0 ){
                System.out.println(" ->USER ID: "+userID+" APPROVED SUCCESSFULLY!");
                return  true;
            }else {
                System.out.println("ID NOT FOUND.");
            }
        }catch (SQLException e){
            System.out.println("Error Fetching User: "+e.getMessage());
        }
        return  false;
    }
}