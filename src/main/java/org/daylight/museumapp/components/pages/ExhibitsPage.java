package org.daylight.museumapp.components.pages;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ExhibitsPage {
    private VBox content;

    public ExhibitsPage() {
        initializePage();
    }

    private void initializePage() {
        content = new VBox(16);
        content.setPadding(new Insets(24));

        Label title = new Label("Страница экспонатов");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // TODO: Добавить таблицу экспонатов, фильтры, кнопки действий
        Label placeholder = new Label("Здесь будет таблица экспонатов с возможностью фильтрации и редактирования");

        content.getChildren().addAll(title, placeholder);
    }

    public VBox getContent() {
        System.out.println(content);
        return content;
    }
}