package model;

import java.util.Objects;

public class Bookmark {
    private String title;
    private String url;

    public Bookmark(String title, String url) {
        this.title = (title == null || title.isBlank()) ? url : title;
        this.url = url;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    @Override
    public String toString() { return "⭐  " + title; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bookmark b)) return false;
        return Objects.equals(url, b.url);
    }

    @Override
    public int hashCode() { return Objects.hash(url); }
}
