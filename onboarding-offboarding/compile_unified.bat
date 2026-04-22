@echo off
setlocal enabledelayedexpansion

cd /d "%~dp0"
echo Compiling Unified HRMS GUI...

if not exist bin mkdir bin

set CP=lib\sqlite-jdbc-3.45.2.0.jar;lib\slf4j-api-2.0.9.jar;lib\slf4j-simple-2.0.9.jar

echo Compiling GUI files...
javac -d bin -cp "%CP%" -sourcepath . gui_src\*.java 2>&1

echo.
if exist "bin\gui\OnboardingGUI.class" (
    echo SUCCESS: Unified OnboardingGUI compiled!
    echo.
    echo Running Unified HRMS...
    java -cp "bin;%CP%" gui.OnboardingGUI
) else (
    echo ERROR: OnboardingGUI not compiled
)
