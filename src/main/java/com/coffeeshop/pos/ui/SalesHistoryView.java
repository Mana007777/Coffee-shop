package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.Order;
import com.coffeeshop.pos.model.OrderItem;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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
            LoginView loginView = new LoginView(stage);
            return loginView.createScene();
        }

        loadOrders();

        Label titleLabel = new Label("Sales History");
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

        CoffeeTheme.styleListView(ordersListView);
        CoffeeTheme.styleListView(orderItemsListView);
        CoffeeTheme.styleStatusLabel(statusLabel);

        Button viewDetailsButton = new Button("View Order Details");
        Button refreshButton = new Button("Refresh");
        Button backButton = new Button("Back");

        CoffeeTheme.stylePrimaryButton(viewDetailsButton);
        CoffeeTheme.styleSecondaryButton(refreshButton);
        CoffeeTheme.styleGhostButton(backButton);

        viewDetailsButton.setOnAction(event -> loadSelectedOrderItems());
        refreshButton.setOnAction(event -> {
            loadOrders();
            orderItemsListView.getItems().clear();
            CoffeeTheme.setStatusNeutral(statusLabel, "Sales history refreshed.");
        });
        backButton.setOnAction(event -> goBackToDashboard());

        Label ordersLabel = new Label("Orders");
        CoffeeTheme.styleSectionTitle(ordersLabel);

        VBox leftPanel = CoffeeTheme.createCard(14);
        leftPanel.setPrefWidth(500);
        leftPanel.getChildren().addAll(ordersLabel, ordersListView);

        Label itemsLabel = new Label("Order Items");
        CoffeeTheme.styleSectionTitle(itemsLabel);

        VBox rightPanel = CoffeeTheme.createCard(14);
        rightPanel.setPrefWidth(500);
        rightPanel.getChildren().addAll(itemsLabel, orderItemsListView);

        HBox centerPanel = new HBox(24, leftPanel, rightPanel);
        centerPanel.setAlignment(Pos.CENTER);

        HBox controls = new HBox(12, viewDetailsButton, refreshButton, backButton);
        controls.setAlignment(Pos.CENTER_LEFT);

        VBox footer = CoffeeTheme.createCard(12);
        footer.getChildren().addAll(controls, statusLabel);

        VBox root = new VBox(24, header, centerPanel, footer);
        root.setPadding(new Insets(26));
        CoffeeTheme.styleRoot(root);

        return new Scene(root, 1120, 760);
    }

    private void loadOrders() {
        List<Order> orders = orderService.getAllOrders();
        ObservableList<Order> orderItems = FXCollections.observableArrayList(orders);
        ordersListView.setItems(orderItems);
    }

    private void loadSelectedOrderItems() {
        Order selectedOrder = ordersListView.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            CoffeeTheme.setStatusError(statusLabel, "Please select an order.");
            return;
        }

        List<OrderItem> items = orderService.getOrderItemsByOrderId(selectedOrder.getId());
        ObservableList<OrderItem> orderItemList = FXCollections.observableArrayList(items);
        orderItemsListView.setItems(orderItemList);

        if (items.isEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "No items found for order #" + selectedOrder.getId());
        } else {
            CoffeeTheme.setStatusSuccess(statusLabel, "Loaded details for order #" + selectedOrder.getId());
        }
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("Coffee POS - Dashboard");
    }
}