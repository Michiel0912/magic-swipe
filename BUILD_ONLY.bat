@echo off
setlocal
cd /d "%~dp0"

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0signing_key_sync.ps1" -BuildOnly
if errorlevel 1 (
  echo.
  echo SIGNING PRE-CHECK FAILED.
  pause
  exit /b 1
)

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0build.ps1" -BuildOnly
if errorlevel 1 (
  echo.
  echo BUILD FAILED.
  pause
  exit /b 1
)

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0signing_key_sync.ps1" -AfterBuild -BuildOnly
if errorlevel 1 (
  echo.
  echo SIGNING BACKUP FAILED.
  pause
  exit /b 1
)

echo.
echo DONE.
pause
