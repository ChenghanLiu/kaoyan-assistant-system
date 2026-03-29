@echo off
setlocal

cd /d %~dp0

where docker >nul 2>nul
if errorlevel 1 (
  echo Docker CLI not found. Please install Docker Desktop first.
  pause
  exit /b 1
)

docker info >nul 2>nul
if errorlevel 1 (
  echo Docker Desktop is not running. Please start Docker Desktop first.
  pause
  exit /b 1
)

echo Starting containers...
docker compose up -d
if errorlevel 1 (
  echo Start failed.
  pause
  exit /b 1
)

echo Done.
echo Frontend: http://localhost:8081
echo Backend API: http://localhost:18080
pause