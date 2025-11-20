package org.daylight.museumapp.components.auth;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.*;
import org.daylight.museumapp.components.common.GlobalHooks;
import org.daylight.museumapp.services.ApiService;
import org.daylight.museumapp.services.AuthService;

import java.util.concurrent.CompletableFuture;

public class AuthOverlay {
    private StackPane overlay;
    private VBox authForm;
    private StackPane formContent;

    private AuthTabPane tabPane;
    private LoginForm loginForm;
    private RegisterForm registerForm;

    private AuthFormListener listener;

    public interface AuthFormListener {
        void onAuthSuccess();
        void onAuthClose();
    }

    public AuthOverlay(AuthFormListener listener) {
        this.listener = listener;
        initializeOverlay();
    }

    private void initializeOverlay() {
        // Затемненный фон
        overlay = new StackPane();
        overlay.getStyleClass().add("auth-overlay");
        overlay.setVisible(false);

        // Форма авторизации
        authForm = createAuthForm();
        overlay.getChildren().add(authForm);

//        VBox.setVgrow(authForm, Priority.NEVER);
//        authForm.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
//        authForm.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        // Закрытие по клику на фон
        overlay.setOnMousePressed(e -> {
            if (e.getTarget() == overlay) {
                hide();
            }
        });
    }

    private VBox createAuthForm() {
        VBox form = new VBox(0);
        form.getStyleClass().add("auth-form");

        // Убираем жесткие ограничения, используем предпочтительный размер
        form.setPrefSize(420, Region.USE_COMPUTED_SIZE); // Высота по содержимому
        form.setMaxSize(420, 600);  // Максимальная высота тоже по содержимому

        form.setAlignment(Pos.TOP_CENTER);

        // Кнопка закрытия
        HBox closeBox = new HBox();
        closeBox.setAlignment(Pos.CENTER_RIGHT);
        closeBox.setPadding(new Insets(10, 20, 10, 20));
        closeBox.setMaxWidth(Double.MAX_VALUE);

        Button closeButton = new Button("✕");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #8a7a6d; -fx-font-size: 16px; -fx-cursor: hand;");
        closeButton.setOnAction(e -> hide());

        closeBox.getChildren().add(closeButton);

        // Заголовок
        Label title = new Label("Музей Искусств");
        title.getStyleClass().add("auth-title");
        title.setPadding(new Insets(0, 30, 10, 30));

        Label subtitle = new Label("Информационная система");
        subtitle.getStyleClass().add("auth-subtitle");
        subtitle.setPadding(new Insets(0, 30, 20, 30));

        // Табы для переключения
        tabPane = new AuthTabPane(tabType -> switchForm(tabType));
        tabPane.getTabs().setMinHeight(40);
        tabPane.getTabs().setMaxHeight(40);

        // Контент форм
        formContent = new StackPane();
        formContent.setMinHeight(Region.USE_PREF_SIZE); // Минимальная высота = предпочтительной
        formContent.setPrefHeight(Region.USE_COMPUTED_SIZE); // Предпочтительная = вычисленной
        formContent.setMaxHeight(Region.USE_PREF_SIZE); // Максимальная = предпочтительной

        // Инициализируем формы
        initializeForms();

        form.getChildren().addAll(closeBox, title, subtitle, tabPane.getTabs(), formContent);
        VBox.setVgrow(form, Priority.NEVER);
        return form;
    }

    private void initializeForms() {
        loginForm = new LoginForm(new LoginForm.AuthFormListener() {
            @Override
            public void onLogin(String username, String password) {
                loginForm.setLoading(true); // TODO

                AuthService.getInstance().loginAsync(username, password)
                        .whenComplete((userData, throwable) -> {
                            Platform.runLater(() -> {
                                loginForm.setLoading(false);

                                if (throwable != null) {
                                    loginForm.showError("Ошибка сети: " + throwable.getMessage());
                                    return;
                                }

                                if (userData != null) {
                                    AuthService.getInstance().setCurrentUser(userData);
                                    GlobalHooks.getInstance().getSidebarAccountButtonChangeHook().run();
                                    if (listener != null) listener.onAuthSuccess();
                                    hide();
                                } else {
                                    loginForm.showError("Неверный логин или пароль");
                                }
                            });
                        });
            }

            @Override
            public void onSwitchToRegister() {
                tabPane.setActiveTab("register");
            }

            @Override
            public void onLoadingStateChanged(boolean isLoading) {

            }
        });

        registerForm = new RegisterForm(new RegisterForm.AuthFormListener() {
            @Override
            public void onRegister(String username, String password, String fullName) {
                registerForm.setLoading(true);

                AuthService.getInstance().registerAsync(username, password, fullName)
                        .whenComplete((success, throwable) -> {
                            Platform.runLater(() -> {
                                if (throwable != null) {
                                    registerForm.showError("Ошибка сети: " + throwable.getMessage());
                                    registerForm.setLoading(false);
                                    return;
                                }

                                if (success) {
                                    if (listener != null) listener.onAuthSuccess();
                                    AuthService.getInstance().loginAsync(username, password)
                                                    .whenComplete((userData, throwable2) -> Platform.runLater(() -> {
                                                        if (throwable2 != null) {
                                                            registerForm.showError("Ошибка сети: " + throwable2.getMessage());
                                                            registerForm.setLoading(false);
                                                            return;
                                                        }

                                                        AuthService.getInstance().setCurrentUser(userData);
                                                        GlobalHooks.getInstance().getSidebarAccountButtonChangeHook().run();
                                                        registerForm.setLoading(false);
                                                    }));
                                    hide();
                                } else {
                                    registerForm.showError("Ошибка регистрации");
                                    registerForm.setLoading(false);
                                }
                            });
                        });
            }

            @Override
            public void onSwitchToLogin() {
                tabPane.setActiveTab("login");
            }

            @Override
            public void onLoadingStateChanged(boolean isLoading) {

            }
        });

        formContent.getChildren().add(loginForm.getForm());
    }

    private void switchForm(String tabType) {
        formContent.getChildren().clear();

        if ("login".equals(tabType)) {
            formContent.getChildren().add(loginForm.getForm());
            loginForm.clearForm();
        } else {
            formContent.getChildren().add(registerForm.getForm());
            registerForm.clearForm();
        }
    }

    public void show() {
        overlay.setVisible(true);
        tabPane.setActiveTab("login");
    }

    public void hide() {
        overlay.setVisible(false);
        if (listener != null) {
            listener.onAuthClose();
        }
    }

    public StackPane getOverlay() {
        return overlay;
    }
}
