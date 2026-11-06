package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.model.ActivityLog;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final ActivityLogDao logDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, ActivityLogDao logDao, Scanner scanner) {
        this.userDao = userDao;
        this.logDao = logDao;
        this.scanner = scanner;
    }

    public void display(User currentAdmin) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("         ADMIN DASHBOARD MENU           ");
            System.out.println("========================================");
            System.out.println("1. Update User Role and Status");
            System.out.println("2. View System Audit Logs");
            System.out.println("3. Logout / Back to Main Menu");
            System.out.print("Select an Option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showUpdateUserMenu(currentAdmin);
                case "2" -> showAuditLogMenu();
                case "3" -> {
                    System.out.println("Logging out from Admin menu...");
                    return;
                }
                default -> System.out.println("Invalid option! Please try again.");
            }
        }
    }

    public void showAuditLogMenu() {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("         SYSTEM AUDIT LOG MENU          ");
            System.out.println("========================================");
            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
            System.out.println("3. Clear Historical Logs");
            System.out.println("4. Return to Admin Menu");
            System.out.print("Select an Option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> renderLogTable(logDao.getAllLogs(), "ALL SYSTEM LOGS");
                case "2" -> {
                    int targetUserId = readIntInput("Enter User ID to filter: ");
                    renderLogTable(logDao.getLogsByUserId(targetUserId), "LOGS FOR USER ID: " + targetUserId);
                }
                case "3" -> {
                    System.out.print("Are you sure you want to clear ALL historical logs? (Y/N): ");
                    String confirm = scanner.nextLine().trim();
                    if (confirm.equalsIgnoreCase("Y")) {
                        if (logDao.clearLogs()) {
                            System.out.println("SUCCESS: Historical activity logs cleared.");
                        }
                    } else {
                        System.out.println("Operation cancelled.");
                    }
                }
                case "4" -> { return; }
                default -> System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void renderLogTable(List<ActivityLog> logs, String title) {
        System.out.println("\n--- " + title + " ---");
        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
            return;
        }

        System.out.printf("%-5s | %-8s | %-18s | %-45s | %-20s%n", "ID", "User ID", "Action Type", "Description", "Timestamp");
        System.out.println("-".repeat(105));

        for (ActivityLog log : logs) {
            System.out.printf(
                    "%-5d | %-8d | %-18s | %-45s | %-20s%n",
                    log.id(),
                    log.userId(),
                    log.actionType(),
                    log.description(),
                    log.timestamp().toString()
            );
        }
        System.out.println("-".repeat(105));
    }

    public void showUpdateUserMenu(User currentAdmin) {
        System.out.println("\n========================================");
        System.out.println("         ADMIN: UPDATE USER MENU        ");
        System.out.println("========================================");

        displayAllUsersTable();

        int targetUserId = readIntInput("Enter Target User ID (or enter 0 to cancel): ");
        if (targetUserId == 0) return;

        if (targetUserId == currentAdmin.id()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        String newRole = readValidRole();
        if (newRole.equals("0")) return;

        String newStatus = readValidStatus();
        if (newStatus.equals("0")) return;

        boolean success = userDao.updateUserRoleAndStatus(currentAdmin.id(), targetUserId, newRole, newStatus);
        if (success) {
            System.out.println("SUCCESS: User role and status successfully updated.");
        } else {
            System.out.println("ERROR: Failed to update user.");
        }
    }

    private void displayAllUsersTable() {
        List<User> users = userDao.getAllUsers();
        System.out.println("\n--- USER LIST ---");
        System.out.printf("%-5s | %-15s | %-10s | %-10s%n", "ID", "Username", "Role", "Status");
        System.out.println("--------------------------------------------------");
        for (User u : users) {
            System.out.printf("%-5d | %-15s | %-10s | %-10s%n", u.id(), u.username(), u.roles(), u.status());
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
            System.out.print("Enter new role (ADMIN, CASHIER) [0 to cancel]: ");
            String role = scanner.nextLine().trim().toUpperCase();

            if (role.equals("0") || role.equals("ADMIN") || role.equals("CASHIER")) {
                return role;
            }
            System.out.println("Invalid role. Accepted values: ADMIN, CASHIER.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED, PENDING) [0 to cancel]: ");
            String status = scanner.nextLine().trim().toUpperCase();
            if (status.equals("0") || status.equals("APPROVED") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("Invalid status. Accepted values: APPROVED, PENDING.");
        }
    }
}