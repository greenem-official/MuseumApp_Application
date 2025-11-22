package org.daylight.museumapp.components.loading;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class LoadingScreen {
    private StackPane overlay;
    private VBox content;

    public LoadingScreen() {
        initializeOverlay();
    }

    private void initializeOverlay() {
        overlay = new StackPane();
        overlay.setStyle("-fx-background-color: #fcfaf7;"); // Тот же фон что и у приложения

        content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(400);

        // Анимированные точки
        LoadingDots loadingDots = new LoadingDots();

        // Текст
        Label loadingText = new Label("Авторизация");
        loadingText.setStyle("-fx-font-size: 18px; -fx-text-fill: #6b7280; -fx-font-weight: 500;");

        content.getChildren().addAll(loadingDots.getDots(), loadingText);
        overlay.getChildren().add(content);
    }

    public void show() {
        overlay.setVisible(true);
        overlay.setOpacity(1.0);
    }

    public void hide(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), overlay);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            overlay.setVisible(false);
            if (onFinished != null) {
                onFinished.run();
            }
        });
        fadeOut.play();
    }

    public StackPane getOverlay() {
        return overlay;
    }
}
