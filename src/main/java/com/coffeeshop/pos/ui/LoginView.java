package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
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
        BorderPane root = new BorderPane();
        CoffeeTheme.styleRoot(root);
        root.setStyle(root.getStyle() + """
                -fx-background-color: linear-gradient(to bottom right, #F9F4EE, #F1E4D7, #EAD8C8);
                """);

        HBox content = new HBox(34);
        content.setPadding(new Insets(34));
        content.setAlignment(Pos.CENTER);

        VBox heroSection = buildHeroSection();
        VBox loginCard = buildLoginCard();

        HBox.setHgrow(heroSection, Priority.ALWAYS);
        HBox.setHgrow(loginCard, Priority.NEVER);

        content.getChildren().addAll(heroSection, loginCard);
        root.setCenter(content);

        return new Scene(root, 1180, 720);
    }

    private VBox buildHeroSection() {
        VBox heroSection = new VBox(22);
        heroSection.setAlignment(Pos.CENTER_LEFT);
        heroSection.setPadding(new Insets(28));
        heroSection.setMaxWidth(Double.MAX_VALUE);

        Label smallTitle = new Label("☕ سیستەمی قاوەخانە");
        smallTitle.setStyle("""
                -fx-text-fill: #8A6245;
                -fx-font-size: 14px;
                -fx-font-weight: 800;
                """);

        Label heroTitle = new Label("بەخێربێیت بۆ دنیای\nقاوەی کوردی");
        heroTitle.setWrapText(true);
        heroTitle.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        heroTitle.setStyle("""
                -fx-text-fill: #2E211B;
                -fx-font-size: 42px;
                -fx-font-weight: bold;
                -fx-line-spacing: 4px;
                """);

        Label heroText = new Label(
                "فرۆشتن، بەڕێوەبردنی کاڵا، ڕاپۆرت و مێژووی داواکارییەکان "
                        + "لە یەک شوێن بە شێوەیەکی جوان و ئاسان."
        );
        heroText.setWrapText(true);
        heroText.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        heroText.setMaxWidth(560);
        heroText.setStyle("""
                -fx-text-fill: #6A5648;
                -fx-font-size: 16px;
                -fx-font-weight: 600;
                """);

        HBox featureRow1 = new HBox(14,
                createFeatureCard("⚡", "خزمەتگوزاری خێرا", "فرۆشتن بە خێرایی و ئارامی"),
                createFeatureCard("📦", "کاڵا و کۆگا", "بەڕێوەبردنی ستۆک بە وردی"),
                createFeatureCard("📈", "ڕاپۆرت", "بینینی داتا و ئەنجامەکان")
        );
        featureRow1.setAlignment(Pos.CENTER_LEFT);

        VBox quoteBox = new VBox(8);
        quoteBox.setPadding(new Insets(18));
        quoteBox.setMaxWidth(620);
        quoteBox.setStyle("""
                -fx-background-color: rgba(255,255,255,0.45);
                -fx-background-radius: 22;
                -fx-border-color: rgba(111,78,55,0.10);
                -fx-border-radius: 22;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.08), 16, 0.12, 0, 4);
                """);

        Label quoteTitle = new Label(" ئامادەی کارکردنی ڕۆژانە");
        quoteTitle.setStyle("""
                -fx-text-fill: #5B3F2E;
                -fx-font-size: 16px;
                -fx-font-weight: 800;
                """);

        Label quoteText = new Label("تامی ڕەسەن، خزمەتگوزاری باش، و سیستەمێکی مۆدێرن بۆ قاوەخانەکەت.");
        quoteText.setWrapText(true);
        quoteText.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        quoteText.setStyle("""
                -fx-text-fill: #6E5A4E;
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);

        quoteBox.getChildren().addAll(quoteTitle, quoteText);

        heroSection.getChildren().addAll(
                smallTitle,
                heroTitle,
                heroText,
                featureRow1,
                quoteBox
        );

        return heroSection;
    }

    private VBox buildLoginCard() {
        Label topBadge = new Label("LOGIN");
        topBadge.setStyle("""
                -fx-text-fill: #8A6245;
                -fx-font-size: 12px;
                -fx-font-weight: 800;
                -fx-background-color: #F3E7DA;
                -fx-background-radius: 999;
                -fx-padding: 8 14 8 14;
                """);

        Label logoLabel = new Label("قاوەخانەی کوردی");
        logoLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        logoLabel.setStyle("""
                -fx-text-fill: #2E211B;
                -fx-font-size: 30px;
                -fx-font-weight: bold;
                """);

        Label welcomeLabel = new Label("تام و بۆن لای خۆمانە");
        welcomeLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        welcomeLabel.setStyle("""
                -fx-text-fill: #7A675C;
                -fx-font-size: 15px;
                -fx-font-weight: 700;
                """);

        Label usernameLabel = new Label("ناوی بەکارهێنەر");
        usernameLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(usernameLabel);

        TextField usernameField = new TextField();
        usernameField.setPromptText("ناو");
        CoffeeTheme.styleTextField(usernameField);
        usernameField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        usernameField.setMaxWidth(Double.MAX_VALUE);
        usernameField.setPrefHeight(46);

        Label passwordLabel = new Label("وشەی نهێنی");
        passwordLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(passwordLabel);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("وشەی نهێنی");
        CoffeeTheme.styleTextField(passwordField);
        passwordField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setPrefHeight(46);

        Button loginButton = new Button("چوونەژوورەوە");
        CoffeeTheme.stylePrimaryButton(loginButton);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(50);

        Label statusLabel = new Label();
        CoffeeTheme.styleStatusLabel(statusLabel);
        statusLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        loginButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                CoffeeTheme.setStatusError(statusLabel, "تکایە ناو و وشەی نهێنی بنووسە.");
                return;
            }

            User user = userService.login(username, password);

            if (user != null) {
                SessionManager.setCurrentUser(user);

                DashboardView dashboardView = new DashboardView(stage);
                Scene dashboardScene = dashboardView.createScene();

                stage.setScene(dashboardScene);
                stage.setTitle("داشبۆردی قاوەخانە");
            } else {
                CoffeeTheme.setStatusError(statusLabel, "ناوی بەکارهێنەر یان وشەی نهێنی هەڵەیە.");
            }
        });

        HBox quickInfo = new HBox(10,
                createMiniChip("مۆدێرن"),
                createMiniChip("خێرا"),
                createMiniChip("ئاسان")
        );
        quickInfo.setAlignment(Pos.CENTER_LEFT);

        VBox loginCard = new VBox(16);
        loginCard.setAlignment(Pos.CENTER_LEFT);
        loginCard.setPadding(new Insets(26));
        loginCard.setPrefWidth(430);
        loginCard.setMaxWidth(430);
        loginCard.setStyle("""
                -fx-background-color: rgba(255,253,249,0.96);
                -fx-background-radius: 28;
                -fx-border-color: #E8D9CC;
                -fx-border-radius: 28;
                -fx-border-width: 1;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.14), 24, 0.18, 0, 8);
                """);

        loginCard.getChildren().addAll(
                topBadge,
                logoLabel,
                welcomeLabel,
                quickInfo,
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                loginButton,
                statusLabel
        );

        return loginCard;
    }

    private VBox createFeatureCard(String icon, String title, String subtitle) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("""
                -fx-font-size: 22px;
                """);

        Label titleLabel = new Label(title);
        titleLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        titleLabel.setStyle("""
                -fx-text-fill: #2E211B;
                -fx-font-size: 16px;
                -fx-font-weight: 800;
                """);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setWrapText(true);
        subtitleLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        subtitleLabel.setStyle("""
                -fx-text-fill: #7A675C;
                -fx-font-size: 13px;
                -fx-font-weight: 600;
                """);

        VBox box = new VBox(10, iconLabel, titleLabel, subtitleLabel);
        box.setPadding(new Insets(18));
        box.setPrefWidth(180);
        box.setStyle("""
                -fx-background-color: rgba(255,253,249,0.70);
                -fx-background-radius: 22;
                -fx-border-color: rgba(111,78,55,0.08);
                -fx-border-radius: 22;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.06), 12, 0.12, 0, 4);
                """);

        return box;
    }

    private Label createMiniChip(String text) {
        Label chip = new Label(text);
        chip.setStyle("""
                -fx-text-fill: #6F4E37;
                -fx-font-size: 12px;
                -fx-font-weight: 800;
                -fx-padding: 8 12 8 12;
                -fx-background-color: #F4E8DB;
                -fx-background-radius: 999;
                -fx-border-color: #E2D0BF;
                -fx-border-radius: 999;
                """);
        return chip;
    }
}