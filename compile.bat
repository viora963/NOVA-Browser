@echo off
echo ============================================
echo   NOVA - Compilation
echo ============================================

set JAVAFX_LIB=C:\Users\nova9\Downloads\openjfx-26.0.1_windows-x64_bin-sdk\javafx-sdk-26.0.1\lib
set SRC_DIR=src
set OUT_DIR=out
set RES_DIR=resources

if not exist %OUT_DIR% mkdir %OUT_DIR%

echo Compilation en cours...

dir /s /b %SRC_DIR%\*.java > sources.txt

javac --module-path "%JAVAFX_LIB%" ^
--add-modules javafx.controls,javafx.fxml,javafx.web ^
-d %OUT_DIR% ^
-sourcepath %SRC_DIR% ^
@sources.txt

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
pause
