param([switch]$BuildOnly)
$ErrorActionPreference = 'Stop'
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$Build = Join-Path $Root 'build'
$OutApk = Join-Path $Root 'EdgeBackExtender-v0.2.1.apk'

function Section([string]$name) { Write-Host "`n===== $name =====" -ForegroundColor Cyan }
function Add-Candidate([System.Collections.Generic.List[string]]$list, [string]$path) {
    if ($path -and (Test-Path $path) -and -not $list.Contains($path)) { $list.Add($path) }
}
function Find-SdkManager([string]$sdkRoot) {
    $candidates = @(
        (Join-Path $sdkRoot 'cmdline-tools\latest\bin\sdkmanager.bat'),
        (Join-Path $sdkRoot 'tools\bin\sdkmanager.bat')
    )
    $cmdlineRoot = Join-Path $sdkRoot 'cmdline-tools'
    if (Test-Path $cmdlineRoot) {
        $candidates += @(Get-ChildItem $cmdlineRoot -Filter sdkmanager.bat -Recurse -ErrorAction SilentlyContinue | ForEach-Object FullName)
    }
    return ($candidates | Where-Object { Test-Path $_ } | Select-Object -First 1)
}
function Find-BuildTools([string]$sdkRoot) {
    $root = Join-Path $sdkRoot 'build-tools'
    if (-not (Test-Path $root)) { return $null }
    $dirs = @(Get-ChildItem $root -Directory -ErrorAction SilentlyContinue | Sort-Object {
        try { [version]($_.Name -replace '-.*$','') } catch { [version]'0.0' }
    } -Descending)
    foreach ($d in $dirs) {
        $need = @('aapt2.exe','d8.bat','zipalign.exe','apksigner.bat')
        $ok = $true
        foreach ($n in $need) { if (-not (Test-Path (Join-Path $d.FullName $n))) { $ok = $false; break } }
        if ($ok) { return $d.FullName }
    }
    return $null
}
function Test-UsableSdk([string]$sdkRoot) {
    if (-not $sdkRoot -or -not (Test-Path $sdkRoot)) { return $false }
    $androidJar = Join-Path $sdkRoot 'platforms\android-36\android.jar'
    if (-not (Test-Path $androidJar)) { return $false }
    return [bool](Find-BuildTools $sdkRoot)
}

Section 'VOORCONTROLE'
$adb = $null
if (-not $BuildOnly) {
    $adbCmd = Get-Command adb.exe -ErrorAction SilentlyContinue
    if ($adbCmd) { $adb = $adbCmd.Source }
    elseif (Test-Path 'C:\platform_tools\adb.exe') { $adb = 'C:\platform_tools\adb.exe' }
    if (-not $adb) { throw 'adb.exe niet gevonden. Zet adb in PATH of gebruik C:\platform_tools\adb.exe.' }

    # Gebruik `adb devices` zodat een niet-verbonden toestel geen lelijke NativeCommandError veroorzaakt.
    $deviceLines = @(& $adb devices 2>$null)
    $device = $deviceLines | Where-Object { $_ -match '\sdevice$' } | Select-Object -First 1
    if (-not $device) { throw 'Geen toestel via ADB verbonden. Verbind eerst de telefoon en controleer met adb devices.' }
    Write-Host "ADB: $adb"
    Write-Host "Toestel: $device"
} else {
    Write-Host 'Build-only modus: ADB/toestel is niet vereist.'
}

$javacCmd = Get-Command javac.exe -ErrorAction SilentlyContinue
$javac = if ($javacCmd) { $javacCmd.Source } else { $null }
if (-not $javac) {
    $javaRoots = @('C:\Program Files\Android\Android Studio\jbr','C:\Program Files\Microsoft','C:\Program Files\Java','C:\Program Files\Eclipse Adoptium')
    foreach ($r in $javaRoots) {
        if (Test-Path $r) {
            $candidate = Get-ChildItem $r -Filter javac.exe -Recurse -ErrorAction SilentlyContinue |
                Sort-Object FullName -Descending | Select-Object -First 1 -ExpandProperty FullName
            if ($candidate) { $javac = $candidate; break }
        }
    }
}
if (-not $javac) { throw 'JDK met javac.exe niet gevonden. Installeer JDK 17+ en voer dit script opnieuw uit.' }
$javaHome = Split-Path (Split-Path $javac)
$jar = Join-Path $javaHome 'bin\jar.exe'
$keytool = Join-Path $javaHome 'bin\keytool.exe'
if (-not (Test-Path $jar) -or -not (Test-Path $keytool)) { throw "Onvolledige JDK: $javaHome" }
Write-Host "JDK: $javaHome"

