package org.daylight.museumapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.daylight.museumapp.components.auth.AuthOverlay;
import org.daylight.museumapp.components.common.GlobalHooks;
import org.daylight.museumapp.components.common.storage.StorageUtil;
import org.daylight.museumapp.components.layout.AppLayout;
import org.daylight.museumapp.components.loading.LoadingScreen;
import org.daylight.museumapp.components.notifications.NotificationContainer;
import org.daylight.museumapp.services.AuthService;
import org.daylight.museumapp.services.NotificationService;

import java.net.ConnectException;
import java.util.Objects;
import java.util.logging.Logger;

public class MuseumApp extends Application {
    public static final Logger LOGGER = Logger.getLogger(MuseumApp.class.getName());
    private AuthOverlay authOverlay;
    private NotificationContainer notificationContainer;
    private LoadingScreen loadingScreen;

    @Override
    public void start(Stage primaryStage) {
        AppLayout appLayout = new AppLayout();

        // AuthOverlay с обработчиками событий
        authOverlay = new AuthOverlay(new AuthOverlay.AuthFormListener() {
            @Override
            public void onAuthSuccess() {
                System.out.println("Authentication successful!");
                // Обновляем интерфейс после успешной авторизации
                updateUIAfterAuth();
            }

            @Override
            public void onAuthClose() {
                System.out.println("Auth overlay closed");
                // Можно добавить логику при закрытии оверлея
            }
        });

        notificationContainer = new NotificationContainer();
        loadingScreen = new LoadingScreen();
        StorageUtil.onStartup();

        // корневой контейнер
        StackPane rootContainer = new StackPane();
        rootContainer.getChildren().addAll(appLayout.getRoot(), authOverlay.getOverlay(), notificationContainer.getContainer(), loadingScreen.getOverlay());

        Scene scene = new Scene(rootContainer, 1400, 900);

        // Загрузка CSS
        try {
            String cssPath = getClass().getResource("/styles/museum-light.css").toExternalForm();
            String notificationsCss = getClass().getResource("/styles/notifications.css").toExternalForm();
            String accountCss = getClass().getResource("/styles/account.css").toExternalForm();
            String tablesCss = getClass().getResource("/styles/tables.css").toExternalForm();
            scene.getStylesheets().addAll(cssPath, notificationsCss, accountCss, tablesCss);
        } catch (Exception e) {
            System.err.println("CSS not found, using default styles");
        }

        try {
            String iconPath = getClass().getResource("/images/icon.png").toExternalForm();
            Image icon = new Image(iconPath);
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Не удалось загрузить иконку: " + e.getMessage());
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("Информационная система музея");
        primaryStage.show();

        checkTokenAndInitialize();

//        testNotifications();
    }

    private void testNotifications() {
        NotificationService service = NotificationService.getInstance();

        for (int i = 1; i <= 3; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    Thread.sleep(index * 200);
                    service.success("Тестовое уведомление " + index);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void checkTokenAndInitialize() {
        loadingScreen.show();

        // Проверяем есть ли токен для автоматической авторизации
        String token = AuthService.getInstance().getCurrentUser() != null
                ? AuthService.getInstance().getCurrentUser().getToken()
                : null;

        if (token != null) {
            // Проверяем токен
            AuthService.getInstance().checkTokenAsync(token)
                    .whenComplete((result, throwable) -> {
                        Platform.runLater(() -> {
                            loadingScreen.hide(() -> {
                                // If java exception
                                if(result.isThrowable()) {
                                    if(result.getThrowable() instanceof ConnectException) {
                                        NotificationService.getInstance().warning("Не удалось подключиться к серверу");
                                    } else {
                                        NotificationService.getInstance().warning("Исключение \"" + result.getThrowable().getClass().getSimpleName() + "\"");
                                        result.getThrowable().printStackTrace();
                                    }
                                }
                                // If most likely an api error responce
                                else if (throwable != null || !result.isSuccess()) {
                                    System.out.println(result.getError());
                                    NotificationService.getInstance().warning("Требуется повторный вход в аккаунт");
                                    if(throwable != null) throwable.printStackTrace();
                                    AuthService.getInstance().logout();
                                    updateUIAfterAuth();
                                    authOverlay.show();
                                // If valid token check
                                } else {
                                    updateUIAfterAuth();
                                }
                            });
                        });
                    });
        } else {
            // If no token
            Platform.runLater(() -> {
                loadingScreen.hide(() -> {
//                    authOverlay.show();
                });
            });
        }
    }

    private void updateUIAfterAuth() {
        System.out.println("Updating UI after auth...");
//        appLayout.updateAuthState();
        GlobalHooks.getInstance().sidebarAccountButtonChangeHook.run();
        GlobalHooks.getInstance().sidebarOnAuthStateChange.run();
    }

    @Override
    public void stop() {
        NotificationService.getInstance().shutdown();
    }
}
