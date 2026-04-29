package controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class BrowserController {

    @FXML
    private TextField addressBar;

    @FXML
    private TextField searchField;

    @FXML
    private Button goButton;

    @FXML
    private Button backButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button bookmarkButton;

    @FXML
    private Label statusLabel;

    @FXML
    private ListView<String> historyList;

    @FXML
    private ListView<String> favoritesList;

    @FXML
    private WebView webView;

    private WebEngine webEngine;
    private final ObservableList<String> allHistory = FXCollections.observableArrayList();
    private final ObservableList<String> filteredHistory = FXCollections.observableArrayList();
    private final ObservableList<String> favorites = FXCollections.observableArrayList();
    private final Set<String> favoriteIndex = new HashSet<>();

    @FXML
    public void initialize() {
        webEngine = webView.getEngine();

        historyList.setItems(filteredHistory);
        favoritesList.setItems(favorites);

        historyList.setOnMouseClicked(event -> {
            String selection = historyList.getSelectionModel().getSelectedItem();
            if (selection != null) {
                webEngine.load(selection);
            }
        });

        favoritesList.setOnMouseClicked(event -> {
            String selection = favoritesList.getSelectionModel().getSelectedItem();
            if (selection != null) {
                webEngine.load(selection);
            }
        });

        searchField.textProperty().addListener((obs, oldValue, newValue) -> filterHistory(newValue));

        // Aura homepage using Google as search backend.
        webEngine.load(buildAuraHomePageUrl(""));

        webEngine.locationProperty().addListener((obs, oldUrl, newUrl) -> {
            addressBar.setText(newUrl);
            addToHistory(newUrl);
            updateButtons();
            updateBookmarkState();
        });

        statusLabel.textProperty().bind(Bindings.concat("History: ", Bindings.size(allHistory), " • Favorites: ", Bindings.size(favorites)));
        updateButtons();
        updateBookmarkState();
    }

    @FXML
    private void goToPage() {
        String input = addressBar.getText();
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        webEngine.load(normalizeInputToUrl(input));
    }

    @FXML
    private void searchWeb() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        webEngine.load(buildAuraHomePageUrl(query));
    }

    @FXML
    private void goBack() {
        WebHistory history = webEngine.getHistory();
        if (history.getCurrentIndex() > 0) {
            history.go(-1);
        }
        updateButtons();
    }

    @FXML
    private void goForward() {
        WebHistory history = webEngine.getHistory();
        if (history.getCurrentIndex() < history.getEntries().size() - 1) {
            history.go(1);
        }
        updateButtons();
    }

    @FXML
    private void refreshPage() {
        webEngine.reload();
    }

    @FXML
    private void loadAuraHome() {
        webEngine.load(buildAuraHomePageUrl(""));
    }

    @FXML
    private void toggleFavorite() {
        String currentUrl = webEngine.getLocation();
        if (currentUrl == null || currentUrl.isBlank()) {
            return;
        }

        if (favoriteIndex.contains(currentUrl)) {
            favorites.remove(currentUrl);
            favoriteIndex.remove(currentUrl);
        } else {
            favorites.add(0, currentUrl);
            favoriteIndex.add(currentUrl);
        }
        updateBookmarkState();
    }

    private void addToHistory(String url) {
        if (url == null || url.isBlank()) {
            return;
        }
        if (allHistory.isEmpty() || !allHistory.get(0).equals(url)) {
            allHistory.add(0, url);
            filterHistory(searchField.getText());
        }
    }

    private void filterHistory(String query) {
        filteredHistory.clear();
        String normalized = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        if (normalized.isEmpty()) {
            filteredHistory.addAll(allHistory);
            return;
        }

        for (String entry : allHistory) {
            if (entry.toLowerCase(Locale.ROOT).contains(normalized)) {
                filteredHistory.add(entry);
            }
        }
    }

    private String normalizeInputToUrl(String input) {
        String trimmed = input.trim();

        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }

        if (trimmed.contains(" ")) {
            return buildAuraHomePageUrl(trimmed);
        }

        return "https://" + trimmed;
    }

    private String buildAuraHomePageUrl(String query) {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return "https://www.google.com/search?q=" + encoded;
    }

    private void updateBookmarkState() {
        String currentUrl = webEngine == null ? "" : webEngine.getLocation();
        boolean isFavorite = currentUrl != null && favoriteIndex.contains(currentUrl);
        bookmarkButton.setText(isFavorite ? "★" : "☆");
    }

    private void updateButtons() {
        WebHistory history = webEngine.getHistory();
        int currentIndex = history.getCurrentIndex();

        backButton.setDisable(currentIndex <= 0);
        forwardButton.setDisable(currentIndex >= history.getEntries().size() - 1);
    }
}
