@echo off
REM Build guide for HRMS Project with spaces in path

REM ============================================
REM METHOD 1: Using /sources.txt (RECOMMENDED)
REM ============================================

REM First, generate list of all .java files:
dir /s /B *.java > sources.txt

REM Compile with proper quoting:
javac -d bin -cp ".;hrms-database-1.0-SNAPSHOT.jar" @sources.txt

REM If you get "invalid flag" error, use this instead:
javac -d bin -cp ".;hrms-database-1.0-SNAPSHOT.jar" ^
  model\*.java ^
  service\*.java ^
  data\*.java ^
  proxy\*.java ^
  strategy\*.java ^
  factory\*.java ^
  integration\*.java ^
  *.java

REM ============================================
REM METHOD 2: Change directory first (SIMPLER)
REM ============================================

cd /d "C:\COLLEGE\sem6\ooad project"
javac -d bin -cp ".;hrms-database-1.0-SNAPSHOT.jar" model\*.java service\*.java data\*.java proxy\*.java strategy\*.java factory\*.java integration\*.java *.java

REM ============================================
REM METHOD 3: Using recursive compile (FOR LOOP)
REM ============================================

setlocal enabledelayedexpansion
set CLASSPATH=.;hrms-database-1.0-SNAPSHOT.jar
for /r . %%f in (*.java) do (
  echo Compiling %%f
)
javac -d bin -cp "%CLASSPATH%" @sources.txt

REM ============================================
REM RUN THE APPLICATION
REM ============================================

REM Make sure you're in the project directory:
cd /d "C:\COLLEGE\sem6\ooad project"

REM Run Main class:
java -cp "bin;hrms-database-1.0-SNAPSHOT.jar" Main

REM If Main needs to access hrms.db (in current directory):
java -cp "bin;hrms-database-1.0-SNAPSHOT.jar" -Ddb.path="hrms.db" Main

REM ============================================
REM STEP-BY-STEP QUICK FIX
REM ============================================

REM Just copy-paste these commands one by one in CMD:

REM 1. Navigate to project:
cd /d "C:\COLLEGE\sem6\ooad project"

REM 2. Create output directory:
mkdir bin

REM 3. Generate source file list:
dir /s /B *.java > sources.txt

REM 4. Compile:
javac -d bin -cp ".;hrms-database-1.0-SNAPSHOT.jar" @sources.txt

REM 5. Run:
java -cp "bin;hrms-database-1.0-SNAPSHOT.jar" Main
