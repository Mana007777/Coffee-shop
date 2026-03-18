package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardView {

    private final Stage stage;

    public DashboardView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            LoginView loginView = new LoginView(stage);
            return loginView.createScene();
        }

        Label titleLabel = new Label("Coffee POS Dashboard");
        Label welcomeLabel = new Label(
                "Welcome, " + user.getUsername() + " [" + user.getRole() + "]"
        );

        Button newSaleButton = new Button("New Sale");
        Button salesHistoryButton = new Button("Sales History");
        Button reportsButton = new Button("Reports");
        Button productsButton = new Button("Product Management");
        Button categoriesButton = new Button("Category Management");
        Button logoutButton = new Button("Logout");

        newSaleButton.setMaxWidth(Double.MAX_VALUE);
        salesHistoryButton.setMaxWidth(Double.MAX_VALUE);
        reportsButton.setMaxWidth(Double.MAX_VALUE);
        productsButton.setMaxWidth(Double.MAX_VALUE);
        categoriesButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setMaxWidth(Double.MAX_VALUE);

        newSaleButton.setOnAction(event -> {
            NewSaleView newSaleView = new NewSaleView(stage);
            stage.setScene(newSaleView.createScene());
            stage.setTitle("Coffee POS - New Sale");
        });

        salesHistoryButton.setOnAction(event -> {
            SalesHistoryView salesHistoryView = new SalesHistoryView(stage);
            stage.setScene(salesHistoryView.createScene());
            stage.setTitle("Coffee POS - Sales History");
        });

        reportsButton.setOnAction(event -> {
            ReportsView reportsView = new ReportsView(stage);
            stage.setScene(reportsView.createScene());
            stage.setTitle("Coffee POS - Reports");
        });

        productsButton.setOnAction(event -> {
            ProductManagementView productManagementView = new ProductManagementView(stage);
            stage.setScene(productManagementView.createScene());
            stage.setTitle("Coffee POS - Product Management");
        });

        categoriesButton.setOnAction(event -> {
            CategoryManagementView categoryManagementView = new CategoryManagementView(stage);
            stage.setScene(categoryManagementView.createScene());
            stage.setTitle("Coffee POS - Category Management");
        });

        logoutButton.setOnAction(event -> {
            SessionManager.clearSession();

            LoginView loginView = new LoginView(stage);
            stage.setScene(loginView.createScene());
            stage.setTitle("Coffee POS - Login");
        });

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titleLabel, welcomeLabel, newSaleButton, salesHistoryButton);

        if (SessionManager.isAdmin()) {
            root.getChildren().addAll(reportsButton, productsButton, categoriesButton);
        }

        root.getChildren().add(logoutButton);

        return new Scene(root, 500, 450);
    }
}