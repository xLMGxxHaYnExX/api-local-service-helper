@echo off
REM API Local Service Helper - Batch Deployment Script
REM This bypasses PowerShell execution policy issues

setlocal enabledelayedexpansion

set action=%1
if "%action%"=="" set action=up

if "%action%"=="up" goto :deploy_up
if "%action%"=="down" goto :deploy_down
if "%action%"=="restart" goto :deploy_restart
if "%action%"=="logs" goto :deploy_logs
if "%action%"=="test" goto :deploy_test
goto :usage

:deploy_up
echo.
echo Deploying API Local Service Helper...
echo.

if not exist ".env" (
    echo Creating .env from .env.local...
    copy .env.local .env
    echo.
    echo Please edit .env file with your database credentials
    echo Then run: deploy.bat up
    pause
    exit /b 1
)

echo Building application...
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Building Docker image...
docker build -t api-local-service:latest .
if errorlevel 1 (
    echo Docker build failed!
    pause
    exit /b 1
)

echo.
echo Deploying with Docker Compose...
docker-compose -f docker-compose.local.yml up -d
if errorlevel 1 (
    echo Deployment failed!
    pause
    exit /b 1
)

echo.
echo ============================================
echo Deployment complete!
echo Application: http://localhost:4000
echo ============================================
echo.
goto :end

:deploy_down
echo Stopping containers...
docker-compose -f docker-compose.local.yml down
goto :end

:deploy_restart
echo Restarting containers...
docker-compose -f docker-compose.local.yml restart
goto :end

:deploy_logs
echo Viewing logs - Press Ctrl+C to exit
docker-compose -f docker-compose.local.yml logs -f api-local-service
goto :end

:deploy_test
echo Testing API...
curl http://localhost:4000/api/commands
goto :end

:usage
echo Usage: deploy.bat [action]
echo.
echo Actions:
echo   up      - Deploy application
echo   down    - Stop application
echo   restart - Restart application
echo   logs    - View logs
echo   test    - Test API
echo.
echo Examples:
echo   deploy.bat up
echo   deploy.bat logs
echo.
goto :end

:end
endlocal



