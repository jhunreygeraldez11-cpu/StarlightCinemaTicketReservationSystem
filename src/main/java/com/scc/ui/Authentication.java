package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner sc;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.sc = scanner;
    }

    public void start() {

        System.out.println("==========================================");
        System.out.println("Starlight Cinema Ticket Reservation System");
        System.out.println("==========================================");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Select an Option: ");
        String choice = sc.nextLine();

        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "2":
                System.out.print("Enter new Username: ");
                String newUser = sc.nextLine().trim();
                System.out.print("Enter Password: ");
                String newPass = sc.nextLine();

                System.out.print("Select Role: [1] Admin [2] Cashier [3] Manager : ");
                String roleChoice = sc.nextLine().trim();
                String role;

                switch (roleChoice) {
                    case "1" -> role = "ADMIN";
                    case "2" -> role = "CASHIER";
                    case "3" -> role = "MANAGER";
                    default -> role = null;
                }

                if (role != null) {
                    userDao.registerUser(newUser, newPass, role);
                } else {
                    System.out.println("Invalid Role Selection!");
                }
                break;
            case "3":
                System.out.print("Exiting Program. THANK YOU FOR USING THE SYSTEM!");
                break;
        }
    }

    private void handleLogin() {
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User user = userDao.loginUser(username, password);

        if (user != null) {
            System.out.println(" > Welcome " + user.username() + " | ROLE: " + user.roles());

            switch (user.roles()) {
                case "ADMIN" -> new AdminMenu(userDao, sc).display(user);
                default -> System.out.println("Account roles not found!");
            }
        }
    }
}