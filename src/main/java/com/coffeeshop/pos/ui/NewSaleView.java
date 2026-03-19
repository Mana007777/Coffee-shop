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
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
        this.totalLabel = new Label("کۆی گشتی: 0 دینار");
        this.changeLabel = new Label("گەڕاندنەوە: 0 دینار");
        this.quantityField = new TextField();
        this.amountPaidField = new TextField();
        this.statusLabel = new Label();
    }

    public Scene createScene() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            return new LoginView(stage).createScene();
        }

        loadProducts();
        refreshCart();

        // Header
        Label titleLabel = new Label("فرۆشتنی نوێ");
        titleLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 30px;
                -fx-font-weight: bold;
                """);

        Label cashierLabel = new Label("فرۆشیار: " + user.getUsername());
        cashierLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.9);
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);

        VBox header = new VBox(6, titleLabel, cashierLabel);
        CoffeeTheme.styleHeaderBar(header);

        // Lists styling
        CoffeeTheme.styleListView(productListView);
        CoffeeTheme.styleListView(cartListView);

        productListView.setPrefHeight(500);
        cartListView.setPrefHeight(500);

        // Inputs
        quantityField.setPromptText("ژمارە");
        amountPaidField.setPromptText("پارەی دراو");

        quantityField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        amountPaidField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        CoffeeTheme.styleTextField(quantityField);
        CoffeeTheme.styleTextField(amountPaidField);

        CoffeeTheme.styleSectionTitle(totalLabel);
        CoffeeTheme.styleSectionTitle(changeLabel);
        CoffeeTheme.styleStatusLabel(statusLabel);

        // Buttons
        Button addToCartButton = new Button("زیادکردن بۆ سەبەت");
        Button updateCartButton = new Button("نوێکردنەوە");
        Button removeCartButton = new Button("لابردن");
        Button checkoutButton = new Button("تەواوکردنی فرۆشتن");
        Button clearCartButton = new Button("پاککردنەوە");
        Button backButton = new Button("گەڕانەوە");

        CoffeeTheme.stylePrimaryButton(addToCartButton);
        CoffeeTheme.styleSecondaryButton(updateCartButton);
        CoffeeTheme.styleDangerButton(removeCartButton);
        CoffeeTheme.stylePrimaryButton(checkoutButton);
        CoffeeTheme.styleGhostButton(clearCartButton);
        CoffeeTheme.styleGhostButton(backButton);

        // Actions
        addToCartButton.setOnAction(e -> addSelectedProductToCart());
        updateCartButton.setOnAction(e -> updateSelectedCartItem());
        removeCartButton.setOnAction(e -> removeSelectedCartItem());
        checkoutButton.setOnAction(e -> handleCheckout());
        clearCartButton.setOnAction(e -> clearCart());
        backButton.setOnAction(e -> goBackToDashboard());

        // Left: Products
        Label productsTitle = new Label("کاڵاکان");
        CoffeeTheme.styleSectionTitle(productsTitle);

        VBox productsCard = CoffeeTheme.createCard(14);
        productsCard.setPrefWidth(500);
        productsCard.getChildren().addAll(productsTitle, productListView);

        // Right: Cart
        Label cartTitle = new Label("سەبەت");
        CoffeeTheme.styleSectionTitle(cartTitle);

        VBox cartCard = CoffeeTheme.createCard(14);
        cartCard.setPrefWidth(500);
        cartCard.getChildren().addAll(cartTitle, cartListView, totalLabel, changeLabel);

        HBox centerPanel = new HBox(24, productsCard, cartCard);
        centerPanel.setAlignment(Pos.CENTER);

        // Bottom actions
        Label qtyLabel = new Label("ژمارە");
        Label paidLabel = new Label("پارەی دراو");

        CoffeeTheme.styleBodyLabel(qtyLabel);
        CoffeeTheme.styleBodyLabel(paidLabel);

        VBox actions = CoffeeTheme.createCard(16);

        HBox row1 = new HBox(12, qtyLabel, quantityField, addToCartButton, updateCartButton, removeCartButton);
        HBox row2 = new HBox(12, paidLabel, amountPaidField, checkoutButton, clearCartButton, backButton);

        actions.getChildren().addAll(row1, row2, statusLabel);

        VBox root = new VBox(24, header, centerPanel, actions);
        root.setPadding(new Insets(26));
        CoffeeTheme.styleRoot(root);

        return new Scene(root, 1200, 780);
    }

    private void loadProducts() {
        List<Product> products = productService.getAvailableProducts();
        productListView.setItems(FXCollections.observableArrayList(products));
    }

    private void refreshCart() {
        cartListView.setItems(FXCollections.observableArrayList(posService.getCartItems()));
        totalLabel.setText("کۆی گشتی: " + posService.calculateTotal() + " دینار");
    }

    private void addSelectedProductToCart() {
        Product product = productListView.getSelectionModel().getSelectedItem();

        if (product == null) {
            CoffeeTheme.setStatusError(statusLabel, "تکایە کاڵا هەڵبژێرە");
            return;
        }

        try {
            int qty = Integer.parseInt(quantityField.getText());
            if (qty <= 0) throw new Exception();

            if (!posService.addToCart(product, qty)) {
                CoffeeTheme.setStatusError(statusLabel, "کۆگا بەس نییە");
                return;
            }

            CoffeeTheme.setStatusSuccess(statusLabel, "زیادکرا بۆ سەبەت");
            quantityField.clear();
            refreshCart();

        } catch (Exception e) {
            CoffeeTheme.setStatusError(statusLabel, "ژمارە دروست بنووسە");
        }
    }

    private void updateSelectedCartItem() {
        CartItem item = cartListView.getSelectionModel().getSelectedItem();

        if (item == null) {
            CoffeeTheme.setStatusError(statusLabel, "شتێک هەڵبژێرە");
            return;
        }

        try {
            int qty = Integer.parseInt(quantityField.getText());
            posService.updateCartItemQuantity(item.getProduct().getId(), qty);
            CoffeeTheme.setStatusSuccess(statusLabel, "نوێکرایەوە");
            refreshCart();
        } catch (Exception e) {
            CoffeeTheme.setStatusError(statusLabel, "ژمارە هەڵەیە");
        }
    }

    private void removeSelectedCartItem() {
        CartItem item = cartListView.getSelectionModel().getSelectedItem();

        if (item == null) {
            CoffeeTheme.setStatusError(statusLabel, "هەڵبژاردن بکە");
            return;
        }

        posService.removeFromCart(item.getProduct().getId());
        CoffeeTheme.setStatusSuccess(statusLabel, "لابرا");
        refreshCart();
    }

    private void handleCheckout() {
        if (posService.isCartEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "سەبەت بەتاڵە");
            return;
        }

        try {
            double paid = Double.parseDouble(amountPaidField.getText());
            double total = posService.calculateTotal();

            if (paid < total) {
                CoffeeTheme.setStatusError(statusLabel, "پارە کەمە");
                return;
            }

            int orderId = posService.checkout("TAKEAWAY", "CASH", paid,
                    SessionManager.getCurrentUser().getId());

            double change = paid - total;
            changeLabel.setText("گەڕاندنەوە: " + change + " دینار");

            CoffeeTheme.setStatusSuccess(statusLabel, "سەرکەوتوو بوو ✔ ID: " + orderId);

            clearCart();

        } catch (Exception e) {
            CoffeeTheme.setStatusError(statusLabel, "پارەی دراو دروست نییە");
        }
    }

    private void clearCart() {
        posService.clearCart();
        cartListView.getItems().clear();
        totalLabel.setText("کۆی گشتی: 0 دینار");
        changeLabel.setText("گەڕاندنەوە: 0 دینار");
        amountPaidField.clear();
        quantityField.clear();
    }

    private void goBackToDashboard() {
        stage.setScene(new DashboardView(stage).createScene());
    }
}