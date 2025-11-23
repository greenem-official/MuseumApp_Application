package org.daylight.museumapp.components.pages;

import javafx.scene.layout.StackPane;
import org.daylight.museumapp.components.table.GenericListDetailView;
import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.PagedResult;
import org.daylight.museumapp.dto.filterrelated.PagedRequest;
import org.daylight.museumapp.dto.filterrelated.SortRequest;
import org.daylight.museumapp.dto.tables.Item;
import org.daylight.museumapp.services.TablesService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ExhibitsPage {
    private StackPane content;

    public ExhibitsPage() {
        initializePage();
    }

    private void initializePage() {
        content = new StackPane();

        Function<PagedRequest, CompletableFuture<ApiResult<PagedResult<Item>>>> fetcher = TablesService::getItems;

        GenericListDetailView<Item> view = new GenericListDetailView<>(Item.class, fetcher, false);

        content.getChildren().addAll(view);
    }

    public StackPane getContent() {
        return content;
    }
}