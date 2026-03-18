package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class NewSaleView {

    private final Stage stage;
    private final ProductService productService;
    private final PosService posService;

    private final ListView<Product> productListView;
    private final ListView<CartItem> cartListView;
    private final Label totalLabel;
    private final Label changeLabel;
    private final TextField quantityField;
    private final TextField amountPaidField;
    private final Label statusLabel;

    public NewSaleView(Stage stage) {
        this.stage = stage;
        this.productService = new ProductService();
        this.posService = new PosService();

        this.productListView = new ListView<>();
        this.cartListView = new ListView<>();
        this.totalLabel = new Label("Total: $0.0");
        this.changeLabel = new Label("Change: $0.0");
        this.quantityField = new TextField();
        this.amountPaidField = new TextField();
        this.statusLabel = new Label();
    }

    public Scene createScene() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            LoginView loginView = new LoginView(stage);
            return loginView.createScene();
        }

        loadProducts();
        refreshCart();

        Label titleLabel = new Label("New Sale");
        titleLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 28px;
                -fx-font-weight: bold;
                """);

        Label cashierLabel = new Label("Cashier: " + user.getUsername());
        cashierLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.9);
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);

        VBox header = new VBox(6, titleLabel, cashierLabel);
        CoffeeTheme.styleHeaderBar(header);

        CoffeeTheme.styleListView(productListView);
        CoffeeTheme.styleListView(cartListView);
        productListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        cartListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        quantityField.setPromptText("Quantity");
        amountPaidField.setPromptText("Amount Paid");
        CoffeeTheme.styleTextField(quantityField);
        CoffeeTheme.styleTextField(amountPaidField);

        CoffeeTheme.styleSectionTitle(totalLabel);
        CoffeeTheme.styleSectionTitle(changeLabel);
        CoffeeTheme.styleStatusLabel(statusLabel);

        Button addToCartButton = new Button("Add to Cart");
        Button updateCartButton = new Button("Update Cart Item");
        Button removeCartButton = new Button("Remove");
        Button checkoutButton = new Button("Checkout");
        Button clearCartButton = new Button("Clear Cart");
        Button backButton = new Button("Back");

        CoffeeTheme.stylePrimaryButton(addToCartButton);
        CoffeeTheme.styleSecondaryButton(updateCartButton);
        CoffeeTheme.styleDangerButton(removeCartButton);
        CoffeeTheme.stylePrimaryButton(checkoutButton);
        CoffeeTheme.styleGhostButton(clearCartButton);
        CoffeeTheme.styleGhostButton(backButton);

        addToCartButton.setOnAction(event -> addSelectedProductToCart());
        updateCartButton.setOnAction(event -> updateSelectedCartItem());
        removeCartButton.setOnAction(event -> removeSelectedCartItem());
        checkoutButton.setOnAction(event -> handleCheckout());
        clearCartButton.setOnAction(event -> clearCart());
        backButton.setOnAction(event -> goBackToDashboard());

        Label productsTitle = new Label("Available Products");
        CoffeeTheme.styleSectionTitle(productsTitle);

        VBox productsCard = CoffeeTheme.createCard(14);
        productsCard.setPrefWidth(470);
        productsCard.getChildren().addAll(productsTitle, productListView);

        Label cartTitle = new Label("Current Cart");
        CoffeeTheme.styleSectionTitle(cartTitle);

        VBox cartCard = CoffeeTheme.createCard(14);
        cartCard.setPrefWidth(470);
        cartCard.getChildren().addAll(cartTitle, cartListView, totalLabel, changeLabel);

        HBox centerPanel = new HBox(24, productsCard, cartCard);
        centerPanel.setAlignment(Pos.CENTER);

        Label qtyLabel = new Label("Quantity");
        Label paidLabel = new Label("Amount Paid");
        CoffeeTheme.styleBodyLabel(qtyLabel);
        CoffeeTheme.styleBodyLabel(paidLabel);

        VBox saleActions = CoffeeTheme.createCard(16);

        HBox row1 = new HBox(12, qtyLabel, quantityField, addToCartButton, updateCartButton, removeCartButton);
        row1.setAlignment(Pos.CENTER_LEFT);

        HBox row2 = new HBox(12, paidLabel, amountPaidField, checkoutButton, clearCartButton, backButton);
        row2.setAlignment(Pos.CENTER_LEFT);

        saleActions.getChildren().addAll(row1, row2, statusLabel);

        VBox root = new VBox(24, header, centerPanel, saleActions);
        root.setPadding(new Insets(26));
        CoffeeTheme.styleRoot(root);

        return new Scene(root, 1120, 760);
    }

    private void loadProducts() {
        List<Product> products = productService.getAvailableProducts();
        ObservableList<Product> items = FXCollections.observableArrayList(products);
        productListView.setItems(items);
    }

    private void refreshCart() {
        ObservableList<CartItem> items = FXCollections.observableArrayList(posService.getCartItems());
        cartListView.setItems(items);
        totalLabel.setText("Total: $" + posService.calculateTotal());
    }

    private void addSelectedProductToCart() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "Please select a product.");
            return;
        }

        String quantityText = quantityField.getText().trim();

        if (quantityText.isEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "Please enter quantity.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            CoffeeTheme.setStatusError(statusLabel, "Quantity must be a number.");
            return;
        }

        if (quantity <= 0) {
            CoffeeTheme.setStatusError(statusLabel, "Quantity must be greater than zero.");
            return;
        }

        boolean added = posService.addToCart(selectedProduct, quantity);

        if (!added) {
            int alreadyInCart = posService.getQuantityInCart(selectedProduct.getId());
            CoffeeTheme.setStatusError(
                    statusLabel,
                    "Cannot add product. Stock: " + selectedProduct.getStockQty() + ", already in cart: " + alreadyInCart
            );
            return;
        }

        quantityField.clear();
        CoffeeTheme.setStatusSuccess(statusLabel, "Added to cart: " + selectedProduct.getName());
        refreshCart();
    }

    private void updateSelectedCartItem() {
        CartItem selectedCartItem = cartListView.getSelectionModel().getSelectedItem();

        if (selectedCartItem == null) {
            CoffeeTheme.setStatusError(statusLabel, "Please select a cart item to update.");
            return;
        }

        String quantityText = quantityField.getText().trim();

        if (quantityText.isEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "Enter the new quantity in the quantity field.");
            return;
        }

        int newQuantity;
        try {
            newQuantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            CoffeeTheme.setStatusError(statusLabel, "Quantity must be a number.");
            return;
        }

        boolean updated = posService.updateCartItemQuantity(
                selectedCartItem.getProduct().getId(),
                newQuantity
        );

        if (!updated) {
            CoffeeTheme.setStatusError(statusLabel, "Failed to update cart item.");
            return;
        }

        quantityField.clear();
        CoffeeTheme.setStatusSuccess(statusLabel, "Cart item updated.");
        refreshCart();
    }

    private void removeSelectedCartItem() {
        CartItem selectedCartItem = cartListView.getSelectionModel().getSelectedItem();

        if (selectedCartItem == null) {
            CoffeeTheme.setStatusError(statusLabel, "Please select a cart item to remove.");
            return;
        }

        boolean removed = posService.removeFromCart(selectedCartItem.getProduct().getId());

        if (!removed) {
            CoffeeTheme.setStatusError(statusLabel, "Failed to remove cart item.");
            return;
        }

        CoffeeTheme.setStatusSuccess(statusLabel, "Cart item removed.");
        refreshCart();
    }

    private void handleCheckout() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            CoffeeTheme.setStatusError(statusLabel, "No active session. Please log in again.");
            return;
        }

        if (posService.isCartEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "Cart is empty. Cannot checkout.");
            return;
        }

        String amountPaidText = amountPaidField.getText().trim();

        if (amountPaidText.isEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "Please enter amount paid.");
            return;
        }

        double amountPaid;
        try {
            amountPaid = Double.parseDouble(amountPaidText);
        } catch (NumberFormatException e) {
            CoffeeTheme.setStatusError(statusLabel, "Amount paid must be a number.");
            return;
        }

        double total = posService.calculateTotal();

        if (amountPaid < total) {
            CoffeeTheme.setStatusError(statusLabel, "Insufficient payment.");
            return;
        }

        int orderId = posService.checkout("TAKEAWAY", "CASH", amountPaid, user.getId());

        if (orderId == -1) {
            CoffeeTheme.setStatusError(statusLabel, "Failed to save order.");
            return;
        }

        double change = amountPaid - total;
        changeLabel.setText("Change: $" + change);
        CoffeeTheme.setStatusSuccess(statusLabel, "Order saved successfully. Order ID: " + orderId);

        posService.clearCart();
        cartListView.getItems().clear();
        totalLabel.setText("Total: $0.0");
        amountPaidField.clear();
        quantityField.clear();

        loadProducts();
    }

    private void clearCart() {
        posService.clearCart();
        cartListView.getItems().clear();
        totalLabel.setText("Total: $0.0");
        changeLabel.setText("Change: $0.0");
        CoffeeTheme.setStatusNeutral(statusLabel, "Cart cleared.");
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("Coffee POS - Dashboard");
    }
}