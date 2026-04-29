package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

public class BrowserController {

    @FXML
    private TextField addressBar;

    @FXML
    private Button goButton;

    @FXML
    private Button backButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button refreshButton;

    @FXML
    private WebView webView;

    private WebEngine webEngine;

    @FXML
    public void initialize() {
        webEngine = webView.getEngine();

        // Page d'accueil
        webEngine.load("https://www.google.com");

        // Update address bar when page changes
        webEngine.locationProperty().addListener((obs, oldUrl, newUrl) -> {
            addressBar.setText(newUrl);
            updateButtons();
        });

        updateButtons();
    }

    @FXML
    private void goToPage() {
        String url = addressBar.getText();

        if (url == null || url.trim().isEmpty()) {
            return;
        }

        url = url.trim();

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        webEngine.load(url);
    }

    @FXML
    private void goBack() {
        WebHistory history = webEngine.getHistory();
        int currentIndex = history.getCurrentIndex();

        if (currentIndex > 0) {
            history.go(-1);
        }

        updateButtons();
    }

    @FXML
    private void goForward() {
        WebHistory history = webEngine.getHistory();
        int currentIndex = history.getCurrentIndex();

        if (currentIndex < history.getEntries().size() - 1) {
            history.go(1);
        }

        updateButtons();
    }

    @FXML
    private void refreshPage() {
        webEngine.reload();
    }

    private void updateButtons() {
        WebHistory history = webEngine.getHistory();
        int currentIndex = history.getCurrentIndex();

        backButton.setDisable(currentIndex <= 0);
        forwardButton.setDisable(currentIndex >= history.getEntries().size() - 1);
    }
}