package org.daylight.museumapp.components.pages;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AuthorsPage {
    private VBox content;

    public AuthorsPage() {
        initializePage();
    }

    private void initializePage() {
        content = new VBox(16);
        content.setPadding(new Insets(24));

        Label title = new Label("Страница авторов");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label placeholder = new Label("Здесь будет база данных авторов произведений");

        content.getChildren().addAll(title, placeholder);
    }

    public VBox getContent() {
        return content;
    }
}