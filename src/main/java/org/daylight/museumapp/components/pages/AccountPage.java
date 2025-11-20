package org.daylight.museumapp.components.pages;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AccountPage {
    private VBox content;

    public AccountPage() {
        initializePage();
    }

    private void initializePage() {
        content = new VBox(16);
        content.setPadding(new Insets(24));

        Label title = new Label("Аккаунт");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label placeholder = new Label("Здесь будет что-то");

        content.getChildren().addAll(title, placeholder);
    }

    public VBox getContent() {
        return content;
    }
}