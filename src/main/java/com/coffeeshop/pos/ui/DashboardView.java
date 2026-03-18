package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
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
        titleLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 28px;
                -fx-font-weight: bold;
                """);

        Label welcomeLabel = new Label("Welcome back, " + user.getUsername() + "  •  Role: " + user.getRole());
        welcomeLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.92);
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);

        VBox header = new VBox(6, titleLabel, welcomeLabel);
        CoffeeTheme.styleHeaderBar(header);

        Button newSaleButton = createDashboardButton("☕ New Sale", "Start a fresh customer order");
        Button salesHistoryButton = createDashboardButton("🧾 Sales History", "Review previous orders");
        Button reportsButton = createDashboardButton("📈 Reports", "View date-based sales reports");
        Button productsButton = createDashboardButton("📦 Product Management", "Add, edit, activate products");
        Button categoriesButton = createDashboardButton("🏷 Category Management", "Manage product categories");
        Button logoutButton = createDashboardButton("↩ Logout", "End current session");

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

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        grid.add(newSaleButton, 0, 0);
        grid.add(salesHistoryButton, 1, 0);

        if (SessionManager.isAdmin()) {
            grid.add(reportsButton, 0, 1);
            grid.add(productsButton, 1, 1);
            grid.add(categoriesButton, 0, 2);
            grid.add(logoutButton, 1, 2);
        } else {
            grid.add(logoutButton, 0, 1, 2, 1);
        }

        VBox root = new VBox(28, header, grid);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        CoffeeTheme.styleRoot(root);

        return new Scene(root, 980, 640);
    }

    private Button createDashboardButton(String title, String subtitle) {
        Button button = new Button(title + "\n" + subtitle);
        button.setWrapText(true);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefSize(360, 120);
        button.setStyle("""
                -fx-background-color: #FFFDF9;
                -fx-text-fill: #2E211B;
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                -fx-background-radius: 20;
                -fx-border-color: #E6D6C8;
                -fx-border-radius: 20;
                -fx-border-width: 1;
                -fx-padding: 18;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.10), 18, 0.15, 0, 6);
                """);

        button.setOnMouseEntered(e -> button.setStyle("""
                -fx-background-color: #F8EFE6;
                -fx-text-fill: #2E211B;
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                -fx-background-radius: 20;
                -fx-border-color: #D7C0AC;
                -fx-border-radius: 20;
                -fx-border-width: 1;
                -fx-padding: 18;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.16), 20, 0.18, 0, 8);
                """));

        button.setOnMouseExited(e -> button.setStyle("""
                -fx-background-color: #FFFDF9;
                -fx-text-fill: #2E211B;
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                -fx-background-radius: 20;
                -fx-border-color: #E6D6C8;
                -fx-border-radius: 20;
                -fx-border-width: 1;
                -fx-padding: 18;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.10), 18, 0.15, 0, 6);
                """));

        return button;
    }
}