package model;

import java.util.ArrayList;

public class History {

    private ArrayList<String> urls;
    private int currentIndex;

    public History() {
        urls = new ArrayList<>();
        currentIndex = -1;
    }

    public void addUrl(String url) {
        // If we go back, then visit a new page,
        // remove the "forward" history
        while (urls.size() > currentIndex + 1) {
            urls.remove(urls.size() - 1);
        }

        urls.add(url);
        currentIndex++;
    }

    public boolean canGoBack() {
        return currentIndex > 0;
    }

    public boolean canGoForward() {
        return currentIndex < urls.size() - 1;
    }

    public String goBack() {
        if (canGoBack()) {
            currentIndex--;
            return urls.get(currentIndex);
        }

        return null;
    }

    public String goForward() {
        if (canGoForward()) {
            currentIndex++;
            return urls.get(currentIndex);
        }

        return null;
    }

    public String getCurrentUrl() {
        if (currentIndex >= 0 && currentIndex < urls.size()) {
            return urls.get(currentIndex);
        }

        return null;
    }

    public ArrayList<String> getAllUrls() {
        return urls;
    }
}