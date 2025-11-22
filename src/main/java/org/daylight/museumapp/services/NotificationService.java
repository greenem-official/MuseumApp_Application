package org.daylight.museumapp.services;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.daylight.museumapp.model.Notification;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationService {
    private static NotificationService instance;
    private final ObservableMap<String, Notification> activeNotifications;
    private final ScheduledExecutorService scheduler;
    private static final int MAX_ACTIVE_NOTIFICATIONS = 3;
    private static final long NOTIFICATION_DURATION = 2500; // 5 секунд

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    private NotificationService() {
        this.activeNotifications = FXCollections.observableMap(new ConcurrentHashMap<>());
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Notification-Service");
            t.setDaemon(true);
            return t;
        });
    }

    public void showNotification(Notification.Type type, String message) {
        Notification notification = new Notification(type, message);

        Platform.runLater(() -> {
            // Если достигли лимита, убираем самое старое СРАЗУ
            if (activeNotifications.size() >= MAX_ACTIVE_NOTIFICATIONS) {
                removeOldestNotificationImmediately();
            }

            // Добавляем новое уведомление
            activeNotifications.put(notification.getId(), notification);

            // Автоматическое скрытие через 5 секунд
            scheduler.schedule(() -> {
                Platform.runLater(() -> removeNotification(notification.getId()));
            }, NOTIFICATION_DURATION, TimeUnit.MILLISECONDS);
        });
    }

    private void removeOldestNotificationImmediately() {
        if (activeNotifications.isEmpty()) return;

        String oldestId = activeNotifications.entrySet().stream()
                .min((e1, e2) -> Long.compare(e1.getValue().getTimestamp(), e2.getValue().getTimestamp()))
                .get()
                .getKey();

        // Удаляем немедленно, без ожидания анимации
        activeNotifications.remove(oldestId);
    }

    public void removeNotification(String notificationId) {
        Platform.runLater(() -> {
            // Проверяем, что уведомление еще существует
            if (activeNotifications.containsKey(notificationId)) {
                activeNotifications.remove(notificationId);
            }
        });
    }

    public ObservableMap<String, Notification> getActiveNotifications() {
        return activeNotifications;
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    // Вспомогательные методы для быстрого создания уведомлений
    public void success(String message) {
        showNotification(Notification.Type.SUCCESS, message);
    }

    public void error(String message) {
        showNotification(Notification.Type.ERROR, message);
    }

    public void warning(String message) {
        showNotification(Notification.Type.WARNING, message);
    }

    public void info(String message) {
        showNotification(Notification.Type.INFO, message);
    }
}
