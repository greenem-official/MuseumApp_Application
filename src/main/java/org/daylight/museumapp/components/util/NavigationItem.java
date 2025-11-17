package org.daylight.museumapp.components.util;

public class NavigationItem {
    private final String title;
    private final String path;
    private final String icon;

    public NavigationItem(String title, String path, String icon) {
        this.title = title;
        this.path = path;
        this.icon = icon;
    }

    public String title() { return title; }
    public String path() { return path; }
    public String icon() { return icon; }
}
