package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
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

        VBox heroSection = buildHeroSection(user);
        HBox statsRow = buildStatsRow(user);
        GridPane menuGrid = buildMenuGrid(user);
        VBox bottomPanel = buildBottomPanel(user);

        VBox content = new VBox(24, heroSection, statsRow, menuGrid, bottomPanel);
        content.setPadding(new Insets(28));
        content.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("""
                -fx-background: transparent;
                -fx-background-color: transparent;
                """);

        BorderPane root = new BorderPane(scrollPane);
        CoffeeTheme.styleRoot(root);

        return new Scene(root, 1220, 820);
    }

    private VBox buildHeroSection(User user) {
        Label eyebrowLabel = new Label("COFFEE SHOP POS");
        eyebrowLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.80);
                -fx-font-size: 12px;
                -fx-font-weight: 800;
                -fx-letter-spacing: 1px;
                """);

        Label titleLabel = new Label("Dashboard");
        titleLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 34px;
                -fx-font-weight: bold;
                """);

        Label welcomeLabel = new Label(
                "Welcome back, " + user.getUsername() + "  •  Role: " + user.getRole()
        );
        welcomeLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.92);
                -fx-font-size: 15px;
                -fx-font-weight: 600;
                """);

        Label subTextLabel = new Label(
                "Access sales, reports, products, and categories from one clean control center."
        );
        subTextLabel.setWrapText(true);
        subTextLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.88);
                -fx-font-size: 14px;
                -fx-font-weight: 500;
                """);

        VBox leftSide = new VBox(8, eyebrowLabel, titleLabel, welcomeLabel, subTextLabel);
        leftSide.setAlignment(Pos.CENTER_LEFT);

        Label roleBadge = new Label(SessionManager.isAdmin() ? "ADMIN ACCESS" : "CASHIER ACCESS");
        roleBadge.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 12px;
                -fx-font-weight: 800;
                -fx-padding: 10 16 10 16;
                -fx-background-color: rgba(255,255,255,0.16);
                -fx-background-radius: 999;
                -fx-border-color: rgba(255,255,255,0.22);
                -fx-border-radius: 999;
                """);

        Label quoteLabel = new Label("Run the shop smoothly, one order at a time.");
        quoteLabel.setWrapText(true);
        quoteLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.82);
                -fx-font-size: 13px;
                -fx-font-style: italic;
                -fx-font-weight: 600;
                """);

        VBox rightSide = new VBox(14, roleBadge, quoteLabel);
        rightSide.setAlignment(Pos.TOP_RIGHT);
        rightSide.setMaxWidth(260);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topRow = new HBox(16, leftSide, spacer, rightSide);
        topRow.setAlignment(Pos.TOP_LEFT);

        HBox metricsRow = new HBox(
                14,
                createHeroMetric("Orders", "Track every sale"),
                createHeroMetric("Inventory", "Live stock control"),
                createHeroMetric("Reports", "Business insights")
        );
        metricsRow.setAlignment(Pos.CENTER_LEFT);

        VBox hero = new VBox(20, topRow, metricsRow);
        hero.setPadding(new Insets(26));
        hero.setStyle("""
                -fx-background-color: linear-gradient(to right, #5C3B29, #7A523A, #9A6B46);
                -fx-background-radius: 26;
                -fx-border-color: rgba(255,255,255,0.10);
                -fx-border-radius: 26;
                -fx-border-width: 1;
                -fx-effect: dropshadow(gaussian, rgba(60,35,20,0.22), 26, 0.22, 0, 10);
                """);

        return hero;
    }

    private HBox buildStatsRow(User user) {
        VBox stat1 = createStatCard("Session User", user.getUsername(), "Current signed-in account");
        VBox stat2 = createStatCard("Role", user.getRole(), "Access level for this session");
        VBox stat3 = createStatCard(
                "Available Modules",
                SessionManager.isAdmin() ? "6 modules" : "3 modules",
                SessionManager.isAdmin()
                        ? "Sales, history, reports, products, categories, logout"
                        : "Sales, history, logout"
        );

        HBox row = new HBox(18, stat1, stat2, stat3);
        row.setAlignment(Pos.CENTER);
        HBox.setHgrow(stat1, Priority.ALWAYS);
        HBox.setHgrow(stat2, Priority.ALWAYS);
        HBox.setHgrow(stat3, Priority.ALWAYS);

        return row;
    }

    private GridPane buildMenuGrid(User user) {
        Button newSaleButton = createDashboardCard(
                "☕ New Sale",
                "Start a fresh customer order and manage the active cart."
        );

        Button salesHistoryButton = createDashboardCard(
                "🧾 Sales History",
                "Review previous orders and inspect sold items."
        );

        Button reportsButton = createDashboardCard(
                "📈 Reports",
                "Open date-based reports and top-selling product insights."
        );

        Button productsButton = createDashboardCard(
                "📦 Product Management",
                "Add products, update pricing, adjust stock, and manage status."
        );

        Button categoriesButton = createDashboardCard(
                "🏷 Category Management",
                "Create and review categories used for product organization."
        );

        Button logoutButton = createDashboardCard(
                "↩ Logout",
                "Sign out safely and return to the login screen."
        );

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

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(col1, col2);

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

        return grid;
    }

    private VBox buildBottomPanel(User user) {
        Label sectionTitle = new Label("Quick Overview");
        CoffeeTheme.styleSectionTitle(sectionTitle);

        Label description = new Label(
                SessionManager.isAdmin()
                        ? "As an admin, you can manage operations, inventory, reports, and category setup."
                        : "As a cashier, you can create new sales, review order history, and end your session."
        );
        description.setWrapText(true);
        description.setStyle("""
                -fx-text-fill: #6E5A4E;
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);

        HBox chips = new HBox(
                12,
                createInfoChip("User: " + user.getUsername()),
                createInfoChip("Role: " + user.getRole()),
                createInfoChip(SessionManager.isAdmin() ? "Full Access" : "Limited Access")
        );
        chips.setAlignment(Pos.CENTER_LEFT);

        VBox panel = CoffeeTheme.createCard(14);
        panel.getChildren().addAll(sectionTitle, description, chips);
        return panel;
    }

    private VBox createHeroMetric(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 16px;
                -fx-font-weight: 800;
                """);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setWrapText(true);
        subtitleLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.84);
                -fx-font-size: 12px;
                -fx-font-weight: 600;
                """);

        VBox box = new VBox(6, titleLabel, subtitleLabel);
        box.setPrefWidth(180);
        box.setPadding(new Insets(14, 16, 14, 16));
        box.setStyle("""
                -fx-background-color: rgba(255,255,255,0.10);
                -fx-background-radius: 18;
                -fx-border-color: rgba(255,255,255,0.12);
                -fx-border-radius: 18;
                """);
        return box;
    }

    private VBox createStatCard(String title, String value, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("""
                -fx-text-fill: #7A675C;
                -fx-font-size: 12px;
                -fx-font-weight: 700;
                """);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("""
                -fx-text-fill: #2E211B;
                -fx-font-size: 22px;
                -fx-font-weight: 800;
                """);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setWrapText(true);
        subtitleLabel.setStyle("""
                -fx-text-fill: #8B776C;
                -fx-font-size: 12px;
                -fx-font-weight: 600;
                """);

        VBox card = new VBox(8, titleLabel, valueLabel, subtitleLabel);
        card.setPadding(new Insets(18));
        card.setPrefHeight(120);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("""
                -fx-background-color: #FFFDF9;
                -fx-background-radius: 20;
                -fx-border-color: #E6D6C8;
                -fx-border-radius: 20;
                -fx-border-width: 1;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.08), 16, 0.15, 0, 5);
                """);

        return card;
    }

    private Label createInfoChip(String text) {
        Label chip = new Label(text);
        chip.setStyle("""
                -fx-text-fill: #6F4E37;
                -fx-font-size: 12px;
                -fx-font-weight: 800;
                -fx-padding: 10 14 10 14;
                -fx-background-color: #F4E8DB;
                -fx-background-radius: 999;
                -fx-border-color: #E2D0BF;
                -fx-border-radius: 999;
                """);
        return chip;
    }

    private Button createDashboardCard(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setStyle("""
                -fx-text-fill: #2E211B;
                -fx-font-size: 22px;
                -fx-font-weight: 800;
                """);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setWrapText(true);
        subtitleLabel.setStyle("""
                -fx-text-fill: #7A675C;
                -fx-font-size: 13px;
                -fx-font-weight: 600;
                """);

        Label actionHint = new Label("Open module");
        actionHint.setStyle("""
                -fx-text-fill: #6F4E37;
                -fx-font-size: 12px;
                -fx-font-weight: 800;
                -fx-padding: 8 12 8 12;
                -fx-background-color: #F5E9DD;
                -fx-background-radius: 999;
                """);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox content = new VBox(10, titleLabel, subtitleLabel, spacer, actionHint);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(22));
        content.setFillWidth(true);

        Button button = new Button();
        button.setGraphic(content);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(170);
        button.setMinHeight(170);
        button.setAlignment(Pos.TOP_LEFT);
        button.setStyle(cardBaseStyle());

        GridPane.setHgrow(button, Priority.ALWAYS);

        button.setOnMouseEntered(e -> button.setStyle(cardHoverStyle()));
        button.setOnMouseExited(e -> button.setStyle(cardBaseStyle()));

        return button;
    }

    private String cardBaseStyle() {
        return """
                -fx-background-color: linear-gradient(to bottom right, #FFFDF9, #FBF4EC);
                -fx-background-radius: 24;
                -fx-border-color: #E6D6C8;
                -fx-border-radius: 24;
                -fx-border-width: 1;
                -fx-cursor: hand;
                -fx-padding: 0;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.10), 18, 0.16, 0, 6);
                """;
    }

    private String cardHoverStyle() {
        return """
                -fx-background-color: linear-gradient(to bottom right, #FFF9F2, #F6EADD);
                -fx-background-radius: 24;
                -fx-border-color: #D7C0AC;
                -fx-border-radius: 24;
                -fx-border-width: 1;
                -fx-cursor: hand;
                -fx-padding: 0;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.18), 22, 0.20, 0, 10);
                """;
    }
}