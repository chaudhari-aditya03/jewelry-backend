# Start E-Commerce Backend Server
# Run this script from the backend directory

Write-Host "🚀 Starting Jewelry E-Commerce Backend..." -ForegroundColor Cyan
Write-Host ""

# Check if Maven is installed
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Maven from: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

# Check if Java is installed
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Java 17+ from: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

# Display Java version
Write-Host "☕ Java Version:" -ForegroundColor Green
java -version
Write-Host ""

# Check if MySQL is running
Write-Host "🔍 Checking MySQL connection..." -ForegroundColor Yellow
$mysqlRunning = Get-Process mysqld -ErrorAction SilentlyContinue
if (-not $mysqlRunning) {
    Write-Host "⚠️  MySQL might not be running!" -ForegroundColor Yellow
    Write-Host "Please ensure MySQL is started and running on localhost:3306" -ForegroundColor Yellow
    Write-Host ""
}

# Navigate to backend directory if not already there
$currentPath = Get-Location
if ($currentPath.Path -notlike "*backend") {
    Write-Host "📁 Navigating to backend directory..." -ForegroundColor Yellow
    Set-Location -Path "backend" -ErrorAction SilentlyContinue
}

# Check if pom.xml exists
if (-not (Test-Path "pom.xml")) {
    Write-Host "❌ pom.xml not found. Are you in the correct directory?" -ForegroundColor Red
    exit 1
}

Write-Host "📦 Installing dependencies..." -ForegroundColor Yellow
mvn clean install -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Maven build failed" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "✅ Dependencies installed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "🚀 Starting Spring Boot application..." -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
Write-Host ""
Write-Host "📍 Backend will be available at: http://localhost:8080" -ForegroundColor Green
Write-Host "📚 Swagger UI will be available at: http://localhost:8080/swagger-ui.html" -ForegroundColor Green
Write-Host ""
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

# Run Spring Boot
mvn spring-boot:run
