package org.daylight.museumapp.services;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.daylight.museumapp.components.auth.AuthOverlay;
import org.daylight.museumapp.components.pages.*;

public class NavigationService {
    private static NavigationService instance;
    private StackPane contentArea;
    private VBox sidebarMenu;
    private VBox bottomSidebarMenu;

    private NavigationService() {}

    public static NavigationService getInstance() {
        if (instance == null) {
            instance = new NavigationService();
        }
        return instance;
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
    }

    public void setSidebarMenu(VBox sidebarMenu) {
        this.sidebarMenu = sidebarMenu;
    }

    public void setBottomSidebarMenu(VBox bottomSidebarMenu) {
        this.bottomSidebarMenu = bottomSidebarMenu;
    }

    public void navigateTo(String path) {
        if(requiresAuthentication(path) && !AuthService.getInstance().isAuthenticated()) {
            AuthOverlay.getInstance().show();
            return;
        }

        System.out.println("navigateTo: " + path);
        if (contentArea == null) {
            System.out.println("Content area is null!");
            return;
        }

        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), contentArea);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);

        fadeOut.setOnFinished(e -> {
            System.out.println("Fade out finished");
            updateActiveNavButton(path);
            loadContentForPath(path);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), contentArea);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private boolean requiresAuthentication(String path) {
        return switch (path) {
            case "/exhibits", "/authors", "/collections", "/halls", "/account" -> true;
            default -> false;
        };
    }

    private void loadContentForPath(String path) {
        System.out.println("Loading content for: " + path);
        Node pageContent = switch (path) {
            case "/" -> new HomePage().getContent();
            case "/exhibits" -> new ExhibitsPage().getContent();
            case "/collections" -> new CollectionsPage().getContent();
            case "/halls" -> new HallsPage().getContent();
            case "/authors" -> new AuthorsPage().getContent();
            case "/stats" -> new StatisticsPage().getContent();
            case "/account" -> new AccountPage().getContent();
            default -> new HomePage().getContent();
        };

        contentArea.getChildren().setAll(pageContent);
    }

    private void updateActiveNavButton(String path) {
        if (sidebarMenu == null) {
            System.out.println("Sidebar menu is null!");
            return;
        }

        System.out.println("Updating active nav button for: " + path);

        // Сбрасываем все кнопки
        for (Node node : sidebarMenu.getChildren()) {
            if (node instanceof Button) {
                node.getStyleClass().remove("nav-button-active");
            }
        }

        for (Node node : bottomSidebarMenu.getChildren()) {
            if (node instanceof Button) {
                node.getStyleClass().remove("nav-button-active");
            }
        }

        // Активируем текущую кнопку
        String targetPath = normalizePath(path);

        for (Node node : sidebarMenu.getChildren()) {
            if (node instanceof Button btn) {
                if(updateSpecificNavButton(btn, targetPath)) break;
            }
        }

        for (Node node : bottomSidebarMenu.getChildren()) {
            if (node instanceof Button btn) {
                if(updateSpecificNavButton(btn, targetPath)) break;
            }
        }
    }

    private boolean updateSpecificNavButton(Button btn, String targetPath) {
        // Получаем путь из пользовательских данных кнопки или из графики
        Object userData = btn.getUserData();
        System.out.println(userData + " = " + targetPath);
        if (userData instanceof String buttonPath && buttonPath.equals(targetPath)) {
            btn.getStyleClass().add("nav-button-active");
            System.out.println("Activated button for path: " + targetPath);
            return true;
        }
        return false;
    }

    private String getTitleByPath(String path) {
        return switch (path) {
            case "/" -> "Главная";
            case "/exhibits" -> "Экспонаты";
            case "/collections" -> "Коллекции";
            case "/halls" -> "Залы";
            case "/authors" -> "Авторы";
            default -> "Главная";
        };
    }

    private String normalizePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}