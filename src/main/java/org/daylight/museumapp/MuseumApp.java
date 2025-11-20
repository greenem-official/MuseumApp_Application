package org.daylight.museumapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.daylight.museumapp.components.auth.AuthOverlay;
import org.daylight.museumapp.components.layout.AppLayout;
import org.daylight.museumapp.services.AuthService;

import java.util.logging.Logger;

public class MuseumApp extends Application {
    public static final Logger LOGGER = Logger.getLogger(MuseumApp.class.getName());

    @Override
    public void start(Stage primaryStage) {
        AppLayout appLayout = new AppLayout();

        // AuthOverlay с обработчиками событий
        AuthOverlay authOverlay = new AuthOverlay(new AuthOverlay.AuthFormListener() {
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

        // корневой контейнер
        StackPane rootContainer = new StackPane();
        rootContainer.getChildren().addAll(appLayout.getRoot(), authOverlay.getOverlay());

        Scene scene = new Scene(rootContainer, 1400, 900);

        // Загрузка CSS
        try {
            String cssPath = getClass().getResource("/styles/museum-light.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("CSS not found, using default styles");
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("Информационная система музея");
        primaryStage.show();

        if (!AuthService.getInstance().isAuthenticated()) {
            authOverlay.show();
        }
    }

    private void updateUIAfterAuth() {
        System.out.println("Updating UI after auth...");
//        appLayout.updateAuthState();
    }
}
