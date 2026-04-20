@echo off
setlocal enabledelayedexpansion

REM Navigate to project directory
cd /d "%~dp0"

echo Cleaning old build...
rmdir /s /q bin 2>nul
mkdir bin

echo.
echo Compiling all Java files...
echo.

REM Get full path to JAR
set JAR_PATH=%cd%\hrms-database-1.0-SNAPSHOT.jar

REM Compile all .java files with quoted paths
for /r . %%f in (*.java) do (
    echo Compiling: %%~nxf
    javac -d bin -cp ".;%JAR_PATH%" "%%f"
)

echo.
echo Build complete! Compiled files are in bin/ folder
echo.
echo To run DEMO (with sample data):
echo   java -cp bin DemoMain
echo.
echo To run REAL (with actual database - requires Hibernate):
echo   java -cp bin;hrms-database-1.0-SNAPSHOT.jar Main
echo.

echo.
if exist "bin\Main.class" (
    echo Compilation successful!
    echo.
    echo Running Main class...
    java -cp "bin;hrms-database-1.0-SNAPSHOT.jar" Main
) else (
    echo ERROR: Compilation failed - Main.class not found
    echo Check that Main.java exists and compiles without errors
    pause
)
