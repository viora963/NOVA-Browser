# 🌐 MiniBrowser — Sans Maven

Navigateur web JavaFX sans Maven, compile et lance avec des scripts .bat.

## 📁 Structure

```
MiniBrowserNoMaven/
├── compile.bat          ← Compile le projet
├── run.bat              ← Lance le navigateur
├── src/
│   └── com/minibrowser/
│       ├── MainApp.java
│       ├── controller/BrowserController.java
│       ├── model/BrowserTab.java
│       ├── model/Bookmark.java
│       ├── model/HistoryEntry.java
│       └── util/UrlUtils.java
└── resources/
    ├── BrowserView.fxml
    └── css/browser.css
```

## ▶️ Lancer le projet

1. Double-clic sur **compile.bat**
2. Double-clic sur **run.bat**

Ou dans PowerShell :
```powershell
cd "chemin\vers\MiniBrowserNoMaven"
.\compile.bat
.\run.bat
```

## ✨ Fonctionnalités

- Onglets multiples
- Navigation ← → ↻ ⌂
- Barre d'URL intelligente (auto-search Google)
- Indicateur HTTPS 🔒
- Favoris ⭐
- Historique 🕐
- Thème sombre
