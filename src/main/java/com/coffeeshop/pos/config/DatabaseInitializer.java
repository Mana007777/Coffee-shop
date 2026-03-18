package com.coffeeshop.pos.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        createUsersTable();
        createCategoriesTable();
        createProductsTable();
        createOrdersTable();
        createOrderItemsTable();
    }

    private static void createUsersTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                );
                """;

        execute(sql, "users");
    }

    private static void createCategoriesTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                );
                """;

        execute(sql, "categories");
    }

    private static void createProductsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE,
                category_id INTEGER,
                price REAL NOT NULL,
                stock_qty INTEGER NOT NULL DEFAULT 0,
                is_active INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY (category_id) REFERENCES categories(id)
            );
            """;

        execute(sql, "products");
    }

    private static void createOrdersTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS orders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_type TEXT NOT NULL,
                    total_amount REAL NOT NULL,
                    payment_method TEXT NOT NULL,
                    amount_paid REAL NOT NULL,
                    change_amount REAL NOT NULL,
                    created_at TEXT NOT NULL,
                    cashier_id INTEGER,
                    FOREIGN KEY (cashier_id) REFERENCES users(id)
                );
                """;

        execute(sql, "orders");
    }

    private static void createOrderItemsTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS order_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_id INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    unit_price REAL NOT NULL,
                    subtotal REAL NOT NULL,
                    note TEXT,
                    FOREIGN KEY (order_id) REFERENCES orders(id),
                    FOREIGN KEY (product_id) REFERENCES products(id)
                );
                """;

        execute(sql, "order_items");
    }

    private static void execute(String sql, String tableName) {
        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);
            System.out.println("Table ready: " + tableName);

        } catch (SQLException e) {
            System.out.println("Failed to create table " + tableName + ": " + e.getMessage());
        }
    }
}