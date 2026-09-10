<hr>

<div align="center">

<h1 align="center">🌌 NOVA — AI-Powered Browser</h1>

</div>

<pre align="center">A lightweight JavaFX web browser with a built-in Google Gemini AI assistant.</pre>

![Java](https://img.shields.io/badge/Java-17%2B-orange) ![JavaFX](https://img.shields.io/badge/JavaFX-26-blue) ![Platform](https://img.shields.io/badge/platform-Windows-lightgrey) ![License](https://img.shields.io/badge/license-Unspecified-lightgrey)

NOVA is a desktop web browser built from scratch in Java and JavaFX. Alongside standard browsing (tabs, bookmarks, history, address bar) it ships with a side panel powered by **Google Gemini** that can summarize the page you're on, explain it in plain language, or just chat — all without leaving the browser.

## Features

### Browser
- Multiple tabs — closable and switchable
- Smart address bar that accepts a URL, a bare domain, or a search query
- Navigate with **Enter** or the Go button
- Back / Forward / Refresh / Home controls
- 🔒 HTTPS indicator
- ⭐ Bookmarks — toggle, double-click to open, remove with 🗑
- 🕐 Real history — auto-tracked, double-click to revisit, one-click clear
- Modern home page with quick shortcuts
- Cosmic dark theme

### Gemini AI
- ✨ Chat with Gemini in a side panel, toggled from the toolbar
- 📄 One-click **Summarize page** — bullet summary of the current page
- 💡 **Explain** — plain-language explanation of the current page
- Multi-turn conversations — Gemini keeps context across messages
- Press **Enter** in the AI input to send

## Contents

* [Quick Start](#quick-start)
* [Configuration](#configuration)
* [Project Structure](#project-structure)
* [Keyboard Shortcuts](#keyboard-shortcuts)
* [Privacy](#privacy)
* [FAQ](#frequently-asked-questions-faq)
* [Contributing](#contributing)
* [License](#license)
* [Support](#support)

## Quick Start

### Requirements

* Windows (the provided scripts use `.bat` — see [Contributing](#contributing) if you'd like to add macOS/Linux scripts)
* JDK 17 or newer
* [JavaFX SDK](https://gluonhq.com/products/javafx/) (developed against 26.0.1)
* A free [Google Gemini API key](https://aistudio.google.com/app/apikey) to use the AI features

### Setup Instructions

1. Clone or download this repository.
2. Download the JavaFX SDK for your platform and unzip it somewhere on disk.
3. Open `compile.bat` and `run.bat` and update the `JAVAFX_LIB` path to point at the `lib` folder of your JavaFX SDK.

### Run Instructions

1. Double-click **`compile.bat`** to build the project (this generates `sources.txt`, compiles into `out/`, and copies `resources/` alongside the classes).
2. Double-click **`run.bat`** to launch NOVA.
3. On first launch, click the ✨ button and paste in your Gemini API key when prompted — see [Configuration](#configuration).

### Usage Examples

* **Browse** — type a URL, a domain, or a search term in the address bar and press Enter.
* **Summarize a page** — open the ✨ AI panel and click **📄 Summarize page**.
* **Explain a page** — open the ✨ AI panel and click **💡 Explain**.
* **Chat with Gemini** — type a message in the AI input and press Enter; NOVA keeps the conversation context across turns.
* **Bookmark a page** — click the ☆ icon in the toolbar (it fills in as ★); double-click a bookmark in the sidebar to open it.

### Build Instructions

Compilation is handled by `compile.bat`, which:
1. Collects all `.java` files under `src/` into `sources.txt`.
2. Runs `javac` against the JavaFX module path with `javafx.controls`, `javafx.fxml`, and `javafx.web`.
3. Copies `resources/` into `out/resources/` so FXML, CSS, and icons are available at runtime.

## Configuration

NOVA needs a Gemini API key to power the AI panel:

1. Get a free key at <https://aistudio.google.com/app/apikey>.
2. Click the ✨ button in NOVA — a dialog will prompt you for the key.
3. Paste it in. It's saved locally to `~/.nova/config.properties` and reused on future launches.
4. You can update the key any time via the ⚙ Settings button.

## Project Structure

```
NOVA-Browser-master/
├── compile.bat              # Builds the project with javac + JavaFX modules
├── run.bat                  # Launches the compiled app
├── src/
│   ├── MainApp.java                       # Application entry point (JavaFX Stage/Scene setup)
│   ├── controller/BrowserController.java  # Wires up the FXML UI and browsing logic
│   ├── model/                             # Bookmark, BrowserTab, HistoryEntry
│   ├── service/                           # GeminiClient (AI calls), Settings (config persistence)
│   └── util/UrlUtils.java                 # Address-bar parsing (URL vs. search query)
└── resources/
    ├── BrowserView.fxml     # Main UI layout
    ├── css/browser.css      # Cosmic dark theme
    └── icons/               # App icons (16px–256px) and logos
```

## Keyboard Shortcuts

| Action | Shortcut |
|---|---|
| Navigate | **Enter** in the address bar |
| Send AI message | **Enter** in the AI input |
| Toggle AI panel | Click ✨ |
| Bookmark page | Click ☆ / ★ |
| Open bookmark | **Double-click** in sidebar |

## Privacy

- Your Gemini API key is stored only on your machine, in `~/.nova/config.properties`.
- No analytics and no telemetry.
- Gemini only receives what you explicitly send it — chat messages, or the page text when you ask for a summary/explanation.
- History and bookmarks live in memory only for the current session; nothing is uploaded.

## Frequently Asked Questions (FAQ)

1. **The AI panel says it needs an API key — where do I get one?**
   - Generate a free key at [Google AI Studio](https://aistudio.google.com/app/apikey), then paste it into the dialog that appears when you click ✨.
2. **Compilation fails immediately.**
   - Double-check that `JAVAFX_LIB` in `compile.bat`/`run.bat` points to the `lib` folder of a JavaFX SDK matching your JDK version.
3. **Can I run this on macOS/Linux?**
   - The app itself is plain Java/JavaFX, but `compile.bat`/`run.bat` are Windows batch scripts. You'd need to translate the `javac`/`java` commands into a shell script with the equivalent module path and classpath flags.

## Contributing

1. Open a GitHub issue describing the change you'd like to make.
2. Fork this repository.
3. Make your changes in your fork.
4. Open a pull request back into this repo for review.

**Working on your first pull request?** See [How to Contribute to an Open Source Project on GitHub](https://kcd.im/pull-request).

## License

No license file is currently included in this repository. Until one is added, all rights are reserved by the author — please reach out before reusing or redistributing this code.

## Support

Open a GitHub issue in this repository for bugs, questions, or feature requests.
