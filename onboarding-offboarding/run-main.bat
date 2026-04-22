@echo off
REM Run HRMS Onboarding with all dependencies

cd /d "c:\COLLEGE\sem6\ooad project"

REM Build classpath from all JARs in current directory
setlocal enabledelayedexpansion
set CLASSPATH=bin
for %%f in (*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)
set CLASSPATH=!CLASSPATH!;db-team/extracted_jar

echo Running Main with classpath:
echo %CLASSPATH%
echo.

java -cp "%CLASSPATH%" Main

pause
