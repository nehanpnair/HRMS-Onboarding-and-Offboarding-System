@echo off
REM This script fixes all package imports in the project files
REM It updates incorrect imports to match the actual folder structure

cd /d "%~dp0"

echo Fixing import statements...
echo.

REM Fix data imports (data/data/*.java)
for /r data %%f in (*.java) do (
    powershell -Command "(Get-Content '%%f') -replace 'import data\.\*;', 'import data.data.*;' | Set-Content '%%f'"
    powershell -Command "(Get-Content '%%f') -replace 'import data\.I', 'import data.data.I' | Set-Content '%%f'"
)

REM Fix model imports (model/model/*.java)
for /r model %%f in (*.java) do (
    powershell -Command "(Get-Content '%%f') -replace 'import model\.\*;', 'import model.model.*;' | Set-Content '%%f'"
    powershell -Command "(Get-Content '%%f') -replace 'import model\.', 'import model.model.' | Set-Content '%%f'"
)

REM Fix service imports
for /r service %%f in (*.java) do (
    powershell -Command "(Get-Content '%%f') -replace 'import data\.\*;', 'import data.data.*;' | Set-Content '%%f'"
    powershell -Command "(Get-Content '%%f') -replace 'import model\.\*;', 'import model.model.*;' | Set-Content '%%f'"
    powershell -Command "(Get-Content '%%f') -replace 'import model\.', 'import model.model.' | Set-Content '%%f'"
    powershell -Command "(Get-Content '%%f') -replace 'import data\.I', 'import data.data.I' | Set-Content '%%f'"
)

REM Fix proxy imports
for /r proxy %%f in (*.java) do (
    powershell -Command "(Get-Content '%%f') -replace 'import model\.', 'import model.model.' | Set-Content '%%f'"
)

REM Fix strategy imports  
for /r strategy %%f in (*.java) do (
    powershell -Command "(Get-Content '%%f') -replace 'import model\.', 'import model.model.' | Set-Content '%%f'"
)

REM Fix factory imports
for /r factory %%f in (*.java) do (
    powershell -Command "(Get-Content '%%f') -replace 'import model\.', 'import model.model.' | Set-Content '%%f'"
)

REM Fix integration imports
for /r integration %%f in (*.java) do (
    powershell -Command "(Get-Content '%%f') -replace 'import model\.', 'import model.model.' | Set-Content '%%f'"
)

REM Fix Main.java specific imports
powershell -Command "(Get-Content 'Main.java') -replace 'import model\.\*;', 'import model.model.*;' | Set-Content 'Main.java'"
powershell -Command "(Get-Content 'Main.java') -replace 'import data\.\*;', 'import data.data.*;' | Set-Content 'Main.java'"
powershell -Command "(Get-Content 'Main.java') -replace 'import service\.\*;', 'import service.*;' | Set-Content 'Main.java'"
powershell -Command "(Get-Content 'Main.java') -replace 'import proxy\.\*;', 'import proxy.*;' | Set-Content 'Main.java'"
powershell -Command "(Get-Content 'Main.java') -replace 'import strategy\.\*;', 'import strategy.*;' | Set-Content 'Main.java'"
powershell -Command "(Get-Content 'Main.java') -replace 'import factory\.\*;', 'import factory.*;' | Set-Content 'Main.java'"

echo Import fixes completed!
echo.
echo Now run: compile.bat
