@echo off
setlocal
cd /d "%~dp0"
set ADB=adb
if exist C:\platform_tools\adb.exe set ADB=C:\platform_tools\adb.exe
if not exist EdgeBackExtender-v0.2.1.apk (
  echo EdgeBackExtender-v0.2.1.apk ontbreekt. Draai eerst BOUW_EN_INSTALLEER.bat of BOUW_ALLEEN.bat.
  pause
  exit /b 1
)
"%ADB%" get-state 1>nul 2>nul || (
  echo Geen ADB-toestel gevonden.
  pause
  exit /b 1
)
"%ADB%" install -r EdgeBackExtender-v0.2.1.apk || exit /b 1
"%ADB%" shell am start -n be.michiel.edgeback/.MainActivity
pause
