package org.daylight.museumapp.services;

import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.PagedResult;
import org.daylight.museumapp.dto.filterrelated.PagedRequest;
import org.daylight.museumapp.dto.tables.*;

import java.util.concurrent.CompletableFuture;

public class TablesService {
    public static CompletableFuture<ApiResult<PagedResult<Item>>> getItems(PagedRequest pagedRequest) {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().getItems(AuthService.getInstance().getCurrentUser().getToken(), pagedRequest)
        );
    }

    public static CompletableFuture<ApiResult<PagedResult<Collection>>> getCollections(PagedRequest pagedRequest) {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().getCollections(AuthService.getInstance().getCurrentUser().getToken(), pagedRequest)
        );
    }

    public static CompletableFuture<ApiResult<PagedResult<Hall>>> getHalls(PagedRequest pagedRequest) {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().getHalls(AuthService.getInstance().getCurrentUser().getToken(), pagedRequest)
        );
    }

    public static CompletableFuture<ApiResult<PagedResult<Author>>> getAuthors(PagedRequest pagedRequest) {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().getAuthors(AuthService.getInstance().getCurrentUser().getToken(), pagedRequest)
        );
    }

    public static CompletableFuture<ApiResult<PagedResult<User>>> getUsers(PagedRequest pagedRequest) {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().getUsers(AuthService.getInstance().getCurrentUser().getToken(), pagedRequest)
        );
    }

    public static CompletableFuture<ApiResult<Void>> createItem(Item item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> updateItem(Item item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> deleteItem(Item item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> createCollection(Collection item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> updateCollection(Collection item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> deleteCollection(Collection item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> createHall(Hall item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> updateHall(Hall item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> deleteHall(Hall item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> createAuthor(Author item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> updateAuthor(Author item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> deleteAuthor(Author item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> createUser(User item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> updateUser(User item) {
        return null;
        // TODO
    }

    public static CompletableFuture<ApiResult<Void>> deleteUser(User item) {
        return null;
        // TODO
    }
}
