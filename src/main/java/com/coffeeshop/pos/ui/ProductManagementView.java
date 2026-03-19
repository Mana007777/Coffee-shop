package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.Category;
import com.coffeeshop.pos.model.Product;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.CategoryService;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ProductManagementView {

    private final Stage stage;
    private final ProductService productService;
    private final CategoryService categoryService;

    private final ListView<Product> productListView;
    private final ListView<Category> categoryListView;

    private final TextField nameField;
    private final TextField priceField;
    private final TextField stockField;
    private final TextField newPriceField;
    private final TextField newStockField;

    private final Label statusLabel;

    public ProductManagementView(Stage stage) {
        this.stage = stage;
        this.productService = new ProductService();
        this.categoryService = new CategoryService();

        this.productListView = new ListView<>();
        this.categoryListView = new ListView<>();

        this.nameField = new TextField();
        this.priceField = new TextField();
        this.stockField = new TextField();
        this.newPriceField = new TextField();
        this.newStockField = new TextField();

        this.statusLabel = new Label();
    }

    public Scene createScene() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            return new LoginView(stage).createScene();
        }

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            VBox deniedRoot = CoffeeTheme.createCard(14);
            deniedRoot.setAlignment(Pos.CENTER);
            deniedRoot.setPadding(new Insets(30));

            Label deniedTitle = new Label("Access Denied");
            CoffeeTheme.styleSectionTitle(deniedTitle);

            Label deniedText = new Label("Only admins can access Product Management.");
            CoffeeTheme.styleBodyLabel(deniedText);

            Button backButton = new Button("Back");
            CoffeeTheme.styleGhostButton(backButton);
            backButton.setOnAction(e -> goBackToDashboard());

            deniedRoot.getChildren().addAll(deniedTitle, deniedText, backButton);
            CoffeeTheme.styleRoot(deniedRoot);

            return new Scene(deniedRoot, 520, 280);
        }

        configureControls();
        loadProducts();
        loadCategories();

        Label titleLabel = new Label("Product Management");
        titleLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 28px;
                -fx-font-weight: bold;
                """);

        Label userLabel = new Label("Admin: " + user.getUsername());
        userLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.92);
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);

        VBox header = new VBox(6, titleLabel, userLabel);
        CoffeeTheme.styleHeaderBar(header);

        VBox productsPanel = buildProductsPanel();
        VBox managementPanel = buildManagementPanel();
        VBox footer = buildFooter();

        HBox centerPanel = new HBox(24, productsPanel, managementPanel);
        centerPanel.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(productsPanel, Priority.ALWAYS);
        HBox.setHgrow(managementPanel, Priority.ALWAYS);

        VBox root = new VBox(24, header, centerPanel, footer);
        root.setPadding(new Insets(26));
        CoffeeTheme.styleRoot(root);
        VBox.setVgrow(centerPanel, Priority.ALWAYS);

        return new Scene(root, 1240, 820);
    }

    private void configureControls() {
        nameField.setPromptText("Product Name");
        priceField.setPromptText("Price");
        stockField.setPromptText("Stock Quantity");
        newPriceField.setPromptText("New Price");
        newStockField.setPromptText("New Stock Quantity");

        CoffeeTheme.styleTextField(nameField);
        CoffeeTheme.styleTextField(priceField);
        CoffeeTheme.styleTextField(stockField);
        CoffeeTheme.styleTextField(newPriceField);
        CoffeeTheme.styleTextField(newStockField);

        nameField.setMaxWidth(Double.MAX_VALUE);
        priceField.setMaxWidth(Double.MAX_VALUE);
        stockField.setMaxWidth(Double.MAX_VALUE);
        newPriceField.setMaxWidth(Double.MAX_VALUE);
        newStockField.setMaxWidth(Double.MAX_VALUE);

        CoffeeTheme.styleListView(productListView);
        CoffeeTheme.styleListView(categoryListView);
        CoffeeTheme.styleStatusLabel(statusLabel);

        productListView.setPrefHeight(580);
        productListView.setMinHeight(500);
        productListView.setMaxWidth(Double.MAX_VALUE);

        categoryListView.setPrefHeight(220);
        categoryListView.setMinHeight(180);
        categoryListView.setMaxWidth(Double.MAX_VALUE);

        VBox.setVgrow(productListView, Priority.ALWAYS);

        productListView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, selectedProduct) -> {
            if (selectedProduct != null) {
                newPriceField.setText(String.valueOf(selectedProduct.getPrice()));
                newStockField.setText(String.valueOf(selectedProduct.getStockQty()));
            }
        });
    }

    private VBox buildProductsPanel() {
        Label productsLabel = new Label("Products");
        CoffeeTheme.styleSectionTitle(productsLabel);

        Label helperLabel = new Label("Select a product to edit its price, stock, or active status.");
        CoffeeTheme.styleBodyLabel(helperLabel);

        VBox panel = CoffeeTheme.createCard(14);
        panel.setPrefWidth(700);
        panel.setMinWidth(640);
        panel.getChildren().addAll(productsLabel, helperLabel, productListView);

        VBox.setVgrow(productListView, Priority.ALWAYS);
        return panel;
    }

    private VBox buildManagementPanel() {
        Label categoriesLabel = new Label("Categories");
        CoffeeTheme.styleSectionTitle(categoriesLabel);

        Label addProductLabel = new Label("Add New Product");
        CoffeeTheme.styleSectionTitle(addProductLabel);

        Label editProductLabel = new Label("Edit Selected Product");
        CoffeeTheme.styleSectionTitle(editProductLabel);

        Label addHintLabel = new Label("Pick a category first, then enter product details.");
        CoffeeTheme.styleBodyLabel(addHintLabel);

        Button addProductButton = new Button("Add Product");
        Button updatePriceButton = new Button("Update Price");
        Button updateStockButton = new Button("Update Stock");
        Button activateButton = new Button("Activate Product");
        Button deactivateButton = new Button("Deactivate Product");

        CoffeeTheme.stylePrimaryButton(addProductButton);
        CoffeeTheme.styleSecondaryButton(updatePriceButton);
        CoffeeTheme.styleSecondaryButton(updateStockButton);
        CoffeeTheme.stylePrimaryButton(activateButton);
        CoffeeTheme.styleDangerButton(deactivateButton);

        addProductButton.setMaxWidth(Double.MAX_VALUE);
        updatePriceButton.setMaxWidth(Double.MAX_VALUE);
        updateStockButton.setMaxWidth(Double.MAX_VALUE);
        activateButton.setMaxWidth(Double.MAX_VALUE);
        deactivateButton.setMaxWidth(Double.MAX_VALUE);

        addProductButton.setOnAction(event -> addProduct());
        updatePriceButton.setOnAction(event -> updateProductPrice());
        updateStockButton.setOnAction(event -> updateProductStock());
        activateButton.setOnAction(event -> activateProduct());
        deactivateButton.setOnAction(event -> deactivateProduct());

        HBox editButtonsRow = new HBox(12, activateButton, deactivateButton);
        HBox.setHgrow(activateButton, Priority.ALWAYS);
        HBox.setHgrow(deactivateButton, Priority.ALWAYS);

        VBox panel = CoffeeTheme.createCard(14);
        panel.setPrefWidth(460);
        panel.setMinWidth(420);

        panel.getChildren().addAll(
                categoriesLabel,
                categoryListView,
                addProductLabel,
                addHintLabel,
                nameField,
                priceField,
                stockField,
                addProductButton,
                editProductLabel,
                newPriceField,
                updatePriceButton,
                newStockField,
                updateStockButton,
                editButtonsRow
        );

        return panel;
    }

    private VBox buildFooter() {
        Button refreshButton = new Button("Refresh");
        Button clearButton = new Button("Clear Fields");
        Button backButton = new Button("Back");

        CoffeeTheme.styleGhostButton(refreshButton);
        CoffeeTheme.styleGhostButton(clearButton);
        CoffeeTheme.styleGhostButton(backButton);

        refreshButton.setOnAction(event -> refreshData());
        clearButton.setOnAction(event -> clearFields());
        backButton.setOnAction(event -> goBackToDashboard());

        HBox buttons = new HBox(12, refreshButton, clearButton, backButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox footer = CoffeeTheme.createCard(12);
        footer.getChildren().addAll(buttons, statusLabel);
        return footer;
    }

    private void loadProducts() {
        List<Product> products = productService.getAllProducts();
        ObservableList<Product> items = FXCollections.observableArrayList(products);
        productListView.setItems(items);
    }

    private void loadCategories() {
        List<Category> categories = categoryService.getAllCategories();
        ObservableList<Category> items = FXCollections.observableArrayList(categories);
        categoryListView.setItems(items);
    }

    private void addProduct() {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        String stockText = stockField.getText().trim();
        Category selectedCategory = categoryListView.getSelectionModel().getSelectedItem();

        if (selectedCategory == null) {
            CoffeeTheme.setStatusError(statusLabel, "Select a category first.");
            return;
        }

        if (name.isEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "Enter a product name.");
            return;
        }

        double price;
        int stockQty;

        try {
            price = Double.parseDouble(priceText);
            stockQty = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            CoffeeTheme.setStatusError(statusLabel, "Price must be a number and stock must be a whole number.");
            return;
        }

        if (price < 0) {
            CoffeeTheme.setStatusError(statusLabel, "Price cannot be negative.");
            return;
        }

        if (stockQty < 0) {
            CoffeeTheme.setStatusError(statusLabel, "Stock cannot be negative.");
            return;
        }

        boolean added = productService.addProduct(name, selectedCategory.getId(), price, stockQty);

        if (added) {
            CoffeeTheme.setStatusSuccess(statusLabel, "Product added successfully.");
            clearAddProductFields();
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "Failed to add product.");
        }
    }

    private void updateProductPrice() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "Select a product to update.");
            return;
        }

        String newPriceText = newPriceField.getText().trim();
        double newPrice;

        try {
            newPrice = Double.parseDouble(newPriceText);
        } catch (NumberFormatException e) {
            CoffeeTheme.setStatusError(statusLabel, "Enter a valid new price.");
            return;
        }

        if (newPrice < 0) {
            CoffeeTheme.setStatusError(statusLabel, "Price cannot be negative.");
            return;
        }

        boolean updated = productService.updateProductPrice(selectedProduct.getId(), newPrice);

        if (updated) {
            CoffeeTheme.setStatusSuccess(statusLabel, "Product price updated.");
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "Failed to update product price.");
        }
    }

    private void updateProductStock() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "Select a product to update.");
            return;
        }

        String newStockText = newStockField.getText().trim();
        int newStock;

        try {
            newStock = Integer.parseInt(newStockText);
        } catch (NumberFormatException e) {
            CoffeeTheme.setStatusError(statusLabel, "Enter a valid stock quantity.");
            return;
        }

        if (newStock < 0) {
            CoffeeTheme.setStatusError(statusLabel, "Stock cannot be negative.");
            return;
        }

        boolean updated = productService.updateProductStock(selectedProduct.getId(), newStock);

        if (updated) {
            CoffeeTheme.setStatusSuccess(statusLabel, "Product stock updated.");
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "Failed to update product stock.");
        }
    }

    private void activateProduct() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "Select a product first.");
            return;
        }

        boolean updated = productService.activateProduct(selectedProduct.getId());

        if (updated) {
            CoffeeTheme.setStatusSuccess(statusLabel, "Product activated.");
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "Failed to activate product.");
        }
    }

    private void deactivateProduct() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "Select a product first.");
            return;
        }

        boolean updated = productService.deactivateProduct(selectedProduct.getId());

        if (updated) {
            CoffeeTheme.setStatusSuccess(statusLabel, "Product deactivated.");
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "Failed to deactivate product.");
        }
    }

    private void refreshData() {
        loadProducts();
        loadCategories();
        CoffeeTheme.setStatusNeutral(statusLabel, "Product data refreshed.");
    }

    private void clearFields() {
        clearAddProductFields();
        newPriceField.clear();
        newStockField.clear();
        productListView.getSelectionModel().clearSelection();
        categoryListView.getSelectionModel().clearSelection();
        CoffeeTheme.setStatusNeutral(statusLabel, "Fields cleared.");
    }

    private void clearAddProductFields() {
        nameField.clear();
        priceField.clear();
        stockField.clear();
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("Coffee POS - Dashboard");
    }
}