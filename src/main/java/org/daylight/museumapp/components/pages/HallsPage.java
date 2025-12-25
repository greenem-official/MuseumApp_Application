package org.daylight.museumapp.components.pages;

import javafx.scene.layout.StackPane;
import org.daylight.museumapp.components.table.GenericListDetailView;
import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.PagedResult;
import org.daylight.museumapp.dto.UserRole;
import org.daylight.museumapp.dto.filterrelated.PagedRequest;
import org.daylight.museumapp.dto.tables.Hall;
import org.daylight.museumapp.services.AuthService;
import org.daylight.museumapp.services.TablesService;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HallsPage {
    private StackPane content;

    public HallsPage() {
        initializePage();
    }

    private void initializePage() {
        content = new StackPane();

        boolean isAdmin = false;
        if(AuthService.getInstance().isAuthenticated()) isAdmin = AuthService.getInstance().getCurrentUser().getRole() == UserRole.ADMIN;
        GenericListDetailView<Hall> view = new GenericListDetailView<>(Hall.class,
                TablesService::getHalls,     // LIST
                TablesService::createHall,   // CREATE
                TablesService::updateHall,   // UPDATE
                TablesService::deleteHall,   // DELETE
                isAdmin, "Залы");

        content.getChildren().addAll(view);
    }

    public StackPane getContent() {
        return content;
    }
}