package com.coffeeshop.pos.ui;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class SalesHistoryView {

    private final Stage stage;
    private final User user;
    private final OrderService orderService;

    private final ListView<Order> ordersListView;
    private final ListView<OrderItem> orderItemsListView;
    private final Label statusLabel;

    public SalesHistoryView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.orderService = new OrderService();

        this.ordersListView = new ListView<>();
        this.orderItemsListView = new ListView<>();
        this.statusLabel = new Label();
    }

    public Scene createScene() {
        Label titleLabel = new Label("Sales History");
        Label userLabel = new Label("User: " + user.getUsername());

        loadOrders();

        Button viewDetailsButton = new Button("View Order Details");
        Button refreshButton = new Button("Refresh");
        Button backButton = new Button("Back");

        viewDetailsButton.setOnAction(event -> loadSelectedOrderItems());
        refreshButton.setOnAction(event -> {
            loadOrders();
            orderItemsListView.getItems().clear();
            statusLabel.setText("Sales history refreshed.");
        });
        backButton.setOnAction(event -> goBackToDashboard());

        VBox leftPanel = new VBox(10,
                new Label("Orders"),
                ordersListView
        );
        leftPanel.setPrefWidth(450);

        VBox rightPanel = new VBox(10,
                new Label("Order Items"),
                orderItemsListView
        );
        rightPanel.setPrefWidth(450);

        HBox centerPanel = new HBox(20, leftPanel, rightPanel);
        centerPanel.setAlignment(Pos.CENTER);

        HBox controls = new HBox(10, viewDetailsButton, refreshButton, backButton);
        controls.setAlignment(Pos.CENTER);

        VBox topPanel = new VBox(8, titleLabel, userLabel);
        topPanel.setAlignment(Pos.CENTER);

        VBox bottomPanel = new VBox(8, controls, statusLabel);
        bottomPanel.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setTop(topPanel);
        root.setCenter(centerPanel);
        root.setBottom(bottomPanel);

        return new Scene(root, 1000, 600);
    }

    private void loadOrders() {
        List<Order> orders = orderService.getAllOrders();
        ObservableList<Order> orderItems = FXCollections.observableArrayList(orders);
        ordersListView.setItems(orderItems);
    }

    private void loadSelectedOrderItems() {
        Order selectedOrder = ordersListView.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            statusLabel.setText("Please select an order.");
            return;
        }

        List<OrderItem> items = orderService.getOrderItemsByOrderId(selectedOrder.getId());
        ObservableList<OrderItem> orderItemList = FXCollections.observableArrayList(items);
        orderItemsListView.setItems(orderItemList);

        if (items.isEmpty()) {
            statusLabel.setText("No items found for order #" + selectedOrder.getId());
        } else {
            statusLabel.setText("Loaded details for order #" + selectedOrder.getId());
        }
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage, user);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("Coffee POS - Dashboard");
    }
}