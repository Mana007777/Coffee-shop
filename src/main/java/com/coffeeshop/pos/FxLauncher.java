package com.coffeeshop.pos;

import com.coffeeshop.pos.config.DataSeeder;
import com.coffeeshop.pos.config.DatabaseInitializer;
import com.coffeeshop.pos.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxLauncher extends Application {

    @Override
    public void start(Stage stage) {
        DatabaseInitializer.initializeDatabase();
        DataSeeder.seedData();

        LoginView loginView = new LoginView(stage);
        Scene scene = loginView.createScene();

        stage.setTitle("Coffee POS - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}