<p align="center">
  <img src="docs/logo.svg" width="128" alt="Edge Back Extender logo">
</p>

# Edge Back Extender

Edge Back Extender adds a narrow transparent touch band just inside Android's native Back-gesture edge. It is useful when a protective case makes it difficult to start the normal edge swipe.

The app does **not** replace the native gesture zone. It only extends the usable start area inward and calls the system Back action after a clear horizontal swipe.

## Features

- Left and right edge can be enabled independently.
- Adjustable total Back-zone width.
- Adjustable swipe distance.
- Adjustable top and bottom exclusions.
- Optional haptic feedback.
- Test mode to visualize the added zones.
- Automatically detects the platform's native Back inset when available.
- No root required.
- No Internet permission, analytics, or screen-content access.
- Per-app language support on Android 13+; otherwise follows the system language.

## Languages

English, Dutch, Spanish, Italian, Portuguese, French, Russian, Hungarian, Simplified Chinese, and Traditional Chinese.

## Privacy and Accessibility

Edge Back Extender uses an Android AccessibilityService only because `TYPE_ACCESSIBILITY_OVERLAY` and `GLOBAL_ACTION_BACK` provide a reliable non-root way to add the extra edge gesture.

The service configuration explicitly uses `canRetrieveWindowContent=false`. The app does not request the `INTERNET` permission and does not collect analytics.

## Build on Windows

Requirements:

- JDK 17 or newer
- Android SDK Platform 36 and compatible Build Tools
- ADB only when installing directly to a device

Run:

```text
BOUW_EN_INSTALLEER.bat
```

For a build without a connected device:

```text
BOUW_ALLEEN.bat
```

The resulting APK is `EdgeBackExtender-v0.2.1.apk`.

### Updating an existing installation

Android requires updates to be signed with the same key. The build script automatically searches sibling Edge Back Extender folders for an existing `edgeback-local.keystore` and reuses it. Keep that keystore safe for future releases.

## Install

1. Install the APK.
2. Open Edge Back Extender.
3. Open Accessibility settings from the app.
4. Enable the Edge Back Extender service.
5. Start with the default 24dp total zone and adjust only if needed.

## Source layout

- `app/src/main/java/` – app and AccessibilityService source
- `app/src/main/res/` – translations, icon, locale config and service config
- `build_install.ps1` – standalone Windows build/install script
- `BOUW_EN_INSTALLEER.bat` – build + install
- `BOUW_ALLEEN.bat` – build only

## License

MIT. See [LICENSE](LICENSE).
