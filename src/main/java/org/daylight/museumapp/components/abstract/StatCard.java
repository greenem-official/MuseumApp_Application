package org.daylight.museumapp.museumapp;

public class StatCard {
    private final String title;
    private final String count;
    private final String link;
    private final String icon;

    public StatCard(String title, String count, String link, String icon) {
        this.title = title;
        this.count = count;
        this.link = link;
        this.icon = icon;
    }

    public String title() { return title; }
    public String count() { return count; }
    public String link() { return link; }
    public String icon() { return icon; }
}
