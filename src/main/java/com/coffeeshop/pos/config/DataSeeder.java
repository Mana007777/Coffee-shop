package com.coffeeshop.pos.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSeeder {

    public static void seedData() {
        insertDefaultAdmin();
        insertDefaultCashier();
        insertDefaultCategories();
        insertDefaultProducts();
    }

    private static void insertDefaultAdmin() {
        String sql = """
                INSERT OR IGNORE INTO users (username, password, role)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "admin");
            statement.setString(2, "admin123");
            statement.setString(3, "ADMIN");
            statement.executeUpdate();

            System.out.println("Default admin ready.");

        } catch (SQLException e) {
            System.out.println("Failed to insert default admin: " + e.getMessage());
        }
    }

    private static void insertDefaultCategories() {
        insertCategory("Coffee");
        insertCategory("Tea");
        insertCategory("Dessert");
    }

    private static void insertCategory(String name) {
        String sql = """
                INSERT OR IGNORE INTO categories (name)
                VALUES (?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.executeUpdate();

            System.out.println("Category ready: " + name);

        } catch (SQLException e) {
            System.out.println("Failed to insert category " + name + ": " + e.getMessage());
        }
    }

    private static void insertDefaultProducts() {
        insertProduct("Espresso", 1, 2.50, 100);
        insertProduct("Cappuccino", 1, 3.50, 100);
        insertProduct("Latte", 1, 4.00, 100);
        insertProduct("Green Tea", 2, 2.00, 100);
        insertProduct("Cheesecake", 3, 5.00, 20);
    }

    private static void insertProduct(String name, int categoryId, double price, int stockQty) {
        String sql = """
                INSERT OR IGNORE INTO products (name, category_id, price, stock_qty, is_active)
                VALUES (?, ?, ?, ?, 1)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setInt(2, categoryId);
            statement.setDouble(3, price);
            statement.setInt(4, stockQty);
            statement.executeUpdate();

            System.out.println("Product ready: " + name);

        } catch (SQLException e) {
            System.out.println("Failed to insert product " + name + ": " + e.getMessage());
        }
    }
    private static void insertDefaultCashier() {
        String sql = """
            INSERT OR IGNORE INTO users (username, password, role)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "cashier");
            statement.setString(2, "cashier123");
            statement.setString(3, "CASHIER");
            statement.executeUpdate();

            System.out.println("Default cashier ready.");

        } catch (SQLException e) {
            System.out.println("Failed to insert default cashier: " + e.getMessage());
        }
    }
}