package org.daylight.museumapp.components.table;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.daylight.museumapp.dto.PagedResult;

import java.util.function.IntConsumer;

/**
 * Малый контрол пагинации. Хранит callback для перехода на страницу.
 */
public class PaginationBar extends HBox {

    private final IntConsumer goToPageCallback;
    private final Button prev = new Button("◀");
    private final Button next = new Button("▶");
    private final Label info = new Label("—");

    public PaginationBar(IntConsumer goToPageCallback) {
        this.goToPageCallback = goToPageCallback;
        this.getStyleClass().add("museum-ld-pagination");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(8, 0, 0, 0));
        this.setSpacing(8);

        prev.setOnAction(e -> goToPageCallback.accept(-1)); // special: owner should handle bounds
        next.setOnAction(e -> goToPageCallback.accept(-2));

        this.getChildren().addAll(prev, info, next);
    }

    /**
     * Обновляет визуал в зависимости от PagedResult и текущей страницы.
     */
    public void update(PagedResult<?> pageResult, int currentPage) {
        if (pageResult == null) {
            info.setText("Загрузка...");
            prev.setDisable(true);
            next.setDisable(true);
            return;
        }
        int totalPages = pageResult.getTotalPages();
        long totalItems = pageResult.getTotalItems();
        info.setText(String.format("Страница %d из %d  —  Всего: %d", currentPage + 1, totalPages, totalItems));
        prev.setDisable(currentPage <= 0);
        next.setDisable(currentPage >= totalPages - 1);

        prev.setOnAction(e -> goToPageCallback.accept(Math.max(0, currentPage - 1)));
        next.setOnAction(e -> goToPageCallback.accept(Math.min(totalPages - 1, currentPage + 1)));
    }
}
