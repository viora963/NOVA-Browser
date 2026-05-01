@echo off
echo ============================================
echo   NOVA - Lancement
echo ============================================

set JAVAFX_LIB=C:\Users\nova9\Downloads\openjfx-26.0.1_windows-x64_bin-sdk\javafx-sdk-26.0.1\lib

java ^
--enable-native-access=javafx.graphics,javafx.web ^
--module-path "%JAVAFX_LIB%" ^
--add-modules javafx.controls,javafx.fxml,javafx.web ^
-cp out ^
MainApp

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERREUR] Le lancement a echoue !
)

pause
