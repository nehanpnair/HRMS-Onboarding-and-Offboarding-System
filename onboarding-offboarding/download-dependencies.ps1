# Download all dependencies for HRMS Onboarding Subsystem
# This script downloads all JARs required by the DB team's Hibernate setup

$libDir = "c:\COLLEGE\sem6\ooad project\lib"
$mavenRepo = "https://repo1.maven.org/maven2"

# Create lib directory if it doesn't exist
if (!(Test-Path $libDir)) {
    New-Item -ItemType Directory -Path $libDir | Out-Null
}

$dependencies = @(
    # Hibernate Core
    "$mavenRepo/org/hibernate/orm/hibernate-core/6.4.4.Final/hibernate-core-6.4.4.Final.jar",
    
    # Hibernate Community Dialects
    "$mavenRepo/org/hibernate/orm/hibernate-community-dialects/6.4.4.Final/hibernate-community-dialects-6.4.4.Final.jar",
    
    # SQLite JDBC
    "$mavenRepo/org/xerial/sqlite-jdbc/3.45.2.0/sqlite-jdbc-3.45.2.0.jar",
    
    # Jakarta Persistence API
    "$mavenRepo/jakarta/persistence/jakarta.persistence-api/3.1.0/jakarta.persistence-api-3.1.0.jar",
    
    # SLF4J
    "$mavenRepo/org/slf4j/slf4j-simple/2.0.12/slf4j-simple-2.0.12.jar",
    "$mavenRepo/org/slf4j/slf4j-api/2.0.12/slf4j-api-2.0.12.jar",
    
    # Jackson
    "$mavenRepo/com/fasterxml/jackson/core/jackson-databind/2.17.2/jackson-databind-2.17.2.jar",
    "$mavenRepo/com/fasterxml/jackson/core/jackson-core/2.17.2/jackson-core-2.17.2.jar",
    "$mavenRepo/com/fasterxml/jackson/core/jackson-annotations/2.17.2/jackson-annotations-2.17.2.jar",
    "$mavenRepo/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.17.2/jackson-datatype-jsr310-2.17.2.jar",
    
    # Additional Hibernate dependencies
    "$mavenRepo/org/jboss/logging/jboss-logging/3.5.3.Final/jboss-logging-3.5.3.Final.jar",
    "$mavenRepo/org/hibernate/common/hibernate-commons-annotations/6.0.6.Final/hibernate-commons-annotations-6.0.6.Final.jar"
)

Write-Host "Downloading dependencies to: $libDir" -ForegroundColor Green
Write-Host ""

$count = 0
foreach ($url in $dependencies) {
    $fileName = Split-Path -Leaf $url
    $filePath = Join-Path $libDir $fileName
    
    if (Test-Path $filePath) {
        Write-Host "✓ Already exists: $fileName"
    } else {
        Write-Host "⬇ Downloading: $fileName"
        try {
            Invoke-WebRequest -Uri $url -OutFile $filePath -ErrorAction Stop
            Write-Host "  ✓ Downloaded successfully"
            $count++
        } catch {
            Write-Host "  ✗ Failed to download: $_" -ForegroundColor Red
        }
    }
}

Write-Host ""
Write-Host "Downloaded $count new JARs" -ForegroundColor Green
Write-Host ""
Write-Host "Now you can run:" -ForegroundColor Yellow
Write-Host 'java -cp "bin;lib/*;hrms-database-1.0-SNAPSHOT.jar" Main'
