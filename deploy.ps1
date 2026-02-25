# Local Deployment Script for API Local Service Helper
# This script automates the deployment process on Windows

param(
    [string]$Action = "up",
    [string]$Profile = "local"
)

# Colors for console output
$Colors = @{
    Green = "Green"
    Red = "Red"
    Yellow = "Yellow"
    Cyan = "Cyan"
}

function Write-Step {
    param([string]$Message)
    Write-Host "`n$Message" -ForegroundColor $Colors.Cyan
    Write-Host ("=" * 60) -ForegroundColor $Colors.Cyan
}

function Write-Success {
    param([string]$Message)
    Write-Host "✓ $Message" -ForegroundColor $Colors.Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "✗ $Message" -ForegroundColor $Colors.Red
}

function Write-Warning {
    param([string]$Message)
    Write-Host "⚠ $Message" -ForegroundColor $Colors.Yellow
}

# Verify prerequisites
function Test-Prerequisites {
    Write-Step "Checking Prerequisites"

    $missing = @()

    # Check Docker
    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        $missing += "Docker"
    } else {
        Write-Success "Docker installed"
    }

    # Check Docker Compose
    if (-not (Get-Command docker-compose -ErrorAction SilentlyContinue)) {
        $missing += "Docker Compose"
    } else {
        Write-Success "Docker Compose installed"
    }

    # Check Java
    if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
        $missing += "Java"
    } else {
        $javaVersion = java -version 2>&1 | Select-Object -First 1
        Write-Success "Java installed: $javaVersion"
    }

    if ($missing.Count -gt 0) {
        Write-Error "Missing prerequisites: $($missing -join ', ')"
        Write-Host "Please install the missing tools and try again."
        exit 1
    }

    Write-Success "All prerequisites met"
}

# Setup environment
function Setup-Environment {
    Write-Step "Setting Up Environment"

    if (-not (Test-Path ".env")) {
        if (Test-Path ".env.local") {
            Write-Warning ".env file not found. Creating from .env.local template..."
            Copy-Item -Path ".env.local" -Destination ".env"
            Write-Success "Created .env file"
            Write-Warning "Please update .env with your database credentials"
        } else {
            Write-Error ".env or .env.local file not found"
            exit 1
        }
    } else {
        Write-Success ".env file exists"
    }

    # Verify .env is in gitignore
    if (Test-Path ".gitignore") {
        $gitignore = Get-Content ".gitignore"
        if ($gitignore -notlike "*\.env*") {
            Write-Warning "Consider adding '.env' to .gitignore"
        }
    }
}

# Build the application
function Build-Application {
    Write-Step "Building Application"

    Write-Host "Running: mvnw.cmd clean package -DskipTests"
    & .\mvnw.cmd clean package -DskipTests

    if ($LASTEXITCODE -ne 0) {
        Write-Error "Build failed"
        exit 1
    }

    Write-Success "Application built successfully"
}

# Build Docker image
function Build-DockerImage {
    Write-Step "Building Docker Image"

    Write-Host "Running: docker build -t api-local-service:latest ."
    docker build -t api-local-service:latest .

    if ($LASTEXITCODE -ne 0) {
        Write-Error "Docker build failed"
        exit 1
    }

    Write-Success "Docker image built successfully"
}

# Deploy with Docker Compose
function Deploy-Container {
    Write-Step "Deploying Container"

    # Load environment from .env
    Write-Host "Loading environment from .env file..."
    $env_content = Get-Content ".env"
    foreach ($line in $env_content) {
        if ($line -and -not $line.StartsWith("#")) {
            $key, $value = $line.Split("=", 2)
            [Environment]::SetEnvironmentVariable($key.Trim(), $value.Trim())
        }
    }

    Write-Host "Running: docker-compose -f docker-compose.local.yml up -d"
    docker-compose -f docker-compose.local.yml up -d

    if ($LASTEXITCODE -ne 0) {
        Write-Error "Docker Compose deployment failed"
        exit 1
    }

    Write-Success "Container deployed successfully"

    # Wait for container to be ready
    Write-Host "Waiting for container to be ready..."
    Start-Sleep -Seconds 5

    # Check status
    Write-Step "Container Status"
    docker ps --filter "name=api-local-service-helper"

    Write-Success "Application should be available at http://localhost:4000"
}

# Stop container
function Stop-Container {
    Write-Step "Stopping Container"

    Write-Host "Running: docker-compose -f docker-compose.local.yml down"
    docker-compose -f docker-compose.local.yml down

    if ($LASTEXITCODE -eq 0) {
        Write-Success "Container stopped successfully"
    }
}

# Restart container
function Restart-Container {
    Write-Step "Restarting Container"

    Write-Host "Running: docker-compose -f docker-compose.local.yml restart"
    docker-compose -f docker-compose.local.yml restart

    if ($LASTEXITCODE -eq 0) {
        Write-Success "Container restarted successfully"
    }
}

# View logs
function View-Logs {
    Write-Step "Container Logs"
    docker-compose -f docker-compose.local.yml logs -f api-local-service
}

# Test endpoints
function Test-Endpoints {
    Write-Step "Testing API Endpoints"

    $baseUrl = "http://localhost:4000"

    try {
        Write-Host "Testing GET /api/commands..."
        $response = Invoke-WebRequest -Uri "$baseUrl/api/commands" -Method GET -TimeoutSec 10
        if ($response.StatusCode -eq 200) {
            Write-Success "API is responding"
            Write-Host "Response: $($response.Content)"
        }
    } catch {
        Write-Error "Failed to reach API: $_"
    }
}

# Main script logic
function Main {
    Write-Host @"
╔═══════════════════════════════════════════════════════════════╗
║  API Local Service Helper - Local Deployment Script          ║
║  Windows PowerShell Edition                                  ║
╚═══════════════════════════════════════════════════════════════╝
"@ -ForegroundColor $Colors.Cyan

    switch ($Action.ToLower()) {
        "up" {
            Test-Prerequisites
            Setup-Environment
            Build-Application
            Build-DockerImage
            Deploy-Container
            Write-Host "`n✓ Deployment complete!" -ForegroundColor $Colors.Green
        }
        "down" {
            Stop-Container
        }
        "restart" {
            Restart-Container
        }
        "logs" {
            View-Logs
        }
        "test" {
            Test-Endpoints
        }
        "build" {
            Test-Prerequisites
            Build-Application
            Build-DockerImage
        }
        default {
            Write-Host @"
Usage: .\deploy.ps1 [Action]

Actions:
  up          - Full deployment (prerequisites, build, deploy)
  down        - Stop and remove container
  restart     - Restart running container
  logs        - View container logs (follow mode)
  test        - Test API endpoints
  build       - Build application and Docker image only

Examples:
  .\deploy.ps1 up
  .\deploy.ps1 logs
  .\deploy.ps1 restart

"@
        }
    }
}

# Run main function
Main

