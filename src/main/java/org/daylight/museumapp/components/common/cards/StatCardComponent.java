package org.daylight.museumapp.components.common.cards;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.daylight.museumapp.components.common.animations.CardAnimations;
import org.daylight.museumapp.model.StatCard;
import org.daylight.museumapp.services.NavigationService;

public class StatCardComponent {
    private VBox card;

    public StatCardComponent(StatCard statCard) {
        initializeCard(statCard);
    }

    private void initializeCard(StatCard statCard) {
        card = new VBox();
        card.getStyleClass().addAll("card", "stat-card");

        // Настройка анимации
        CardAnimations.setupCardAnimations(card, true);

        // Обработчик клика
        card.setOnMouseClicked(e -> {
            NavigationService navigationService = NavigationService.getInstance();
            navigationService.navigateTo(statCard.getLink());
        });

        // Header с иконкой
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 16, 8, 16));

        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().add("icon-container");

        Label icon = new Label(statCard.getIcon());
        icon.getStyleClass().add("stat-icon");

        Label title = new Label(statCard.getTitle());
        title.getStyleClass().add("card-title");

        header.getChildren().addAll(iconContainer, title);
        iconContainer.getChildren().add(icon);

        // Content с числом
        VBox content = new VBox();
        content.setPadding(new Insets(0, 16, 16, 16));

        Label count = new Label(statCard.getCount());
        count.getStyleClass().add("stat-count");

        content.getChildren().add(count);
        card.getChildren().addAll(header, content);
    }

    public StackPane getContent() {
        StackPane container = new StackPane(card);
        container.getStyleClass().add("card-container");
        return container;
    }
}
