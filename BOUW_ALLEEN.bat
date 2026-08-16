@echo off
setlocal
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0build_install.ps1" -BuildOnly
if errorlevel 1 (
  echo.
  echo BUILD MISLUKT.
  pause
  exit /b 1
)
echo.
echo KLAAR.
pause
