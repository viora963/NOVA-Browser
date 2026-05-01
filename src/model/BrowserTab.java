package model;

import javafx.beans.property.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class BrowserTab {

    private final StringProperty title = new SimpleStringProperty("New Tab");
    private final StringProperty url = new SimpleStringProperty("");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final DoubleProperty loadProgress = new SimpleDoubleProperty(0.0);

    private final Deque<String> backStack = new ArrayDeque<>();
    private final Deque<String> forwardStack = new ArrayDeque<>();

    private final BooleanProperty canGoBack = new SimpleBooleanProperty(false);
    private final BooleanProperty canGoForward = new SimpleBooleanProperty(false);

    private String currentUrl = "";

    public StringProperty titleProperty() { return title; }
    public StringProperty urlProperty() { return url; }
    public BooleanProperty loadingProperty() { return loading; }
    public DoubleProperty loadProgressProperty() { return loadProgress; }
    public BooleanProperty canGoBackProperty() { return canGoBack; }
    public BooleanProperty canGoForwardProperty() { return canGoForward; }

    public String getTitle() { return title.get(); }
    public void setTitle(String t) { title.set(t); }

    public String getUrl() { return url.get(); }
    public void setUrl(String u) { url.set(u); }

    public boolean isLoading() { return loading.get(); }
    public void setLoading(boolean l) { loading.set(l); }

    public double getLoadProgress() { return loadProgress.get(); }
    public void setLoadProgress(double p) { loadProgress.set(p); }

    public void navigateTo(String newUrl) {
        if (!currentUrl.isEmpty()) {
            backStack.push(currentUrl);
            canGoBack.set(true);
        }
        forwardStack.clear();
        canGoForward.set(false);
        currentUrl = newUrl;
        url.set(newUrl);
    }

    public String goBack() {
        if (!backStack.isEmpty()) {
            forwardStack.push(currentUrl);
            canGoForward.set(true);
            currentUrl = backStack.pop();
            canGoBack.set(!backStack.isEmpty());
            url.set(currentUrl);
            return currentUrl;
        }
        return null;
    }

    public String goForward() {
        if (!forwardStack.isEmpty()) {
            backStack.push(currentUrl);
            canGoBack.set(true);
            currentUrl = forwardStack.pop();
            canGoForward.set(!forwardStack.isEmpty());
            url.set(currentUrl);
            return currentUrl;
        }
        return null;
    }

    public String getCurrentUrl() { return currentUrl; }
}
