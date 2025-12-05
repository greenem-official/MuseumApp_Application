package org.daylight.museumapp.dto.filterrelated;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.util.Pair;

import java.util.function.Consumer;

public class SortButton {

    private final Button button;
    private final String fieldName;
    private final Consumer<Pair<String, Boolean>> onSortChanged;

    // текущее состояние: null = неактивна, true = asc, false = desc
    private Boolean ascending = null;

    public SortButton(String fieldName, Consumer<Pair<String, Boolean>> onSortChanged) {
        this.fieldName = fieldName;
        this.onSortChanged = onSortChanged;

        button = new Button("↕");
        button.setCursor(Cursor.HAND);
        button.getStyleClass().add("museum-ld-nav-button");

        button.setOnAction(evt -> toggle());
    }

    private void toggle() {
        if (ascending == null || ascending == false) {
            ascending = true;
        } else {
            ascending = false;
        }
        updateIcon();
        onSortChanged.accept(new Pair<>(fieldName, ascending));
    }

    private void updateIcon() {
        if (ascending == null) {
            button.setText("↕");
        } else if (ascending) {
            button.setText("↑");
        } else {
            button.setText("↓");
        }
    }

    public Button getButton() {
        return button;
    }

    // сброс состояния кнопки
    public void reset() {
        ascending = null;
        updateIcon();
    }

    // принудительно установить направление сортировки
    public void setSort(Boolean asc) {
        ascending = asc;
        updateIcon();
    }

    // получить состояние
    public Boolean getSort() {
        return ascending;
    }
}

