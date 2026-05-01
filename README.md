# 🌌 NOVA — AI-Powered Browser

A lightweight web browser built in JavaFX, powered by **Google Gemini AI**.

## ▶️ Run the project

1. Double-click **compile.bat**
2. Double-click **run.bat**

> Make sure the JavaFX SDK path in `compile.bat` and `run.bat` matches the one on your machine.

## ✨ Features

### Browser
- Multiple tabs (closable, switchable)
- Smart address bar — URL, domain, or search query
- Press **Enter** to navigate, or use the Go button
- Back / Forward / Refresh / Home
- HTTPS indicator 🔒
- Bookmarks ⭐ — toggle, double-click to open, remove with 🗑
- Real history 🕐 — auto-tracked, double-click to revisit, "Clear" to wipe
- Modern home page with quick shortcuts
- Cosmic dark theme

### Gemini AI ✨ NEW
- **Chat with Gemini** in a side panel — click the ✨ button in the toolbar
- **📄 Summarize page** — one-click bullet summary of any webpage
- **💡 Explain** — get any page explained in simple language
- **Multi-turn conversations** — Gemini remembers context across messages
- Press **Enter** in the AI input to send

## 🔑 Setting up Gemini

1. Get a free API key at <https://aistudio.google.com/app/apikey>
2. Click the ✨ button in NOVA — a dialog will ask for your key
3. Paste it in. Done — it's saved to `~/.nova/config.properties`

You can change the key any time via the ⚙ Settings button.

## 📁 Structure

```
nova/
├── compile.bat
├── run.bat
├── src/
│   ├── MainApp.java
│   ├── controller/BrowserController.java
│   ├── model/{Bookmark, BrowserTab, HistoryEntry}.java
│   ├── service/{GeminiClient, Settings}.java     ← NEW
│   └── util/UrlUtils.java
└── resources/
    ├── BrowserView.fxml
    └── css/browser.css
```

## ⌨️ Keyboard

| Action | Shortcut |
|--------|----------|
| Navigate | **Enter** in the address bar |
| Send AI message | **Enter** in the AI input |
| Toggle AI panel | Click ✨ |
| Bookmark | Click ☆ / ★ |
| Open bookmark | **Double-click** in sidebar |

## 🔒 Privacy

- Your API key is stored only on your machine (`~/.nova/config.properties`)
- No analytics, no telemetry
- Gemini receives only what you send (chat messages, page text for summaries)
- History and bookmarks live in memory only — nothing leaves your computer
