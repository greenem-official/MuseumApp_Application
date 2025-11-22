package org.daylight.museumapp.components.notifications;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.daylight.museumapp.model.Notification;
import org.daylight.museumapp.services.NotificationService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationContainer {
    private static final double TOAST_SPACING = 10;
    private static final Duration MOVE_ANIMATION = Duration.millis(200);

    private final VBox container;
    private final Map<String, NotificationToast> activeToasts;
    private final NotificationService notificationService;

    public NotificationContainer() {
        this.activeToasts = new ConcurrentHashMap<>();
        this.notificationService = NotificationService.getInstance();

        container = new VBox(TOAST_SPACING);
        container.setAlignment(Pos.BOTTOM_CENTER);
        container.setPadding(new Insets(0, 0, 30, 0));
        container.setMouseTransparent(true);

        setupListeners();
    }

    private void setupListeners() {
        notificationService.getActiveNotifications().addListener(
                (MapChangeListener<String, Notification>) change -> {
                    if (change.wasAdded()) {
                        Platform.runLater(() -> addNotification(change.getValueAdded()));
                    }
                    if (change.wasRemoved()) {
                        Platform.runLater(() -> removeNotification(change.getValueRemoved().getId()));
                    }
                }
        );
    }

    private void addNotification(Notification notification) {
        NotificationToast toast = new NotificationToast(notification);
        activeToasts.put(notification.getId(), toast);

        // добавляем сверху
        container.getChildren().add(0, toast.getToast());

        animateReposition();
        toast.playShowAnimation();
    }

    private void removeNotification(String id) {
        NotificationToast toast = activeToasts.get(id);
        if (toast == null) return;

        // Когда один уходит — все остальные тоже начинают плавно сползать
        toast.playHideAnimation(() -> {
            container.getChildren().remove(toast.getToast());
            activeToasts.remove(id);

            smoothShiftAllDown();
        });
    }

    /**
     * Плавное "проседание" всех уведомлений при исчезновении одного
     */
    private void smoothShiftAllDown() {
        for (var node : container.getChildren()) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(250), node);
            transition.setFromY(node.getTranslateY() - 20);
            transition.setToY(0);
            transition.setInterpolator(Interpolator.EASE_OUT);
            transition.play();
        }
    }

    /**
     * Плавное перераспределение тостов без кривых прыжков
     */
    private void animateReposition() {
        for (int i = 0; i < container.getChildren().size(); i++) {
            var node = container.getChildren().get(i);

            TranslateTransition tt = new TranslateTransition(MOVE_ANIMATION, node);
            tt.setToY(0);
            tt.setInterpolator(Interpolator.EASE_BOTH);
            tt.play();
        }
    }

    public VBox getContainer() {
        return container;
    }
}
