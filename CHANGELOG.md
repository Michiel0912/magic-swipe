# Changelog

## 0.4.0 - 2026-08-17

- Added an optional built-in GitHub update checker.
- Magic Swipe checks the public GitHub Releases API at most once every 24 hours when the settings screen is opened, with a manual "Check for updates now" option.
- When a newer release is available, Magic Swipe shows the installed and available versions, release notes, and gives the user the choice to update or postpone.
- The Update action opens the official APK asset from the GitHub Release in the browser; Magic Swipe does not request package-installer privileges or silently install updates.
- Added System, Light, and Dark appearance modes. System is the default and follows the phone theme.
- Added a dedicated light color palette rather than simply inverting the existing dark interface.
- Added INTERNET permission only for the settings-screen update checker. The AccessibilityService itself does not use the network and still does not retrieve screen content.
- Kept the v0.3.1 keyboard/IME and 80dp top-exclusion fixes unchanged.
- Confirmed on HONOR Magic8 Pro / MagicOS 10 / Android 16.

## 0.3.1 - 2026-08-16

- Prevent Magic Swipe's transparent edge overlays from covering the visible software keyboard on Android 11+.
- Dynamically read the IME bottom inset and shorten the gesture overlays above the keyboard while it is visible.
- Keep the user's configured bottom exclusion when it is larger than the keyboard inset.
- Raise the default top exclusion from 28dp to 80dp so toolbar actions near the upper corners, such as three-dot menus, remain immediately responsive.
- Migrate the old v0.3.0 default top exclusion to 80dp while preserving custom top-exclusion values.
- Confirmed on HONOR Magic8 Pro / MagicOS 10 / Android 16: edge keyboard keys remain immediately responsive, upper-corner toolbar actions work normally, and Back swipes continue to work in the remaining active edge area.

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
