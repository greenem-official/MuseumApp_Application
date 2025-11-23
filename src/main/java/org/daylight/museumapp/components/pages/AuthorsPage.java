package org.daylight.museumapp.components.pages;

import javafx.scene.layout.StackPane;
import org.daylight.museumapp.components.table.GenericListDetailView;
import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.PagedResult;
import org.daylight.museumapp.dto.filterrelated.PagedRequest;
import org.daylight.museumapp.dto.tables.Author;
import org.daylight.museumapp.services.TablesService;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AuthorsPage {
    private StackPane content;

    public AuthorsPage() {
        initializePage();
    }

    private void initializePage() {
        content = new StackPane();

        Function<PagedRequest, CompletableFuture<ApiResult<PagedResult<Author>>>> fetcher = pageRequest -> {
            return TablesService.getAuthors(); // pageRequest
        };

        GenericListDetailView<Author> view = new GenericListDetailView<>(Author.class, fetcher, false);

        content.getChildren().addAll(view);
    }

    public StackPane getContent() {
        return content;
    }
}