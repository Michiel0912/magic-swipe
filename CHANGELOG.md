# Changelog

## 0.3.0 - 2026-08-16

- Fully rebranded the app as **Magic Swipe** while keeping the existing Android package ID and AccessibilityService component for upgrade compatibility.
- Rebuilt the settings screen with the dark navy/cyan Magic Swipe visual identity.
- Added a new Magic Swipe app icon and repository logo inspired by the edge-swipe/back gesture concept.
- Added explicit compatibility documentation: currently tested only on HONOR Magic8 Pro / MagicOS 10, with broader MagicOS compatibility marked as expected or experimental until confirmed.
- Expanded language coverage for HONOR's major regions, including South Asian, Middle Eastern, African, Eastern European, and South-Eastern Asian locales.
- Added `COMPATIBILITY.md` and `LANGUAGES.md`.
- Renamed all public build helpers to English: `BUILD_AND_INSTALL.bat`, `BUILD_ONLY.bat`, and `INSTALL_ONLY.bat`.
- Renamed the PowerShell build helper to `build.ps1` and converted build output/messages/comments to English.
- Removed the separate Dutch README so the public repository uses English documentation consistently.
- Kept the app privacy model unchanged: no Internet permission, analytics, trackers, root, or screen-content retrieval.

## 0.2.1 - 2026-08-16

- Respect system-bar and display-cutout safe insets in the settings screen.
- Prevent the header/title from rendering underneath centered punch-hole/front-camera cutouts on edge-to-edge Android 15/16 layouts.

## 0.2.0 - 2026-08-16

- Added automatic localization plus Android 13+ per-app language settings.
- Added initial multilingual coverage.
- Added an adaptive app icon and in-app logo.
- Added GitHub-ready README files, license, issue templates, changelog, and `.gitignore`.
- Added a build-only workflow without ADB.
- Build script can reuse an existing local signing key from older pre-v0.3 project folders so upgrades can keep the same app signature.

## 0.1.2

- Reused an existing Android SDK / Android Studio SDK.
- Removed the hard-coded command-line-tools download hash.
- Improved ADB preflight errors.

## 0.1.1

- Added the explicit manifest package required by the direct AAPT2 build.
- Added Android Studio JBR discovery.

## 0.1.0

- First working prototype.
