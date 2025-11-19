package org.daylight.museumapp.components.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.daylight.museumapp.model.NavigationItem;
import org.daylight.museumapp.services.NavigationService;
import org.daylight.museumapp.util.Icons;

public class Sidebar {
    private VBox sidebar;
    private VBox navigationMenu;
    private NavigationService navigationService;

    public Sidebar() {
        navigationService = NavigationService.getInstance();
        initializeSidebar();
    }

    private void initializeSidebar() {
        sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(280);
        sidebar.setMinWidth(280);

        // Заголовок
        HBox titleBox = createTitleBox();

        // Меню навигации
        navigationMenu = createNavigationMenu();

        // Spacer и футер
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(titleBox, navigationMenu, spacer, createFooter());
    }

    private HBox createTitleBox() {
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 24, 24, 24));

        Label title = new Label("Музей Искусств");
        title.getStyleClass().add("sidebar-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setWrapText(false);

        titleBox.getChildren().add(title);
        HBox.setHgrow(title, Priority.ALWAYS);

        return titleBox;
    }

    private VBox createNavigationMenu() {
        VBox menu = new VBox(4);
        menu.getStyleClass().add("sidebar-menu");
        menu.setPadding(new Insets(0, 12, 20, 12));

        NavigationItem[] navItems = {
                new NavigationItem("Главная", "/", Icons.HOME),
                new NavigationItem("Экспонаты", "/exhibits", Icons.EXHIBITS),
                new NavigationItem("Коллекции", "/collections", Icons.COLLECTIONS),
                new NavigationItem("Залы", "/halls", Icons.HALLS),
                new NavigationItem("Авторы", "/authors", Icons.AUTHORS)
        };

        for (NavigationItem item : navItems) {
            Button navButton = createNavButton(item);
            menu.getChildren().add(navButton);

            // Устанавливаем активный класс для главной страницы по умолчанию
            if (item.path().equals("/")) {
                navButton.getStyleClass().add("nav-button-active");
            }
        }

        return menu;
    }

    private Button createNavButton(NavigationItem item) {
        Button button = new Button();

        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(item.icon());
        iconLabel.getStyleClass().add("icon-label");
        iconLabel.setMinWidth(24);
        iconLabel.setPrefWidth(24);

        Label textLabel = new Label(item.title());
        textLabel.setStyle("-fx-text-fill: inherit; -fx-wrap-text: true;");

        content.getChildren().addAll(iconLabel, textLabel);
        button.setGraphic(content);

        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);

        button.setOnAction(e -> {
            System.out.println("Button clicked: " + item.title());
            navigationService.navigateTo(item.path());
        });

        return button;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(16, 20, 20, 20));
        footer.setAlignment(Pos.CENTER);

        Label version = new Label("v1.0.0");
        version.setStyle("-fx-text-fill: #8a7a6d; -fx-font-size: 12px;");

        footer.getChildren().add(version);
        return footer;
    }

    public VBox getContent() {
        return sidebar;
    }

    public VBox getNavigationMenu() {
        return navigationMenu;
    }
}
