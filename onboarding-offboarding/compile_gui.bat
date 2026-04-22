@echo off
setlocal enabledelayedexpansion

cd /d "%~dp0"

echo Compiling Pre-Onboarding GUI Only
echo.

if not exist bin mkdir bin

REM Get short name for long directory
for /d %%D in (nehan*) do set GUI_DIR=%%~sD

echo GUI directory: %GUI_DIR%

REM Simple classpath: extracted classes + our JAR
set CP=db-team/extracted_jar;bin;hrms-database-1.0-SNAPSHOT.jar

echo Compiling GUI files...
javac -d bin -cp "%CP%" ^
  "%GUI_DIR%\*.java" 2>&1

echo.
if exist "bin\gui\OnboardingGUI.class" (
    echo SUCCESS: OnboardingGUI compiled!
    echo.
    echo Running OnboardingGUI...
    java -cp "%CP%" gui.OnboardingGUI
) else (
    echo ERROR: OnboardingGUI not compiled
    dir bin\gui
)

pause
