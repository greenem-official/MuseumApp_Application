package org.daylight.museumapp.model;

public class Notification {
    public enum Type {
        SUCCESS, ERROR, WARNING, INFO
    }

    private final String id;
    private final Type type;
    private final String message;
    private final long timestamp;

    public Notification(Type type, String message) {
        this.id = java.util.UUID.randomUUID().toString();
        this.type = type;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // Геттеры
    public String getId() { return id; }
    public Type getType() { return type; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
