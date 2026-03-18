package com.coffeeshop.pos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FxLauncher extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Coffee POS - JavaFX is working");

        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 800, 500);

        stage.setTitle("Coffee POS");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}