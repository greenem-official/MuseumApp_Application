package org.daylight.museumapp.components.auth;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.daylight.museumapp.components.common.GlobalHooks;
import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.UserData;
import org.daylight.museumapp.services.AuthService;

public class AuthOverlay {
    private Duration animationDuration = Duration.millis(200);
    private double scaleFrom = 0.85;
    private Interpolator interpolatorShow = Interpolator.EASE_OUT;
    private Interpolator interpolatorHide = Interpolator.EASE_IN;

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
        tabPane = new AuthTabPane(this::switchForm);

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

    private void processAuthResult(ApiResult<UserData> result, IAuthForm form) {
        Platform.runLater(() -> {
            form.setLoading(false);

            if (!result.isSuccess()) {
                form.showError(result.getError());
                return;
            }

            AuthService.getInstance().setCurrentUser(result.getData());
            GlobalHooks.getInstance().getSidebarAccountButtonChangeHook().run();
            listener.onAuthSuccess();
            hide();
        });
    }

    private void initializeForms() {
        loginForm = new LoginForm(new LoginForm.AuthFormListener() {
            @Override
            public void onLogin(String username, String password) {
                loginForm.setLoading(true);

                AuthService.getInstance().loginAsync(username, password)
                        .thenAccept(result -> processAuthResult(result, loginForm));
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

                AuthService.getInstance().registerAndLogin(username, password, fullName)
                        .thenAccept(result -> processAuthResult(result, registerForm));
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
        overlay.requestFocus();

        ScaleTransition scaleIn = new ScaleTransition(animationDuration, authForm);
        scaleIn.setFromX(scaleFrom);
        scaleIn.setFromY(scaleFrom);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.setInterpolator(interpolatorShow);

        FadeTransition fadeIn = new FadeTransition(animationDuration, overlay);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        new ParallelTransition(scaleIn, fadeIn).play();
    }

    public void hide() {
        ScaleTransition scaleOut = new ScaleTransition(animationDuration, authForm);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(scaleFrom);
        scaleOut.setToY(scaleFrom);
        scaleOut.setInterpolator(interpolatorHide);

        FadeTransition fadeOut = new FadeTransition(animationDuration, overlay);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        ParallelTransition parallelOut = new ParallelTransition(scaleOut, fadeOut);
        parallelOut.setOnFinished(e -> finishHide());
        parallelOut.play();
    }

    private void finishHide() {
        overlay.setVisible(false);
        authForm.setScaleX(1.0);
        authForm.setScaleY(1.0);
        if (listener != null) listener.onAuthClose();
    }

    public StackPane getOverlay() {
        return overlay;
    }
}
