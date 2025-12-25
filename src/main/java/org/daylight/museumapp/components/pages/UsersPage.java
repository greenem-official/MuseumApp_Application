package org.daylight.museumapp.components.pages;

import javafx.scene.layout.StackPane;
import org.daylight.museumapp.components.table.GenericListDetailView;
import org.daylight.museumapp.dto.UserRole;
import org.daylight.museumapp.dto.tables.Author;
import org.daylight.museumapp.dto.tables.User;
import org.daylight.museumapp.services.AuthService;
import org.daylight.museumapp.services.TablesService;

public class UsersPage {
    private StackPane content;

    public UsersPage() {
        initializePage();
    }

    private void initializePage() {
        content = new StackPane();

        boolean isAdmin = false;
        if(AuthService.getInstance().isAuthenticated()) isAdmin = AuthService.getInstance().getCurrentUser().getRole() == UserRole.ADMIN;
        GenericListDetailView<User> view = new GenericListDetailView<>(User.class,
                TablesService::getUsers,     // LIST
                TablesService::createUser,   // CREATE
                TablesService::updateUser,   // UPDATE
                TablesService::deleteUser,   // DELETE
                isAdmin, "Пользователи");

        content.getChildren().addAll(view);
    }

    public StackPane getContent() {
        return content;
    }
}