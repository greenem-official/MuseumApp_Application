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
    private static final double TOAST_MARGIN = 10;

    private VBox container;
    private Map<String, NotificationToast> activeToasts;
    private NotificationService notificationService;

    public NotificationContainer() {
        this.activeToasts = new ConcurrentHashMap<>(); // Потокобезопасная мапа
        this.notificationService = NotificationService.getInstance();
        initializeContainer();
        setupListeners();
    }

    private void initializeContainer() {
        container = new VBox(TOAST_MARGIN);
        container.setAlignment(Pos.BOTTOM_CENTER);
        container.setPadding(new Insets(0, 0, 30, 0));
        container.setMouseTransparent(true);
    }

    private void setupListeners() {
        notificationService.getActiveNotifications().addListener(
                (MapChangeListener<String, Notification>) change -> {
                    if (change.wasAdded()) {
                        addNotification(change.getValueAdded());
                    }
                    if (change.wasRemoved()) {
                        removeNotification(change.getValueRemoved().getId());
                    }
                }
        );
    }

    private void addNotification(Notification notification) {
        Platform.runLater(() -> {
            NotificationToast toast = new NotificationToast(notification);
            activeToasts.put(notification.getId(), toast);

            // Добавляем в контейнер (новые сверху)
            container.getChildren().add(0, toast.getToast());

            // Ждем отрисовки чтобы получить реальные размеры
            Platform.runLater(() -> {
                updateToastPositions();
                toast.show();
            });
        });
    }

    private void removeNotification(String notificationId) {
        Platform.runLater(() -> {
            NotificationToast toast = activeToasts.get(notificationId);
            if (toast != null) {
                toast.hide(() -> {
                    // Удаляем из контейнера и мапы
                    container.getChildren().remove(toast.getToast());
                    activeToasts.remove(notificationId);

                    // Обновляем позиции после полного удаления
                    updateToastPositions();
                });
            }
        });
    }

    private void updateToastPositions() {
        double currentY = 0;

        // Проходим по уведомлениям СНИЗУ ВВЕРХ (от старых к новым)
        for (int i = container.getChildren().size() - 1; i >= 0; i--) {
            int finalI = i;
            NotificationToast toast = activeToasts.values().stream()
                    .filter(t -> t.getToast() == container.getChildren().get(finalI))
                    .findFirst()
                    .orElse(null);

            if (toast != null) {
                double targetY = -currentY;
                toast.setTargetY(targetY);

                // Анимируем перемещение только если позиция изменилась
                if (Math.abs(toast.getToast().getTranslateY() - targetY) > 1) {
                    TranslateTransition move = new TranslateTransition(Duration.millis(150), toast.getToast());
                    move.setToY(targetY);
                    move.setInterpolator(Interpolator.EASE_OUT);
                    move.play();
                }

                // Увеличиваем текущую позицию на высоту этого тоста + отступ
                currentY += toast.getToast().getBoundsInLocal().getHeight() + TOAST_MARGIN;
            }
        }
    }

    public VBox getContainer() {
        return container;
    }
}
