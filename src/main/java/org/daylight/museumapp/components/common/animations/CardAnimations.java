package org.daylight.museumapp.components.common.animations;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class CardAnimations {
    public static void setupCardAnimations(Node card, boolean scale) {
        // Сохраняем оригинальный масштаб
        card.setScaleX(1.0);
        card.setScaleY(1.0);

        // Анимация при наведении
        card.setOnMouseEntered(e -> {
            if(scale) {
                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
                scaleUp.setToX(1.02);
                scaleUp.setToY(1.02);
                scaleUp.play();
            }

            // Также анимируем тень
            card.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.15)));
        });

        // Анимация при уходе мыши
        card.setOnMouseExited(e -> {
            if(scale) {
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);
                scaleDown.play();
            }

            // Возвращаем оригинальную тень
            card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.08)));
        });
    }
}