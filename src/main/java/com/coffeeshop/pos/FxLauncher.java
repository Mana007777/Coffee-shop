package com.coffeeshop.pos;

import com.coffeeshop.pos.config.DataSeeder;
import com.coffeeshop.pos.config.DatabaseInitializer;
import com.coffeeshop.pos.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;
public class FxLauncher extends Application {

    @Override
    public void start(Stage stage) {


        Font.loadFont("file:/usr/share/fonts/truetype/noto/NotoSansArabic-Regular.ttf", 14);
        Font.loadFont("file:/usr/share/fonts/truetype/noto/NotoSansArabic-Bold.ttf", 14);

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