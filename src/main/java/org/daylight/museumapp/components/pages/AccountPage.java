package org.daylight.museumapp.components.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.daylight.museumapp.components.common.GlobalHooks;
import org.daylight.museumapp.dto.UserData;
import org.daylight.museumapp.dto.UserRole;
import org.daylight.museumapp.services.AuthService;
import org.daylight.museumapp.services.NavigationService;
import org.daylight.museumapp.services.NotificationService;

public class AccountPage {
    private VBox content;
    private AuthService authService;

    public AccountPage() {
        this.authService = AuthService.getInstance();
        initializePage();
    }

    private void initializePage() {
        content = new VBox(24);
        content.setPadding(new Insets(32));
        content.setAlignment(Pos.TOP_LEFT);
        content.getStyleClass().add("account-container");

        // Проверка авторизации
        if (!authService.isAuthenticated()) {
            showNotAuthorized();
            return;
        }

        UserData user = authService.getCurrentUser();

        // Заголовок
        Label title = new Label("Аккаунт");
        title.getStyleClass().add("page-title");

        // Основная карточка с информацией
        VBox accountCard = createAccountCard(user);

        // Карточка действий
        VBox actionsCard = createActionsCard();

        content.getChildren().addAll(title, accountCard, actionsCard);
    }

    private void showNotAuthorized() {
        VBox errorContainer = new VBox(16);
        errorContainer.setAlignment(Pos.CENTER);
        errorContainer.setPadding(new Insets(60, 0, 0, 0));

        Label errorIcon = new Label("🔒");
        errorIcon.setStyle("-fx-font-size: 48px;");

        Label errorTitle = new Label("Требуется авторизация");
        errorTitle.getStyleClass().add("error-title");

        Label errorMessage = new Label("Для просмотра этой страницы необходимо войти в систему");
        errorMessage.getStyleClass().add("error-message");
        errorMessage.setWrapText(true);
        errorMessage.setMaxWidth(400);
        errorMessage.setAlignment(Pos.CENTER);

        errorContainer.getChildren().addAll(errorIcon, errorTitle, errorMessage);
        content.getChildren().add(errorContainer);
    }

    private VBox createAccountCard(UserData user) {
        VBox card = new VBox(20);
        card.getStyleClass().add("account-card");
        card.setPadding(new Insets(24));
        card.setMaxWidth(500);

        // Заголовок карточки
        Label cardTitle = new Label("Информация о пользователе");
        cardTitle.getStyleClass().add("card-title");

        // Информация о пользователе
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(16);
        infoGrid.setVgap(12);
        infoGrid.getStyleClass().add("info-grid");

        // Поле: Логин
        Label usernameLabel = new Label("Логин:");
        usernameLabel.getStyleClass().add("info-label");

        Label usernameValue = new Label(user.getUsername());
        usernameValue.getStyleClass().add("info-value");

        // Поле: Полное имя
        Label fullNameLabel = new Label("Полное имя:");
        fullNameLabel.getStyleClass().add("info-label");

        Label fullNameValue = new Label(user.getFullName() != null ? user.getFullName() : "Не указано");
        fullNameValue.getStyleClass().add("info-value");

        // Поле: Роль
        Label roleLabel = new Label("Роль:");
        roleLabel.getStyleClass().add("info-label");

        Label roleValue = new Label(getRoleDisplayName(user.getRole()));
        roleValue.getStyleClass().add("info-value");
        roleValue.setStyle("-fx-text-fill: " + getRoleColor(user.getRole()) + ";");

        // Размещаем в сетке
        infoGrid.add(usernameLabel, 0, 0);
        infoGrid.add(usernameValue, 1, 0);
        infoGrid.add(fullNameLabel, 0, 1);
        infoGrid.add(fullNameValue, 1, 1);
        infoGrid.add(roleLabel, 0, 2);
        infoGrid.add(roleValue, 1, 2);

        // Настройка колонок
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setPrefWidth(120);
        ColumnConstraints valueCol = new ColumnConstraints();
        valueCol.setHgrow(Priority.ALWAYS);
        infoGrid.getColumnConstraints().addAll(labelCol, valueCol);

        card.getChildren().addAll(cardTitle, infoGrid);
        return card;
    }

    private VBox createActionsCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("actions-card");
        card.setPadding(new Insets(24));
        card.setMaxWidth(500);

        Label cardTitle = new Label("Действия");
        cardTitle.getStyleClass().add("card-title");

        Separator separator = new Separator();
        separator.getStyleClass().add("card-separator");

        // Кнопка выхода
        Button logoutButton = new Button("Выйти из системы");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setOnAction(e -> handleLogout());

        // Кнопка обновления токена (если нужно)
//        Button refreshTokenButton = new Button("Обновить токен");
//        refreshTokenButton.getStyleClass().add("secondary-button");
//        refreshTokenButton.setMaxWidth(Double.MAX_VALUE);
//        refreshTokenButton.setOnAction(e -> handleRefreshToken());

        card.getChildren().addAll(cardTitle, separator, logoutButton);
        return card;
    }

    private void handleLogout() {
        authService.logout();
        NotificationService.getInstance().success("Вы успешно вышли из системы");
        GlobalHooks.getInstance().sidebarAccountButtonChangeHook.run();
        NavigationService.getInstance().navigateTo("/");
    }

    private void handleRefreshToken() {
        // TODO: Реализовать обновление токена
        NotificationService.getInstance().info("Функция обновления токена в разработке");
    }

    private String getRoleDisplayName(UserRole role) {
        if (role == null) return "Не определена";

        return switch (role) {
            case ADMIN -> "Администратор";
            case EMPLOYEE -> "Сотрудник";
            case VISITOR -> "Посетитель";
            default -> "Неизвестная роль";
        };
    }

    private String getRoleColor(UserRole role) {
        if (role == null) return "#6b7280";

        return switch (role) {
            case ADMIN -> "#ef4444";    // Красный для админа
            case EMPLOYEE -> "#3b82f6"; // Синий для сотрудника
            case VISITOR -> "#10b981";   // Зеленый для просмотра
            default -> "#6b7280";       // Серый по умолчанию
        };
    }

    public VBox getContent() {
        return content;
    }
}