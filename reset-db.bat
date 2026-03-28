@echo off
setlocal

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

echo Resetting database and rebuilding images...
docker compose down -v
if errorlevel 1 (
  echo Docker compose down -v failed.
  pause
  exit /b 1
)

docker compose up -d --build
if errorlevel 1 (
  echo Reset failed.
  pause
  exit /b 1
)

echo Done.
echo Frontend: http://localhost:8081
echo Backend API: http://localhost:18080
pause
