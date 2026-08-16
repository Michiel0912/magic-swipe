@echo off
setlocal
cd /d "%~dp0"
set ADB=adb
if exist C:\platform_tools\adb.exe set ADB=C:\platform_tools\adb.exe
if not exist MagicSwipe-v0.3.0.apk (
  echo MagicSwipe-v0.3.0.apk is missing. Run BUILD_AND_INSTALL.bat or BUILD_ONLY.bat first.
  pause
  exit /b 1
)
"%ADB%" get-state 1>nul 2>nul || (
  echo No ADB device found.
  pause
  exit /b 1
)
"%ADB%" install -r MagicSwipe-v0.3.0.apk || exit /b 1
"%ADB%" shell am start -n be.michiel.edgeback/.MainActivity
pause
