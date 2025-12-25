package org.daylight.museumapp.components.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.daylight.museumapp.components.common.GlobalHooks;
import org.daylight.museumapp.dto.UserRole;
import org.daylight.museumapp.model.NavigationItem;
import org.daylight.museumapp.services.AuthService;
import org.daylight.museumapp.services.NavigationService;
import org.daylight.museumapp.util.Icons;

import java.util.function.Consumer;

public class Sidebar {
    private VBox sidebar;
    private VBox navigationMenu;
    private VBox bottomSidebarMenu;
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

        bottomSidebarMenu = createBottomNavigationMenu();

        sidebar.getChildren().addAll(titleBox, navigationMenu, spacer, bottomSidebarMenu);
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

        Button homePageButton = createNavButton(new NavigationItem("Главная", "/", Icons.HOME), null);
        Button exhibitsPageButton = createNavButton(new NavigationItem("Экспонаты", "/exhibits", Icons.EXHIBITS), null);
        Button collectionsPageButton = createNavButton(new NavigationItem("Коллекции", "/collections", Icons.COLLECTIONS), null);
        Button hallsPageButton = createNavButton(new NavigationItem("Залы", "/halls", Icons.HALLS), null);
        Button authorsPageButton = createNavButton(new NavigationItem("Авторы", "/authors", Icons.AUTHORS), null);
        Button statysticsPageButton = createNavButton(new NavigationItem("Статистика", "/stats", Icons.STATS), null);

        menu.getChildren().addAll(homePageButton, exhibitsPageButton, collectionsPageButton, hallsPageButton, authorsPageButton, statysticsPageButton);
        homePageButton.getStyleClass().add("nav-button-active");

        GlobalHooks.getInstance().sidebarOnAuthStateChange = () -> {
            boolean authenticated = AuthService.getInstance().isAuthenticated();
            exhibitsPageButton.setVisible(authenticated);
            collectionsPageButton.setVisible(authenticated);
            hallsPageButton.setVisible(authenticated);
            authorsPageButton.setVisible(authenticated);
            statysticsPageButton.setVisible(authenticated && AuthService.getInstance().getCurrentUser().getRole() == UserRole.ADMIN);
        };

        GlobalHooks.getInstance().sidebarOnAuthStateChange.run();

        return menu;
    }

    private VBox createBottomNavigationMenu() {
        VBox menu = new VBox(4);
        menu.getStyleClass().add("sidebar-menu");
        menu.setPadding(new Insets(0, 12, 20, 12));

        NavigationItem[] navItems = {
                new NavigationItem("Аккаунт", "/account", Icons.USERS)
        };

        for (NavigationItem item : navItems) {
            Button navButton = createNavButton(item, item.path().equals("/account") ? label -> GlobalHooks.getInstance().setSidebarAccountButtonChangeHook(() -> {
                label.setText(AuthService.getInstance().isAuthenticated() ? AuthService.getInstance().getCurrentUser().getUsername() : "Войти...");
            }) : null);
            menu.getChildren().add(navButton);
        }

        GlobalHooks.getInstance().sidebarAccountButtonChangeHook.run();

        return menu;
    }

    private Button createNavButton(NavigationItem item, Consumer<Label> forLabelChange) {
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

        // Сохраняем путь в userData для идентификации кнопки
        button.setUserData(item.path());

        button.setOnAction(e -> {
            System.out.println("Button clicked: " + item.title() + " -> " + item.path());
            navigationService.navigateTo(item.path());
        });

        if(forLabelChange != null) forLabelChange.accept(textLabel);

        return button;
    }

    public VBox getContent() {
        return sidebar;
    }

    public VBox getNavigationMenu() {
        return navigationMenu;
    }

    public VBox getBottomNavigationMenu() {
        return bottomSidebarMenu;
    }
}
