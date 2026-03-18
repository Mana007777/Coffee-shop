package com.coffeeshop.pos.config;

import com.coffeeshop.pos.model.User;

public class SessionManager {

    private static User currentUser;

    private SessionManager() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void clearSession() {
        currentUser = null;
    }

    public static boolean isAdmin() {
        return currentUser != null &&
                "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    public static boolean isCashier() {
        return currentUser != null &&
                "CASHIER".equalsIgnoreCase(currentUser.getRole());
    }
}