package org.daylight.museumapp.services;

import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.PagedResult;
import org.daylight.museumapp.dto.tables.Item;

import java.util.concurrent.CompletableFuture;

public class TablesService {
    public static CompletableFuture<ApiResult<PagedResult<Item>>> getItems() {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().getItems(AuthService.getInstance().getCurrentUser().getToken())
        );
    }
}
