package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {

    private final UserService userService;
    private final Stage stage;

    public LoginView(Stage stage) {
        this.userService = new UserService();
        this.stage = stage;
    }

    public Scene createScene() {
        Label titleLabel = new Label("Coffee POS Login");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");

        Label statusLabel = new Label();

        loginButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter username and password.");
                return;
            }

            User user = userService.login(username, password);

            if (user != null) {
                DashboardView dashboardView = new DashboardView(stage, user);
                Scene dashboardScene = dashboardView.createScene();

                stage.setScene(dashboardScene);
                stage.setTitle("Coffee POS - Dashboard");
            } else {
                statusLabel.setText("Invalid username or password.");
            }
        });

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(
                titleLabel,
                usernameField,
                passwordField,
                loginButton,
                statusLabel
        );

        return new Scene(root, 400, 300);
    }
}