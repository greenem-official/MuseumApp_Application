package org.daylight.museumapp.services;

import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.PagedResult;
import org.daylight.museumapp.dto.tables.Author;
import org.daylight.museumapp.dto.tables.Collection;
import org.daylight.museumapp.dto.tables.Hall;
import org.daylight.museumapp.dto.tables.Item;

import java.util.concurrent.CompletableFuture;

public class TablesService {
    public static CompletableFuture<ApiResult<PagedResult<Item>>> getItems() {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().getItems(AuthService.getInstance().getCurrentUser().getToken())
        );
    }

    public static CompletableFuture<ApiResult<PagedResult<Collection>>> getCollections() {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().getCollections(AuthService.getInstance().getCurrentUser().getToken())
        );
    }

    public static CompletableFuture<ApiResult<PagedResult<Hall>>> getHalls() {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().getHalls(AuthService.getInstance().getCurrentUser().getToken())
        );
    }

    public static CompletableFuture<ApiResult<PagedResult<Author>>> getAuthors() {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().getAuthors(AuthService.getInstance().getCurrentUser().getToken())
        );
    }
}
