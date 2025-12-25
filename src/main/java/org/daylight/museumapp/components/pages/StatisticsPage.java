package org.daylight.museumapp.components.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lombok.Setter;
import org.daylight.museumapp.dto.UserData;
import org.daylight.museumapp.dto.UserRole;
import org.daylight.museumapp.services.AuthService;
import org.daylight.museumapp.services.NavigationService;
import org.daylight.museumapp.services.NotificationService;

import java.util.Arrays;
import java.util.List;

public class StatisticsPage {
    private VBox content;
    private ToggleGroup modeToggleGroup;
    private VBox statsContentContainer;

    // Текущие данные статистики
    @Setter
    private List<StatItem> exhibitsStats;
    @Setter
    private List<StatItem> collectionsStats;
    @Setter
    private List<StatItem> hallsStats;
    @Setter
    private List<StatItem> authorsStats;

    // Класс для представления элемента статистики
    public static class StatItem {
        private int place;
        private String id;
        private String name;
        private String description;
        private int visitCount;

        public StatItem(int place, String id, String name, String description, int visitCount) {
            this.place = place;
            this.id = id;
            this.name = name;
            this.description = description;
            this.visitCount = visitCount;
        }

        public int getPlace() {
            return place;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getVisitCount() {
            return visitCount;
        }

        public String getCombinedInfo() {
            return String.format("%s (%s)", name, description);
        }
    }

    public StatisticsPage() {
        initializePage();
        loadTestData();
    }

    private void initializePage() {
        content = new VBox(24);
        content.setPadding(new Insets(32));
        content.setAlignment(Pos.TOP_LEFT);
        content.getStyleClass().add("stats-container");

        Label title = new Label("Статистика");
        title.getStyleClass().add("page-title");

        // Создаем переключатель режимов
        HBox modeSelector = createModeSelector();

        // Контейнер для отображения статистики
        statsContentContainer = new VBox(16);
        statsContentContainer.setPadding(new Insets(16, 0, 0, 0));

        content.getChildren().addAll(title, modeSelector, statsContentContainer);

        // Показываем пустое состояние по умолчанию
        showEmptyState();
    }

    private HBox createModeSelector() {
        HBox selectorBox = new HBox(8);
        selectorBox.setAlignment(Pos.CENTER_LEFT);

        Label modeLabel = new Label("Режим:");
        modeLabel.getStyleClass().add("mode-label");

        modeToggleGroup = new ToggleGroup();

        ToggleButton exhibitsBtn = createToggleButton("Экспонаты", "exhibits");
        ToggleButton collectionsBtn = createToggleButton("Коллекции", "collections");
        ToggleButton hallsBtn = createToggleButton("Залы", "halls");
        ToggleButton authorsBtn = createToggleButton("Авторы", "authors");

        selectorBox.getChildren().addAll(modeLabel, exhibitsBtn, collectionsBtn, hallsBtn, authorsBtn);

        return selectorBox;
    }

    private ToggleButton createToggleButton(String text, String mode) {
        ToggleButton button = new ToggleButton(text);
        button.setToggleGroup(modeToggleGroup);
        button.getStyleClass().add("mode-toggle");

        button.setOnAction(e -> {
            switch (mode) {
                case "exhibits":
                    showStatistics(exhibitsStats, "Экспонаты");
                    break;
                case "collections":
                    showStatistics(collectionsStats, "Коллекции");
                    break;
                case "halls":
                    showStatistics(hallsStats, "Залы");
                    break;
                case "authors":
                    showStatistics(authorsStats, "Авторы");
                    break;
            }
        });

        return button;
    }

    private void showStatistics(List<StatItem> stats, String title) {
        statsContentContainer.getChildren().clear();

        if (stats == null || stats.isEmpty()) {
            showEmptyState();
            return;
        }

        Label statsTitle = new Label("Топ " + title);
        statsTitle.getStyleClass().add("stats-title");

        VBox statsList = new VBox(12);

        for (StatItem item : stats) {
            HBox statItem = createStatItemView(item);
            statsList.getChildren().add(statItem);
        }

        statsContentContainer.getChildren().addAll(statsTitle, statsList);
    }

    private HBox createStatItemView(StatItem item) {
        HBox itemBox = new HBox(16);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.getStyleClass().add("stat-item");
        itemBox.setPadding(new Insets(12));

        // Место
        Label placeLabel = new Label(String.valueOf(item.getPlace()));
        placeLabel.getStyleClass().add("place-label");

        // ID
        Label idLabel = new Label("ID: " + item.getId());
        idLabel.getStyleClass().add("id-label");

        // Название и описание
        VBox infoBox = new VBox(4);
        Label nameLabel = new Label(item.getCombinedInfo());
        nameLabel.getStyleClass().add("name-label");

        Label countLabel = new Label("Посещений: " + item.getVisitCount());
        countLabel.getStyleClass().add("count-label");

        infoBox.getChildren().addAll(nameLabel, countLabel);

        itemBox.getChildren().addAll(placeLabel, idLabel, infoBox);

        return itemBox;
    }

    private void showEmptyState() {
        statsContentContainer.getChildren().clear();
        Label emptyLabel = new Label("Выберите режим для отображения статистики");
        emptyLabel.getStyleClass().add("empty-label");
        statsContentContainer.getChildren().add(emptyLabel);
    }

