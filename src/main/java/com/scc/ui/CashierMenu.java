package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.model.User;

import java.util.Scanner;

public class CashierMenu {

    private final ActivityLogDao logDao;
    private final Scanner scanner;

    public CashierMenu(ActivityLogDao logDao, Scanner scanner) {
        this.logDao = logDao;
        this.scanner = scanner;
    }

    public void display(User currentCashier) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("        CASHIER DASHBOARD MENU          ");
            System.out.println("========================================");
            System.out.println("1. Process Ticket Sale");
            System.out.println("2. Cancel Ticket / Process Refund");
            System.out.println("3. View Today's Sales Activity");
            System.out.println("4. Logout");
            System.out.print("Select an Option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> processTicketSale(currentCashier);
                case "2" -> cancelTicketSale(currentCashier);
                case "3" -> viewCashierActivity(currentCashier);
                case "4" -> {
                    System.out.println("Logging out from Cashier menu...");
                    return;
                }
                default -> System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void processTicketSale(User cashier) {
        System.out.println("\n--- PROCESS TICKET SALE ---");
        System.out.print("Enter Movie Title: ");
        String movieTitle = scanner.nextLine().trim();

        int ticketQty = readIntInput("Enter Number of Tickets: ");
        if (ticketQty <= 0) {
            System.out.println("Invalid ticket quantity.");
            return;
        }

        double pricePerTicket = readDoubleInput("Enter Price Per Ticket (P): ");
        double totalAmount = ticketQty * pricePerTicket;

        System.out.println("\n----------------------------------------");
        System.out.println("Summary:");
        System.out.println("Movie: " + movieTitle);
        System.out.println("Quantity: " + ticketQty);
        System.out.printf("Total Amount: P%.2f%n", totalAmount);
        System.out.println("----------------------------------------");
        System.out.print("Confirm Sale? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y")) {
            System.out.printf("SUCCESS: Ticket sale processed for %s - Total: P%.2f%n", movieTitle, totalAmount);

            // Domain Business Event Log (Deliverable 8 Requirement 3)
            logDao.logAction(
                    cashier.id(),
                    "TICKET_SOLD",
                    String.format("Cashier ID %d processed sale for %d ticket(s) - Movie: %s | Total: P%.2f",
                            cashier.id(), ticketQty, movieTitle, totalAmount)
            );
        } else {
            System.out.println("Sale transaction cancelled.");
        }
    }

    private void cancelTicketSale(User cashier) {
        System.out.println("\n--- CANCEL / VOID TICKET ---");
        int ticketId = readIntInput("Enter Ticket ID to cancel: ");
        if (ticketId <= 0) return;

        System.out.print("Enter Reason for Cancellation: ");
        String reason = scanner.nextLine().trim();

        System.out.printf("SUCCESS: Ticket #%d has been status-updated to CANCELLED.%n", ticketId);

        // Record Lifecycle / State Change Log (Deliverable 8 Requirement 4)
        logDao.logAction(
                cashier.id(),
                "TICKET_CANCELLED",
                String.format("Cashier ID %d updated Ticket #%d status from CONFIRMED to CANCELLED. Reason: %s",
                        cashier.id(), ticketId, reason)
        );
    }

    private void viewCashierActivity(User cashier) {
        System.out.println("\n--- CASHIER RECENT ACTIVITY LOGS ---");
        var cashierLogs = logDao.getLogsByUserId(cashier.id());

        if (cashierLogs.isEmpty()) {
            System.out.println("No activity recorded for this session.");
            return;
        }

        System.out.printf("%-5s | %-18s | %-45s | %-20s%n", "ID", "Action Type", "Description", "Timestamp");
        System.out.println("-".repeat(95));
        for (var log : cashierLogs) {
            System.out.printf("%-5d | %-18s | %-45s | %-20s%n",
                    log.id(), log.actionType(), log.description(), log.timestamp().toString());
        }
        System.out.println("-".repeat(95));
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric integer.");
            }
        }
    }

    private double readDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid decimal amount.");
            }
        }
    }
}