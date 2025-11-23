package org.daylight.museumapp.components.common;

public class SeparateStyles {
    public static String tablesCss;

    public static void init() {
        try {
            tablesCss = SeparateStyles.class.getResource("/styles/tables.css").toExternalForm();
        } catch (Exception e) {
            System.err.println("CSS not found, using default styles");
        }
    }
}
