package org.daylight.museumapp;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.daylight.museumapp.components.util.Icons;
import org.daylight.museumapp.components.util.NavigationItem;
import org.daylight.museumapp.components.util.StatCard;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class MuseumApp extends Application {
    public static final Logger LOGGER = Logger.getLogger(MuseumApp.class.getName());

    private BorderPane root;
    private StackPane contentArea;
    private VBox sidebar;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        Scene scene = new Scene(root, 1400, 900);

        // Загрузка CSS
        try {
            String cssPath = getClass().getResource("/styles/museum-light.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("CSS not found, using default styles");
        }

        initializeLayout();
        showHomePage(); // Показываем главную страницу по умолчанию

        primaryStage.setScene(scene);
        primaryStage.setTitle("Информационная система музея");
        primaryStage.show();
    }

    private void initializeLayout() {
        // Создаем сайдбар
        sidebar = createSidebar();
        root.setLeft(sidebar);

        // Область для контента
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        root.setCenter(contentArea);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(280);
        sidebar.setMinWidth(280);

        // Заголовок с иконкой музея
        HBox titleBox = new HBox(12);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(0, 24, 24, 24));

//        Label icon = new Label(Icons.MUSEUM);
//        icon.setStyle("-fx-font-size: 24px;");

        Label title = new Label("Музей Искусств");
        title.getStyleClass().add("sidebar-title");
        title.setMaxWidth(Double.MAX_VALUE); // Важно!
        title.setWrapText(false); // Разрешаем перенос текста

        titleBox.getChildren().addAll(title); // icon,
        HBox.setHgrow(title, Priority.ALWAYS); // Разрешаем растягивание

        // Меню навигации
        VBox navMenu = createNavigationMenu();

        // Spacer чтобы прижать меню к верху
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(titleBox, navMenu, spacer, createFooter());
        return sidebar;
    }

    private VBox createNavigationMenu() {
        VBox menu = new VBox(4);
        menu.getStyleClass().add("sidebar-menu");
        menu.setPadding(new Insets(0, 12, 20, 12));

        NavigationItem[] navItems = {
                new NavigationItem("Главная", "/", Icons.HOME),
                new NavigationItem("Экспонаты", "/exhibits", Icons.EXHIBITS),
                new NavigationItem("Коллекции", "/collections", Icons.COLLECTIONS),
                new NavigationItem("Залы", "/halls", Icons.HALLS),
                new NavigationItem("Авторы", "/authors", Icons.AUTHORS)
        };

        for (NavigationItem item : navItems) {
            menu.getChildren().add(createNavButton(item));
        }

        return menu;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(16, 20, 20, 20));
        footer.setAlignment(Pos.CENTER);

        Label version = new Label("v1.0.0");
        version.setStyle("-fx-text-fill: #8a7a6d; -fx-font-size: 12px;");

        footer.getChildren().add(version);
        return footer;
    }

    private Button createNavButton(NavigationItem item) {
        Button button = new Button();

        // Создаем HBox для содержимого
        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setMaxWidth(Double.MAX_VALUE);

        // Иконка
        Label iconLabel = new Label(item.icon());
        iconLabel.getStyleClass().add("icon-label");
        iconLabel.setMinWidth(24);
        iconLabel.setPrefWidth(24);

        // Текст
        Label textLabel = new Label(item.title());
        textLabel.setStyle("-fx-text-fill: inherit; -fx-wrap-text: true;");
        textLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textLabel, Priority.ALWAYS); // Текст занимает всё доступное пространство

        content.getChildren().addAll(iconLabel, textLabel);
        button.setGraphic(content);

        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);

        // Обработчик клика
        button.setOnAction(e -> navigateTo(item.path()));

        return button;
    }

    private void navigateTo(String path) {
        // Плавное исчезновение текущего контента
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), contentArea);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);

        fadeOut.setOnFinished(e -> {
            // Обновляем активные кнопки
            updateActiveNavButton(path);

            // Загружаем новый контент
            loadContentForPath(path);

            // Плавное появление нового контента
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), contentArea);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private void loadContentForPath(String path) {
        switch (path) {
            case "/" -> showHomePage();
            case "/exhibits" -> showExhibitsPage();
            case "/collections" -> showCollectionsPage();
            case "/halls" -> showHallsPage();
            case "/authors" -> showAuthorsPage();
            default -> showHomePage();
        }
    }

    private void updateActiveNavButton(String path) {
        // Сбрасываем все кнопки
        for (Node node : ((VBox) sidebar.getChildren().get(1)).getChildren()) {
            if (node instanceof Button) {
                node.getStyleClass().remove("nav-button-active");
            }
        }

        // Активируем текущую кнопку
        String targetTitle = getTitleByPath(path);
        for (Node node : ((VBox) sidebar.getChildren().get(1)).getChildren()) {
            if (node instanceof Button btn) {
                HBox graphic = (HBox) btn.getGraphic();
                if (graphic.getChildren().size() > 1) {
                    Label textLabel = (Label) graphic.getChildren().get(1);
                    if (textLabel.getText().equals(targetTitle)) {
                        btn.getStyleClass().add("nav-button-active");
                        break;
                    }
                }
            }
        }
    }

    private String getTitleByPath(String path) {
        return switch (path) {
            case "/" -> "Главная";
            case "/exhibits" -> "Экспонаты";
            case "/collections" -> "Коллекции";
            case "/halls" -> "Залы";
            case "/authors" -> "Авторы";
            default -> "Главная";
        };
    }

    private void showHomePage() {
        ScrollPane homeContent = createContent(); // Ваш существующий метод
        contentArea.getChildren().setAll(homeContent);
    }

    private void showExhibitsPage() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.getChildren().add(new Label("Страница экспонатов"));
        contentArea.getChildren().setAll(content);
    }

    private void showCollectionsPage() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.getChildren().add(new Label("Страница коллекций"));
        contentArea.getChildren().setAll(content);
    }

    private void showHallsPage() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.getChildren().add(new Label("Страница залов"));
        contentArea.getChildren().setAll(content);
    }

    private void showAuthorsPage() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.getChildren().add(new Label("Страница авторов"));
        contentArea.getChildren().setAll(content);
    }

    // Ваш существующий метод createContent() остается без изменений
    private ScrollPane createContent() {
        VBox content = new VBox(32);
        content.setPadding(new Insets(24));
        content.getStyleClass().add("container");

        content.getChildren().addAll(
                createHeader(),
                createStatsGrid(),
                createBottomCards()
        );

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

        // Настройка анимации
        setupCardAnimations(card);

        // Остальной код создания карточки...
        card.setOnMouseClicked(e -> navigateTo(stat.link()));

        // Header с иконкой
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 16, 8, 16));

        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("icon-container");

        Label icon = new Label(stat.icon());
        icon.getStyleClass().add("stat-icon");

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

    // Java animations

    private void setupCardAnimations(Node card) {
        // Сохраняем оригинальный масштаб
        card.setScaleX(1.0);
        card.setScaleY(1.0);

        // Анимация при наведении
        card.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
            scaleUp.setToX(1.02);
            scaleUp.setToY(1.02);
            scaleUp.play();

            // Также анимируем тень
            card.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.15)));
        });

        // Анимация при уходе мыши
        card.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();

            // Возвращаем оригинальную тень
            card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.08)));
        });
    }
}
