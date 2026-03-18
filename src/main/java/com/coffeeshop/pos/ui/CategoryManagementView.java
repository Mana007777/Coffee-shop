package com.coffeeshop.pos.ui;

import com.coffeeshop.pos.config.SessionManager;
import com.coffeeshop.pos.model.Category;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.CategoryService;
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
            LoginView loginView = new LoginView(stage);
            return loginView.createScene();
        }

        Label titleLabel = new Label("Category Management");
        titleLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 28px;
                -fx-font-weight: bold;
                """);

        Label userLabel = new Label("User: " + user.getUsername());
        userLabel.setStyle("""
                -fx-text-fill: rgba(255,255,255,0.92);
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);

        VBox header = new VBox(6, titleLabel, userLabel);
        CoffeeTheme.styleHeaderBar(header);

        categoryNameField.setPromptText("Category Name");
        CoffeeTheme.styleTextField(categoryNameField);
        CoffeeTheme.styleListView(categoryListView);
        CoffeeTheme.styleStatusLabel(statusLabel);

        loadCategories();

        Button addCategoryButton = new Button("Add Category");
        Button refreshButton = new Button("Refresh");
        Button backButton = new Button("Back");

        CoffeeTheme.stylePrimaryButton(addCategoryButton);
        CoffeeTheme.styleSecondaryButton(refreshButton);
        CoffeeTheme.styleGhostButton(backButton);

        addCategoryButton.setOnAction(event -> addCategory());
        refreshButton.setOnAction(event -> refreshCategories());
        backButton.setOnAction(event -> goBackToDashboard());

        Label categoriesLabel = new Label("Categories");
        CoffeeTheme.styleSectionTitle(categoriesLabel);

        VBox listCard = CoffeeTheme.createCard(14);
        listCard.getChildren().addAll(categoriesLabel, categoryListView);

        Label addLabel = new Label("Add New Category");
        CoffeeTheme.styleSectionTitle(addLabel);

        VBox formCard = CoffeeTheme.createCard(14);
        formCard.getChildren().addAll(addLabel, categoryNameField, addCategoryButton, statusLabel);

        HBox bottomButtons = new HBox(12, refreshButton, backButton);
        bottomButtons.setAlignment(Pos.CENTER_LEFT);

        VBox footerCard = CoffeeTheme.createCard(12);
        footerCard.getChildren().add(bottomButtons);

        VBox root = new VBox(24, header, listCard, formCard, footerCard);
        root.setPadding(new Insets(26));
        CoffeeTheme.styleRoot(root);

        return new Scene(root, 840, 700);
    }

    private void loadCategories() {
        List<Category> categories = categoryService.getAllCategories();
        ObservableList<Category> items = FXCollections.observableArrayList(categories);
        categoryListView.setItems(items);
    }

    private void addCategory() {
        String name = categoryNameField.getText().trim();

        if (name.isEmpty()) {
            CoffeeTheme.setStatusError(statusLabel, "Enter a category name.");
            return;
        }

        boolean added = categoryService.addCategory(name);

        if (added) {
            CoffeeTheme.setStatusSuccess(statusLabel, "Category added successfully.");
            categoryNameField.clear();
            loadCategories();
        } else {
            CoffeeTheme.setStatusError(statusLabel, "Failed to add category. It may already exist.");
        }
    }

    private void refreshCategories() {
        loadCategories();
        CoffeeTheme.setStatusNeutral(statusLabel, "Categories refreshed.");
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("Coffee POS - Dashboard");
    }
}