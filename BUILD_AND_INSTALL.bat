@echo off
setlocal
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0build.ps1"
if errorlevel 1 (
  echo.
  echo BUILD/INSTALL FAILED.
  pause
  exit /b 1
)
echo.
echo DONE.
pause
