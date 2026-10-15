package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class Menu {
    private UserDao userDao = new UserDao();
    private Scanner scanner = new Scanner(System.in);

    public void showMainMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== STARLIGHT CINEMA TICKET RESERVATION SYSTEM ===");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("choice option (1-3): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    handleRegister();
                    break;
                case 2:
                    handleLogin();
                    break;
                case 3:
                    exit = true;
                    System.out.println("Thank you for using Starlight Cinema System!");
                    break;
                default:
                    System.out.println("Invalid choice! Choice again.");
            }
        }
    }

    private void handleRegister() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (ADMIN / CASHIER / MANAGER): ");
        String role = scanner.nextLine().toUpperCase();

        User newUser = new User(username, password, role);
        if (userDao.registerUser(newUser)) {
            System.out.println("Registration successful! Account status: PENDING");
        }
    }

    private void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = userDao.loginUser(username, password);
        if (user != null) {
            System.out.println("\nLogin Successful! Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
            System.out.println("Account Status: " + user.getStatus());

            boolean loggedIn = true;
            while (loggedIn) {
                System.out.println("\n--- User Dashboard ---");
                System.out.println("1. Logout");
                System.out.print("Pili og option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    loggedIn = false;
                    System.out.println("Successfully logged out.");
                }
            }
        } else {
            System.out.println("Invalid username or password!");
        }
    }
}
