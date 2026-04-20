param()

$projectRoot = "c:\COLLEGE\sem6\ooad project"  # Don't use backtick here
$mavenCentral = "https://repo1.maven.org/maven2"

$hibernateJars = @(
    @{name = "hibernate-core-6.4.4.Final.jar"; url = "$mavenCentral/org/hibernate/orm/hibernate-core/6.4.4.Final/hibernate-core-6.4.4.Final.jar"},
    @{name = "hibernate-community-dialects-6.4.4.Final.jar"; url = "$mavenCentral/org/hibernate/orm/hibernate-community-dialects/6.4.4.Final/hibernate-community-dialects-6.4.4.Final.jar"},
    @{name = "jboss-logging-3.5.3.Final.jar"; url = "$mavenCentral/org/jboss/logging/jboss-logging/3.5.3.Final/jboss-logging-3.5.3.Final.jar"},
    @{name = "hibernate-commons-annotations-6.0.6.Final.jar"; url = "$mavenCentral/org/hibernate/common/hibernate-commons-annotations/6.0.6.Final/hibernate-commons-annotations-6.0.6.Final.jar"},
    @{name = "jakarta.persistence-api-3.1.0.jar"; url = "$mavenCentral/jakarta/persistence/jakarta.persistence-api/3.1.0/jakarta.persistence-api-3.1.0.jar"},
    @{name = "jackson-core-2.17.2.jar"; url = "$mavenCentral/com/fasterxml/jackson/core/jackson-core/2.17.2/jackson-core-2.17.2.jar"},
    @{name = "jackson-annotations-2.17.2.jar"; url = "$mavenCentral/com/fasterxml/jackson/core/jackson-annotations/2.17.2/jackson-annotations-2.17.2.jar"},
    @{name = "jackson-databind-2.17.2.jar"; url = "$mavenCentral/com/fasterxml/jackson/core/jackson-databind/2.17.2/jackson-databind-2.17.2.jar"}
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Downloading Hibernate Dependencies" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$downloaded = 0
$failed = 0

foreach ($jar in $hibernateJars) {
    $filePath = Join-Path $projectRoot $jar.name
    
    if (Test-Path $filePath) {
        Write-Host "✓ Already exists: $($jar.name)" -ForegroundColor Green
    } else {
        Write-Host "⬇ Downloading: $($jar.name)..."
        try {
            [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
            Invoke-WebRequest -Uri $jar.url -OutFile $filePath -UseBasicParsing -ErrorAction Stop
            Write-Host "  ✓ Success" -ForegroundColor Green
            $downloaded++
        } catch {
            Write-Host "  ✗ Failed: $_" -ForegroundColor Red
            $failed++
        }
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Summary: Downloaded $downloaded, Failed $failed" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

if ($failed -eq 0) {
    Write-Host ""
    Write-Host "Now run your Main class with:" -ForegroundColor Yellow
    Write-Host 'java -cp "bin;*.jar;db-team/extracted_jar" Main' -ForegroundColor Yellow
    Write-Host ""
} else {
    Write-Host "Some downloads failed. Check your internet connection and try again." -ForegroundColor Red
}
