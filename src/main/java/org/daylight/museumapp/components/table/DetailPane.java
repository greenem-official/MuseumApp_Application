package org.daylight.museumapp.components.table;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.ScrollPane;

import java.lang.reflect.Field;

/**
 * Небольшой компонент для отображения сущности в detail area.
 */
public class DetailPane<T> {
    private final VBox root = new VBox(12);

    public DetailPane(Class<T> type, T item, Runnable onClose) {
        root.setPadding(new Insets(12));
        root.getStyleClass().add("museum-ld-muted");

        VBox header = new VBox();
        header.setAlignment(Pos.CENTER_RIGHT);
        Button close = new Button("✕");
        close.getStyleClass().add("museum-ld-close");
        close.setOnAction(e -> onClose.run());
        header.getChildren().add(close);

        Label title = new Label(type.getSimpleName() + " — Подробно");
        title.getStyleClass().add("museum-ld-title");

        VBox fieldsBox = new VBox(8);
        for (Field f : type.getDeclaredFields()) {
            String fname = prettyColumnName(f.getName());
            Object val = safeGetProperty(item, f);
            Label label = new Label(fname + ":");
            label.getStyleClass().add("museum-ld-field-label");
            Text valueText = new Text(val == null ? "-" : String.valueOf(val));
            valueText.getStyleClass().add("museum-ld-muted");
            VBox block = new VBox(2, label, valueText);
            block.setPadding(new Insets(4, 0, 4, 0));
            fieldsBox.getChildren().add(block);
        }

        ScrollPane sp = new ScrollPane(fieldsBox);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.getStyleClass().add("museum-ld-detail-scroll");

        root.getChildren().addAll(header, title, sp);
    }

    public VBox getRoot() {
        return root;
    }

    private Object safeGetProperty(Object row, Field field) {
        try {
            String name = field.getName();
            String getter = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            java.lang.reflect.Method m = null;
            try { m = row.getClass().getMethod(getter); } catch (NoSuchMethodException ignored) {}
            Object v = null;
            if (m != null) v = m.invoke(row);
            else {
                field.setAccessible(true);
                v = field.get(row);
            }
            return v;
        } catch (Exception e) {
            e.printStackTrace();
            return "(error)";
        }
    }

    private String prettyColumnName(String raw) {
        if (raw == null || raw.isEmpty()) return raw;
        StringBuilder sb = new StringBuilder();
        char[] ch = raw.toCharArray();
        sb.append(Character.toUpperCase(ch[0]));
        for (int i = 1; i < ch.length; i++) {
            if (Character.isUpperCase(ch[i])) sb.append(' ');
            sb.append(ch[i]);
        }
        return sb.toString();
    }
}
