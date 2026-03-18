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
import javafx.scene.layout.*;
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
            LoginView loginView = new LoginView(stage);
            return loginView.createScene();
        }

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            VBox root = CoffeeTheme.createCard(12);
            root.setAlignment(Pos.CENTER);
            root.getChildren().addAll(
                    new Label("Access denied."),
                    new Label("Only admins can access Product Management.")
            );
            CoffeeTheme.styleRoot(root);
            return new Scene(root, 500, 300);
        }

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

        nameField.setPromptText("Product Name");
        priceField.setPromptText("Price");
        stockField.setPromptText("Stock Quantity");
        newPriceField.setPromptText("New Price");
        newStockField.setPromptText("New Stock");

        CoffeeTheme.styleTextField(nameField);
        CoffeeTheme.styleTextField(priceField);
        CoffeeTheme.styleTextField(stockField);
        CoffeeTheme.styleTextField(newPriceField);
        CoffeeTheme.styleTextField(newStockField);

        CoffeeTheme.styleListView(productListView);
        CoffeeTheme.styleListView(categoryListView);
        CoffeeTheme.styleStatusLabel(statusLabel);

        Button addProductButton = new Button("Add Product");
        Button updatePriceButton = new Button("Update Price");
        Button updateStockButton = new Button("Update Stock");
        Button activateButton = new Button("Activate");
        Button deactivateButton = new Button("Deactivate");
        Button refreshButton = new Button("Refresh");
        Button backButton = new Button("Back");

        CoffeeTheme.stylePrimaryButton(addProductButton);
        CoffeeTheme.styleSecondaryButton(updatePriceButton);
        CoffeeTheme.styleSecondaryButton(updateStockButton);
        CoffeeTheme.stylePrimaryButton(activateButton);
        CoffeeTheme.styleDangerButton(deactivateButton);
        CoffeeTheme.styleGhostButton(refreshButton);
        CoffeeTheme.styleGhostButton(backButton);

        addProductButton.setOnAction(event -> addProduct());
        updatePriceButton.setOnAction(event -> updateProductPrice());
        updateStockButton.setOnAction(event -> updateProductStock());
        activateButton.setOnAction(event -> activateProduct());
        deactivateButton.setOnAction(event -> deactivateProduct());
        refreshButton.setOnAction(event -> refreshData());
        backButton.setOnAction(event -> goBackToDashboard());

        Label productsLabel = new Label("Products");
        CoffeeTheme.styleSectionTitle(productsLabel);

        VBox leftPanel = CoffeeTheme.createCard(14);
        leftPanel.setPrefWidth(520);
        leftPanel.getChildren().addAll(productsLabel, productListView);

        Label categoriesLabel = new Label("Categories");
        CoffeeTheme.styleSectionTitle(categoriesLabel);

        Label addLabel = new Label("Add New Product");
        CoffeeTheme.styleSectionTitle(addLabel);

        Label editLabel = new Label("Edit Selected Product");
        CoffeeTheme.styleSectionTitle(editLabel);

        VBox rightPanel = CoffeeTheme.createCard(12);
        rightPanel.setPrefWidth(420);
        rightPanel.getChildren().addAll(
                categoriesLabel,
                categoryListView,
                addLabel,
                nameField,
                priceField,
                stockField,
                addProductButton,
                editLabel,
                newPriceField,
                updatePriceButton,
                newStockField,
                updateStockButton,
                activateButton,
                deactivateButton
        );

        HBox centerPanel = new HBox(24, leftPanel, rightPanel);
        centerPanel.setAlignment(Pos.CENTER);

        HBox footerButtons = new HBox(12, refreshButton, backButton);
        footerButtons.setAlignment(Pos.CENTER_LEFT);

        VBox footer = CoffeeTheme.createCard(12);
        footer.getChildren().addAll(footerButtons, statusLabel);

        VBox root = new VBox(24, header, centerPanel, footer);
        root.setPadding(new Insets(26));
        CoffeeTheme.styleRoot(root);

        return new Scene(root, 1160, 780);
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

        if (name.isEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "Enter a product name.");
            return;
        }

        if (selectedCategory == null) {
            CoffeeTheme.setStatusError(statusLabel, "Select a category.");
            return;
        }

        double price;
        int stockQty;

        try {
            price = Double.parseDouble(priceText);
            stockQty = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            CoffeeTheme.setStatusError(statusLabel, "Price and stock must be valid numbers.");
            return;
        }

        boolean added = productService.addProduct(name, selectedCategory.getId(), price, stockQty);

        if (added) {
            CoffeeTheme.setStatusSuccess(statusLabel, "Product added successfully.");
            nameField.clear();
            priceField.clear();
            stockField.clear();
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "Failed to add product.");
        }
    }

    private void updateProductPrice() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "Select a product.");
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

        boolean updated = productService.updateProductPrice(selectedProduct.getId(), newPrice);

        if (updated) {
            CoffeeTheme.setStatusSuccess(statusLabel, "Product price updated.");
            newPriceField.clear();
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "Failed to update product price.");
        }
    }

    private void updateProductStock() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "Select a product.");
            return;
        }

        String newStockText = newStockField.getText().trim();
        int newStock;

        try {
            newStock = Integer.parseInt(newStockText);
        } catch (NumberFormatException e) {
            CoffeeTheme.setStatusError(statusLabel, "Enter a valid new stock quantity.");
            return;
        }

        boolean updated = productService.updateProductStock(selectedProduct.getId(), newStock);

        if (updated) {
            CoffeeTheme.setStatusSuccess(statusLabel, "Product stock updated.");
            newStockField.clear();
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "Failed to update product stock.");
        }
    }

    private void activateProduct() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "Select a product.");
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
            CoffeeTheme.setStatusError(statusLabel, "Select a product.");
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

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("Coffee POS - Dashboard");
    }
}