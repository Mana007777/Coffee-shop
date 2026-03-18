package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView {

    private final UserService userService;
    private final Stage stage;

    public LoginView(Stage stage) {
        this.userService = new UserService();
        this.stage = stage;
    }

    public Scene createScene() {
        Label logoLabel = new Label("☕ Coffee POS");
        CoffeeTheme.styleTitle(logoLabel);

        Label welcomeLabel = new Label("Brew better service, one order at a time");
        CoffeeTheme.styleSubtitle(welcomeLabel);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        CoffeeTheme.styleTextField(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        CoffeeTheme.styleTextField(passwordField);

        Button loginButton = new Button("Login");
        CoffeeTheme.stylePrimaryButton(loginButton);
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Label statusLabel = new Label();
        CoffeeTheme.styleStatusLabel(statusLabel);

        loginButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                CoffeeTheme.setStatusError(statusLabel, "Please enter username and password.");
                return;
            }

            User user = userService.login(username, password);

            if (user != null) {
                SessionManager.setCurrentUser(user);

                DashboardView dashboardView = new DashboardView(stage);
                Scene dashboardScene = dashboardView.createScene();

                stage.setScene(dashboardScene);
                stage.setTitle("Coffee POS - Dashboard");
            } else {
                CoffeeTheme.setStatusError(statusLabel, "Invalid username or password.");
            }
        });

        VBox loginCard = CoffeeTheme.createCard(16);
        loginCard.setAlignment(Pos.CENTER_LEFT);
        loginCard.setMaxWidth(430);
        loginCard.getChildren().addAll(
                logoLabel,
                welcomeLabel,
                usernameField,
                passwordField,
                loginButton,
                statusLabel
        );

        VBox leftHero = new VBox(14);
        leftHero.setAlignment(Pos.CENTER_LEFT);
        leftHero.setPadding(new Insets(10));

        Label heroTitle = new Label("Modern coffee shop operations");
        heroTitle.setStyle("""
                -fx-text-fill: #2E211B;
                -fx-font-size: 34px;
                -fx-font-weight: bold;
                """);

        Label heroText = new Label(
                "Manage sales, products, reports, and categories in one polished POS experience."
        );
        heroText.setWrapText(true);
        heroText.setStyle("""
                -fx-text-fill: #6E5A4E;
                -fx-font-size: 15px;
                -fx-font-weight: 600;
                """);

        Label chip1 = CoffeeTheme.createMetricLabel("Fast Checkout", "Instant cart flow");
        Label chip2 = CoffeeTheme.createMetricLabel("Inventory", "Live stock updates");
        Label chip3 = CoffeeTheme.createMetricLabel("Reports", "Daily insights");

        HBox chips = new HBox(12, chip1, chip2, chip3);
        chips.setAlignment(Pos.CENTER_LEFT);

        leftHero.getChildren().addAll(heroTitle, heroText, chips);

        HBox content = new HBox(30, leftHero, loginCard);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        HBox.setHgrow(leftHero, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        CoffeeTheme.styleRoot(root);
        root.setCenter(content);

        Scene scene = new Scene(root, 1000, 620);
        return scene;
    }
}