package org.daylight.museumapp.components.pages;

import javafx.scene.layout.StackPane;
import org.daylight.museumapp.components.table.GenericListDetailView;
import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.PagedResult;
import org.daylight.museumapp.dto.UserRole;
import org.daylight.museumapp.dto.filterrelated.PagedRequest;
import org.daylight.museumapp.dto.tables.Collection;
import org.daylight.museumapp.services.AuthService;
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

        Function<PagedRequest, CompletableFuture<ApiResult<PagedResult<Collection>>>> fetcher = TablesService::getCollections;

        boolean isAdmin = false;
        if(AuthService.getInstance().isAuthenticated()) isAdmin = AuthService.getInstance().getCurrentUser().getRole() == UserRole.ADMIN;
        GenericListDetailView<Collection> view = new GenericListDetailView<>(Collection.class, fetcher, isAdmin, "Коллекции");

        content.getChildren().addAll(view);
    }

    public StackPane getContent() {
        return content;
    }
}