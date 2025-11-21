package org.daylight.museumapp.components.auth;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;

public class AuthTabPane {
    private HBox tabContainer;
    private Button loginTab;
    private Button registerTab;
    private AuthFormListener listener;

    public interface AuthFormListener {
        void onTabChanged(String tabType);
    }

    public AuthTabPane(AuthFormListener listener) {
        this.listener = listener;
        initializeTabs();
    }

    private void initializeTabs() {
        tabContainer = new HBox(0);
        tabContainer.setMaxWidth(Double.MAX_VALUE);
        tabContainer.setMinHeight(40);
        tabContainer.setMaxHeight(40);
        tabContainer.setAlignment(Pos.CENTER);

        loginTab = createTab("Вход", true, true);
        registerTab = createTab("Регистрация", false, false);

        loginTab.setMaxWidth(Double.MAX_VALUE);
        registerTab.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(loginTab, Priority.ALWAYS);
        HBox.setHgrow(registerTab, Priority.ALWAYS);

        tabContainer.getChildren().addAll(loginTab, registerTab);
    }

    private Button createTab(String text, boolean isLogin, boolean isActive) {
        Button tab = new Button(text);
        tab.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tab.setPrefHeight(44);
        HBox.setHgrow(tab, Priority.ALWAYS);

        // Центрируем текст
        tab.setAlignment(Pos.CENTER);

        updateTabStyle(tab, isActive);

        tab.setOnAction(e -> switchTab(isLogin ? "login" : "register"));

        return tab;
    }

    private void switchTab(String tabType) {
        boolean isLogin = "login".equals(tabType);

        updateTabStyle(loginTab, isLogin);
        updateTabStyle(registerTab, !isLogin);

        if (listener != null) {
            listener.onTabChanged(isLogin ? "login" : "register");
        }
    }

    private void updateTabStyle(Button tab, boolean isActive) {
        tab.getStyleClass().clear();
        tab.getStyleClass().add(isActive ? "auth-tab-active" : "auth-tab-inactive");
    }

    public HBox getTabs() {
        return tabContainer;
    }

    public void setActiveTab(String tabType) {
        switchTab(tabType);
    }
}
