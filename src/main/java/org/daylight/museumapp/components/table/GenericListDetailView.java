package org.daylight.museumapp.components.table;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.PagedResult;
import org.daylight.museumapp.dto.filterrelated.PagedRequest;
import org.daylight.museumapp.dto.filterrelated.SortRequest;
import org.daylight.museumapp.dto.tables.Author;
import org.daylight.museumapp.dto.tables.Collection;
import org.daylight.museumapp.dto.tables.Hall;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Универсальная панель: таблица слева + detail pane справа.
 * @param <T> тип сущности (Item, Collection, Author, Hall и т.д.)
 */
public class GenericListDetailView<T> extends HBox {

    private final Class<T> type;
    private final Function<PagedRequest, CompletableFuture<ApiResult<PagedResult<T>>>> fetcher;

    private final TableView<T> table = new TableView<>();
    private final VBox leftPane = new VBox(12);
    private final StackPane rightPaneWrapper = new StackPane();
    private final BorderPane detailPane = new BorderPane();
    private final ProgressIndicator loading = new ProgressIndicator();
    private final HBox paginationBar = new HBox(8);

    // controls for headers
    private final Map<String, Button> sortButtons = new HashMap<>();
    private final Map<String, Button> filterButtons = new HashMap<>();

    // paging / sort state
    private int page = 0;
    private int pageSize = 10;
    private String sortField = "id";
    private boolean sortAsc = true;
    private boolean adminMode = false;

    // last loaded page result
    private PagedResult<T> lastPage;

    public GenericListDetailView(Class<T> type,
                                 Function<PagedRequest, CompletableFuture<ApiResult<PagedResult<T>>>> fetcher,
                                 boolean adminMode) {
        this.type = type;
        this.fetcher = fetcher;
        this.adminMode = adminMode;

        initialize();
        buildColumns();
        refresh();
    }

