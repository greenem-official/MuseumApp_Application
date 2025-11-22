package org.daylight.museumapp.components.notifications;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.daylight.museumapp.model.Notification;

public class NotificationToast {
    private static final double MAX_TOAST_WIDTH = 500;
    private static final double MIN_TOAST_WIDTH = 100;
    private static final Duration ANIMATION_DURATION = Duration.millis(300);
    private static final int PADDING = 16;
    private boolean isHiding = false;

    private HBox toast;
    private Label messageLabel;
    private Notification notification;
    private double targetY;

    public NotificationToast(Notification notification) {
        this.notification = notification;
        initializeToast();
    }

    private void initializeToast() {
        toast = new HBox(14);
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

        // УБИРАЕМ все ограничения ширины - только по содержимому
        toast.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        toast.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE); // Важно!

        // Применяем CSS классы
        applyStyleClasses();

        // Иконка
        Label icon = new Label(getIconForType(notification.getType()));
        icon.getStyleClass().add("notification-icon");

        // Текст сообщения - тоже без ограничений ширины
        messageLabel = new Label(notification.getMessage());
        messageLabel.getStyleClass().add("notification-text");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE); // Разрешаем перенос текста
        messageLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);

        HBox.setHgrow(messageLabel, Priority.ALWAYS);
        toast.getChildren().addAll(icon, messageLabel);

        // Начальная позиция
        toast.setTranslateY(200);
        toast.setOpacity(0);
        toast.setMinWidth(MIN_TOAST_WIDTH);
    }

    private void applyStyleClasses() {
        toast.getStyleClass().add("notification-toast");

        switch (notification.getType()) {
            case SUCCESS -> toast.getStyleClass().add("notification-success");
            case ERROR -> toast.getStyleClass().add("notification-error");
            case WARNING -> toast.getStyleClass().add("notification-warning");
            case INFO -> toast.getStyleClass().add("notification-info");
        }
    }

    private String getIconForType(Notification.Type type) {
        return switch (type) {
            case SUCCESS -> "✓";
            case ERROR -> "✕";
            case WARNING -> "⚠";
            case INFO -> "ⓘ";
        };
    }

    public void playShowAnimation() {
        toast.setOpacity(0);
        toast.setTranslateY(30);

        FadeTransition fade = new FadeTransition(Duration.millis(200), toast);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(200), toast);
        slide.setFromY(30);
        slide.setToY(0);

        new ParallelTransition(fade, slide).play();
    }

    public void playHideAnimation(Runnable onFinish) {
        FadeTransition fade = new FadeTransition(Duration.millis(200), toast);
        fade.setToValue(0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(200), toast);
        slide.setToY(30);

        ParallelTransition pt = new ParallelTransition(fade, slide);
        pt.setOnFinished(e -> onFinish.run());
        pt.play();
    }

    public void setTargetY(double y) {
        this.targetY = y;
    }

    public HBox getToast() {
        return toast;
    }

    public String getId() {
        return notification.getId();
    }

    public Notification getNotification() {
        return notification;
    }

    // Метод для получения вычисленной высоты
    public double getComputedHeight() {
        return toast.getHeight();
    }
}