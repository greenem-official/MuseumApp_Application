package org.daylight.museumapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.daylight.museumapp.museumapp.StatCard;
import org.daylight.museumapp.museumapp.Icons;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class MuseumApp extends Application {
    public static final Logger LOGGER = Logger.getLogger(MuseumApp.class.getName());

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(createRoot(), 1200, 800);
        String cssPath = getClass().getResource("/styles/museum-light.css").toExternalForm();
        System.out.println("CSS path: " + cssPath); // для отладки
        scene.getStylesheets().add(cssPath);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Информационная система музея");
        primaryStage.show();
    }

    private BorderPane createRoot() {
        BorderPane root = new BorderPane();
        root.setCenter(createContent());
        return root;
    }

    private ScrollPane createContent() {
        VBox content = new VBox(32);
        content.setPadding(new Insets(24));
        content.getStyleClass().add("container");

        // Заголовок
        content.getChildren().add(createHeader());

        // Карточки статистики
        content.getChildren().add(createStatsGrid());

        // Нижние карточки
        content.getChildren().add(createBottomCards());

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
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

    private GridPane createStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(24);
        grid.getStyleClass().add("stats-grid");

        List<Object> stats = Arrays.asList(
                new StatCard("Экспонаты", "156", "exhibits", Icons.EXTERNAL_LINK),
                new StatCard("Коллекции", "12", "collections", Icons.FOLDER_OPEN),
                new StatCard("Залы", "8", "halls", Icons.DOOR_OPEN),
                new StatCard("Авторы", "45", "authors", Icons.USERS)
        );

        for (int i = 0; i < stats.size(); i++) {
            grid.add(createStatCard((StatCard) stats.get(i)), i % 4, i / 4);
        }

        return grid;
    }

    private StackPane createStatCard(StatCard stat) {
        VBox card = new VBox();
        card.getStyleClass().addAll("card", "stat-card");
        card.setOnMouseClicked(e -> navigateTo(stat.link()));

        // Header с иконкой
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 16, 8, 16));

        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("icon-container");

        Label icon = new Label(stat.icon());
        icon.getStyleClass().add("stat-icon");
        icon.setFont(Font.font("FontAwesome", 20));

        Label title = new Label(stat.title());
        title.getStyleClass().add("card-title");

        header.getChildren().addAll(iconContainer, title);
        iconContainer.getChildren().add(icon);

        // Content с числом
        VBox content = new VBox();
        content.setPadding(new Insets(0, 16, 16, 16));

        Label count = new Label(stat.count());
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

    private void navigateTo(String link) {
        System.out.println("Navigating to: " + link);
    }
}