Section 'ANDROID SDK ZOEKEN'
$candidates = New-Object 'System.Collections.Generic.List[string]'
Add-Candidate $candidates $env:ANDROID_SDK_ROOT
Add-Candidate $candidates $env:ANDROID_HOME
Add-Candidate $candidates (Join-Path $env:LOCALAPPDATA 'Android\Sdk')
Add-Candidate $candidates (Join-Path $env:USERPROFILE 'AppData\Local\Android\Sdk')
Add-Candidate $candidates 'C:\Android\Sdk'
Add-Candidate $candidates (Join-Path $Root '.android-sdk')

# Hergebruik eventueel de SDK die een oudere EdgeBackExtender-build al downloadde.
$parent = Split-Path $Root -Parent
if (Test-Path $parent) {
    foreach ($d in @(Get-ChildItem $parent -Directory -ErrorAction SilentlyContinue)) {
        Add-Candidate $candidates (Join-Path $d.FullName '.android-sdk')
    }
}

$SdkRoot = $null
foreach ($c in $candidates) {
    if (Test-UsableSdk $c) { $SdkRoot = $c; break }
}

# Als er wel een SDK is maar API 36/build-tools ontbreken, probeer die via een reeds geïnstalleerde sdkmanager bij te plaatsen.
if (-not $SdkRoot) {
    foreach ($c in $candidates) {
        $sm = Find-SdkManager $c
        if ($sm) {
            Write-Host "SDK gevonden maar nog niet compleet: $c"
            Write-Host "Installeer Android 16 / API 36 buildonderdelen via: $sm"
            $yes = (1..40 | ForEach-Object { 'y' })
            $yes | & $sm --sdk_root=$c --licenses | Out-Null
            & $sm --sdk_root=$c 'platforms;android-36' 'build-tools;36.0.0'
            if ($LASTEXITCODE -eq 0 -and (Test-UsableSdk $c)) { $SdkRoot = $c; break }
        }
    }
}

if (-not $SdkRoot) {
    throw @'
Geen bruikbare Android SDK gevonden.
Omdat Android Studio nu geinstalleerd is: open Android Studio eenmaal en installeer via SDK Manager:
  - Android 16 / API 36 (SDK Platform)
  - Android SDK Build-Tools 36.x
Voer daarna BOUW_EN_INSTALLEER.bat opnieuw uit.
Het script downloadt vanaf v0.1.2 geen aparte command-line-tools ZIP meer en heeft dus ook geen SHA-256-probleem meer.
'@
}

$env:ANDROID_HOME = $SdkRoot
$env:ANDROID_SDK_ROOT = $SdkRoot
$androidJar = Join-Path $SdkRoot 'platforms\android-36\android.jar'
$bt = Find-BuildTools $SdkRoot
$aapt2 = Join-Path $bt 'aapt2.exe'
$d8 = Join-Path $bt 'd8.bat'
$zipalign = Join-Path $bt 'zipalign.exe'
$apksigner = Join-Path $bt 'apksigner.bat'
Write-Host "SDK: $SdkRoot"
Write-Host "Platform: $androidJar"
Write-Host "Build-tools: $bt"

Section 'BUILD'
Remove-Item $Build -Recurse -Force -ErrorAction SilentlyContinue
$resCompiled = Join-Path $Build 'res.zip'
$gen = Join-Path $Build 'gen'
$classes = Join-Path $Build 'classes'
$dex = Join-Path $Build 'dex'
New-Item -ItemType Directory -Force -Path $Build,$gen,$classes,$dex | Out-Null

& $aapt2 compile --dir (Join-Path $Root 'app\src\main\res') -o $resCompiled
if ($LASTEXITCODE -ne 0) { throw 'aapt2 compile mislukt.' }

