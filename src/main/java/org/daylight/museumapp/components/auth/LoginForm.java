package org.daylight.museumapp.components.auth;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.daylight.museumapp.components.loading.LoadingDots;

public class LoginForm implements IAuthForm {
    private VBox form;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private LoadingDots loadingDots;
    private Label errorLabel;
    private AuthFormListener listener;

    public interface AuthFormListener {
        void onLogin(String username, String password);
        void onSwitchToRegister();

        void onLoadingStateChanged(boolean isLoading);
    }

    public LoginForm(AuthFormListener listener) {
        this.listener = listener;
        initializeForm();
    }

    private void initializeForm() {
        form = new VBox(16);
        form.setPadding(new Insets(20, 30, 20, 30));
        form.setAlignment(Pos.CENTER);

        // Поля ввода
        usernameField = new TextField();
        usernameField.getStyleClass().add("auth-field");
        usernameField.setPromptText("Логин");
        usernameField.setMaxWidth(Double.MAX_VALUE);

        passwordField = new PasswordField();
        passwordField.getStyleClass().add("auth-field");
        passwordField.setPromptText("Пароль");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        // Ошибка
        errorLabel = new Label();
        errorLabel.getStyleClass().add("auth-error");
        errorLabel.setVisible(false);
        errorLabel.setMaxWidth(Double.MAX_VALUE);

        loadingDots = new LoadingDots();
        loadingDots.getDots().setVisible(false);

        // Кнопка входа
        loginButton = new Button("Войти");
        loginButton.getStyleClass().add("auth-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> attemptLogin());

        // Ссылка на регистрацию
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.CENTER);

        Label registerLabel = new Label("Нет аккаунта? ");
        registerLabel.setStyle("-fx-text-fill: #8a7a6d;");

        Hyperlink registerLink = new Hyperlink("Зарегистрироваться");
        registerLink.getStyleClass().add("auth-link");
        registerLink.setOnAction(e -> {
            if (listener != null) {
                listener.onSwitchToRegister();
            }
        });

        bottomBox.getChildren().addAll(registerLabel, registerLink);

        form.getChildren().addAll(usernameField, passwordField, errorLabel, loadingDots.getDots(), loginButton, bottomBox);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Заполните все поля");
            return;
        }

        if (listener != null) {
            listener.onLogin(username, password);
        }
    }

    @Override
    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void clearForm() {
        usernameField.clear();
        passwordField.clear();
        errorLabel.setVisible(false);
    }

    @Override
    public void setLoading(boolean isLoading) {
        Platform.runLater(() -> {
            loginButton.setDisable(isLoading);
            loginButton.setText(isLoading ? "Вход..." : "Войти");
            loadingDots.getDots().setVisible(isLoading);
            usernameField.setDisable(isLoading);
            passwordField.setDisable(isLoading);
        });
    }

    public VBox getForm() {
        return form;
    }
}
