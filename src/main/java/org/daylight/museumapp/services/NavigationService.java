package org.daylight.museumapp.services;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.daylight.museumapp.components.pages.*;

public class NavigationService {
    private static NavigationService instance;
    private StackPane contentArea;
    private VBox sidebarMenu;

    private NavigationService() {}

    public static NavigationService getInstance() {
        if (instance == null) {
            instance = new NavigationService();
        }
        return instance;
    }

    public void setContentArea(StackPane contentArea) {
        this.contentArea = contentArea;
         System.out.println("Content area set in NavigationService");
    }

    public void setSidebarMenu(VBox sidebarMenu) {
        this.sidebarMenu = sidebarMenu;
        System.out.println("Sidebar menu set in NavigationService");
    }

    public void navigateTo(String path) {
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

    private void loadContentForPath(String path) {
        System.out.println("Loading content for: " + path);
        Node pageContent = switch (path) {
            case "/" -> new HomePage().getContent();
            case "/exhibits" -> new ExhibitsPage().getContent();
            case "/collections" -> new CollectionsPage().getContent();
            case "/halls" -> new HallsPage().getContent();
            case "/authors" -> new AuthorsPage().getContent();
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

        // Активируем текущую кнопку
        String targetTitle = getTitleByPath(path);
        for (Node node : sidebarMenu.getChildren()) {
            if (node instanceof Button btn) {
                if (btn.getText() != null && btn.getText().equals(targetTitle)) {
                    btn.getStyleClass().add("nav-button-active");
                    System.out.println("Activated button: " + targetTitle);
                    break;
                }
            }
        }
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
}