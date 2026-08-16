# Changelog

## 0.2.1 - 2026-08-16

- Respect system-bar and display-cutout safe insets in the settings screen.
- Prevent the header/title from rendering underneath centred punch-hole/front-camera cutouts on edge-to-edge Android 15/16 layouts.

## 0.2.0 - 2026-08-16

- Added automatic localization plus Android 13+ per-app language settings.
- Added English, Dutch, Spanish, Italian, Portuguese, French, Russian, Hungarian, Simplified Chinese, and Traditional Chinese.
- Added a new adaptive app icon and in-app logo.
- Added GitHub-ready README files, license, issue templates, changelog, and `.gitignore`.
- Added `BOUW_ALLEEN.bat` for build-only use without ADB.
- Build script now reuses an existing local signing key from older Edge Back Extender folders when available, so upgrades can keep the same app signature.
- Fixed the standalone installer to use the current APK filename.

## 0.1.2

- Reused an existing Android SDK / Android Studio SDK.
- Removed the hard-coded command-line-tools download hash.
- Improved ADB preflight errors.

## 0.1.1

- Added the explicit manifest package required by the direct AAPT2 build.
- Added Android Studio JBR discovery.

## 0.1.0

- First working prototype.