    private void initialize() {
        this.getStyleClass().addAll("list-detail-root");
        this.setSpacing(16);
        this.setPadding(new Insets(16));

        // leftPane: controls + table + pagination
        HBox topControls = new HBox(12);
        topControls.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(type.getSimpleName() + " — Список");
        title.getStyleClass().addAll("info-card-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshButton = new Button("Обновить");
        refreshButton.setOnAction(evt -> refresh());

        ComboBox<Integer> pageSizeBox = new ComboBox<>(FXCollections.observableArrayList(5,10,20,50,100));
        pageSizeBox.setValue(pageSize);
        pageSizeBox.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) setPageSize(newV);
        });

        topControls.getChildren().addAll(title, spacer, new Label("Строк/страница:"), pageSizeBox, refreshButton);

        leftPane.getChildren().addAll(topControls, table, paginationBar);
        leftPane.getStyleClass().add("left-pane");
        leftPane.setPrefWidth(700);
        VBox.setVgrow(table, Priority.ALWAYS);

        // table appearance
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().addAll("card", "stat-card");
        table.setPlaceholder(new Label("Нет данных"));

        // row interaction
        table.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (! row.isEmpty() && evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 1) {
                    T item = row.getItem();
                    openDetail(item);
                }
            });
            return row;
        });

        // right detail wrapper
        rightPaneWrapper.setPrefWidth(380);
        rightPaneWrapper.getStyleClass().add("detail-wrapper");
        rightPaneWrapper.setVisible(false);
        rightPaneWrapper.setManaged(false); // when not visible, doesn't take space

        // detailPane basic style
        detailPane.getStyleClass().addAll("card", "info-card");
        detailPane.setPrefWidth(380);

        rightPaneWrapper.getChildren().add(detailPane);

        // loading overlay
        loading.setVisible(false);
        loading.setMaxSize(64, 64);

        StackPane leftStack = new StackPane(leftPane, loading);
        StackPane.setAlignment(loading, Pos.CENTER);

        this.getChildren().addAll(leftStack, rightPaneWrapper);
        HBox.setHgrow(leftStack, Priority.ALWAYS);
        HBox.setHgrow(rightPaneWrapper, Priority.NEVER);

        // pagination bar initial
        buildPaginationBar();
    }

    private void buildColumns() {
        table.getColumns().clear();
        sortButtons.clear();
        filterButtons.clear();

        // collect fields, prefer "id" first
        List<Field> fields = new ArrayList<>();
        Field idField = Arrays.stream(type.getDeclaredFields())
                .filter(f -> f.getName().equalsIgnoreCase("id"))
                .findFirst().orElse(null);
        if (idField != null) fields.add(idField);
        for (Field f : type.getDeclaredFields()) {
            if (!fields.contains(f)) fields.add(f);
        }

        for (Field field : fields) {
            String name = field.getName();
            TableColumn<T, String> col = new TableColumn<>();
            col.getStyleClass().add("table-col");
            col.setText(prettyColumnName(name));

            HBox headerBox = createColumnHeader(name, col);
            col.setGraphic(headerBox);

            // ensure ID column has readable fixed width to avoid 'spreading' weirdness
            if (name.equalsIgnoreCase("id")) {
                col.setMinWidth(70);
                col.setPrefWidth(90);
                col.setMaxWidth(140);
            }

            // Cell value factory returns a StringProperty always
            col.setCellValueFactory(cellData -> {
                T row = cellData.getValue();
                String safe = "";
                if (row != null) {
                    Object value = safeGetProperty(row, field);
                    safe = value == null ? "" : String.valueOf(value);
                }
                return new SimpleStringProperty(safe);
            });

            // custom cell factory: label with wrapping, clipping
            col.setCellFactory(new Callback<TableColumn<T, String>, TableCell<T, String>>() {
                @Override
                public TableCell<T, String> call(TableColumn<T, String> param) {
                    return new TableCell<>() {
                        private final Label lbl = new Label();
                        {
                            lbl.setWrapText(true);
                            lbl.getStyleClass().add("muted-text");
                            lbl.setMaxHeight(Double.MAX_VALUE);
                        }

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null || item.isEmpty()) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                lbl.setText(formatCellValue(item));
                                // limit label width so wrap works when table constrained
                                lbl.setMaxWidth(param.getWidth() - 16);
                                setGraphic(lbl);
                                setText(null);
                            }
                        }
                    };
                }
            });

            table.getColumns().add(col);
        }

        // admin actions column (placeholder)
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
                        // placeholder
                        System.out.println("Edit " + item);
                    });
                    del.setOnAction(e -> {
                        T item = getTableView().getItems().get(getIndex());
                        // placeholder
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

    private HBox createColumnHeader(String fieldName, TableColumn<T, ?> col) {
        Label lbl = new Label(prettyColumnName(fieldName));
        lbl.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lbl, Priority.ALWAYS);

        Button sortBtn = new Button();
        sortBtn.setCursor(Cursor.HAND);
        sortBtn.getStyleClass().add("nav-button");
        updateSortButtonGraphic(sortBtn, fieldName.equals(sortField), sortAsc);
        sortButtons.put(fieldName, sortBtn);

        sortBtn.setOnAction(evt -> {
            if (!fieldName.equals(sortField)) {
                sortField = fieldName;
                sortAsc = true;
            } else {
                sortAsc = !sortAsc;
            }
            // update visuals
            updateAllSortButtons();
            goToPage(0); // refresh from first page with new sort
        });

        Button filterBtn = new Button("☰"); // placeholder icon
        filterBtn.setCursor(Cursor.HAND);
        filterBtn.getStyleClass().add("nav-button");
        filterButtons.put(fieldName, filterBtn);

        filterBtn.setOnAction(evt -> showFilterPopup(filterBtn, fieldName));

        HBox header = new HBox(6, lbl, sortBtn, filterBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(6, 2, 6, 2));
        return header;
    }

    private void updateAllSortButtons() {
        for (Map.Entry<String, Button> e : sortButtons.entrySet()) {
            String field = e.getKey();
            Button b = e.getValue();
            updateSortButtonGraphic(b, field.equals(sortField), sortAsc);
        }
    }

    private void updateSortButtonGraphic(Button b, boolean active, boolean asc) {
        if (!active) {
            b.setText("↕");
            b.getStyleClass().remove("nav-button-active");
        } else {
            b.setText(asc ? "↑" : "↓");
            if (!b.getStyleClass().contains("nav-button-active")) {
                b.getStyleClass().add("nav-button-active");
            }
        }
    }

    private void showFilterPopup(Node owner, String fieldName) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Фильтр — " + prettyColumnName(fieldName));
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        VBox content = new VBox(8, new Label("Плейсхолдер фильтра для поля \"" + fieldName + "\""),
                new Label("Здесь можно будет настроить фильтрацию (равно, содержит, диапазон и т.д.)"));
        content.setPadding(new Insets(12));
        dlg.getDialogPane().setContent(content);
        dlg.showAndWait();
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

    private Object safeGetProperty(T row, Field field) {
        try {
            String name = field.getName();
            // try getter
            String getter = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            Method m = null;
            try {
                m = type.getMethod(getter);
            } catch (NoSuchMethodException ignored) {}
            Object v = null;
            if (m != null) {
                v = m.invoke(row);
            } else {
                field.setAccessible(true);
                v = field.get(row);
            }
            return readableValue(v);
        } catch (Exception e) {
            // don't throw — return marker
            e.printStackTrace();
            return "(error)";
        }
    }

    private String readableValue(Object v) {
        if (v == null) return null;
        // primitives / wrappers / string
        if (v instanceof String || v instanceof Number || v instanceof Boolean || v instanceof Character) {
            return String.valueOf(v);
        }
        // try getName()
        try {
            Method m = v.getClass().getMethod("getName");
            Object name = m.invoke(v);
            if (name != null) return String.valueOf(name);
        } catch (Exception ignored) {}
        // try getId()
        try {
            Method m2 = v.getClass().getMethod("getId");
            Object id = m2.invoke(v);
            if (id != null) return String.valueOf(id);
        } catch (Exception ignored) {}
        // fallback to toString (but keep short)
        String s = v.toString();
        if (s.length() > 120) return s.substring(0, 117) + "...";
        return s;
    }

    private String formatCellValue(Object item) {
        if (item == null) return "";
        if (item instanceof Author author) return author.getName();
        if (item instanceof Hall hall) return hall.getName();
        if (item instanceof Collection collection) return collection.getName();
        String s = String.valueOf(item);
        if (s.length() > 120) return s.substring(0, 117) + "...";
        return s;
    }

    private void openDetail(T item) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(12));
        root.getStyleClass().add("muted-text");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);
        Button close = new Button("✕");
        close.getStyleClass().add("auth-button");
        close.setOnAction(evt -> closeDetail());
        header.getChildren().add(close);

        Label title = new Label(type.getSimpleName() + " — Подробно");
        title.getStyleClass().addAll("info-card-title");

        VBox fieldsBox = new VBox(8);
        for (Field f : type.getDeclaredFields()) {
            String fname = prettyColumnName(f.getName());
            Object val = safeGetProperty(item, f);
            Label label = new Label(fname + ":");
            label.getStyleClass().add("card-description");
            Text valueText = new Text(val == null ? "-" : String.valueOf(val));
            valueText.getStyleClass().add("muted-text");
            VBox block = new VBox(2, label, valueText);
            block.setPadding(new Insets(4,0,4,0));
            fieldsBox.getChildren().add(block);
        }

        ScrollPane sp = new ScrollPane(fieldsBox);
        sp.setFitToWidth(true);
        sp.setVmax(1.0);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.getStyleClass().add("detail-scroll");

        root.getChildren().addAll(header, title, sp);
        detailPane.setCenter(root);

        rightPaneWrapper.getChildren().clear();
        rightPaneWrapper.getChildren().add(detailPane);
        showDetailPane(true);
    }

    private void closeDetail() {
        showDetailPane(false);
    }

    private void showDetailPane(boolean show) {
        if (show) {
            // make visible and animate in
            rightPaneWrapper.setManaged(true);
            rightPaneWrapper.setVisible(true);
            rightPaneWrapper.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(160), rightPaneWrapper);
            ft.setToValue(1.0);
            ft.play();
        } else {
            // animate out then hide
            FadeTransition ft = new FadeTransition(Duration.millis(140), rightPaneWrapper);
            ft.setToValue(0.0);
            ft.setOnFinished(evt -> {
                rightPaneWrapper.setVisible(false);
                rightPaneWrapper.setManaged(false);
            });
            ft.play();
        }

        // НЕ менять prefWidth у leftPane — это убирает дерганья.
        // Но попросим TableView пересчитать размеры после анимации/события:
        Platform.runLater(() -> {
            table.refresh();         // обновит содержимое и размеры ячеек
            table.requestLayout();   // форсирует перерасчёт layout
        });
    }


    private void buildPaginationBar() {
        paginationBar.getChildren().clear();
        paginationBar.setAlignment(Pos.CENTER_LEFT);
        paginationBar.setPadding(new Insets(8,0,0,0));

        Button prev = new Button("◀");
        Button next = new Button("▶");
        Label pageInfo = new Label("—");
        TextField pageInput = new TextField();
        pageInput.setPrefWidth(60);
        Button go = new Button("Перейти");

        prev.setOnAction(e -> { if (page > 0) goToPage(page - 1); });
        next.setOnAction(e -> { if (lastPage != null && page < lastPage.getTotalPages() - 1) goToPage(page + 1); });
        go.setOnAction(e -> {
            try {
                int p = Integer.parseInt(pageInput.getText()) - 1;
                goToPage(Math.max(0, p));
            } catch (NumberFormatException ignored) {}
        });

        paginationBar.getChildren().addAll(prev, pageInfo, next, new Label("Стр:"), pageInput, go);
    }

    private void updatePaginationUI() {
        paginationBar.getChildren().clear();
        paginationBar.setAlignment(Pos.CENTER_LEFT);

        Button prev = new Button("◀");
        Button next = new Button("▶");

        prev.setOnAction(e -> { if (page > 0) goToPage(page - 1); });
        next.setOnAction(e -> { if (lastPage != null && page < lastPage.getTotalPages() - 1) goToPage(page + 1); });

        Label info;
        if (lastPage == null) {
            info = new Label("Загрузка...");
        } else {
            info = new Label(String.format("Страница %d из %d  —  Всего: %d",
                    page + 1, lastPage.getTotalPages(), lastPage.getTotalItems()));
        }

        paginationBar.getChildren().addAll(prev, info, next);
    }

    // ----- Networking / data load

    public void refresh() {
        loadPage(page, pageSize, sortField, sortAsc);
    }

    private void loadPage(int page, int size, String sort, boolean asc) {
        this.page = page;
        this.pageSize = size;
        showLoading(true);

        PagedRequest req = new PagedRequest(page, size, new SortRequest(sort, asc ? "asc" : "desc"), List.of());

        CompletableFuture<ApiResult<PagedResult<T>>> future = fetcher.apply(req);
        future.thenAccept(result -> {
            Platform.runLater(() -> {
                showLoading(false);
                if (result == null) {
                    showError("Нет ответа от сервера");
                    return;
                }
                if (!result.isSuccess()) {
                    String msg = result.getError();
                    if (result.isThrowable() && result.getThrowable() != null) {
                        msg = result.getThrowable().getMessage();
                    }
                    showError("Ошибка: " + msg);
                    System.out.println("Ошибка: " + result.getError());
                    if(result.isThrowable()) result.getThrowable().printStackTrace();
                    return;
                }
                lastPage = result.getData();
                List<T> items = lastPage.getItems() == null ? Collections.emptyList() : lastPage.getItems();
                table.getItems().setAll(items);
                updatePaginationUI();
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                showLoading(false);
                showError("Ошибка запроса: " + ex.getMessage());
            });
            return null;
        });
    }

    private void showLoading(boolean on) {
        loading.setVisible(on);
    }

    private void showError(String text) {
        Alert a = new Alert(Alert.AlertType.ERROR, text, ButtonType.OK);
        a.showAndWait();
    }

    public void goToPage(int p) {
        if (p < 0) p = 0;
        this.page = p;
        loadPage(page, pageSize, sortField, sortAsc);
    }

    public void setPageSize(int size) {
        this.pageSize = size;
        goToPage(0);
    }

    public void setAdminMode(boolean admin) {
        this.adminMode = admin;
        buildColumns();
    }
}
