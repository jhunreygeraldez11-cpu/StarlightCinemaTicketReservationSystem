package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:CinemaTicket.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        createTables(conn);
        return conn;
    }

    private static void createTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users ("
                    + "UserID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "Username TEXT UNIQUE NOT NULL, "
                    + "PasswordHash TEXT NOT NULL, "
                    + "role TEXT NOT NULL DEFAULT 'CASHIER', "
                    + "Status TEXT DEFAULT 'PENDING', "
                    + "LastLoginDate TIMESTAMP"
                    + ");";
            stmt.execute(createUsersTable);

            // 2. Auto-patch existing old tables that might be missing columns
            try {
                stmt.execute("ALTER TABLE Users ADD COLUMN role TEXT;");
            } catch (SQLException ignored) {
                // Column already exists, safe to ignore
            }

            try {
                stmt.execute("ALTER TABLE Users ADD COLUMN PasswordHash TEXT;");
            } catch (SQLException ignored) {
                // Column already exists, safe to ignore
            }

            // 3. Create other tables
            String createMoviesTable = "CREATE TABLE IF NOT EXISTS Movies ("
                    + "MovieID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "Title TEXT NOT NULL, "
                    + "Genre TEXT, "
                    + "DurationMinutes INTEGER, "
                    + "BaseTicketPrice REAL NOT NULL, "
                    + "Rating TEXT"
                    + ");";
            stmt.execute(createMoviesTable);

            String createTheatersTable = "CREATE TABLE IF NOT EXISTS Theaters ("
                    + "TheaterID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "TheaterName TEXT NOT NULL, "
                    + "SeatingCapacity INTEGER, "
                    + "ScreenType TEXT"
                    + ");";
            stmt.execute(createTheatersTable);

            String createBookingsTable = "CREATE TABLE IF NOT EXISTS Bookings ("
                    + "BookingID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "MovieID INTEGER NOT NULL, "
                    + "CashierID INTEGER NOT NULL, "
                    + "SeatNumber TEXT NOT NULL, "
                    + "BookingTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "TotalAmountPaid REAL NOT NULL, "
                    + "FOREIGN KEY (MovieID) REFERENCES Movies(MovieID), "
                    + "FOREIGN KEY (CashierID) REFERENCES Users(UserID)"
                    + ");";
            stmt.execute(createBookingsTable);

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
}