package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.Category;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.CategoryService;
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

public class CategoryManagementView {

    private final Stage stage;
    private final CategoryService categoryService;

    private final ListView<Category> categoryListView;
    private final TextField categoryNameField;
    private final Label statusLabel;

    public CategoryManagementView(Stage stage) {
        this.stage = stage;
        this.categoryService = new CategoryService();

        this.categoryListView = new ListView<>();
        this.categoryNameField = new TextField();
        this.statusLabel = new Label();
    }

    public Scene createScene() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            return new LoginView(stage).createScene();
        }

        configureControls();
        loadCategories();

        VBox header = buildHeader(user);
        HBox overviewRow = buildOverviewRow();
        VBox listCard = buildListCard();
        VBox formCard = buildFormCard();
        VBox footerCard = buildFooterCard();

        VBox root = new VBox(24, header, overviewRow, listCard, formCard, footerCard);
        root.setPadding(new Insets(26));
        CoffeeTheme.styleRoot(root);
        VBox.setVgrow(listCard, Priority.ALWAYS);

        return new Scene(root, 980, 820);
    }

    private void configureControls() {
        categoryNameField.setPromptText("ناوی پۆل");
        categoryNameField.setMaxWidth(Double.MAX_VALUE);
        categoryNameField.setPrefHeight(46);
        categoryNameField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        CoffeeTheme.styleTextField(categoryNameField);
        categoryNameField.setStyle(categoryNameField.getStyle() + """
                -fx-alignment: center-right;
                """);

        CoffeeTheme.styleListView(categoryListView);
        CoffeeTheme.styleStatusLabel(statusLabel);
        statusLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        categoryListView.setPrefHeight(420);
        categoryListView.setMinHeight(340);
        categoryListView.setMaxWidth(Double.MAX_VALUE);

        VBox.setVgrow(categoryListView, Priority.ALWAYS);
    }

    private VBox buildHeader(User user) {
        Label titleLabel = new Label("بەڕێوەبردنی پۆلەکان");
        titleLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 30px;
                -fx-font-weight: bold;
                """);

        Label userLabel = new Label("بەکارهێنەر: " + user.getUsername());
        userLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.92);
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);

        Label helperLabel = new Label("زیادکردن و بینینی پۆلەکانی کاڵا بۆ ڕێکخستنی باشتر");
        helperLabel.setWrapText(true);
        helperLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.86);
                -fx-font-size: 13px;
                -fx-font-weight: 500;
                """);

        VBox header = new VBox(8, titleLabel, userLabel, helperLabel);
        CoffeeTheme.styleHeaderBar(header);
        return header;
    }

    private HBox buildOverviewRow() {
        VBox stat1 = createInfoCard("پۆلەکان", "ڕێکخستنی کاڵا", "هەر کاڵایەک دەکرێت بخرێتە ژێر پۆلێک");
        VBox stat2 = createInfoCard("زیادکردنی نوێ", "بە خێرایی", "پۆلی نوێ بۆ بەڕێوەبردنی باشتر زیاد بکە");
        VBox stat3 = createInfoCard("نوێکردنەوە", "بینینی داتای تازە", "لیستی پۆلەکان هەمیشە نوێ دەبێتەوە");

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

    private VBox buildListCard() {
        Label categoriesLabel = new Label("لیستی پۆلەکان");
        CoffeeTheme.styleSectionTitle(categoriesLabel);

        Label helperLabel = new Label("هەموو پۆلە تۆمارکراوەکان لێرە دەر دەکەون.");
        helperLabel.setWrapText(true);
        helperLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(helperLabel);

        VBox listCard = CoffeeTheme.createCard(14);
        VBox.setVgrow(categoryListView, Priority.ALWAYS);
        listCard.getChildren().addAll(categoriesLabel, helperLabel, categoryListView);

        return listCard;
    }

    private VBox buildFormCard() {
        Label addLabel = new Label("زیادکردنی پۆلی نوێ");
        CoffeeTheme.styleSectionTitle(addLabel);

        Label helperLabel = new Label("ناوی پۆل بنووسە، پاشان کلیک لە دوگمەی زیادکردن بکە.");
        helperLabel.setWrapText(true);
        helperLabel.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        CoffeeTheme.styleBodyLabel(helperLabel);

        Button addCategoryButton = new Button("زیادکردن");
        Button clearButton = new Button("پاککردنەوە");

        CoffeeTheme.stylePrimaryButton(addCategoryButton);
        CoffeeTheme.styleGhostButton(clearButton);

        addCategoryButton.setOnAction(event -> addCategory());
        clearButton.setOnAction(event -> {
            categoryNameField.clear();
            CoffeeTheme.setStatusNeutral(statusLabel, "خانەکان پاککرانەوە.");
        });

        HBox buttonRow = new HBox(12, addCategoryButton, clearButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox formCard = CoffeeTheme.createCard(14);
        formCard.getChildren().addAll(addLabel, helperLabel, categoryNameField, buttonRow, statusLabel);

        return formCard;
    }

    private VBox buildFooterCard() {
        Button refreshButton = new Button("نوێکردنەوە");
        Button backButton = new Button("گەڕانەوە");

        CoffeeTheme.styleSecondaryButton(refreshButton);
        CoffeeTheme.styleGhostButton(backButton);

        refreshButton.setOnAction(event -> refreshCategories());
        backButton.setOnAction(event -> goBackToDashboard());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonRow = new HBox(12, refreshButton, spacer, backButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox footerCard = CoffeeTheme.createCard(12);
        footerCard.getChildren().add(buttonRow);
        return footerCard;
    }

    private void loadCategories() {
        List<Category> categories = categoryService.getAllCategories();
        ObservableList<Category> items = FXCollections.observableArrayList(categories);
        categoryListView.setItems(items);
    }

    private void addCategory() {
        String name = categoryNameField.getText().trim();

        if (name.isEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "تکایە ناوی پۆل بنووسە.");
            return;
        }

        boolean added = categoryService.addCategory(name);

        if (added) {
            CoffeeTheme.setStatusSuccess(statusLabel, "پۆل بە سەرکەوتوویی زیادکرا.");
            categoryNameField.clear();
            loadCategories();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "هەڵەیەک ڕوویدا. لەوانەیە ئەم پۆلە پێشتر هەبێت.");
        }
    }

    private void refreshCategories() {
        loadCategories();
        CoffeeTheme.setStatusNeutral(statusLabel, "پۆلەکان نوێکرانەوە.");
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("داشبۆردی قاوەخانە");
    }
}