package com.coffeeshop.pos;

import com.coffeeshop.pos.config.DataSeeder;
import com.coffeeshop.pos.config.DatabaseInitializer;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.UserService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Coffee POS...");

        DatabaseInitializer.initializeDatabase();
        DataSeeder.seedData();

        UserService userService = new UserService();
        User user = userService.login("admin", "admin123");

        if (user != null) {
            System.out.println("Login successful.");
            System.out.println("Welcome, " + user.getUsername() + " [" + user.getRole() + "]");
        } else {
            System.out.println("Invalid username or password.");
        }
    }
}