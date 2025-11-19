package org.daylight.museumapp;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.daylight.museumapp.components.layout.AppLayout;
import org.daylight.museumapp.util.Icons;
import org.daylight.museumapp.model.NavigationItem;
import org.daylight.museumapp.model.StatCard;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class MuseumApp extends Application {
    public static final Logger LOGGER = Logger.getLogger(MuseumApp.class.getName());

    @Override
    public void start(Stage primaryStage) {
        AppLayout appLayout = new AppLayout();
        Scene scene = new Scene(appLayout.getRoot(), 1400, 900);

        // Загрузка CSS
        try {
            String cssPath = getClass().getResource("/styles/museum-light.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("CSS not found, using default styles");
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("Информационная система музея");
        primaryStage.show();
    }
}
