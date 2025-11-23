package org.daylight.museumapp.components.pages;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.daylight.museumapp.components.table.GenericListDetailView;
import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.PageRequest;
import org.daylight.museumapp.dto.PagedResult;
import org.daylight.museumapp.dto.tables.Item;
import org.daylight.museumapp.services.TablesService;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ExhibitsPage {
    private StackPane content;

    public ExhibitsPage() {
        initializePage();
    }

    private void initializePage() {
        content = new StackPane();

        Function<PageRequest, CompletableFuture<ApiResult<PagedResult<Item>>>> fetcher = pageRequest -> {
            return TablesService.getItems(); // pageRequest
        };

        GenericListDetailView<Item> view = new GenericListDetailView<>(Item.class, fetcher, false);

        content.getChildren().addAll(view);
    }

    public StackPane getContent() {
        return content;
    }
}