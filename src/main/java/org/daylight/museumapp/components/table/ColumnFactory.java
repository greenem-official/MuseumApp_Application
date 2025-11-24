package org.daylight.museumapp.components.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.daylight.museumapp.components.annotations.ColumnMeta;
import org.daylight.museumapp.dto.filterrelated.FilterRule;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Создаёт колонки для TableView<T> на основе полей класса.
 * Делегирует события сортировки и открытия детали.
 */
public class ColumnFactory<T> {
    private final Class<T> type;
    private final Consumer<T> onOpenDetail;
    private final BiConsumer<String, Boolean> onSortChanged;
    private final boolean adminMode;

    public ColumnFactory(Class<T> type, Consumer<T> onOpenDetail, BiConsumer<String, Boolean> onSortChanged, boolean adminMode) {
        this.type = type;
        this.onOpenDetail = onOpenDetail;
        this.onSortChanged = onSortChanged;
        this.adminMode = adminMode;
    }

    public void buildColumnsInto(TableView<T> table) {
        table.getColumns().clear();

        List<Field> fields = collectFields();

        for (Field field : fields) {
            String name = field.getName();
            TableColumn<T, String> col = new TableColumn<>();
            col.getStyleClass().add("museum-ld-col");
            col.setText(prettyColumnName(name));

            HBox headerBox = createColumnHeader(field, name, col);
            col.setGraphic(headerBox);

            if (name.equalsIgnoreCase("id")) {
                col.setMinWidth(70);
                col.setPrefWidth(90);
                col.setMaxWidth(140);
            }

            col.setCellValueFactory(cellData -> {
                T row = cellData.getValue();
                String safe = "";
                if (row != null) {
                    Object value = safeGetProperty(row, field);
                    safe = value == null ? "" : String.valueOf(value);
                }
                return new SimpleStringProperty(safe);
            });

            col.setCellFactory(new Callback<>() {
                @Override
                public TableCell<T, String> call(TableColumn<T, String> param) {
                    return new TableCell<>() {
                        private final Label lbl = new Label();
                        {
                            lbl.setWrapText(true);
                            lbl.getStyleClass().add("museum-ld-muted");
                            lbl.setMaxHeight(Double.MAX_VALUE);
                            param.widthProperty().addListener((obs, oldW, newW) -> lbl.setMaxWidth(newW.doubleValue() - 16));
                        }

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null || item.isEmpty()) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                lbl.setText(formatCellValue(item));
                                setGraphic(lbl);
                                setText(null);
                            }
                        }
                    };
                }
            });

            table.getColumns().add(col);
        }

        if (adminMode) {
            TableColumn<T, Void> actions = new TableColumn<>("Действия");
            actions.setMinWidth(140);
            actions.setCellFactory(col -> new TableCell<>() {
                private final HBox box = new HBox(8);
                private final Button edit = new Button("Edit");
                private final Button del = new Button("Del");
                {
                    box.setPadding(new Insets(6));
                    box.getChildren().addAll(edit, del);
                    edit.setOnAction(e -> {
                        T item = getTableView().getItems().get(getIndex());
                        System.out.println("Edit " + item);
                    });
                    del.setOnAction(e -> {
                        T item = getTableView().getItems().get(getIndex());
                        System.out.println("Delete " + item);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) setGraphic(null);
                    else setGraphic(box);
                }
            });
            table.getColumns().add(actions);
        }
    }

    private List<Field> collectFields() {
        List<Field> fields = new ArrayList<>();
        Field idField = Arrays.stream(type.getDeclaredFields())
                .filter(f -> f.getName().equalsIgnoreCase("id"))
                .findFirst().orElse(null);
        if (idField != null) fields.add(idField);
        for (Field f : type.getDeclaredFields()) {
            if (!fields.contains(f)) fields.add(f);
        }
        return fields;
    }

    private HBox createColumnHeader(Field field, String fieldName, TableColumn<T, ?> col) {
        Label lbl = new Label(prettyColumnName(fieldName));
        lbl.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lbl, Priority.ALWAYS);

        Button sortBtn = new Button("↕");
        sortBtn.setCursor(Cursor.HAND);
        sortBtn.getStyleClass().add("museum-ld-nav-button");

        sortBtn.setOnAction(evt -> {
            // simple toggle behaviour, inform owner
            boolean asc = true;
            // if user clicks repeatedly we invert via callback (owner keeps state)
            onSortChanged.accept(fieldName, asc);
        });

        Button filterBtn = new Button("☰");
        filterBtn.setCursor(Cursor.HAND);
        filterBtn.getStyleClass().add("museum-ld-nav-button");

        filterBtn.setOnAction(evt -> showFilterDialog(fieldName, field));

        HBox header = new HBox(6, lbl, sortBtn, filterBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(6, 2, 6, 2));
        return header;
    }

    private void showFilterDialog(String fieldName, Field field) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Фильтр — " + prettyColumnName(fieldName));
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.setResizable(true);

        VBox root = new VBox(12);
        root.setPadding(new Insets(12));

        Label header = new Label("Фильтры для поля: " + prettyColumnName(fieldName));

        ComboBox<Class<? extends FilterRule>> filterSelector = new ComboBox<>();
        filterSelector.setPrefWidth(250);

        Button addBtn = new Button("Добавить");

        VBox filtersContainer = new VBox(8);
        filtersContainer.setStyle("-fx-border-color: #ddd; -fx-padding: 8;");

        // Загружаем фильтры из аннотации
        ColumnMeta meta = field.getAnnotation(ColumnMeta.class);
        if (meta != null) {
            filterSelector.getItems().addAll(meta.filters());
        }

        // человекочитаемые названия
        filterSelector.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Class<? extends FilterRule> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    try {
                        setText(item.getDeclaredConstructor().newInstance().getTitle());
                    } catch (Exception e) {
                        setText(item.getSimpleName());
                    }
                }
            }
        });
        filterSelector.setButtonCell(filterSelector.getCellFactory().call(null));

        addBtn.setOnAction(e -> {
            Class<? extends FilterRule<?>> clazz = (Class<? extends FilterRule<?>>) filterSelector.getValue();
            if (clazz == null) return;

            try {
                FilterRule<?> filter = clazz.getDeclaredConstructor().newInstance();
                filter.setField(fieldName);

                HBox filterRow = new HBox(8);
                filterRow.setAlignment(Pos.CENTER_LEFT);

                Node editor = filter.createEditor();

                Button removeBtn = new Button("✕");
                removeBtn.setOnAction(ev -> filtersContainer.getChildren().remove(filterRow));

                filterRow.getChildren().addAll(
                        new Label(filter.getTitle() + ":"),
                        editor,
                        removeBtn
                );

                filtersContainer.getChildren().add(filterRow);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox topControls = new HBox(10, filterSelector, addBtn);

        root.getChildren().addAll(
                header,
                topControls,
                new Separator(),
                filtersContainer
        );

        dlg.getDialogPane().setContent(root);
        dlg.showAndWait();
    }


    private Object safeGetProperty(T row, Field field) {
        try {
            String name = field.getName();
            String getter = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            Method m = null;
            try { m = type.getMethod(getter); } catch (NoSuchMethodException ignored) {}
            Object v = null;
            if (m != null) v = m.invoke(row);
            else {
                field.setAccessible(true);
                v = field.get(row);
            }
            return readableValue(v);
        } catch (Exception e) {
            e.printStackTrace();
            return "(error)";
        }
    }

    private String readableValue(Object v) {
        if (v == null) return null;
        if (v instanceof String || v instanceof Number || v instanceof Boolean || v instanceof Character) return String.valueOf(v);
        try {
            Method m = v.getClass().getMethod("getName");
            Object name = m.invoke(v);
            if (name != null) return String.valueOf(name);
        } catch (Exception ignored) {}
        try {
            Method m2 = v.getClass().getMethod("getId");
            Object id = m2.invoke(v);
            if (id != null) return String.valueOf(id);
        } catch (Exception ignored) {}
        String s = v.toString();
        if (s.length() > 120) return s.substring(0, 117) + "...";
        return s;
    }

    private String formatCellValue(Object item) {
        if (item == null) return "";
        String s = String.valueOf(item);
        if (s.length() > 120) return s.substring(0, 117) + "...";
        return s;
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
