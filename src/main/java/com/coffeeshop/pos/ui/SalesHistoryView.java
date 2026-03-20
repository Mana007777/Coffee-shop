package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.Order;
import com.coffeeshop.pos.model.OrderItem;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class SalesHistoryView {

    private final Stage stage;
    private final OrderService orderService;

    private final ListView<Order> ordersListView;
    private final ListView<OrderItem> orderItemsListView;
    private final Label statusLabel;

    public SalesHistoryView(Stage stage) {
        this.stage = stage;
        this.orderService = new OrderService();

        this.ordersListView = new ListView<>();
        this.orderItemsListView = new ListView<>();
        this.statusLabel = new Label();
    }

    public Scene createScene() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            return new LoginView(stage).createScene();
        }

        configureControls();
        loadOrders();

        VBox header = buildHeader(user);
        HBox overviewRow = buildOverviewRow();
        VBox ordersPanel = buildOrdersPanel();
        VBox itemsPanel = buildItemsPanel();
        VBox footer = buildFooter();

        HBox centerPanel = new HBox(24, ordersPanel, itemsPanel);
        centerPanel.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(ordersPanel, Priority.ALWAYS);
        HBox.setHgrow(itemsPanel, Priority.ALWAYS);

        VBox root = new VBox(24, header, overviewRow, centerPanel, footer);
        root.setPadding(new Insets(26));
        CoffeeTheme.styleRoot(root);
        VBox.setVgrow(centerPanel, Priority.ALWAYS);

        return new Scene(root, 1240, 820);
    }

    private void configureControls() {
        CoffeeTheme.styleListView(ordersListView);
        CoffeeTheme.styleListView(orderItemsListView);
        CoffeeTheme.styleStatusLabel(statusLabel);

        ordersListView.setPrefHeight(560);
        ordersListView.setMinHeight(500);
        ordersListView.setMaxWidth(Double.MAX_VALUE);

        orderItemsListView.setPrefHeight(560);
        orderItemsListView.setMinHeight(500);
        orderItemsListView.setMaxWidth(Double.MAX_VALUE);

        statusLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        VBox.setVgrow(ordersListView, Priority.ALWAYS);
        VBox.setVgrow(orderItemsListView, Priority.ALWAYS);
    }

    private VBox buildHeader(User user) {
        Label titleLabel = new Label("مێژووی فرۆشتن");
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

        Label helperLabel = new Label("بینینی داواکارییەکان و وردەکاریی بابەتەکانی هەر فرۆشتنێک");
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
        VBox stat1 = createInfoCard("داواکارییەکان", "بینینی هەموو فرۆشتنەکان", "لە لیستی چەپدا دەر دەکەون");
        VBox stat2 = createInfoCard("بابەتەکان", "وردەکاریی هەر داواکارییەک", "دوای هەڵبژاردنی داواکاری پیشان دەدرێن");
        VBox stat3 = createInfoCard("خزمەتگوزاری", "گەڕان و نوێکردنەوە", "زانیارییەکان بە خێرایی نوێ دەکرێنەوە");

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

    private VBox buildOrdersPanel() {
        Label ordersLabel = new Label("داواکارییەکان");
        CoffeeTheme.styleSectionTitle(ordersLabel);

        Label helperLabel = new Label("داواکارییەک هەڵبژێرە بۆ بینینی وردەکارییەکانی.");
        helperLabel.setWrapText(true);
        helperLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(helperLabel);

        VBox panel = CoffeeTheme.createCard(14);
        panel.setPrefWidth(580);
        panel.setMinWidth(540);

        VBox.setVgrow(ordersListView, Priority.ALWAYS);
        panel.getChildren().addAll(ordersLabel, helperLabel, ordersListView);

        return panel;
    }

    private VBox buildItemsPanel() {
        Label itemsLabel = new Label("بابەتەکانی داواکاری");
        CoffeeTheme.styleSectionTitle(itemsLabel);

        Label helperLabel = new Label("دوای هەڵبژاردنی داواکاری، وردەکاریی بابەتەکان لێرە دەر دەکەون.");
        helperLabel.setWrapText(true);
        helperLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(helperLabel);

        VBox panel = CoffeeTheme.createCard(14);
        panel.setPrefWidth(580);
        panel.setMinWidth(540);

        VBox.setVgrow(orderItemsListView, Priority.ALWAYS);
        panel.getChildren().addAll(itemsLabel, helperLabel, orderItemsListView);

        return panel;
    }

    private VBox buildFooter() {
        Button viewDetailsButton = new Button("پیشاندانی وردەکاری");
        Button refreshButton = new Button("نوێکردنەوە");
        Button backButton = new Button("گەڕانەوە");

        CoffeeTheme.stylePrimaryButton(viewDetailsButton);
        CoffeeTheme.styleSecondaryButton(refreshButton);
        CoffeeTheme.styleGhostButton(backButton);

        viewDetailsButton.setOnAction(event -> loadSelectedOrderItems());
        refreshButton.setOnAction(event -> {
            loadOrders();
            orderItemsListView.getItems().clear();
            CoffeeTheme.setStatusNeutral(statusLabel, "مێژووی فرۆشتن نوێکرایەوە.");
        });
        backButton.setOnAction(event -> goBackToDashboard());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox controls = new HBox(12, viewDetailsButton, refreshButton, spacer, backButton);
        controls.setAlignment(Pos.CENTER_LEFT);

        VBox footer = CoffeeTheme.createCard(12);
        footer.getChildren().addAll(controls, statusLabel);
        return footer;
    }

    private void loadOrders() {
        List<Order> orders = orderService.getAllOrders();
        ObservableList<Order> orderItems = FXCollections.observableArrayList(orders);
        ordersListView.setItems(orderItems);
    }

    private void loadSelectedOrderItems() {
        Order selectedOrder = ordersListView.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            CoffeeTheme.setStatusError(statusLabel, "تکایە داواکارییەک هەڵبژێرە.");
            return;
        }

        List<OrderItem> items = orderService.getOrderItemsByOrderId(selectedOrder.getId());
        ObservableList<OrderItem> orderItemList = FXCollections.observableArrayList(items);
        orderItemsListView.setItems(orderItemList);

        if (items.isEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "هیچ بابەتێک بۆ داواکاری ژمارە " + selectedOrder.getId() + " نەدۆزرایەوە.");
        } else {
            CoffeeTheme.setStatusSuccess(statusLabel, "وردەکاریی داواکاری ژمارە " + selectedOrder.getId() + " بارکرا.");
        }
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("داشبۆردی قاوەخانە");
    }
}