package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final ActivityLogDao logDao;
    private final Scanner sc;

    public Authentication(UserDao userDao, ActivityLogDao logDao, Scanner scanner) {
        this.userDao = userDao;
        this.logDao = logDao;
        this.sc = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("Starlight Cinema Ticket Reservation System");
            System.out.println("==========================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Select an Option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> handleRegister();
                case "3" -> {
                    System.out.println("Exiting Program. THANK YOU FOR USING THE SYSTEM!");
                    return;
                }
                default -> System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void handleLogin() {
        System.out.print("Enter Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = sc.nextLine().trim();

        User user = userDao.loginUser(username, password);

        if (user != null) {
            System.out.println(" > Welcome " + user.username() + " | ROLE: " + user.roles());

            // Log Login Event
            logDao.logAction(
                    user.id(),
                    "USER_LOGIN",
                    String.format("User %s (ID: %d) logged in.", user.username(), user.id())
            );

            // Menu Routing based on Role
            switch (user.roles()) {
                case "ADMIN" -> new AdminMenu(userDao, logDao, sc).display(user);
                case "CASHIER" -> new CashierMenu(logDao, sc).display(user);
                default -> System.out.println("Account role not recognized or pending approval!");
            }

            // Log Logout Event
            logDao.logAction(
                    user.id(),
                    "USER_LOGOUT",
                    String.format("User %s (ID: %d) logged out.", user.username(), user.id())
            );

        } else {
            System.out.println("ERROR: Invalid credentials or account pending approval.");
        }
    }

    private void handleRegister() {
        System.out.print("Enter new Username: ");
        String newUser = sc.nextLine().trim();
        System.out.print("Enter Password: ");
        String newPass = sc.nextLine().trim();

        System.out.print("Select Role: [1] Admin [2] Cashier : ");
        String roleChoice = sc.nextLine().trim();
        String role = switch (roleChoice) {
            case "1" -> "ADMIN";
            case "2" -> "CASHIER";
            default -> null;
        };

        if (role != null) {
            userDao.registerUser(newUser, newPass, role);
        } else {
            System.out.println("Invalid Role Selection!");
        }
    }
}