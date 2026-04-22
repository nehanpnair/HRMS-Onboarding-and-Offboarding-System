@echo off
REM Run HRMS Offboarding GUI (Teammate's Implementation)

cd /d "c:\COLLEGE\sem6\ooad project"

REM Build classpath from all JARs
setlocal enabledelayedexpansion
set CLASSPATH=bin
for %%f in (*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)
set CLASSPATH=!CLASSPATH!;db-team/extracted_jar

echo Starting Offboarding GUI...
echo.

java -cp "%CLASSPATH%" gui.MainGUI

pause
