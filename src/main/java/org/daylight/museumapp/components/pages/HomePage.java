package org.daylight.museumapp.components.pages;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.daylight.museumapp.components.common.animations.CardAnimations;
import org.daylight.museumapp.components.common.cards.StatCardComponent;
import org.daylight.museumapp.model.StatCard;
import org.daylight.museumapp.services.ApiService;
import org.daylight.museumapp.services.NavigationService;
import org.daylight.museumapp.util.Icons;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomePage {
    private VBox content;
    private GridPane statsGrid;

    public HomePage() {
        initializePage();
        loadStatsAsync();
    }

    private void initializePage() {
        content = new VBox(32);
        content.setPadding(new Insets(24));
        content.getStyleClass().add("container");

        content.getChildren().addAll(
                createHeader(),
                createStatsPlaceholder(),
                createBottomCards()
        );
    }

    private void loadStatsAsync() {
        CompletableFuture.supplyAsync(() -> {
            return ApiService.getInstance().getStats();
        }).thenAccept(stats -> {
            Platform.runLater(() -> {
                // Заменяем плейсхолдеры на реальные данные
                updateStatsGrid(stats);
            });
        }).exceptionally(e -> {
            System.err.println("Error loading stats: " + e.getMessage());
            return null;
        });
    }

    private VBox createHeader() {
        VBox header = new VBox(16);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header");

        Label title = new Label("Информационная система музея");
        title.getStyleClass().add("main-title");

        Label subtitle = new Label("Управление музейной коллекцией, каталогизация экспонатов и организация экспозиций");
        subtitle.getStyleClass().add("subtitle");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(600);

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private GridPane createStatsGrid(List<StatCard> stats) {
        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(24);
        grid.getStyleClass().add("stats-grid");

        for (int i = 0; i < stats.size(); i++) {
            StatCardComponent statCardComponent = new StatCardComponent(stats.get(i));
            grid.add(statCardComponent.getContent(), i % 4, i / 4);
        }

        return grid;
    }

    private GridPane createStatsPlaceholder() {
        GridPane placeholderGrid = new GridPane();
        placeholderGrid.setHgap(24);
        placeholderGrid.setVgap(24);
        placeholderGrid.getStyleClass().add("stats-grid");

        // Плейсхолдеры с анимацией загрузки
        List<StatCard> placeholderStats = Arrays.asList(
                new StatCard("Загрузка...", "...", "/", Icons.EXTERNAL_LINK),
                new StatCard("Загрузка...", "...", "/", Icons.FOLDER_OPEN),
                new StatCard("Загрузка...", "...", "/", Icons.DOOR_OPEN),
                new StatCard("Загрузка...", "...", "/", Icons.USERS)
        );

        for (int i = 0; i < placeholderStats.size(); i++) {
            VBox placeholderCard = createPlaceholderCard(placeholderStats.get(i));
            placeholderGrid.add(placeholderCard, i % 4, i / 4);
        }

        return placeholderGrid;
    }

    private VBox createPlaceholderCard(StatCard stat) {
        VBox card = new VBox();
        card.getStyleClass().addAll("card", "stat-card", "placeholder-card");

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 16, 8, 16));

        Label icon = new Label(stat.getIcon());
        icon.getStyleClass().add("stat-icon");

        Label title = new Label(stat.getTitle());
        title.getStyleClass().add("card-title");

        header.getChildren().addAll(icon, title);

        VBox content = new VBox();
        content.setPadding(new Insets(0, 16, 16, 16));

        Label count = new Label(stat.getCount());
        count.getStyleClass().add("stat-count");

        content.getChildren().add(count);
        card.getChildren().addAll(header, content);

        return card;
    }

    private StackPane createStatCard(StatCard stat) {
        VBox card = new VBox();
        card.getStyleClass().addAll("card", "stat-card");

        // Настройка анимации
        CardAnimations.setupCardAnimations(card);

        // Остальной код создания карточки...
        card.setOnMouseClicked(e -> {
            NavigationService.getInstance().navigateTo(stat.getLink());
        });

        // Header с иконкой
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 16, 8, 16));

        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("icon-container");

        Label icon = new Label(stat.getIcon());
        icon.getStyleClass().add("stat-icon");

        Label title = new Label(stat.getTitle());
        title.getStyleClass().add("card-title");

        header.getChildren().addAll(iconContainer, title);
        iconContainer.getChildren().add(icon);

        // Content с числом
        VBox content = new VBox();
        content.setPadding(new Insets(0, 16, 16, 16));

        Label count = new Label(stat.getCount());
        count.getStyleClass().add("stat-count");

        content.getChildren().add(count);
        card.getChildren().addAll(header, content);

        StackPane container = new StackPane(card);
        container.getStyleClass().add("card-container");
        return container;
    }

    private HBox createBottomCards() {
        HBox cardsRow = new HBox(24);
        cardsRow.getStyleClass().add("bottom-cards");

        VBox aboutCard = createInfoCard(
                "О системе",
                "Возможности информационной системы",
                Arrays.asList(
                        "• Каталогизация музейных экспонатов",
                        "• Управление коллекциями и залами",
                        "• База данных авторов произведений",
                        "• Статистика и отчётность"
                )
        );

        VBox updatesCard = createInfoCard(
                "Последние обновления",
                "Недавние изменения в коллекции",
                Arrays.asList(
                        "• Добавлено 5 новых экспонатов",
                        "• Обновлена коллекция \"Импрессионизм\"",
                        "• Открыт новый выставочный зал",
                        "• Добавлены работы 3 новых авторов"
                )
        );

        cardsRow.getChildren().addAll(aboutCard, updatesCard);
        return cardsRow;
    }

    private VBox createInfoCard(String title, String description, List<String> items) {
        VBox card = new VBox();
        card.getStyleClass().addAll("card", "info-card");
        card.setPrefWidth(400);

        // Header
        VBox header = new VBox(4);
        header.setPadding(new Insets(20, 20, 12, 20));

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("info-card-title");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("card-description");

        header.getChildren().addAll(titleLabel, descLabel);

        // Content
        VBox content = new VBox(8);
        content.setPadding(new Insets(0, 20, 20, 20));

        for (String item : items) {
            Label itemLabel = new Label(item);
            itemLabel.getStyleClass().add("muted-text");
            content.getChildren().add(itemLabel);
        }

        card.getChildren().addAll(header, content);
        return card;
    }

    private void updateStatsGrid(List<StatCard> stats) {
        // Удаляем плейсхолдер
        if (content.getChildren().size() > 1) {
            content.getChildren().remove(1);
        }

        // Создаем и добавляем реальную сетку
        statsGrid = createStatsGrid(stats);
        content.getChildren().add(1, statsGrid); // Добавляем на вторую позицию
    }

    public ScrollPane getContent() {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
}