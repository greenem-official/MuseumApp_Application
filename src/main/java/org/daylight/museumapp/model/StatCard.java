package org.daylight.museumapp.model;

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

    public String getTitle() {
        return title;
    }

    public String getCount() {
        return count;
    }

    public String getLink() {
        return link;
    }

    public String getIcon() {
        return icon;
    }
}