$unsigned = Join-Path $Build 'resources-unsigned.apk'
& $aapt2 link -o $unsigned -I $androidJar --manifest (Join-Path $Root 'app\src\main\AndroidManifest.xml') `
    --java $gen --min-sdk-version 26 --target-sdk-version 36 --version-code 5 --version-name '0.2.1' $resCompiled
if ($LASTEXITCODE -ne 0) { throw 'aapt2 link mislukt.' }

$sources = @(Get-ChildItem (Join-Path $Root 'app\src\main\java') -Filter '*.java' -Recurse | ForEach-Object FullName)
$sources += @(Get-ChildItem $gen -Filter '*.java' -Recurse | ForEach-Object FullName)
& $javac -encoding UTF-8 -source 17 -target 17 -classpath $androidJar -d $classes $sources
if ($LASTEXITCODE -ne 0) { throw 'javac mislukt.' }

$classesJar = Join-Path $Build 'classes.jar'
Push-Location $classes
& $jar cf $classesJar .
Pop-Location
& $d8 --lib $androidJar --min-api 26 --output $dex $classesJar
if ($LASTEXITCODE -ne 0) { throw 'd8 mislukt.' }

$withDex = Join-Path $Build 'with-dex.apk'
Copy-Item $unsigned $withDex
& $jar uf $withDex -C $dex 'classes.dex'
if ($LASTEXITCODE -ne 0) { throw 'classes.dex toevoegen mislukt.' }

$aligned = Join-Path $Build 'aligned.apk'
& $zipalign -f -p 4 $withDex $aligned
if ($LASTEXITCODE -ne 0) { throw 'zipalign mislukt.' }

$keystore = Join-Path $Root 'edgeback-local.keystore'
if (-not (Test-Path $keystore)) {
    # Hergebruik de signing key uit een eerdere Edge Back Extender-map zodat upgrades over de bestaande app werken.
    $oldKey = $null
    $parentForKey = Split-Path $Root -Parent
    if (Test-Path $parentForKey) {
        $oldKey = Get-ChildItem $parentForKey -Filter 'edgeback-local.keystore' -Recurse -ErrorAction SilentlyContinue |
            Where-Object { $_.FullName -ne $keystore } | Sort-Object LastWriteTime -Descending |
            Select-Object -First 1 -ExpandProperty FullName
    }
    if ($oldKey) {
        Copy-Item $oldKey $keystore
        Write-Host "Bestaande signing key hergebruikt: $oldKey"
    } else {
        & $keytool -genkeypair -keystore $keystore -storepass android -keypass android -alias edgeback `
            -keyalg RSA -keysize 2048 -validity 10000 -dname 'CN=Edge Back Extender,O=Local,C=BE'
        if ($LASTEXITCODE -ne 0) { throw 'Lokale signing key genereren mislukt.' }
        Write-Host 'Nieuwe lokale signing key aangemaakt. Bewaar edgeback-local.keystore voor toekomstige updates.'
    }
}

Remove-Item $OutApk -Force -ErrorAction SilentlyContinue
Copy-Item $aligned $OutApk
& $apksigner sign --ks $keystore --ks-key-alias edgeback --ks-pass pass:android --key-pass pass:android $OutApk
if ($LASTEXITCODE -ne 0) { throw 'APK signing mislukt.' }
& $apksigner verify --verbose $OutApk
if ($LASTEXITCODE -ne 0) { throw 'APK verificatie mislukt.' }
$apkHash = (Get-FileHash $OutApk -Algorithm SHA256).Hash
Write-Host "APK: $OutApk"
Write-Host "SHA-256: $apkHash"

if ($BuildOnly) {
    Section 'KLAAR'
    Write-Host 'Build-only voltooid; APK is niet geïnstalleerd.' -ForegroundColor Green
    exit 0
}

Section 'INSTALLATIE'
& $adb install -r $OutApk
if ($LASTEXITCODE -ne 0) { throw 'adb install mislukt. Als INSTALL_FAILED_UPDATE_INCOMPATIBLE verschijnt, gebruik dan de signing key van de reeds geïnstalleerde versie.' }
& $adb shell am start -n 'be.michiel.edgeback/.MainActivity'
Write-Host 'App geopend. Activeer Edge Back Extender onder Toegankelijkheid indien nodig.' -ForegroundColor Green
Write-Host 'Startinstelling: totaal 24dp; de native back-zone wordt automatisch gedetecteerd en NIET overlapt.'
