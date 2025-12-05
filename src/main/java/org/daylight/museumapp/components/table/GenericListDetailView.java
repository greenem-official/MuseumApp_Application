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
import javafx.util.Pair;
import org.daylight.museumapp.components.common.SeparateStyles;
import org.daylight.museumapp.components.loading.LoadingDots;
import org.daylight.museumapp.dto.ApiResult;
import org.daylight.museumapp.dto.PagedResult;
import org.daylight.museumapp.dto.filterrelated.FilterRule;
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
/**
 * High-level container: left — table + controls, right — detail pane.
 * Делегирует создание колонок в ColumnFactory и пагинацию в PaginationBar.
 */
public class GenericListDetailView<T> extends HBox {

    private final Class<T> type;
    private final Function<PagedRequest, CompletableFuture<ApiResult<PagedResult<T>>>> fetcher;

    private final TableView<T> table = new TableView<>();
    private final VBox leftPane = new VBox(12);
    private final StackPane rightPaneWrapper = new StackPane();
    private final BorderPane detailPane = new BorderPane();
    private final LoadingDots loading = new LoadingDots();
    private final PaginationBar paginationBar;

    private int page = 0;
    private int pageSize = 10;
    private String sortField = "id";
    private boolean sortAsc = true;
    private boolean adminMode = false;
    private String titleName;
    private Map<String, List<FilterRule<?>>> filterRules;

    private PagedResult<T> lastPage;

    public GenericListDetailView(Class<T> type,
                                 Function<PagedRequest, CompletableFuture<ApiResult<PagedResult<T>>>> fetcher,
                                 boolean adminMode, String titleName) {
        this.type = type;
        this.fetcher = fetcher;
        this.adminMode = adminMode;
        this.titleName = titleName;
        this.paginationBar = new PaginationBar(this::goToPage);

        this.getStylesheets().addAll(SeparateStyles.tablesCss);

        this.filterRules = new HashMap<>();
        initialize();

        ColumnFactory<T> cf = new ColumnFactory<>(type,
                this::openDetail,
                this::onSortChanged,
                this::onFiltersChanged,
                adminMode);
        cf.buildColumnsInto(table);

        refresh();
    }

    private void initialize() {
        this.getStyleClass().add("museum-ld-root");
        this.setSpacing(16);
        this.setPadding(new Insets(12));

        // top controls
        HBox topControls = new HBox(12);
        topControls.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(titleName);
        title.getStyleClass().addAll("museum-ld-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshButton = new Button("Обновить");
        refreshButton.setOnAction(evt -> refresh());

        ComboBox<Integer> pageSizeBox = new ComboBox<>(FXCollections.observableArrayList(5, 10, 20, 50, 100));
        pageSizeBox.setValue(pageSize);
        pageSizeBox.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) setPageSize(newV);
        });

        topControls.getChildren().addAll(title, spacer, new Label("Строк/страница:"), pageSizeBox, refreshButton);

        leftPane.getChildren().addAll(topControls, table, paginationBar);
        leftPane.getStyleClass().add("museum-ld-left");
        leftPane.setPrefWidth(700);
        VBox.setVgrow(table, Priority.ALWAYS);

        table.getStyleClass().add("museum-ld-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Нет данных"));

        table.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (!row.isEmpty() && evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 1) {
                    T item = row.getItem();
                    openDetail(item);
                }
            });
            return row;
        });

        rightPaneWrapper.setPrefWidth(380);
        rightPaneWrapper.getStyleClass().add("museum-ld-right-wrapper");
        rightPaneWrapper.setVisible(false);
        rightPaneWrapper.setManaged(false);

        detailPane.getStyleClass().add("museum-ld-detail");
        detailPane.setPrefWidth(380);

        rightPaneWrapper.getChildren().add(detailPane);

        StackPane leftStack = new StackPane(leftPane, loading.getDots());
        StackPane.setAlignment(loading.getDots(), Pos.CENTER);

        this.getChildren().addAll(leftStack, rightPaneWrapper);
        HBox.setHgrow(leftStack, Priority.ALWAYS);
        HBox.setHgrow(rightPaneWrapper, Priority.NEVER);

        // pagination bar initial state already in its constructor
    }

    private void onSortChanged(String field, boolean asc) {
        this.sortField = field;
        this.sortAsc = asc;
        goToPage(0);
    }

    private void onFiltersChanged(Pair<String, List<FilterRule<?>>> filters) {
        this.filterRules.put(filters.getKey(), filters.getValue());
    }

    // ----- detail pane -----
    private void openDetail(T item) {
        DetailPane<T> dp = new DetailPane<>(type, item, this::closeDetail);
        detailPane.setCenter(dp.getRoot());
        showDetailPane(true);
    }

    private void closeDetail() {
        showDetailPane(false);
    }

    private void showDetailPane(boolean show) {
        if (show) {
            rightPaneWrapper.setManaged(true);
            rightPaneWrapper.setVisible(true);
            rightPaneWrapper.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(160), rightPaneWrapper);
            ft.setToValue(1.0);
            ft.play();
        } else {
            FadeTransition ft = new FadeTransition(Duration.millis(140), rightPaneWrapper);
            ft.setToValue(0.0);
            ft.setOnFinished(evt -> {
                rightPaneWrapper.setVisible(false);
                rightPaneWrapper.setManaged(false);
            });
            ft.play();
        }

        // Не менять prefWidth у leftPane — только refresh/layout
        Platform.runLater(() -> {
            table.refresh();
            table.requestLayout();
        });
    }

    // ----- networking -----
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
                    if (result.isThrowable()) result.getThrowable().printStackTrace();
                    return;
                }
                lastPage = result.getData();
                List<T> items = lastPage.getItems() == null ? List.of() : lastPage.getItems();
                table.getItems().setAll(items);
                paginationBar.update(lastPage, page);
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
        loading.getDots().setVisible(on);
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
        ColumnFactory<T> cf = new ColumnFactory<>(type, this::openDetail, this::onSortChanged, this::onFiltersChanged, admin);
        cf.buildColumnsInto(table);
    }
}
