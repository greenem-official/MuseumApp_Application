package org.daylight.museumapp.components.table;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.daylight.museumapp.services.TablesService;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GenericEditDialog<T> {
    private final Class<T> type;
    private final T source;
    private final Map<Field, Control> editors = new LinkedHashMap<>();

    public GenericEditDialog(Class<T> type, T source) {
        this.type = type;
        this.source = source;
    }

    public Optional<T> show() {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(source == null ? "Добавление" : "Редактирование");

        ButtonType ok = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(12));

        for (Field f : type.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            if (f.getName().equalsIgnoreCase("id")) continue;

            Node editor = createEditorForField(f);
            if (editor != null) {
                content.getChildren().add(new VBox(
                        new Label(prettyName(f.getName())),
                        editor
                ));
            }
        }

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(btn -> {
            if (btn == ok) {
                return buildResultObject();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Control createEditorForField(Field field) {
        field.setAccessible(true);
        Class<?> t = field.getType();

        Control control;

        if (t == String.class) {
            TextField tf = new TextField();
            if (source != null) tf.setText(String.valueOf(getValue(field)));
            control = tf;

        } else if (Number.class.isAssignableFrom(t) || t == int.class || t == long.class) {
            TextField tf = new TextField();
            if (source != null) tf.setText(String.valueOf(getValue(field)));
            control = tf;

        } else if (t == boolean.class || t == Boolean.class) {
            CheckBox cb = new CheckBox();
            if (source != null) cb.setSelected((Boolean) getValue(field));
            control = cb;

        } else if (t == LocalDate.class) {
            DatePicker dp = new DatePicker();
            if (source != null) dp.setValue((LocalDate) getValue(field));
            control = dp;

        } else {
            // связи (Author, Collection, Hall, User)
            ComboBox<Object> cb = new ComboBox<>();
            cb.getItems().add(getValue(field));
            control = cb;
        }

        editors.put(field, control);
        return control;
    }

    private T buildResultObject() {
        try {
            T obj = source != null ? source : type.getDeclaredConstructor().newInstance();

            for (var e : editors.entrySet()) {
                Field f = e.getKey();
                Control c = e.getValue();
                Object value = extractValue(c, f.getType());
                f.setAccessible(true);
                f.set(obj, value);
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object extractValue(Control c, Class<?> type) {
        if (c instanceof TextField tf) {
            String v = tf.getText();
            if (type == int.class || type == Integer.class) return Integer.parseInt(v);
            if (type == long.class || type == Long.class) return Long.parseLong(v);
            return v;
        }
        if (c instanceof CheckBox cb) return cb.isSelected();
        if (c instanceof DatePicker dp) return dp.getValue();
        if (c instanceof ComboBox<?> cb) return cb.getValue();
        return null;
    }

    private String prettyName(String raw) {
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

    private Object getValue(Field field) {
        if (source == null) return null;
        try {
            field.setAccessible(true);
            return field.get(source);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
