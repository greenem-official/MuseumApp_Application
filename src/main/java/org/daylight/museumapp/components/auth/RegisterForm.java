package org.daylight.museumapp.components.auth;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RegisterForm implements IAuthForm {
    private VBox form;
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField fullNameField;
    private Button registerButton;
    private ProgressIndicator progressIndicator;
    private Label errorLabel;
    private AuthFormListener listener;

    public interface AuthFormListener {
        void onRegister(String username, String password, String fullName);
        void onSwitchToLogin();

        void onLoadingStateChanged(boolean isLoading);
    }

    public RegisterForm(AuthFormListener listener) {
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

        fullNameField = new TextField();
        fullNameField.getStyleClass().add("auth-field");
        fullNameField.setPromptText("Полное Имя");
        fullNameField.setMaxWidth(Double.MAX_VALUE);

        passwordField = new PasswordField();
        passwordField.getStyleClass().add("auth-field");
        passwordField.setPromptText("Пароль");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.getStyleClass().add("auth-field");
        confirmPasswordField.setPromptText("Подтвердите пароль");
        confirmPasswordField.setMaxWidth(Double.MAX_VALUE);

        // Ошибка
        errorLabel = new Label();
        errorLabel.getStyleClass().add("auth-error");
        errorLabel.setVisible(false);
        errorLabel.setMaxWidth(Double.MAX_VALUE);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setMaxSize(30, 30);

        // Кнопка регистрации
        registerButton = new Button("Зарегистрироваться");
        registerButton.getStyleClass().add("auth-button");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setOnAction(e -> attemptRegister());

        // Ссылка на вход
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.CENTER);

        Label loginLabel = new Label("Уже есть аккаунт? ");
        loginLabel.setStyle("-fx-text-fill: #8a7a6d;");

        Hyperlink loginLink = new Hyperlink("Войти");
        loginLink.getStyleClass().add("auth-link");
        loginLink.setOnAction(e -> {
            if (listener != null) {
                listener.onSwitchToLogin();
            }
        });

        bottomBox.getChildren().addAll(loginLabel, loginLink);

        form.getChildren().addAll(usernameField, fullNameField, passwordField,
                confirmPasswordField, errorLabel, progressIndicator, registerButton, bottomBox);
    }

    private void attemptRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String fullName = fullNameField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Заполните все поля");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Пароли не совпадают");
            return;
        }

        if (listener != null) {
            listener.onRegister(username, password, fullName);
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
        confirmPasswordField.clear();
        errorLabel.setVisible(false);
    }

    @Override
    public void setLoading(boolean isLoading) {
        Platform.runLater(() -> {
            registerButton.setDisable(isLoading);
            registerButton.setText(isLoading ? "Вход..." : "Войти");
            progressIndicator.setVisible(isLoading);
            usernameField.setDisable(isLoading);
            fullNameField.setDisable(isLoading);
            passwordField.setDisable(isLoading);
            confirmPasswordField.setDisable(isLoading);
        });
    }

    public VBox getForm() {
        return form;
    }
}
