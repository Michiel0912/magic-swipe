param(
    [switch]$AfterBuild,
    [switch]$BuildOnly
)

$ErrorActionPreference = 'Stop'
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectKey = Join-Path $Root 'edgeback-local.keystore' # Legacy local filename used by build.ps1.
$PrivateDir = Join-Path $env:USERPROFILE '.magic-swipe'
$ReleaseKey = Join-Path $PrivateDir 'magic-swipe-release.keystore'

function Same-File([string]$a, [string]$b) {
    if (-not (Test-Path $a) -or -not (Test-Path $b)) { return $false }
    return (Get-FileHash $a -Algorithm SHA256).Hash -eq (Get-FileHash $b -Algorithm SHA256).Hash
}

if ($AfterBuild) {
    if (-not (Test-Path $ProjectKey)) {
        throw 'Build completed without a local signing keystore.'
    }
    New-Item -ItemType Directory -Force -Path $PrivateDir | Out-Null
    if (Test-Path $ReleaseKey) {
        if (-not (Same-File $ProjectKey $ReleaseKey)) {
            throw "Signing key mismatch: project key differs from the persistent Magic Swipe release key at $ReleaseKey. The persistent key was NOT overwritten."
        }
    } else {
        Copy-Item $ProjectKey $ReleaseKey
        Write-Host "Backed up signing key to: $ReleaseKey" -ForegroundColor Green
    }
    exit 0
}

if (Test-Path $ReleaseKey) {
    if (-not (Test-Path $ProjectKey) -or -not (Same-File $ProjectKey $ReleaseKey)) {
        Copy-Item $ReleaseKey $ProjectKey -Force
        Write-Host "Restored persistent Magic Swipe signing key from: $ReleaseKey" -ForegroundColor Green
    } else {
        Write-Host "Persistent signing key verified: $ReleaseKey"
    }
    exit 0
}

if (Test-Path $ProjectKey) {
    New-Item -ItemType Directory -Force -Path $PrivateDir | Out-Null
    Copy-Item $ProjectKey $ReleaseKey
    Write-Host "Created persistent signing-key backup: $ReleaseKey" -ForegroundColor Green
    exit 0
}

if (-not $BuildOnly) {
    $adb = $null
    $adbCmd = Get-Command adb.exe -ErrorAction SilentlyContinue
    if ($adbCmd) { $adb = $adbCmd.Source }
    elseif (Test-Path 'C:\platform_tools\adb.exe') { $adb = 'C:\platform_tools\adb.exe' }

    if ($adb) {
        $installed = @(& $adb shell pm path be.michiel.edgeback 2>$null)
        if ($installed -match '^package:') {
            throw @"
Magic Swipe / Edge Back Extender is already installed, but no local signing key is available.
A new key would make Android reject the update with INSTALL_FAILED_UPDATE_INCOMPATIBLE.
Recover the original signing key or intentionally uninstall the old app before creating a new signing identity.
"@
        }
    }
}

Write-Host 'No existing signing key found. build.ps1 may create a new local key; it will be backed up after a successful build.' -ForegroundColor Yellow
