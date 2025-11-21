package org.daylight.museumapp.services;

import org.daylight.museumapp.dto.ApiResult;
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
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public UserData getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserData currentUser) {
        this.currentUser = currentUser;
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
}
