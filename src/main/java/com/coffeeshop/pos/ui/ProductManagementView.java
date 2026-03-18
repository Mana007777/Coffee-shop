package com.coffeeshop.pos.ui;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ProductManagementView {

    private final Stage stage;
    private final User user;
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

    public ProductManagementView(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
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
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            VBox root = new VBox(10);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(20));
            root.getChildren().addAll(
                    new Label("Access denied."),
                    new Label("Only admins can access Product Management.")
            );

            return new Scene(root, 500, 300);
        }
        Label titleLabel = new Label("Product Management");
        Label userLabel = new Label("User: " + user.getUsername());

        nameField.setPromptText("Product Name");
        priceField.setPromptText("Price");
        stockField.setPromptText("Stock Quantity");
        newPriceField.setPromptText("New Price");
        newStockField.setPromptText("New Stock");

        loadProducts();
        loadCategories();

        Button addProductButton = new Button("Add Product");
        Button updatePriceButton = new Button("Update Price");
        Button updateStockButton = new Button("Update Stock");
        Button activateButton = new Button("Activate");
        Button deactivateButton = new Button("Deactivate");
        Button refreshButton = new Button("Refresh");
        Button backButton = new Button("Back");

        addProductButton.setOnAction(event -> addProduct());
        updatePriceButton.setOnAction(event -> updateProductPrice());
        updateStockButton.setOnAction(event -> updateProductStock());
        activateButton.setOnAction(event -> activateProduct());
        deactivateButton.setOnAction(event -> deactivateProduct());
        refreshButton.setOnAction(event -> refreshData());
        backButton.setOnAction(event -> goBackToDashboard());

        VBox leftPanel = new VBox(10,
                new Label("Products"),
                productListView
        );
        leftPanel.setPrefWidth(500);

        VBox rightPanel = new VBox(10,
                new Label("Categories"),
                categoryListView,
                new Label("Add New Product"),
                nameField,
                priceField,
                stockField,
                addProductButton,
                new Label("Edit Selected Product"),
                newPriceField,
                updatePriceButton,
                newStockField,
                updateStockButton,
                activateButton,
                deactivateButton
        );
        rightPanel.setPrefWidth(350);

        HBox centerPanel = new HBox(20, leftPanel, rightPanel);
        centerPanel.setAlignment(Pos.CENTER);

        HBox bottomControls = new HBox(10, refreshButton, backButton);
        bottomControls.setAlignment(Pos.CENTER);

        VBox topPanel = new VBox(8, titleLabel, userLabel);
        topPanel.setAlignment(Pos.CENTER);

        VBox bottomPanel = new VBox(8, bottomControls, statusLabel);
        bottomPanel.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setTop(topPanel);
        root.setCenter(centerPanel);
        root.setBottom(bottomPanel);

        return new Scene(root, 1100, 700);
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
            statusLabel.setText("Enter a product name.");
            return;
        }

        if (selectedCategory == null) {
            statusLabel.setText("Select a category.");
            return;
        }

        double price;
        int stockQty;

        try {
            price = Double.parseDouble(priceText);
            stockQty = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Price and stock must be valid numbers.");
            return;
        }

        boolean added = productService.addProduct(name, selectedCategory.getId(), price, stockQty);

        if (added) {
            statusLabel.setText("Product added successfully.");
            nameField.clear();
            priceField.clear();
            stockField.clear();
            loadProducts();
        } else {
            statusLabel.setText("Failed to add product.");
        }
    }

    private void updateProductPrice() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            statusLabel.setText("Select a product.");
            return;
        }

        String newPriceText = newPriceField.getText().trim();
        double newPrice;

        try {
            newPrice = Double.parseDouble(newPriceText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Enter a valid new price.");
            return;
        }

        boolean updated = productService.updateProductPrice(selectedProduct.getId(), newPrice);

        if (updated) {
            statusLabel.setText("Product price updated.");
            newPriceField.clear();
            loadProducts();
        } else {
            statusLabel.setText("Failed to update product price.");
        }
    }

    private void updateProductStock() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            statusLabel.setText("Select a product.");
            return;
        }

        String newStockText = newStockField.getText().trim();
        int newStock;

        try {
            newStock = Integer.parseInt(newStockText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Enter a valid new stock quantity.");
            return;
        }

        boolean updated = productService.updateProductStock(selectedProduct.getId(), newStock);

        if (updated) {
            statusLabel.setText("Product stock updated.");
            newStockField.clear();
            loadProducts();
        } else {
            statusLabel.setText("Failed to update product stock.");
        }
    }

    private void activateProduct() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            statusLabel.setText("Select a product.");
            return;
        }

        boolean updated = productService.activateProduct(selectedProduct.getId());

        if (updated) {
            statusLabel.setText("Product activated.");
            loadProducts();
        } else {
            statusLabel.setText("Failed to activate product.");
        }
    }

    private void deactivateProduct() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            statusLabel.setText("Select a product.");
            return;
        }

        boolean updated = productService.deactivateProduct(selectedProduct.getId());

        if (updated) {
            statusLabel.setText("Product deactivated.");
            loadProducts();
        } else {
            statusLabel.setText("Failed to deactivate product.");
        }
    }

    private void refreshData() {
        loadProducts();
        loadCategories();
        statusLabel.setText("Product data refreshed.");
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage, user);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("Coffee POS - Dashboard");
    }
}