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
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
            return createAccessDeniedScene();
        }

        configureControls();
        loadProducts();
        loadCategories();

        VBox header = buildHeader(user);
        HBox overviewRow = buildOverviewRow();
        VBox productsPanel = buildProductsPanel();
        VBox managementPanel = buildManagementPanel();
        VBox footer = buildFooter();

        HBox centerPanel = new HBox(24, productsPanel, managementPanel);
        centerPanel.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(productsPanel, Priority.ALWAYS);
        HBox.setHgrow(managementPanel, Priority.ALWAYS);

        VBox root = new VBox(24, header, overviewRow, centerPanel, footer);
        root.setPadding(new Insets(26));
        CoffeeTheme.styleRoot(root);
        VBox.setVgrow(centerPanel, Priority.ALWAYS);

        return new Scene(root, 1280, 860);
    }

    private Scene createAccessDeniedScene() {
        Label deniedTitle = new Label("دەستگەیشتن ڕێگەپێنەدراوە");
        CoffeeTheme.styleSectionTitle(deniedTitle);

        Label deniedText = new Label("تەنها ئەدمین دەتوانێت بەشی بەڕێوەبردنی کاڵاکان بەکاربهێنێت.");
        deniedText.setWrapText(true);
        deniedText.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(deniedText);

        Button backButton = new Button("گەڕانەوە");
        CoffeeTheme.styleGhostButton(backButton);
        backButton.setOnAction(e -> goBackToDashboard());

        VBox deniedRoot = CoffeeTheme.createCard(16);
        deniedRoot.setAlignment(Pos.CENTER);
        deniedRoot.setPadding(new Insets(32));
        deniedRoot.setMaxWidth(520);
        deniedRoot.getChildren().addAll(deniedTitle, deniedText, backButton);

        VBox wrapper = new VBox(deniedRoot);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(30));
        CoffeeTheme.styleRoot(wrapper);

        return new Scene(wrapper, 700, 360);
    }

    private VBox buildHeader(User user) {
        Label titleLabel = new Label("بەڕێوەبردنی کاڵاکان");
        titleLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 30px;
                -fx-font-weight: bold;
                """);

        Label userLabel = new Label("ئەدمین: " + user.getUsername());
        userLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.92);
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);

        Label helperLabel = new Label("زیادکردن، دەستکاریکردن، چالاککردن و کۆنتڕۆڵی کۆگا لە یەک شوێن");
        helperLabel.setWrapText(true);
        helperLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.84);
                -fx-font-size: 13px;
                -fx-font-weight: 500;
                """);

        VBox header = new VBox(8, titleLabel, userLabel, helperLabel);
        CoffeeTheme.styleHeaderBar(header);
        return header;
    }

    private HBox buildOverviewRow() {
        VBox stat1 = createInfoCard("کاڵاکان", "بینینی هەموو کاڵاکان", "هەڵبژاردنی کاڵا بۆ دەستکاری");
        VBox stat2 = createInfoCard("پۆلەکان", "ڕێکخستنی جۆری کاڵا", "پێویستە بۆ زیادکردنی کاڵای نوێ");
        VBox stat3 = createInfoCard("کۆگا و نرخ", "نوێکردنەوەی خێرا", "بۆ کارکردنی ڕۆژانەی قاوەخانە");

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

    private void configureControls() {
        nameField.setPromptText("ناوی کاڵا");
        priceField.setPromptText("نرخ بە دینار");
        stockField.setPromptText("ژمارەی کۆگا");
        newPriceField.setPromptText("نرخی نوێ بە دینار");
        newStockField.setPromptText("کۆگای نوێ");

        styleInput(nameField);
        styleInput(priceField);
        styleInput(stockField);
        styleInput(newPriceField);
        styleInput(newStockField);

        CoffeeTheme.styleListView(productListView);
        CoffeeTheme.styleListView(categoryListView);
        CoffeeTheme.styleStatusLabel(statusLabel);

        productListView.setPrefHeight(600);
        productListView.setMinHeight(520);
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

    private void styleInput(TextField field) {
        CoffeeTheme.styleTextField(field);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        field.setStyle(field.getStyle() + """
                -fx-alignment: center-right;
                """);
    }

    private VBox buildProductsPanel() {
        Label productsLabel = new Label("کاڵاکان");
        CoffeeTheme.styleSectionTitle(productsLabel);

        Label helperLabel = new Label("کاڵایەک هەڵبژێرە بۆ دەستکاری کردنی نرخ، کۆگا یان دۆخی چالاک/ناچالاک.");
        helperLabel.setWrapText(true);
        helperLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(helperLabel);

        VBox panel = CoffeeTheme.createCard(14);
        panel.setPrefWidth(760);
        panel.setMinWidth(700);

        VBox.setVgrow(productListView, Priority.ALWAYS);
        panel.getChildren().addAll(productsLabel, helperLabel, productListView);

        return panel;
    }

    private VBox buildManagementPanel() {
        Label categoriesLabel = new Label("پۆلەکان");
        CoffeeTheme.styleSectionTitle(categoriesLabel);

        Label addProductLabel = new Label("زیادکردنی کاڵای نوێ");
        CoffeeTheme.styleSectionTitle(addProductLabel);

        Label editProductLabel = new Label("دەستکاری کاڵا");
        CoffeeTheme.styleSectionTitle(editProductLabel);

        Label addHintLabel = new Label("سەرەتا پۆل هەڵبژێرە، پاشان زانیارییەکانی کاڵا بنووسە.");
        addHintLabel.setWrapText(true);
        addHintLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(addHintLabel);

        Button addProductButton = new Button("زیادکردن");
        Button updatePriceButton = new Button("نوێکردنەوەی نرخ");
        Button updateStockButton = new Button("نوێکردنەوەی کۆگا");
        Button activateButton = new Button("چالاککردن");
        Button deactivateButton = new Button("ناچالاککردن");

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
        panel.setPrefWidth(470);
        panel.setMinWidth(440);

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
        Button refreshButton = new Button("نوێکردنەوە");
        Button clearButton = new Button("پاککردنەوە");
        Button backButton = new Button("گەڕانەوە");

        CoffeeTheme.styleGhostButton(refreshButton);
        CoffeeTheme.styleGhostButton(clearButton);
        CoffeeTheme.styleGhostButton(backButton);

        refreshButton.setOnAction(event -> refreshData());
        clearButton.setOnAction(event -> clearFields());
        backButton.setOnAction(event -> goBackToDashboard());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttons = new HBox(12, refreshButton, clearButton, spacer, backButton);
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
            CoffeeTheme.setStatusError(statusLabel, "تکایە سەرەتا پۆل هەڵبژێرە.");
            return;
        }

        if (name.isEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "تکایە ناوی کاڵا بنووسە.");
            return;
        }

        double price;
        int stockQty;

        try {
            price = Double.parseDouble(priceText);
            stockQty = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            CoffeeTheme.setStatusError(statusLabel, "نرخ و کۆگا دەبێت بە ژمارەی دروست بنووسرێن.");
            return;
        }

        if (price < 0) {
            CoffeeTheme.setStatusError(statusLabel, "نرخ ناتوانێت منفی بێت.");
            return;
        }

        if (stockQty < 0) {
            CoffeeTheme.setStatusError(statusLabel, "کۆگا ناتوانێت منفی بێت.");
            return;
        }

        boolean added = productService.addProduct(name, selectedCategory.getId(), price, stockQty);

        if (added) {
            CoffeeTheme.setStatusSuccess(statusLabel, "کاڵا بە سەرکەوتوویی زیادکرا.");
            clearAddProductFields();
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "هەڵەیەک لە زیادکردنی کاڵا ڕوویدا.");
        }
    }

    private void updateProductPrice() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "تکایە کاڵایەک هەڵبژێرە.");
            return;
        }

        String newPriceText = newPriceField.getText().trim();
        double newPrice;

        try {
            newPrice = Double.parseDouble(newPriceText);
        } catch (NumberFormatException e) {
            CoffeeTheme.setStatusError(statusLabel, "تکایە نرخی نوێ بە شێوەی دروست بنووسە.");
            return;
        }

        if (newPrice < 0) {
            CoffeeTheme.setStatusError(statusLabel, "نرخ ناتوانێت منفی بێت.");
            return;
        }

        boolean updated = productService.updateProductPrice(selectedProduct.getId(), newPrice);

        if (updated) {
            CoffeeTheme.setStatusSuccess(statusLabel, "نرخی کاڵا نوێکرایەوە.");
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "هەڵەیەک لە نوێکردنەوەی نرخ ڕوویدا.");
        }
    }

    private void updateProductStock() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "تکایە کاڵایەک هەڵبژێرە.");
            return;
        }

        String newStockText = newStockField.getText().trim();
        int newStock;

        try {
            newStock = Integer.parseInt(newStockText);
        } catch (NumberFormatException e) {
            CoffeeTheme.setStatusError(statusLabel, "تکایە کۆگای نوێ بە شێوەی دروست بنووسە.");
            return;
        }

        if (newStock < 0) {
            CoffeeTheme.setStatusError(statusLabel, "کۆگا ناتوانێت منفی بێت.");
            return;
        }

        boolean updated = productService.updateProductStock(selectedProduct.getId(), newStock);

        if (updated) {
            CoffeeTheme.setStatusSuccess(statusLabel, "کۆگای کاڵا نوێکرایەوە.");
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "هەڵەیەک لە نوێکردنەوەی کۆگا ڕوویدا.");
        }
    }

    private void activateProduct() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "تکایە کاڵایەک هەڵبژێرە.");
            return;
        }

        boolean updated = productService.activateProduct(selectedProduct.getId());

        if (updated) {
            CoffeeTheme.setStatusSuccess(statusLabel, "کاڵا چالاککرا.");
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "هەڵەیەک لە چالاککردنی کاڵا ڕوویدا.");
        }
    }

    private void deactivateProduct() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            CoffeeTheme.setStatusError(statusLabel, "تکایە کاڵایەک هەڵبژێرە.");
            return;
        }

        boolean updated = productService.deactivateProduct(selectedProduct.getId());

        if (updated) {
            CoffeeTheme.setStatusSuccess(statusLabel, "کاڵا ناچالاککرا.");
            loadProducts();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "هەڵەیەک لە ناچالاککردنی کاڵا ڕوویدا.");
        }
    }

    private void refreshData() {
        loadProducts();
        loadCategories();
        CoffeeTheme.setStatusNeutral(statusLabel, "داتاکان نوێکرانەوە.");
    }

    private void clearFields() {
        clearAddProductFields();
        newPriceField.clear();
        newStockField.clear();
        productListView.getSelectionModel().clearSelection();
        categoryListView.getSelectionModel().clearSelection();
        CoffeeTheme.setStatusNeutral(statusLabel, "خانەکان پاککرانەوە.");
    }

    private void clearAddProductFields() {
        nameField.clear();
        priceField.clear();
        stockField.clear();
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("داشبۆردی قاوەخانە");
    }
}