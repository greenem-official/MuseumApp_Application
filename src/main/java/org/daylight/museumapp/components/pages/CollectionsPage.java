package org.daylight.museumapp.components.pages;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.daylight.museumapp.components.table.GenericListDetailView;
import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.PageRequest;
import org.daylight.museumapp.dto.PagedResult;
import org.daylight.museumapp.dto.tables.Collection;
import org.daylight.museumapp.dto.tables.Item;
import org.daylight.museumapp.services.TablesService;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CollectionsPage {
    private StackPane content;

    public CollectionsPage() {
        initializePage();
    }

    private void initializePage() {
        content = new StackPane();

        Function<PageRequest, CompletableFuture<ApiResult<PagedResult<Collection>>>> fetcher = pageRequest -> {
            return TablesService.getCollections(); // pageRequest
        };

        GenericListDetailView<Collection> view = new GenericListDetailView<>(Collection.class, fetcher, false);

        content.getChildren().addAll(view);
    }

    public StackPane getContent() {
        return content;
    }
}