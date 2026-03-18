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
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

        Label titleLabel = new Label("New Sale");
        Label cashierLabel = new Label("Cashier: " + user.getUsername());

        loadProducts();
        refreshCart();

        quantityField.setPromptText("Quantity");
        amountPaidField.setPromptText("Amount Paid");

        Button addToCartButton = new Button("Add to Cart");
        Button updateCartButton = new Button("Update Cart Item");
        Button removeCartButton = new Button("Remove Cart Item");
        Button checkoutButton = new Button("Checkout");
        Button clearCartButton = new Button("Clear Cart");
        Button backButton = new Button("Back");

        addToCartButton.setOnAction(event -> addSelectedProductToCart());
        updateCartButton.setOnAction(event -> updateSelectedCartItem());
        removeCartButton.setOnAction(event -> removeSelectedCartItem());
        checkoutButton.setOnAction(event -> handleCheckout());
        clearCartButton.setOnAction(event -> clearCart());
        backButton.setOnAction(event -> goBackToDashboard());

        VBox leftPanel = new VBox(10,
                new Label("Available Products"),
                productListView
        );
        leftPanel.setPrefWidth(350);

        VBox rightPanel = new VBox(10,
                new Label("Cart"),
                cartListView,
                totalLabel,
                changeLabel
        );
        rightPanel.setPrefWidth(350);

        HBox centerPanel = new HBox(20, leftPanel, rightPanel);
        centerPanel.setAlignment(Pos.CENTER);

        HBox addControls = new HBox(10,
                new Label("Quantity:"),
                quantityField,
                addToCartButton
        );
        addControls.setAlignment(Pos.CENTER);

        HBox cartEditControls = new HBox(10,
                updateCartButton,
                removeCartButton
        );
        cartEditControls.setAlignment(Pos.CENTER);

        HBox checkoutControls = new HBox(10,
                new Label("Amount Paid:"),
                amountPaidField,
                checkoutButton,
                clearCartButton,
                backButton
        );
        checkoutControls.setAlignment(Pos.CENTER);

        VBox topPanel = new VBox(8, titleLabel, cashierLabel);
        topPanel.setAlignment(Pos.CENTER);

        VBox bottomPanel = new VBox(10, addControls, cartEditControls, checkoutControls, statusLabel);
        bottomPanel.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setTop(topPanel);
        root.setCenter(centerPanel);
        root.setBottom(bottomPanel);

        return new Scene(root, 1000, 650);
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

    private void updateSelectedCartItem() {
        CartItem selectedCartItem = cartListView.getSelectionModel().getSelectedItem();

        if (selectedCartItem == null) {
            statusLabel.setText("Please select a cart item to update.");
            return;
        }

        String quantityText = quantityField.getText().trim();

        if (quantityText.isEmpty()) {
            statusLabel.setText("Enter the new quantity in the quantity field.");
            return;
        }

        int newQuantity;
        try {
            newQuantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Quantity must be a number.");
            return;
        }

        boolean updated = posService.updateCartItemQuantity(
                selectedCartItem.getProduct().getId(),
                newQuantity
        );

        if (!updated) {
            statusLabel.setText("Failed to update cart item.");
            return;
        }

        quantityField.clear();
        statusLabel.setText("Cart item updated.");
        refreshCart();
    }

    private void removeSelectedCartItem() {
        CartItem selectedCartItem = cartListView.getSelectionModel().getSelectedItem();

        if (selectedCartItem == null) {
            statusLabel.setText("Please select a cart item to remove.");
            return;
        }

        boolean removed = posService.removeFromCart(selectedCartItem.getProduct().getId());

        if (!removed) {
            statusLabel.setText("Failed to remove cart item.");
            return;
        }

        statusLabel.setText("Cart item removed.");
        refreshCart();
    }

    private void handleCheckout() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            statusLabel.setText("No active session. Please log in again.");
            return;
        }

        if (posService.isCartEmpty()) {
            statusLabel.setText("Cart is empty. Cannot checkout.");
            return;
        }

        String amountPaidText = amountPaidField.getText().trim();

        if (amountPaidText.isEmpty()) {
            statusLabel.setText("Please enter amount paid.");
            return;
        }

        double amountPaid;
        try {
            amountPaid = Double.parseDouble(amountPaidText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Amount paid must be a number.");
            return;
        }

        double total = posService.calculateTotal();

        if (amountPaid < total) {
            statusLabel.setText("Insufficient payment.");
            return;
        }

        int orderId = posService.checkout("TAKEAWAY", "CASH", amountPaid, user.getId());

        if (orderId == -1) {
            statusLabel.setText("Failed to save order.");
            return;
        }

        double change = amountPaid - total;
        changeLabel.setText("Change: $" + change);
        statusLabel.setText("Order saved successfully. Order ID: " + orderId);

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
        statusLabel.setText("Cart cleared.");
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("Coffee POS - Dashboard");
    }
}