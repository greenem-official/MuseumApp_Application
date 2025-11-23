package org.daylight.museumapp.components.table;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

@Deprecated
public class LoadingOverlay extends StackPane {
    private final ProgressIndicator progress = new ProgressIndicator();

    public LoadingOverlay() {
        this.getStyleClass().add("museum-ld-loading");
        progress.setMaxSize(64, 64);
        this.getChildren().add(progress);
        this.setAlignment(Pos.CENTER);
        this.setVisible(false);
        this.setManaged(false);

        // bind visibility to managed so overlay doesn't take space
        this.visibleProperty().addListener((obs, oldV, newV) -> this.setManaged(newV));
    }
}
