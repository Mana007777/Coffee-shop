package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.OrderService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ReportsView {

    private final Stage stage;
    private final OrderService orderService;

    private final TextField dateField;
    private final TextField startDateField;
    private final TextField endDateField;
    private final TextArea resultArea;
    private final Label statusLabel;

    public ReportsView(Stage stage) {
        this.stage = stage;
        this.orderService = new OrderService();

        this.dateField = new TextField();
        this.startDateField = new TextField();
        this.endDateField = new TextField();
        this.resultArea = new TextArea();
        this.statusLabel = new Label();
    }

    public Scene createScene() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            LoginView loginView = new LoginView(stage);
            return loginView.createScene();
        }

        Label titleLabel = new Label("Reports");
        titleLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 28px;
                -fx-font-weight: bold;
                """);

        Label userLabel = new Label("User: " + user.getUsername());
        userLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.92);
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);

        VBox header = new VBox(6, titleLabel, userLabel);
        CoffeeTheme.styleHeaderBar(header);

        dateField.setPromptText("YYYY-MM-DD");
        startDateField.setPromptText("Start Date (YYYY-MM-DD)");
        endDateField.setPromptText("End Date (YYYY-MM-DD)");

        CoffeeTheme.styleTextField(dateField);
        CoffeeTheme.styleTextField(startDateField);
        CoffeeTheme.styleTextField(endDateField);

        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefRowCount(18);
        CoffeeTheme.styleTextArea(resultArea);

        CoffeeTheme.styleStatusLabel(statusLabel);

        Button todayReportButton = new Button("Today's Report");
        Button dateReportButton = new Button("Report by Date");
        Button rangeReportButton = new Button("Date Range Report");
        Button topProductsButton = new Button("Top Products");
        Button clearButton = new Button("Clear");
        Button backButton = new Button("Back");

        CoffeeTheme.stylePrimaryButton(todayReportButton);
        CoffeeTheme.styleSecondaryButton(dateReportButton);
        CoffeeTheme.styleSecondaryButton(rangeReportButton);
        CoffeeTheme.stylePrimaryButton(topProductsButton);
        CoffeeTheme.styleGhostButton(clearButton);
        CoffeeTheme.styleGhostButton(backButton);

        todayReportButton.setOnAction(event -> showTodayReport());
        dateReportButton.setOnAction(event -> showDateReport());
        rangeReportButton.setOnAction(event -> showDateRangeReport());
        topProductsButton.setOnAction(event -> showTopProductsReport());
        clearButton.setOnAction(event -> clearOutput());
        backButton.setOnAction(event -> goBackToDashboard());

        Label singleDateTitle = new Label("Report by Specific Date");
        CoffeeTheme.styleSectionTitle(singleDateTitle);

        HBox dateRow = new HBox(12, dateField, dateReportButton);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        VBox dateCard = CoffeeTheme.createCard(14);
        dateCard.getChildren().addAll(singleDateTitle, dateRow);

        Label rangeTitle = new Label("Report by Date Range");
        CoffeeTheme.styleSectionTitle(rangeTitle);

        HBox rangeRow = new HBox(12, startDateField, endDateField, rangeReportButton);
        rangeRow.setAlignment(Pos.CENTER_LEFT);

        VBox rangeCard = CoffeeTheme.createCard(14);
        rangeCard.getChildren().addAll(rangeTitle, rangeRow);

        HBox actionRow = new HBox(12, todayReportButton, topProductsButton, clearButton, backButton);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        VBox outputCard = CoffeeTheme.createCard(14);
        Label outputTitle = new Label("Report Output");
        CoffeeTheme.styleSectionTitle(outputTitle);
        outputCard.getChildren().addAll(outputTitle, resultArea, statusLabel);

        VBox root = new VBox(24, header, dateCard, rangeCard, actionRow, outputCard);
        root.setPadding(new Insets(26));
        CoffeeTheme.styleRoot(root);

        return new Scene(root, 1100, 780);
    }

    private void showTodayReport() {
        resultArea.setText(orderService.buildTodaySalesReport());
        CoffeeTheme.setStatusSuccess(statusLabel, "Loaded today's report.");
    }

    private void showDateReport() {
        String date = dateField.getText().trim();

        if (!isValidDate(date)) {
            CoffeeTheme.setStatusError(statusLabel, "Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        resultArea.setText(orderService.buildSalesReportByDate(date));
        CoffeeTheme.setStatusSuccess(statusLabel, "Loaded report for " + date + ".");
    }

    private void showDateRangeReport() {
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();

        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            CoffeeTheme.setStatusError(statusLabel, "Invalid date range. Use YYYY-MM-DD.");
            return;
        }

        if (java.time.LocalDate.parse(startDate).isAfter(java.time.LocalDate.parse(endDate))) {
            CoffeeTheme.setStatusError(statusLabel, "Start date cannot be after end date.");
            return;
        }

        resultArea.setText(orderService.buildSalesReportBetweenDates(startDate, endDate));
        CoffeeTheme.setStatusSuccess(statusLabel, "Loaded date range report.");
    }

    private void showTopProductsReport() {
        resultArea.setText(orderService.buildTopSellingProductsReport());
        CoffeeTheme.setStatusSuccess(statusLabel, "Loaded top-selling products report.");
    }

    private void clearOutput() {
        resultArea.clear();
        dateField.clear();
        startDateField.clear();
        endDateField.clear();
        CoffeeTheme.setStatusNeutral(statusLabel, "Cleared.");
    }

    private boolean isValidDate(String value) {
        try {
            java.time.LocalDate.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("Coffee POS - Dashboard");
    }
}