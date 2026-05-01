package controller;

import model.Bookmark;
import model.HistoryEntry;
import service.GeminiClient;
import service.Settings;
import util.UrlUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BrowserController implements Initializable {

    // ───────────── UI ─────────────
    @FXML private Button btnBack;
    @FXML private Button btnForward;
    @FXML private Button btnRefresh;
    @FXML private Button btnHome;
    @FXML private TextField urlBar;
    @FXML private Button btnGo;
    @FXML private Button btnBookmark;
    @FXML private Button btnAi;
    @FXML private Button btnSettings;
    @FXML private Label lblSecurity;

    @FXML private TabPane tabPane;
    @FXML private Button btnNewTab;

    @FXML private Label lblStatus;
    @FXML private ProgressBar progressBar;

    @FXML private VBox sidebar;
    @FXML private ListView<Bookmark> bookmarkList;
    @FXML private ListView<HistoryEntry> historyList;
    @FXML private Button btnToggleSidebar;
    @FXML private Button btnRemoveBookmark;
    @FXML private Button btnClearHistory;

    // AI panel
    @FXML private VBox aiPanel;
    @FXML private Label aiSubtitle;
    @FXML private VBox aiMessages;
    @FXML private ScrollPane aiScroll;
    @FXML private TextField aiInput;
    @FXML private Button btnAiSend;
    @FXML private Button btnSummarize;
    @FXML private Button btnExplain;
    @FXML private Button btnClearChat;

    // ───────────── DATA ─────────────
    private final ObservableList<Bookmark> bookmarks = FXCollections.observableArrayList();
    private final ObservableList<HistoryEntry> history = FXCollections.observableArrayList();

    // AI services
    private final Settings settings = new Settings();
    private final GeminiClient gemini = new GeminiClient();
    private final List<GeminiClient.Message> chatHistory = new ArrayList<>();

    private static final String HOME = "home";

    // ───────────── HOME PAGE ─────────────
    private static final String HOME_PAGE = """
    <!DOCTYPE html>
    <html lang="en">
    <head>
    <meta charset="UTF-8">
    <title>NOVA — Home</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Segoe UI', 'Inter', -apple-system, sans-serif;
            background:
                radial-gradient(circle at 15% 20%, rgba(168,85,247,0.18), transparent 50%),
                radial-gradient(circle at 85% 80%, rgba(56,189,248,0.15), transparent 50%),
                radial-gradient(circle at 50% 50%, rgba(236,72,153,0.08), transparent 60%),
                #0a0a14;
            color: #e6edf3;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 40px 20px;
            overflow: hidden;
            position: relative;
        }

        body::before {
            content: '';
            position: fixed;
            inset: 0;
            background-image:
                radial-gradient(1px 1px at 20% 30%, rgba(255,255,255,0.5), transparent),
                radial-gradient(1px 1px at 60% 70%, rgba(255,255,255,0.4), transparent),
                radial-gradient(1px 1px at 80% 20%, rgba(255,255,255,0.6), transparent),
                radial-gradient(1px 1px at 30% 80%, rgba(255,255,255,0.3), transparent),
                radial-gradient(1px 1px at 90% 50%, rgba(255,255,255,0.5), transparent),
                radial-gradient(1px 1px at 10% 60%, rgba(255,255,255,0.4), transparent);
            pointer-events: none;
            opacity: 0.6;
        }

        .logo {
            display: flex;
            align-items: center;
            gap: 18px;
            margin-bottom: 12px;
            position: relative;
            z-index: 1;
        }

        .logo-icon {
            width: 110px;
            height: 110px;
            display: flex;
            align-items: center;
            justify-content: center;
            animation: pulse 3s ease-in-out infinite;
            filter: drop-shadow(0 0 30px rgba(168,85,247,0.5)) drop-shadow(0 8px 24px rgba(236,72,153,0.3));
        }

        @keyframes pulse {
            0%, 100% { transform: scale(1); }
            50%      { transform: scale(1.04); }
        }

        .logo-icon svg {
            width: 110px;
            height: 110px;
        }

        h1 {
            font-size: 56px;
            font-weight: 800;
            letter-spacing: 6px;
            background: linear-gradient(135deg, #ffffff 0%, #a855f7 50%, #38bdf8 100%);
            -webkit-background-clip: text;
            background-clip: text;
            color: transparent;
        }

        .tagline {
            color: #8b949e;
            font-size: 14px;
            letter-spacing: 2px;
            text-transform: uppercase;
            margin-bottom: 12px;
            position: relative;
            z-index: 1;
        }

        .ai-badge {
            background: linear-gradient(135deg, rgba(236,72,153,0.2), rgba(168,85,247,0.2));
            color: #f472b6;
            border: 1px solid rgba(236,72,153,0.4);
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 600;
            letter-spacing: 1.5px;
            text-transform: uppercase;
            margin-bottom: 36px;
            position: relative;
            z-index: 1;
        }

        .search-box {
            display: flex;
            width: 100%;
            max-width: 640px;
            background: rgba(22, 27, 34, 0.7);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(168,85,247,0.3);
            border-radius: 28px;
            padding: 6px;
            transition: all 0.3s ease;
            box-shadow: 0 8px 32px rgba(0,0,0,0.4);
            position: relative;
            z-index: 1;
        }

        .search-box:focus-within {
            border-color: #a855f7;
            box-shadow: 0 0 0 4px rgba(168,85,247,0.2), 0 8px 32px rgba(0,0,0,0.4);
        }

        .search-box input {
            flex: 1;
            background: transparent;
            border: none;
            outline: none;
            color: #e6edf3;
            font-size: 15px;
            padding: 12px 18px;
        }

        .search-box input::placeholder { color: #6e7681; }

        .search-box button {
            background: linear-gradient(135deg, #a855f7, #ec4899);
            color: white;
            border: none;
            border-radius: 22px;
            padding: 0 28px;
            font-size: 14px;
            font-weight: 600;
            letter-spacing: 0.5px;
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .search-box button:hover {
            background: linear-gradient(135deg, #c084fc, #f472b6);
            transform: translateY(-1px);
            box-shadow: 0 4px 16px rgba(168,85,247,0.4);
        }

        .shortcuts {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
            gap: 14px;
            margin-top: 48px;
            width: 100%;
            max-width: 640px;
            position: relative;
            z-index: 1;
        }

        .shortcut {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 10px;
            padding: 18px 12px;
            background: rgba(22, 27, 34, 0.6);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255,255,255,0.06);
            border-radius: 14px;
            text-decoration: none;
            color: #c9d1d9;
            font-size: 13px;
            transition: all 0.25s ease;
            cursor: pointer;
        }

        .shortcut:hover {
            background: rgba(168,85,247,0.1);
            border-color: rgba(168,85,247,0.4);
            transform: translateY(-3px);
            box-shadow: 0 8px 24px rgba(168,85,247,0.2);
        }

        .shortcut-icon {
            width: 38px;
            height: 38px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 18px;
            font-weight: bold;
            color: white;
        }

        .footer {
            margin-top: 60px;
            color: #484f58;
            font-size: 11px;
            letter-spacing: 1.5px;
            text-transform: uppercase;
            position: relative;
            z-index: 1;
        }
    </style>
    </head>

    <body>

    <div class="logo">
        <div class="logo-icon">
            <svg viewBox="-100 -100 200 200" xmlns="http://www.w3.org/2000/svg">
                <defs>
                    <linearGradient id="g1" x1="0%" y1="0%" x2="100%" y2="100%">
                        <stop offset="0%" stop-color="#a855f7"/>
                        <stop offset="50%" stop-color="#ec4899"/>
                        <stop offset="100%" stop-color="#38bdf8"/>
                    </linearGradient>
                    <linearGradient id="g2" x1="0%" y1="0%" x2="100%" y2="100%">
                        <stop offset="0%" stop-color="#c084fc"/>
                        <stop offset="100%" stop-color="#f472b6"/>
                    </linearGradient>
                    <linearGradient id="comet" x1="0%" y1="0%" x2="100%" y2="0%">
                        <stop offset="0%" stop-color="#38bdf8" stop-opacity="0"/>
                        <stop offset="60%" stop-color="#ec4899" stop-opacity="0.6"/>
                        <stop offset="100%" stop-color="#a855f7" stop-opacity="1"/>
                    </linearGradient>
                    <radialGradient id="halo" cx="50%" cy="50%" r="50%">
                        <stop offset="0%" stop-color="#ec4899" stop-opacity="0.55"/>
                        <stop offset="100%" stop-color="#ec4899" stop-opacity="0"/>
                    </radialGradient>
                </defs>
                <circle r="95" fill="url(#halo)"/>
                <g transform="rotate(-25)">
                    <ellipse rx="78" ry="32" fill="none" stroke="url(#g1)" stroke-width="2.5" opacity="0.95"/>
                </g>
                <g transform="rotate(35)">
                    <ellipse rx="92" ry="22" fill="none" stroke="url(#g2)" stroke-width="1.8" opacity="0.7"/>
                </g>
                <g transform="rotate(-25)">
                    <path d="M -78 0 A 78 32 0 0 1 -10 -28" fill="none" stroke="url(#comet)" stroke-width="4" stroke-linecap="round"/>
                    <circle cx="-10" cy="-28" r="5" fill="#a855f7"/>
                    <circle cx="-10" cy="-28" r="9" fill="#a855f7" opacity="0.35"/>
                </g>
                <g transform="translate(8, -4)">
                    <polygon points="0,-32 5,-6 32,0 5,6 0,32 -5,6 -32,0 -5,-6" fill="url(#g1)"/>
                    <circle r="4" fill="white"/>
                </g>
                <circle cx="62" cy="34" r="3" fill="#f472b6"/>
                <circle cx="62" cy="34" r="6" fill="#f472b6" opacity="0.3"/>
                <polygon points="-50,46 -49,49 -46,50 -49,51 -50,54 -51,51 -54,50 -51,49" fill="white" opacity="0.9"/>
                <circle cx="80" cy="-50" r="1.5" fill="white" opacity="0.7"/>
                <circle cx="-70" cy="-30" r="1" fill="#38bdf8" opacity="0.8"/>
            </svg>
        </div>
        <h1>NOVA</h1>
    </div>
    <p class="tagline">Browse the universe</p>
    <div class="ai-badge">✨ Powered by Gemini AI</div>

    <div class="search-box">
        <input id="q" type="text" placeholder="Search or enter a URL..." autofocus>
        <button onclick="go()">Search</button>
    </div>

    <div class="shortcuts">
        <a class="shortcut" onclick="visit('https://www.google.com')">
            <div class="shortcut-icon" style="background:#4285f4">G</div>
            Google
        </a>
        <a class="shortcut" onclick="visit('https://www.youtube.com')">
            <div class="shortcut-icon" style="background:#ff0000">▶</div>
            YouTube
        </a>
        <a class="shortcut" onclick="visit('https://github.com')">
            <div class="shortcut-icon" style="background:#24292f">GH</div>
            GitHub
        </a>
        <a class="shortcut" onclick="visit('https://en.wikipedia.org')">
            <div class="shortcut-icon" style="background:#636363">W</div>
            Wikipedia
        </a>
        <a class="shortcut" onclick="visit('https://stackoverflow.com')">
            <div class="shortcut-icon" style="background:#f48024">SO</div>
            StackOverflow
        </a>
    </div>

    <p class="footer">NOVA · Press Enter to launch</p>

    <script>
        function go() {
            const q = document.getElementById("q").value.trim();
            if (!q) return;
            if (q.startsWith("http://") || q.startsWith("https://")) {
                window.location.href = q;
            } else if (q.includes(".") && !q.includes(" ")) {
                window.location.href = "https://" + q;
            } else {
                window.location.href = "https://duckduckgo.com/?q=" + encodeURIComponent(q);
            }
        }
        function visit(url) { window.location.href = url; }
        document.getElementById("q").addEventListener("keydown", e => {
            if (e.key === "Enter") go();
        });
    </script>

    </body>
    </html>
    """;

    // ───────────── VIDEO SITES (don't work in JavaFX WebView) ─────────────
    private static final String[] VIDEO_SITES = {
        "youtube.com", "youtu.be", "vimeo.com", "dailymotion.com",
        "twitch.tv", "netflix.com", "tiktok.com", "facebook.com/watch",
        "instagram.com/reel", "instagram.com/tv"
    };

    // ───────────── INIT ─────────────
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSidebar();
        setupSidebarInteractions();
        setupTabCloseHandler();
        setupAiPanel();
        createNewTab(HOME);
    }

    /** When the last tab is closed, exit the application entirely. */
    private void setupTabCloseHandler() {
        tabPane.getTabs().addListener((ListChangeListener<Tab>) change -> {
            if (tabPane.getTabs().isEmpty()) {
                Platform.exit();
            }
        });
    }

    private void setupAiPanel() {
        aiPanel.setVisible(false);
        aiPanel.setManaged(false);
        showEmptyAiHint();

        aiMessages.heightProperty().addListener((obs, o, n) -> aiScroll.setVvalue(1.0));

        // Just make the arrow pink — much cleaner than fighting JavaFX's button background.
        btnAiSend.setStyle(
            "-fx-text-fill: #ec4899;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );
        btnAiSend.setOnMouseEntered(e -> {
            if (!btnAiSend.isDisabled()) {
                btnAiSend.setStyle(
                    "-fx-text-fill: #f472b6;" +
                    "-fx-font-size: 20px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;"
                );
            }
        });
        btnAiSend.setOnMouseExited(e -> {
            if (!btnAiSend.isDisabled()) {
                btnAiSend.setStyle(
                    "-fx-text-fill: #ec4899;" +
                    "-fx-font-size: 20px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;"
                );
            }
        });
    }

    private void showEmptyAiHint() {
        aiMessages.getChildren().clear();
        Label hint = new Label(
            "👋 Hi! I'm Gemini.\n\n" +
            "Ask me anything, or use the buttons above to summarize\n" +
            "or explain the page you're browsing."
        );
        hint.getStyleClass().add("ai-empty-hint");
        hint.setWrapText(true);
        VBox.setVgrow(hint, Priority.NEVER);
        aiMessages.getChildren().add(hint);
    }

    // ───────────── TABS ─────────────
    @FXML
    private void onNewTab() {
        createNewTab(HOME);
    }

    private void createNewTab(String url) {

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        engine.setUserAgent(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/124.0.0.0 Safari/537.36"
        );
        engine.setJavaScriptEnabled(true);

        Tab tab = new Tab("New Tab");
        tab.setContent(webView);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);

        engine.locationProperty().addListener((obs, o, newLoc) -> {
            if (isActive(tab)) {
                if (newLoc == null || newLoc.isBlank() || newLoc.equals("about:blank")) {
                    urlBar.setText("");
                } else {
                    urlBar.setText(newLoc);
                }
                updateSecurity(newLoc);
                updateBookmarkButtonState(newLoc);
                updateAiSubtitle(newLoc);
            }
        });

        engine.titleProperty().addListener((obs, o, t) -> {
            if (t != null && !t.isBlank()) {
                tab.setText(truncate(t, 22));
            }
        });

        engine.getLoadWorker().stateProperty().addListener((obs, o, s) -> {
            boolean loading = s == Worker.State.RUNNING;

            if (isActive(tab)) {
                progressBar.setVisible(loading);
                lblStatus.setText(loading ? "Loading..." : "Ready");
            }

            if (s == Worker.State.SUCCEEDED) {
                String pageUrl = engine.getLocation();
                String pageTitle = engine.getTitle();
                addHistoryEntry(pageUrl, pageTitle);
            }
        });

        engine.getLoadWorker().progressProperty().addListener((obs, o, p) -> {
            if (isActive(tab)) {
                progressBar.setProgress(p.doubleValue());
            }
        });

        tab.setUserData(engine);

        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) {
                String loc = engine.getLocation();
                urlBar.setText(loc == null || loc.equals("about:blank") ? "" : loc);
                updateSecurity(loc);
                updateBookmarkButtonState(loc);
                updateAiSubtitle(loc);
            }
        });

        if (url == null || url.equals(HOME)) {
            engine.loadContent(HOME_PAGE);
        } else {
            engine.load(url);
        }
    }

    // ───────────── NAVIGATION ─────────────
    @FXML
    private void onGo() {
        navigate(urlBar.getText());
    }

    @FXML
    private void onHome() {
        getEngine().ifPresent(e -> e.loadContent(HOME_PAGE));
        urlBar.setText("");
    }

    @FXML
    private void onRefresh() {
        getEngine().ifPresent(WebEngine::reload);
    }

    @FXML
    private void onBack() {
        getEngine().ifPresent(e -> e.executeScript("history.back()"));
    }

    @FXML
    private void onForward() {
        getEngine().ifPresent(e -> e.executeScript("history.forward()"));
    }

    private void navigate(String raw) {
        String url = UrlUtils.normalize(raw);

        if (isVideoSite(url)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Open in system browser?");
            alert.setHeaderText("Video sites don't play in NOVA");
            alert.setContentText(
                "JavaFX WebView can't play modern videos (codec limitation).\n\n" +
                "Open this page in your default browser instead?\n\n" + url
            );

            ButtonType openExternal = new ButtonType("Open externally");
            ButtonType tryAnyway = new ButtonType("Try anyway");
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(openExternal, tryAnyway, cancel);

            alert.showAndWait().ifPresent(result -> {
                if (result == openExternal) {
                    openInSystemBrowser(url);
                } else if (result == tryAnyway) {
                    getEngine().ifPresent(e -> e.load(url));
                }
            });
            return;
        }

        getEngine().ifPresent(e -> e.load(url));
    }

    // ───────────── BOOKMARKS ─────────────
    @FXML
    private void onBookmark() {
        getEngine().ifPresent(engine -> {
            String url = engine.getLocation();
            if (url == null || url.isBlank() || url.equals("about:blank")) {
                lblStatus.setText("Cannot bookmark this page");
                return;
            }

            Bookmark current = new Bookmark(engine.getTitle(), url);

            if (bookmarks.contains(current)) {
                bookmarks.remove(current);
                btnBookmark.setText("☆");
                btnBookmark.getStyleClass().remove("bookmarked");
                lblStatus.setText("Bookmark removed");
            } else {
                bookmarks.add(current);
                btnBookmark.setText("★");
                if (!btnBookmark.getStyleClass().contains("bookmarked")) {
                    btnBookmark.getStyleClass().add("bookmarked");
                }
                lblStatus.setText("Bookmark added");
            }
        });
    }

    @FXML
    private void onRemoveBookmark() {
        Bookmark selected = bookmarkList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            bookmarks.remove(selected);
            lblStatus.setText("Bookmark removed");
            getEngine().ifPresent(e -> updateBookmarkButtonState(e.getLocation()));
        }
    }

    private void updateBookmarkButtonState(String url) {
        if (url == null || url.isBlank() || url.equals("about:blank")) {
            btnBookmark.setText("☆");
            btnBookmark.getStyleClass().remove("bookmarked");
            return;
        }
        boolean isBookmarked = bookmarks.stream().anyMatch(b -> b.getUrl().equals(url));
        if (isBookmarked) {
            btnBookmark.setText("★");
            if (!btnBookmark.getStyleClass().contains("bookmarked")) {
                btnBookmark.getStyleClass().add("bookmarked");
            }
        } else {
            btnBookmark.setText("☆");
            btnBookmark.getStyleClass().remove("bookmarked");
        }
    }

    // ───────────── HISTORY ─────────────
    private void addHistoryEntry(String url, String title) {
        if (url == null || url.isBlank() || url.equals("about:blank")) return;
        if (!history.isEmpty() && history.get(0).getUrl().equals(url)) return;

        history.add(0, new HistoryEntry(url, title));

        while (history.size() > 200) {
            history.remove(history.size() - 1);
        }
    }

    @FXML
    private void onClearHistory() {
        history.clear();
        lblStatus.setText("History cleared");
    }

    // ───────────── SIDEBAR ─────────────
    @FXML
    private void onToggleSidebar() {
        boolean visible = sidebar.isVisible();
        sidebar.setVisible(!visible);
        sidebar.setManaged(!visible);
    }

    private void setupSidebar() {
        bookmarkList.setItems(bookmarks);
        historyList.setItems(history);

        sidebar.setVisible(false);
        sidebar.setManaged(false);
    }

    private void setupSidebarInteractions() {
        bookmarkList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                Bookmark b = bookmarkList.getSelectionModel().getSelectedItem();
                if (b != null) {
                    navigate(b.getUrl());
                }
            }
        });

        historyList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                HistoryEntry h = historyList.getSelectionModel().getSelectedItem();
                if (h != null) {
                    navigate(h.getUrl());
                }
            }
        });
    }

    // ═══════════════════════════════════════════════
    //   GEMINI AI INTEGRATION
    // ═══════════════════════════════════════════════

    @FXML
    private void onToggleAi() {
        boolean visible = aiPanel.isVisible();
        aiPanel.setVisible(!visible);
        aiPanel.setManaged(!visible);

        if (!visible) {
            // Just opened
            if (!btnAi.getStyleClass().contains("active")) {
                btnAi.getStyleClass().add("active");
            }
            // Prompt for key on first open if not set
            if (!settings.hasGeminiApiKey()) {
                Platform.runLater(this::askForApiKey);
            } else {
                aiInput.requestFocus();
            }
        } else {
            btnAi.getStyleClass().remove("active");
        }
    }

    @FXML
    private void onSettings() {
        askForApiKey();
    }

    @FXML
    private void onClearChat() {
        chatHistory.clear();
        showEmptyAiHint();
        lblStatus.setText("Conversation cleared");
    }

    @FXML
    private void onAiSend() {
        String text = aiInput.getText();
        if (text == null || text.isBlank()) return;

        if (!ensureApiKey()) return;

        aiInput.clear();

        // On the first message of a fresh conversation, silently include
        // the current page content so Gemini can answer questions about it.
        String internalPrompt = null;
        if (chatHistory.isEmpty()) {
            String pageText = extractPageText();
            if (pageText != null && !pageText.isBlank()) {
                internalPrompt =
                    "The user is currently browsing this webpage. " +
                    "Use this content as context when answering their questions.\n\n" +
                    "PAGE CONTENT:\n" + pageText + "\n\n" +
                    "USER QUESTION: " + text.trim();
            }
        }

        sendToGemini(text.trim(), internalPrompt);
    }

    @FXML
    private void onSummarize() {
        if (!ensureApiKey()) return;
        String pageText = extractPageText();
        if (pageText == null || pageText.isBlank()) {
            appendError("No page content to summarize. Open a webpage first.");
            return;
        }
        String userMessage = "Summarize this page";
        String prompt = "Summarize the following webpage in 4-6 concise bullet points. " +
                        "Focus on the main ideas and key takeaways.\n\n" +
                        "PAGE CONTENT:\n" + pageText;
        sendToGemini(userMessage, prompt);
    }

    @FXML
    private void onExplain() {
        if (!ensureApiKey()) return;
        String pageText = extractPageText();
        if (pageText == null || pageText.isBlank()) {
            appendError("No page content to explain. Open a webpage first.");
            return;
        }
        String userMessage = "Explain this page like I'm 12";
        String prompt = "Explain the following webpage in simple, friendly language " +
                        "as if you were teaching a curious 12-year-old. Avoid jargon.\n\n" +
                        "PAGE CONTENT:\n" + pageText;
        sendToGemini(userMessage, prompt);
    }

    /**
     * Adds the user message to the chat (visible bubble + history),
     * then dispatches a Gemini call. If `internalPrompt` is non-null,
     * that's what actually gets sent to the model — useful for
     * "Summarize" where we want a short user-facing label but a long
     * actual prompt with the page content baked in.
     */
    private void sendToGemini(String userMessage, String internalPrompt) {
        // Clear hint on first real message
        if (chatHistory.isEmpty()) aiMessages.getChildren().clear();

        appendMessage(userMessage, true);
        Label loading = appendLoading();

        String promptToSend = (internalPrompt != null) ? internalPrompt : userMessage;
        chatHistory.add(new GeminiClient.Message("user", promptToSend));

        setAiBusy(true);

        gemini.chat(settings.getGeminiApiKey(), chatHistory)
              .whenComplete((reply, err) -> Platform.runLater(() -> {
                  aiMessages.getChildren().remove(loading);
                  setAiBusy(false);

                  if (err != null) {
                      // Roll back — don't keep a failed user turn in history
                      chatHistory.remove(chatHistory.size() - 1);
                      Throwable cause = err.getCause() != null ? err.getCause() : err;
                      appendError(cause.getMessage());
                      return;
                  }

                  chatHistory.add(new GeminiClient.Message("model", reply));
                  appendMessage(reply, false);
              }));
    }

    private void setAiBusy(boolean busy) {
        btnAiSend.setDisable(busy);
        btnSummarize.setDisable(busy);
        btnExplain.setDisable(busy);
        aiInput.setDisable(busy);
    }

    private void appendMessage(String text, boolean fromUser) {
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(300);
        bubble.getStyleClass().add(fromUser ? "ai-message-user" : "ai-message-bot");

        HBox row = new HBox(bubble);
        row.setAlignment(fromUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        aiMessages.getChildren().add(row);

        // Force the scroll pane to scroll to the bottom after the new message renders
        javafx.application.Platform.runLater(() -> aiScroll.setVvalue(1.0));
    }

    private Label appendLoading() {
        Label dots = new Label("Thinking…");
        dots.getStyleClass().add("ai-message-loading");
        HBox row = new HBox(dots);
        row.setAlignment(Pos.CENTER_LEFT);
        aiMessages.getChildren().add(row);
        javafx.application.Platform.runLater(() -> aiScroll.setVvalue(1.0));
        return dots;
    }

    private void appendError(String message) {
        Label err = new Label("⚠  " + message);
        err.setWrapText(true);
        err.getStyleClass().add("ai-message-error");
        HBox row = new HBox(err);
        row.setAlignment(Pos.CENTER_LEFT);
        aiMessages.getChildren().add(row);
        javafx.application.Platform.runLater(() -> aiScroll.setVvalue(1.0));
    }
    private void updateAiSubtitle(String url) {
        if (aiSubtitle == null) return;
        if (url == null || url.isBlank() || url.equals("about:blank")) {
            aiSubtitle.setText("Ask anything");
            return;
        }
        try {
            String host = URI.create(url).getHost();
            aiSubtitle.setText(host != null ? "Browsing " + host : "Ask anything about this page");
        } catch (Exception ignored) {
            aiSubtitle.setText("Ask anything about this page");
        }
    }

    /** Pulls visible text from the current WebView's DOM, capped to keep prompts small. */
    private String extractPageText() {
        Optional<WebEngine> opt = getEngine();
        if (opt.isEmpty()) return null;
        WebEngine engine = opt.get();
        try {
            Object result = engine.executeScript(
                "(function() {" +
                "  var t = document.body ? document.body.innerText : '';" +
                "  return t ? t.substring(0, 12000) : '';" +
                "})();"
            );
            return result == null ? null : result.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /** Returns true if a key is configured (after possibly prompting the user). */
    private boolean ensureApiKey() {
        if (settings.hasGeminiApiKey()) return true;
        askForApiKey();
        return settings.hasGeminiApiKey();
    }

    private void askForApiKey() {
        TextInputDialog dialog = new TextInputDialog(settings.getGeminiApiKey());
        dialog.setTitle("Gemini API Key");
        dialog.setHeaderText("Connect NOVA to Gemini");
        dialog.setContentText(
            "Paste your free Gemini API key below.\n" +
            "Get one at: https://aistudio.google.com/app/apikey\n\n" +
            "Stored locally at ~/.nova/config.properties"
        );
        dialog.getEditor().setPromptText("AIza...");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(key -> {
            String trimmed = key.trim();
            if (!trimmed.isEmpty()) {
                settings.setGeminiApiKey(trimmed);
                lblStatus.setText("Gemini API key saved");
            }
        });
    }

    // ───────────── HELPERS ─────────────
    private Optional<WebEngine> getEngine() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab == null) return Optional.empty();
        return Optional.of((WebEngine) tab.getUserData());
    }

    private boolean isActive(Tab tab) {
        return tabPane.getSelectionModel().getSelectedItem() == tab;
    }

    private void updateSecurity(String url) {
        if (url == null || url.isBlank() || url.startsWith("about")) {
            lblSecurity.setText("");
        } else if (UrlUtils.isSecure(url)) {
            lblSecurity.setText("🔒");
        } else {
            lblSecurity.setText("⚠");
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max - 1) + "…";
    }

    private boolean isVideoSite(String url) {
        if (url == null) return false;
        String lower = url.toLowerCase();
        for (String site : VIDEO_SITES) {
            if (lower.contains(site)) return true;
        }
        return false;
    }

    private void openInSystemBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                lblStatus.setText("Opened in system browser: " + url);
            } else {
                lblStatus.setText("Cannot open external browser on this system");
            }
        } catch (Exception ex) {
            lblStatus.setText("Failed to open: " + ex.getMessage());
        }
    }
}
