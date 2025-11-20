package org.daylight.museumapp.services;

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

    public CompletableFuture<UserData> loginAsync(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return ApiService.getInstance().login(username, password);
            } catch (Exception e) {
                // for CompletableFuture
                throw new RuntimeException("Registration failed: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<Boolean> registerAsync(String username, String password, String fullName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return ApiService.getInstance().register(username, password, fullName);
            } catch (IOException | InterruptedException e) {
                // for CompletableFuture
                throw new RuntimeException("Registration failed: " + e.getMessage(), e);
            }
        });
    }
}
