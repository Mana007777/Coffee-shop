package com.coffeeshop.pos.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public final class CoffeeTheme {

    private CoffeeTheme() {
    }

    public static final String BG = "#F6F1EB";
    public static final String CARD = "#FFFDF9";
    public static final String CARD_ALT = "#F3E7DA";
    public static final String PRIMARY = "#6F4E37";
    public static final String PRIMARY_DARK = "#4B3224";
    public static final String ACCENT = "#C08B5C";
    public static final String TEXT_DARK = "#2E211B";
    public static final String TEXT_MUTED = "#7A675C";
    public static final String SUCCESS = "#2E7D32";
    public static final String ERROR = "#B23A48";
    public static final String BORDER = "#E6D6C8";

    public static void styleRoot(Region root) {
        root.setStyle("""
            -fx-background-color: linear-gradient(to bottom right, #F8F3EE, #EFE4D8);
            -fx-font-family: "Noto Sans Arabic", "Segoe UI", "Arial";
            """);
    }

    public static VBox createCard(double spacing) {
        VBox box = new VBox(spacing);
        box.setPadding(new Insets(20));
        box.setStyle("""
                -fx-background-color: #FFFDF9;
                -fx-background-radius: 18;
                -fx-border-color: #E6D6C8;
                -fx-border-radius: 18;
                -fx-border-width: 1;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.10), 18, 0.18, 0, 6);
                """);
        return box;
    }

    public static HBox createCardRow(double spacing) {
        HBox box = new HBox(spacing);
        box.setPadding(new Insets(20));
        box.setStyle("""
                -fx-background-color: #FFFDF9;
                -fx-background-radius: 18;
                -fx-border-color: #E6D6C8;
                -fx-border-radius: 18;
                -fx-border-width: 1;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.10), 18, 0.18, 0, 6);
                """);
        return box;
    }

    public static void styleTitle(Label label) {
        label.setStyle("""
            -fx-text-fill: #2E211B;
            -fx-font-size: 24px;
            -fx-font-weight: bold;
            """);
    }

    public static void styleSubtitle(Label label) {
        label.setStyle("""
                -fx-text-fill: #7A675C;
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                """);
    }

    public static void styleSectionTitle(Label label) {
        label.setStyle("""
            -fx-text-fill: #4B3224;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            """);
    }

    public static void styleBodyLabel(Label label) {
        label.setStyle("""
            -fx-text-fill: #5A463B;
            -fx-font-size: 12px;
            -fx-font-weight: 600;
            """);
    }

    public static void styleStatusLabel(Label label) {
        label.setWrapText(true);
        label.setStyle("""
                -fx-text-fill: #6F4E37;
                -fx-font-size: 13px;
                -fx-font-weight: 700;
                -fx-padding: 8 12 8 12;
                -fx-background-color: rgba(192,139,92,0.12);
                -fx-background-radius: 12;
                """);
    }

    public static void setStatusSuccess(Label label, String text) {
        label.setText(text);
        label.setStyle("""
                -fx-text-fill: #2E7D32;
                -fx-font-size: 13px;
                -fx-font-weight: 700;
                -fx-padding: 8 12 8 12;
                -fx-background-color: rgba(46,125,50,0.12);
                -fx-background-radius: 12;
                """);
    }

    public static void setStatusError(Label label, String text) {
        label.setText(text);
        label.setStyle("""
                -fx-text-fill: #B23A48;
                -fx-font-size: 13px;
                -fx-font-weight: 700;
                -fx-padding: 8 12 8 12;
                -fx-background-color: rgba(178,58,72,0.12);
                -fx-background-radius: 12;
                """);
    }

    public static void setStatusNeutral(Label label, String text) {
        label.setText(text);
        styleStatusLabel(label);
    }

    public static void stylePrimaryButton(Button button) {
        button.setStyle("""
                -fx-background-color: #6F4E37;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 14;
                -fx-padding: 12 18 12 18;
                -fx-cursor: hand;
                """);
        button.setOnMouseEntered(e -> button.setStyle("""
                -fx-background-color: #5E412E;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 14;
                -fx-padding: 12 18 12 18;
                -fx-cursor: hand;
                """));
        button.setOnMouseExited(e -> button.setStyle("""
                -fx-background-color: #6F4E37;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 14;
                -fx-padding: 12 18 12 18;
                -fx-cursor: hand;
                """));
    }

    public static void styleSecondaryButton(Button button) {
        button.setStyle("""
                -fx-background-color: #C08B5C;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 14;
                -fx-padding: 12 18 12 18;
                -fx-cursor: hand;
                """);
        button.setOnMouseEntered(e -> button.setStyle("""
                -fx-background-color: #AF7A4B;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 14;
                -fx-padding: 12 18 12 18;
                -fx-cursor: hand;
                """));
        button.setOnMouseExited(e -> button.setStyle("""
                -fx-background-color: #C08B5C;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 14;
                -fx-padding: 12 18 12 18;
                -fx-cursor: hand;
                """));
    }

    public static void styleDangerButton(Button button) {
        button.setStyle("""
                -fx-background-color: #B23A48;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 14;
                -fx-padding: 12 18 12 18;
                -fx-cursor: hand;
                """);
    }

    public static void styleGhostButton(Button button) {
        button.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #6F4E37;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-border-color: #D7C0AC;
                -fx-border-width: 1.4;
                -fx-border-radius: 14;
                -fx-background-radius: 14;
                -fx-padding: 12 18 12 18;
                -fx-cursor: hand;
                """);
    }

    public static void styleTextField(TextField field) {
        field.setStyle("""
                -fx-background-color: white;
                -fx-text-fill: #2E211B;
                -fx-prompt-text-fill: #9C8777;
                -fx-font-size: 14px;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-border-color: #E1D1C3;
                -fx-padding: 11 14 11 14;
                """);
    }

    public static void styleTextArea(TextArea area) {
        area.setStyle("""
                -fx-background-color: white;
                -fx-control-inner-background: white;
                -fx-text-fill: #2E211B;
                -fx-font-size: 14px;
                -fx-background-radius: 14;
                -fx-border-radius: 14;
                -fx-border-color: #E1D1C3;
                -fx-padding: 12;
                """);
    }

    public static <T> void styleListView(ListView<T> listView) {
        listView.setStyle("""
                -fx-background-color: white;
                -fx-control-inner-background: white;
                -fx-background-radius: 16;
                -fx-border-radius: 16;
                -fx-border-color: #E1D1C3;
                -fx-padding: 6;
                -fx-font-size: 14px;
                """);
    }

    public static void styleHeaderBar(Region region) {
        region.setStyle("""
                -fx-background-color: linear-gradient(to right, #6F4E37, #8B5E3C);
                -fx-background-radius: 20;
                -fx-padding: 18 24 18 24;
                -fx-effect: dropshadow(gaussian, rgba(70,40,20,0.18), 20, 0.18, 0, 8);
                """);
    }

    public static Label createMetricLabel(String title, String value) {
        Label label = new Label(title + "\n" + value);
        label.setTextFill(Color.web(TEXT_DARK));
        label.setStyle("""
                -fx-background-color: #F6EDE4;
                -fx-background-radius: 16;
                -fx-border-color: #E6D6C8;
                -fx-border-radius: 16;
                -fx-padding: 16;
                -fx-font-size: 15px;
                -fx-font-weight: bold;
                """);
        return label;
    }
}