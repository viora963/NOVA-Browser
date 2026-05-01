package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoryEntry {
    private final String url;
    private final String title;
    private final LocalDateTime visitedAt;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM");

    public HistoryEntry(String url, String title) {
        this.url = url;
        this.title = (title == null || title.isBlank()) ? url : title;
        this.visitedAt = LocalDateTime.now();
    }

    public String getUrl() { return url; }
    public String getTitle() { return title; }
    public LocalDateTime getVisitedAt() { return visitedAt; }
    public String getFormattedTime() { return visitedAt.format(FMT); }
    public String getFormattedDate() { return visitedAt.format(FMT_DATE); }

    @Override
    public String toString() {
        return getFormattedTime() + "  ·  " + title;
    }
}
