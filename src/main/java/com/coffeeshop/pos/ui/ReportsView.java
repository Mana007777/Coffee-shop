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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        Label userLabel = new Label("User: " + user.getUsername());

        dateField.setPromptText("YYYY-MM-DD");
        startDateField.setPromptText("Start Date (YYYY-MM-DD)");
        endDateField.setPromptText("End Date (YYYY-MM-DD)");

        resultArea.setEditable(false);
        resultArea.setWrapText(true);

        Button todayReportButton = new Button("Today's Report");
        Button dateReportButton = new Button("Report by Date");
        Button rangeReportButton = new Button("Report by Date Range");
        Button topProductsButton = new Button("Top Products");
        Button clearButton = new Button("Clear");
        Button backButton = new Button("Back");

        todayReportButton.setOnAction(event -> showTodayReport());
        dateReportButton.setOnAction(event -> showDateReport());
        rangeReportButton.setOnAction(event -> showDateRangeReport());
        topProductsButton.setOnAction(event -> showTopProductsReport());
        clearButton.setOnAction(event -> clearOutput());
        backButton.setOnAction(event -> goBackToDashboard());

        HBox dateControls = new HBox(10,
                new Label("Date:"),
                dateField,
                dateReportButton
        );
        dateControls.setAlignment(Pos.CENTER_LEFT);

        HBox rangeControls = new HBox(10,
                new Label("From:"),
                startDateField,
                new Label("To:"),
                endDateField,
                rangeReportButton
        );
        rangeControls.setAlignment(Pos.CENTER_LEFT);

        HBox actionControls = new HBox(10,
                todayReportButton,
                topProductsButton,
                clearButton,
                backButton
        );
        actionControls.setAlignment(Pos.CENTER);

        VBox topPanel = new VBox(10,
                titleLabel,
                userLabel,
                dateControls,
                rangeControls,
                actionControls
        );
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(10));

        VBox centerPanel = new VBox(10,
                new Label("Report Output"),
                resultArea,
                statusLabel
        );
        centerPanel.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setTop(topPanel);
        root.setCenter(centerPanel);

        return new Scene(root, 1000, 650);
    }

    private void showTodayReport() {
        resultArea.setText(orderService.buildTodaySalesReport());
        statusLabel.setText("Loaded today's report.");
    }

    private void showDateReport() {
        String date = dateField.getText().trim();

        if (!isValidDate(date)) {
            statusLabel.setText("Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        resultArea.setText(orderService.buildSalesReportByDate(date));
        statusLabel.setText("Loaded report for " + date + ".");
    }

    private void showDateRangeReport() {
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();

        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            statusLabel.setText("Invalid date range. Use YYYY-MM-DD.");
            return;
        }

        if (java.time.LocalDate.parse(startDate).isAfter(java.time.LocalDate.parse(endDate))) {
            statusLabel.setText("Start date cannot be after end date.");
            return;
        }

        resultArea.setText(orderService.buildSalesReportBetweenDates(startDate, endDate));
        statusLabel.setText("Loaded date range report.");
    }

    private void showTopProductsReport() {
        resultArea.setText(orderService.buildTopSellingProductsReport());
        statusLabel.setText("Loaded top-selling products report.");
    }

    private void clearOutput() {
        resultArea.clear();
        dateField.clear();
        startDateField.clear();
        endDateField.clear();
        statusLabel.setText("Cleared.");
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