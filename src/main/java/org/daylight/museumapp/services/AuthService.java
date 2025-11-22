package org.daylight.museumapp.services;

import javafx.application.Platform;
import org.daylight.museumapp.components.common.GlobalHooks;
import org.daylight.museumapp.components.common.storage.StorageUtil;
import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.TokenCheckResponse;
import org.daylight.museumapp.dto.UserData;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private static AuthService instance;
    private UserData currentUser;

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public void logout() {
        currentUser = null;
        StorageUtil.onSave();
        Platform.runLater(GlobalHooks.getInstance().sidebarOnAuthStateChange);
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public UserData getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserData currentUser) {
        this.currentUser = currentUser;
        StorageUtil.onSave();
    }

    public CompletableFuture<ApiResult<UserData>> loginAsync(String username, String password) {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().login(username, password)
        );
    }

    public CompletableFuture<ApiResult<Void>> registerAsync(String username, String password, String fullName) {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().register(username, password, fullName)
        );
    }

    public CompletableFuture<ApiResult<UserData>> registerAndLogin(String username, String password, String fullName) {
        return registerAsync(username, password, fullName)
                .thenCompose(result -> {
                    if (!result.isSuccess()) {
                        return CompletableFuture.completedFuture(ApiResult.error(result.getError()));
                    }
                    return loginAsync(username, password);
                });
    }

    public CompletableFuture<ApiResult<TokenCheckResponse>> checkTokenAsync(String token) {
        return CompletableFuture.supplyAsync(() ->
                ApiService.getInstance().checkToken(token)
        );
    }
}
