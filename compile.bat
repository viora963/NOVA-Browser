@echo off
echo ============================================
echo   MiniBrowser - Compilation
echo ============================================

set JAVAFX_LIB=C:\openjfx-21.0.2_windows-x64_bin-sdk\javafx-sdk-21.0.2\lib
set SRC_DIR=src
set OUT_DIR=out
set RES_DIR=resources

if not exist %OUT_DIR% mkdir %OUT_DIR%

echo Compilation en cours...

javac --module-path "%JAVAFX_LIB%" ^
      --add-modules javafx.controls,javafx.fxml,javafx.web ^
      -d %OUT_DIR% ^
      -sourcepath %SRC_DIR% ^
      src\com\minibrowser\MainApp.java ^
      src\com\minibrowser\controller\BrowserController.java ^
      src\com\minibrowser\model\BrowserTab.java ^
      src\com\minibrowser\model\Bookmark.java ^
      src\com\minibrowser\model\HistoryEntry.java ^
      src\com\minibrowser\util\UrlUtils.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERREUR] La compilation a echoue !
    pause
    exit /b 1
)

echo Copie des ressources...
xcopy /E /Y /Q "%RES_DIR%\*" "%OUT_DIR%\resources\"

echo.
echo [OK] Compilation reussie !
echo Lancez run.bat pour demarrer le navigateur.
echo ============================================
pause