    // Метод для установки всех данных сразу
    public void setAllStats(
            List<StatItem> exhibits,
            List<StatItem> collections,
            List<StatItem> halls,
            List<StatItem> authors
    ) {
        setExhibitsStats(exhibits);
        setCollectionsStats(collections);
        setHallsStats(halls);
        setAuthorsStats(authors);
    }

    // Метод для принудительного обновления отображения
    public void refreshDisplay() {
        if (modeToggleGroup.getSelectedToggle() != null) {
            ToggleButton selected = (ToggleButton) modeToggleGroup.getSelectedToggle();
            String text = selected.getText();

            switch (text) {
                case "Экспонаты":
                    showStatistics(exhibitsStats, "Экспонаты");
                    break;
                case "Коллекции":
                    showStatistics(collectionsStats, "Коллекции");
                    break;
                case "Залы":
                    showStatistics(hallsStats, "Залы");
                    break;
                case "Авторы":
                    showStatistics(authorsStats, "Авторы");
                    break;
            }
        }
    }

    public void loadTestData() {
        // Экспонаты с осмысленными названиями на основе года
        List<StatItem> testExhibits = Arrays.asList(
                new StatItem(1, "EXH-6", "Древняя ваза (1604)", "Самая старая находка в коллекции", 3200),
                new StatItem(2, "EXH-15", "Золотой кубок (1678)", "Отлично сохранившийся артефакт", 2850),
                new StatItem(3, "EXH-30", "Ренессансная картина (1648)", "Работа неизвестного мастера", 2650),
                new StatItem(4, "EXH-3", "Барокко скульптура (1750)", "Мраморная композиция", 2450),
                new StatItem(5, "EXH-19", "Портрет эпохи (1650)", "Масло на холсте", 2250),
                new StatItem(6, "EXH-4", "Гравюра (1684)", "Редкая печатная работа", 2050),
                new StatItem(7, "EXH-23", "Миниатюра (1633)", "Ручная роспись", 1850),
                new StatItem(8, "EXH-1", "Классицизм (1821)", "Академическая живопись", 1650)
        );

        // Коллекции с тематическими названиями
        List<StatItem> testCollections = Arrays.asList(
                new StatItem(1, "COL-1", "Древности", "Артефакты до XVIII века", 3100),
                new StatItem(2, "COL-2", "Классическое искусство", "XVIII-XIX века", 2800),
                new StatItem(3, "COL-3", "Модерн", "Рубеж XIX-XX веков", 2550),
                new StatItem(4, "COL-4", "Современное", "XX-XXI века", 2300),
                new StatItem(5, "COL-5", "Этнография", "Народное искусство", 2050),
                new StatItem(6, "COL-6", "Нумизматика", "Монеты и медали", 1800),
                new StatItem(7, "COL-7", "Фотография", "Исторические снимки", 1550),
                new StatItem(8, "COL-8", "Графика", "Рисунки и эстампы", 1300)
        );

        // Залы с описаниями
        List<StatItem> testHalls = Arrays.asList(
                new StatItem(1, "HALL-1", "Исторический зал", "Постоянная экспозиция", 3350),
                new StatItem(2, "HALL-2", "Зал искусств", "Живопись и скульптура", 3050),
                new StatItem(3, "HALL-3", "Современный зал", "Инсталляции и видеоарт", 2750),
                new StatItem(4, "HALL-4", "Детский зал", "Интерактивные экспонаты", 2500),
                new StatItem(5, "HALL-5", "Временные выставки", "Сменные экспозиции", 2250),
                new StatItem(6, "HALL-6", "Мультимедиа", "Цифровые проекции", 2000),
                new StatItem(7, "HALL-7", "Образовательный", "Лекции и мастер-классы", 1750),
                new StatItem(8, "HALL-8", "Камерный зал", "Малые формы", 1500)
        );

        // Авторы с "реальными" именами
        List<StatItem> testAuthors = Arrays.asList(
                new StatItem(1, "AUTH-3", "Иванов А.И.", "Мастер исторической живописи", 4150),
                new StatItem(2, "AUTH-12", "Петрова М.К.", "Современный скульптор", 3850),
                new StatItem(3, "AUTH-25", "Сидоров П.В.", "Фотограф-документалист", 3550),
                new StatItem(4, "AUTH-7", "Кузнецова Е.С.", "Художник-график", 3250),
                new StatItem(5, "AUTH-18", "Васильев Д.Н.", "Иллюстратор детских книг", 2950),
                new StatItem(6, "AUTH-41", "Морозова А.П.", "Керамист", 2650),
                new StatItem(7, "AUTH-10", "Николаев Г.Р.", "Миниатюрист", 2350),
                new StatItem(8, "AUTH-33", "Орлова Л.М.", "Текстильный художник", 2050)
        );

        setAllStats(testExhibits, testCollections, testHalls, testAuthors);

        if (modeToggleGroup != null && !modeToggleGroup.getToggles().isEmpty()) {
            modeToggleGroup.getToggles().get(0).setSelected(true);
            showStatistics(testExhibits, "Экспонаты");
        }
    }

    public VBox getContent() {
        return content;
    }
}