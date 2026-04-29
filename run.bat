@echo off

set JAVAFX_LIB=C:\openjfx-21.0.2_windows-x64_bin-sdk\javafx-sdk-21.0.2\lib

echo ============================================
echo   MiniBrowser - Lancement (Java 21 STABLE)
echo ============================================

java ^
--module-path "%JAVAFX_LIB%" ^
--add-modules javafx.controls,javafx.fxml,javafx.web ^
-cp out ^
com.minibrowser.MainApp

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERREUR] Le lancement a echoue !
)

pause