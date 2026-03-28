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

echo Stopping containers...
docker compose down
if errorlevel 1 (
  echo Stop failed.
  pause
  exit /b 1
)

echo Done.
pause
