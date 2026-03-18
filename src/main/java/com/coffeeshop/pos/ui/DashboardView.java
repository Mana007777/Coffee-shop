package com.coffeeshop.pos.ui;

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
    private final User user;

    public DashboardView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public Scene createScene() {
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
            NewSaleView newSaleView = new NewSaleView(stage, user);
            stage.setScene(newSaleView.createScene());
            stage.setTitle("Coffee POS - New Sale");
        });

        salesHistoryButton.setOnAction(event ->
                System.out.println("Open Sales History screen")
        );

        reportsButton.setOnAction(event ->
                System.out.println("Open Reports screen")
        );

        productsButton.setOnAction(event ->
                System.out.println("Open Product Management screen")
        );

        categoriesButton.setOnAction(event ->
                System.out.println("Open Category Management screen")
        );

        logoutButton.setOnAction(event -> {
            LoginView loginView = new LoginView(stage);
            stage.setScene(loginView.createScene());
            stage.setTitle("Coffee POS - Login");
        });

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(
                titleLabel,
                welcomeLabel,
                newSaleButton,
                salesHistoryButton,
                reportsButton,
                productsButton,
                categoriesButton,
                logoutButton
        );

        return new Scene(root, 500, 450);
    }
}