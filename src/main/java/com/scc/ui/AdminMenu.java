package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void display(User currentAdmin) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("         ADMIN DASHBOARD MENU           ");
            System.out.println("========================================");
            System.out.println("1. Update User Role and Status");
            System.out.println("2. Logout / Back to Main Menu");
            System.out.print("Select an Option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showUpdateUserMenu(currentAdmin);
                    break;
                case "2":
                    System.out.println("Logging out from Admin menu...");
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    public void showUpdateUserMenu(User currentAdmin) {
        System.out.println("\n========================================");
        System.out.println("         ADMIN: UPDATE USER MENU        ");
        System.out.println("========================================");

        // Display all users in a formatted table
        displayAllUsersTable();

        // 1. Get target User ID with crash prevention
        int targetUserId = readIntInput("Enter Target User ID (or enter 0 to cancel): ");
        if (targetUserId == 0) {
            System.out.println("Update cancelled by user.");
            return;
        }

        // 2. Enforce Self-Edit Security Rule
        if (targetUserId == currentAdmin.id()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        // 3. Read and validate Role
        String newRole = readValidRole();
        if (newRole.equals("0")) {
            System.out.println("Update cancelled.");
            return;
        }

        // 4. Read and validate Status
        String newStatus = readValidStatus();
        if (newStatus.equals("0")) {
            System.out.println("Update cancelled.");
            return;
        }

        // 5. Execute Update via DAO
        boolean success = userDao.updateUserRoleAndStatus(targetUserId, newRole, newStatus);
        if (success) {
            System.out.println("SUCCESS: User role and status successfully updated.");
        } else {
            System.out.println("ERROR: Failed to update user. Please verify the User ID exists.");
        }
    }

    private void displayAllUsersTable() {
        List<User> users = userDao.getAllUsers();
        System.out.println("\n--- USER LIST ---");
        System.out.printf("%-5s | %-15s | %-10s | %-10s\n", "ID", "Username", "Role", "Status");
        System.out.println("--------------------------------------------------");
        for (User u : users) {
            System.out.printf("%-5d | %-15s | %-10s | %-10s\n", u.id(), u.username(), u.roles(), u.status());
        }
        System.out.println("--------------------------------------------------");
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric ID.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role (ADMIN, CASHIER, MANAGER) [0 to cancel]: ");
            String role = scanner.nextLine().trim().toUpperCase();

            if (role.equals("0")) return "0";
            if (role.equals("STUDENT") || role.equals("TEACHER") || role.equals("ADMIN") || role.equals("CASHIER") || role.equals("MANAGER")) {
                return role;
            }
            System.out.println("Invalid role. Accepted values: STUDENT, TEACHER, ADMIN, CASHIER, MANAGER.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED, PENDING) [0 to cancel]: ");
            String status = scanner.nextLine().trim().toUpperCase();

            if (status.equals("0")) return "0";
            if (status.equals("APPROVED") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("Invalid status. Accepted values: APPROVED, PENDING.");
        }
    }
}