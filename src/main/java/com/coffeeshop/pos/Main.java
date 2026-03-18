package com.coffeeshop.pos;

import com.coffeeshop.pos.config.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Coffee POS...");
        DatabaseInitializer.initializeDatabase();
        System.out.println("Database is ready.");
    }
}