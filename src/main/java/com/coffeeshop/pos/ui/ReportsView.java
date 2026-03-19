package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.OrderService;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
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
            return new LoginView(stage).createScene();
        }

        configureControls();

        VBox header = buildHeader(user);
        HBox overviewRow = buildOverviewRow();
        VBox dateCard = buildSingleDateCard();
        VBox rangeCard = buildDateRangeCard();
        HBox quickActionsRow = buildQuickActionsRow();
        VBox outputCard = buildOutputCard();

        VBox root = new VBox(24, header, overviewRow, dateCard, rangeCard, quickActionsRow, outputCard);
        root.setPadding(new Insets(26));
        CoffeeTheme.styleRoot(root);
        VBox.setVgrow(outputCard, Priority.ALWAYS);

        return new Scene(root, 1160, 840);
    }

    private void configureControls() {
        dateField.setPromptText("بەروار (YYYY-MM-DD)");
        startDateField.setPromptText("بەرواری دەستپێک (YYYY-MM-DD)");
        endDateField.setPromptText("بەرواری کۆتایی (YYYY-MM-DD)");

        styleInput(dateField);
        styleInput(startDateField);
        styleInput(endDateField);

        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefRowCount(18);
        resultArea.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleTextArea(resultArea);
        resultArea.setStyle(resultArea.getStyle() + """
                -fx-font-size: 14px;
                -fx-alignment: top-right;
                """);

        CoffeeTheme.styleStatusLabel(statusLabel);
        statusLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    }

    private void styleInput(TextField field) {
        CoffeeTheme.styleTextField(field);
        field.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setPrefHeight(46);
        field.setStyle(field.getStyle() + """
                -fx-alignment: center-right;
                """);
    }

    private VBox buildHeader(User user) {
        Label titleLabel = new Label("ڕاپۆرتەکان");
        titleLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 30px;
                -fx-font-weight: bold;
                """);

        Label userLabel = new Label("بەکارهێنەر: " + user.getUsername());
        userLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.92);
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);

        Label helperLabel = new Label("بینینی ڕاپۆرتی ئەمڕۆ، بەروارێک، ماوەیەک و باشترین کاڵاکان");
        helperLabel.setWrapText(true);
        helperLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.86);
                -fx-font-size: 13px;
                -fx-font-weight: 500;
                """);

        VBox header = new VBox(8, titleLabel, userLabel, helperLabel);
        CoffeeTheme.styleHeaderBar(header);
        return header;
    }

    private HBox buildOverviewRow() {
        VBox stat1 = createInfoCard("ڕاپۆرتی ئەمڕۆ", "خێرا و ئامادە", "بینینی فرۆشتنی هەمان ڕۆژ");
        VBox stat2 = createInfoCard("ڕاپۆرتی بەروار", "گەڕان بە بەروار", "دەتوانیت ڕۆژێکی دیاریکراو ببینیت");
        VBox stat3 = createInfoCard("باشترین کاڵاکان", "زانیاری گرنگ", "بزانە کام کاڵا زیاتر دەفرۆشرێت");

        HBox row = new HBox(18, stat1, stat2, stat3);
        row.setAlignment(Pos.CENTER);
        HBox.setHgrow(stat1, Priority.ALWAYS);
        HBox.setHgrow(stat2, Priority.ALWAYS);
        HBox.setHgrow(stat3, Priority.ALWAYS);
        return row;
    }

    private VBox createInfoCard(String title, String value, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("""
                -fx-text-fill: #7A675C;
                -fx-font-size: 12px;
                -fx-font-weight: 700;
                """);

        Label valueLabel = new Label(value);
        valueLabel.setWrapText(true);
        valueLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        valueLabel.setStyle("""
                -fx-text-fill: #2E211B;
                -fx-font-size: 19px;
                -fx-font-weight: 800;
                """);

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setWrapText(true);
        subtitleLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
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

    private VBox buildSingleDateCard() {
        Label title = new Label("ڕاپۆرت بە بەرواری دیاریکراو");
        CoffeeTheme.styleSectionTitle(title);

        Label helper = new Label("بەروارێک بنووسە بۆ بینینی زانیاریی فرۆشتن لەو ڕۆژەدا.");
        helper.setWrapText(true);
        helper.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(helper);

        Button dateReportButton = new Button("پیشاندانی ڕاپۆرت");
        CoffeeTheme.styleSecondaryButton(dateReportButton);
        dateReportButton.setOnAction(event -> showDateReport());

        HBox row = new HBox(12, dateField, dateReportButton);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(dateField, Priority.ALWAYS);

        VBox card = CoffeeTheme.createCard(14);
        card.getChildren().addAll(title, helper, row);
        return card;
    }

    private VBox buildDateRangeCard() {
        Label title = new Label("ڕاپۆرت بۆ ماوەی بەروار");
        CoffeeTheme.styleSectionTitle(title);

        Label helper = new Label("بەرواری دەستپێک و کۆتایی دیاری بکە بۆ درووستکردنی ڕاپۆرتی ماوە.");
        helper.setWrapText(true);
        helper.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(helper);

        Button rangeReportButton = new Button("پیشاندانی ڕاپۆرتی ماوە");
        CoffeeTheme.styleSecondaryButton(rangeReportButton);
        rangeReportButton.setOnAction(event -> showDateRangeReport());

        HBox row = new HBox(12, startDateField, endDateField, rangeReportButton);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(startDateField, Priority.ALWAYS);
        HBox.setHgrow(endDateField, Priority.ALWAYS);

        VBox card = CoffeeTheme.createCard(14);
        card.getChildren().addAll(title, helper, row);
        return card;
    }

    private HBox buildQuickActionsRow() {
        Button todayReportButton = new Button("ڕاپۆرتی ئەمڕۆ");
        Button topProductsButton = new Button("باشترین کاڵاکان");
        Button clearButton = new Button("پاککردنەوە");
        Button backButton = new Button("گەڕانەوە");

        CoffeeTheme.stylePrimaryButton(todayReportButton);
        CoffeeTheme.stylePrimaryButton(topProductsButton);
        CoffeeTheme.styleGhostButton(clearButton);
        CoffeeTheme.styleGhostButton(backButton);

        todayReportButton.setOnAction(event -> showTodayReport());
        topProductsButton.setOnAction(event -> showTopProductsReport());
        clearButton.setOnAction(event -> clearOutput());
        backButton.setOnAction(event -> goBackToDashboard());

        HBox row = new HBox(12, todayReportButton, topProductsButton, clearButton, backButton);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox buildOutputCard() {
        Label outputTitle = new Label("دەرئەنجامی ڕاپۆرت");
        CoffeeTheme.styleSectionTitle(outputTitle);

        Label helper = new Label("ئەنجامەکانی ڕاپۆرت لێرە دەردەکەون.");
        helper.setWrapText(true);
        helper.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(helper);

        VBox card = CoffeeTheme.createCard(14);
        VBox.setVgrow(resultArea, Priority.ALWAYS);
        card.getChildren().addAll(outputTitle, helper, resultArea, statusLabel);
        return card;
    }

    private void showTodayReport() {
        resultArea.setText(orderService.buildTodaySalesReport());
        CoffeeTheme.setStatusSuccess(statusLabel, "ڕاپۆرتی ئەمڕۆ بارکرا.");
    }

    private void showDateReport() {
        String date = dateField.getText().trim();

        if (!isValidDate(date)) {
            CoffeeTheme.setStatusError(statusLabel, "شێوازی بەروار هەڵەیە. تکایە YYYY-MM-DD بەکاربهێنە.");
            return;
        }

        resultArea.setText(orderService.buildSalesReportByDate(date));
        CoffeeTheme.setStatusSuccess(statusLabel, "ڕاپۆرتی بەرواری " + date + " بارکرا.");
    }

    private void showDateRangeReport() {
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();

        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            CoffeeTheme.setStatusError(statusLabel, "بەرواری ماوە هەڵەیە. تکایە YYYY-MM-DD بەکاربهێنە.");
            return;
        }

        if (java.time.LocalDate.parse(startDate).isAfter(java.time.LocalDate.parse(endDate))) {
            CoffeeTheme.setStatusError(statusLabel, "بەرواری دەستپێک نابێت لە بەرواری کۆتایی دواوەتر بێت.");
            return;
        }

        resultArea.setText(orderService.buildSalesReportBetweenDates(startDate, endDate));
        CoffeeTheme.setStatusSuccess(statusLabel, "ڕاپۆرتی ماوە بارکرا.");
    }

    private void showTopProductsReport() {
        resultArea.setText(orderService.buildTopSellingProductsReport());
        CoffeeTheme.setStatusSuccess(statusLabel, "ڕاپۆرتی باشترین کاڵاکان بارکرا.");
    }

    private void clearOutput() {
        resultArea.clear();
        dateField.clear();
        startDateField.clear();
        endDateField.clear();
        CoffeeTheme.setStatusNeutral(statusLabel, "خانەکان پاککرانەوە.");
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
        stage.setTitle("داشبۆردی قاوەخانە");
    }
}