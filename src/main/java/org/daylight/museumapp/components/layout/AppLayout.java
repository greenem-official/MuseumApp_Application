package org.daylight.museumapp.components.layout;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.daylight.museumapp.components.pages.HomePage;
import org.daylight.museumapp.services.NavigationService;

public class AppLayout {
    private BorderPane root;
    private Sidebar sidebar;
    private StackPane contentArea;
    private NavigationService navigationService;

    public AppLayout() {
        navigationService = NavigationService.getInstance();
        initializeLayout();
    }

    private void initializeLayout() {
        root = new BorderPane();

        // Создаем область контента
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        root.setCenter(contentArea);

        // Устанавливаем contentArea в сервис навигации
        navigationService.setContentArea(contentArea);

        // Создаем сайдбар
        sidebar = new Sidebar();
        root.setLeft(sidebar.getContent());

        // Устанавливаем меню сайдбара в сервис навигации
        navigationService.setSidebarMenu(sidebar.getNavigationMenu());
        navigationService.setBottomSidebarMenu(sidebar.getBottomNavigationMenu());

        // Показываем главную страницу по умолчанию
        showHomePage();
    }

    public BorderPane getRoot() {
        return root;
    }

    public void showHomePage() {
        HomePage homePage = new HomePage();
        contentArea.getChildren().setAll(homePage.getContent());
    }
}