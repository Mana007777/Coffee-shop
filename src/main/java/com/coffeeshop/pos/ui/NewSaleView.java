package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.model.CartItem;
import com.coffeeshop.pos.model.Product;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.PosService;
import com.coffeeshop.pos.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class NewSaleView {

    private final Stage stage;
    private final User user;
    private final ProductService productService;
    private final PosService posService;

    private final ListView<String> productListView;
    private final ListView<String> cartListView;
    private final Label totalLabel;
    private final TextField quantityField;
    private final Label statusLabel;

    public NewSaleView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        this.productService = new ProductService();
        this.posService = new PosService();

        this.productListView = new ListView<>();
        this.cartListView = new ListView<>();
        this.totalLabel = new Label("Total: $0.0");
        this.quantityField = new TextField();
        this.statusLabel = new Label();
    }

    public Scene createScene() {
        Label titleLabel = new Label("New Sale");
        Label cashierLabel = new Label("Cashier: " + user.getUsername());

        loadProducts();

        quantityField.setPromptText("Quantity");

        Button addToCartButton = new Button("Add to Cart");
        Button backButton = new Button("Back");

        addToCartButton.setOnAction(event -> addSelectedProductToCart());
        backButton.setOnAction(event -> goBackToDashboard());

        VBox leftPanel = new VBox(10,
                new Label("Available Products"),
                productListView
        );
        leftPanel.setPrefWidth(350);

        VBox rightPanel = new VBox(10,
                new Label("Cart"),
                cartListView,
                totalLabel
        );
        rightPanel.setPrefWidth(350);

        HBox centerPanel = new HBox(20, leftPanel, rightPanel);
        centerPanel.setAlignment(Pos.CENTER);

        HBox controls = new HBox(10,
                new Label("Quantity:"),
                quantityField,
                addToCartButton,
                backButton
        );
        controls.setAlignment(Pos.CENTER);

        VBox topPanel = new VBox(8, titleLabel, cashierLabel);
        topPanel.setAlignment(Pos.CENTER);

        VBox bottomPanel = new VBox(8, controls, statusLabel);
        bottomPanel.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setTop(topPanel);
        root.setCenter(centerPanel);
        root.setBottom(bottomPanel);

        return new Scene(root, 900, 550);
    }

    private void loadProducts() {
        List<Product> products = productService.getAvailableProducts();
        ObservableList<String> items = FXCollections.observableArrayList();

        for (Product product : products) {
            items.add(formatProduct(product));
        }

        productListView.setItems(items);
    }

    private void refreshCart() {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (CartItem item : posService.getCartItems()) {
            items.add(
                    item.getProduct().getName() +
                            " x " + item.getQuantity() +
                            " = $" + item.getSubtotal()
            );
        }

        cartListView.setItems(items);
        totalLabel.setText("Total: $" + posService.calculateTotal());
    }

    private void addSelectedProductToCart() {
        int selectedIndex = productListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0) {
            statusLabel.setText("Please select a product.");
            return;
        }

        String quantityText = quantityField.getText().trim();

        if (quantityText.isEmpty()) {
            statusLabel.setText("Please enter quantity.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Quantity must be a number.");
            return;
        }

        if (quantity <= 0) {
            statusLabel.setText("Quantity must be greater than zero.");
            return;
        }

        List<Product> products = productService.getAvailableProducts();
        Product selectedProduct = products.get(selectedIndex);

        boolean added = posService.addToCart(selectedProduct, quantity);

        if (!added) {
            int alreadyInCart = posService.getQuantityInCart(selectedProduct.getId());
            statusLabel.setText(
                    "Cannot add product. Stock: " +
                            selectedProduct.getStockQty() +
                            ", already in cart: " +
                            alreadyInCart
            );
            return;
        }

        quantityField.clear();
        statusLabel.setText("Added to cart: " + selectedProduct.getName());
        refreshCart();
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage, user);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("Coffee POS - Dashboard");
    }

    private String formatProduct(Product product) {
        return product.getId() + " | " +
                product.getName() + " | $" +
                product.getPrice() + " | stock: " +
                product.getStockQty();
    }
}