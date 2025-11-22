package org.daylight.museumapp.components.loading;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class LoadingDots {
    private HBox dotsContainer;
    private Label dot1, dot2, dot3;
    private Timeline animation;

    public LoadingDots() {
        initializeDots();
        setupAnimation();
    }

    private void initializeDots() {
        dotsContainer = new HBox(8);
        dotsContainer.setAlignment(Pos.CENTER);

        dot1 = createDot();
        dot2 = createDot();
        dot3 = createDot();

        dotsContainer.getChildren().addAll(dot1, dot2, dot3);
    }

    private Label createDot() {
        Label dot = new Label("•");
        dot.setStyle("-fx-font-size: 32px; -fx-text-fill: #b3592d; -fx-opacity: 0.3;");
        return dot;
    }

    private void setupAnimation() {
        animation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dot1.opacityProperty(), 0.3),
                        new KeyValue(dot2.opacityProperty(), 0.3),
                        new KeyValue(dot3.opacityProperty(), 0.3)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(dot1.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(dot1.opacityProperty(), 0.3),
                        new KeyValue(dot2.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(900),
                        new KeyValue(dot2.opacityProperty(), 0.3),
                        new KeyValue(dot3.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(1200),
                        new KeyValue(dot3.opacityProperty(), 0.3)
                )
        );

        animation.setCycleCount(Timeline.INDEFINITE);
    }

    public void startAnimation() {
        animation.play();
    }

    public void stopAnimation() {
        animation.stop();
    }

    public HBox getDots() {
        return dotsContainer;
    }
}
