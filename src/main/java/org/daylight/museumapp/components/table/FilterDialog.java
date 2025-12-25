package org.daylight.museumapp.components.table;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.daylight.museumapp.components.annotations.ColumnMeta;
import org.daylight.museumapp.dto.filterrelated.FilterRule;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FilterDialog {
    private final Field field;
    private final String fieldName;

    private final List<FilterRule<?>> activeFilters = new ArrayList<>();

    public FilterDialog(Field field, String fieldName) {
        this.field = field;
        this.fieldName = fieldName;
    }

    public List<FilterRule<?>> show(List<FilterRule<?>> previousFilters) {
        Dialog<List<FilterRule<?>>> dlg = new Dialog<>();
        dlg.setTitle("Фильтр — " + prettyColumnName(fieldName));
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.APPLY);
        dlg.setResizable(true);

        VBox root = new VBox(12);
        root.setMinWidth(500);
        root.setMinHeight(350);
        root.setPadding(new Insets(12));

        Label header = new Label("Фильтры для поля: " + prettyColumnName(fieldName));

        ComboBox<Class<? extends FilterRule>> filterSelector = new ComboBox<>();
        filterSelector.setPrefWidth(250);

        VBox filtersContainer = new VBox(8);
        filtersContainer.setStyle("-fx-border-color: #ddd; -fx-padding: 8;");

        // ----- загрузка фильтров из аннотации -----
        ColumnMeta meta = field.getAnnotation(ColumnMeta.class);
        if (meta != null) {
            filterSelector.getItems().addAll(meta.filters());
        }

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

        Button addBtn = new Button("Добавить");

        addBtn.setOnAction(e -> {
            Class<? extends FilterRule<?>> clazz = (Class<? extends FilterRule<?>>) filterSelector.getValue();
            if (clazz == null) return;

            try {
                FilterRule<?> filter = clazz.getDeclaredConstructor().newInstance();
                filter.setField(fieldName);

                activeFilters.add(filter);

                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);

                Node editor = filter.createEditor();

                Button removeBtn = new Button("✕");
                removeBtn.setOnAction(ev -> {
                    activeFilters.remove(filter);
                    filtersContainer.getChildren().remove(row);
                });

                row.getChildren().addAll(
                        new Label(filter.getTitle() + ":"),
                        editor,
                        removeBtn
                );

                filtersContainer.getChildren().add(row);

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

        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.APPLY) {
                activeFilters.forEach(FilterRule::extractValueFromEditor);
                return activeFilters;
            }
            return null;
        });

        return dlg.showAndWait().orElse(null);
    }

    private String prettyColumnName(String f) {
        return Character.toUpperCase(f.charAt(0)) + f.substring(1);
    }
}
