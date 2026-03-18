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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
            LoginView loginView = new LoginView(stage);
            return loginView.createScene();
        }

        Label titleLabel = new Label("Category Management");
        Label userLabel = new Label("User: " + user.getUsername());

        categoryNameField.setPromptText("Category Name");

        loadCategories();

        Button addCategoryButton = new Button("Add Category");
        Button refreshButton = new Button("Refresh");
        Button backButton = new Button("Back");

        addCategoryButton.setOnAction(event -> addCategory());
        refreshButton.setOnAction(event -> refreshCategories());
        backButton.setOnAction(event -> goBackToDashboard());

        VBox centerPanel = new VBox(12,
                new Label("Categories"),
                categoryListView,
                new Label("Add New Category"),
                categoryNameField,
                addCategoryButton
        );
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(10));

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

        return new Scene(root, 700, 550);
    }

    private void loadCategories() {
        List<Category> categories = categoryService.getAllCategories();
        ObservableList<Category> items = FXCollections.observableArrayList(categories);
        categoryListView.setItems(items);
    }

    private void addCategory() {
        String name = categoryNameField.getText().trim();

        if (name.isEmpty()) {
            statusLabel.setText("Enter a category name.");
            return;
        }

        boolean added = categoryService.addCategory(name);

        if (added) {
            statusLabel.setText("Category added successfully.");
            categoryNameField.clear();
            loadCategories();
        } else {
            statusLabel.setText("Failed to add category. It may already exist.");
        }
    }

    private void refreshCategories() {
        loadCategories();
        statusLabel.setText("Categories refreshed.");
    }

    private void goBackToDashboard() {
        DashboardView dashboardView = new DashboardView(stage);
        stage.setScene(dashboardView.createScene());
        stage.setTitle("Coffee POS - Dashboard");
    }
}