@echo off
REM Download Hibernate JARs from Maven Central

cd /d "c:\COLLEGE\sem6\ooad project"

echo Downloading Hibernate JARs...
echo.

set MAVEN_REPO=https://repo1.maven.org/maven2

REM Hibernate Core
echo Downloading hibernate-core-6.4.4.Final.jar...
powershell -Command "(New-Object System.Net.ServicePointManager).SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%MAVEN_REPO%/org/hibernate/orm/hibernate-core/6.4.4.Final/hibernate-core-6.4.4.Final.jar' -OutFile 'hibernate-core-6.4.4.Final.jar'"

REM Hibernate Community Dialects
echo Downloading hibernate-community-dialects-6.4.4.Final.jar...
powershell -Command "(New-Object System.Net.ServicePointManager).SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%MAVEN_REPO%/org/hibernate/orm/hibernate-community-dialects/6.4.4.Final/hibernate-community-dialects-6.4.4.Final.jar' -OutFile 'hibernate-community-dialects-6.4.4.Final.jar'"

REM jboss-logging
echo Downloading jboss-logging-3.5.3.Final.jar...
powershell -Command "(New-Object System.Net.ServicePointManager).SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%MAVEN_REPO%/org/jboss/logging/jboss-logging/3.5.3.Final/jboss-logging-3.5.3.Final.jar' -OutFile 'jboss-logging-3.5.3.Final.jar'"

REM Hibernate Commons Annotations
echo Downloading hibernate-commons-annotations-6.0.6.Final.jar...
powershell -Command "(New-Object System.Net.ServicePointManager).SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%MAVEN_REPO%/org/hibernate/common/hibernate-commons-annotations/6.0.6.Final/hibernate-commons-annotations-6.0.6.Final.jar' -OutFile 'hibernate-commons-annotations-6.0.6.Final.jar'"

echo.
echo Done! Run with: java -cp "bin;*.jar;db-team/extracted_jar" Main
pause
