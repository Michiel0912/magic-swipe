<p align="center">
  <img src="docs/logo.svg" width="132" alt="Magic Swipe logo">
</p>

# Magic Swipe

**Make Back swipes comfortable again.**

Magic Swipe adds narrow, transparent touch bands just inside Android's native Back-gesture edges. It is designed for phones where a protective case, raised side lip, or curved display makes the normal edge swipe difficult to start.

It does **not** replace Android's native gesture area. Magic Swipe only fills the extra space between the detected native Back edge and the total width selected by the user.

## Compatibility

> **Important:** Magic Swipe has currently been tested only on an **HONOR Magic8 Pro running MagicOS 10 / Android 16**. All other compatibility statements below are expectations based on the Android APIs used by the app, not confirmed device testing.

| Platform / device family | Status | Notes |
|---|---|---|
| HONOR Magic8 Pro / MagicOS 10 | ✅ Tested | Development and validation device. |
| HONOR devices on MagicOS 6–10 | 🟡 Expected | Uses standard Android AccessibilityService overlay and global Back APIs. Real-device reports are welcome. |
| HONOR Magic V foldables | 🟡 Expected / untested | The service rebuilds its overlays after configuration changes, but foldable postures still need community testing. |
| Older Magic UI / MagicOS releases | 🟠 Experimental | May work when gesture navigation and accessibility overlays are available. |
| Other Android brands | 🟠 Experimental | The implementation is not HONOR-specific, but behavior can vary by OEM. |

The APK has `minSdkVersion 26` (Android 8.0). `TYPE_ACCESSIBILITY_OVERLAY` and `GLOBAL_ACTION_BACK` are standard Android accessibility capabilities; Magic Swipe relies on those rather than root access or HONOR system modification.

See [COMPATIBILITY.md](COMPATIBILITY.md) for the test policy and how to report a working device.

## Features

- Extends the usable Back-swipe start area on the left and/or right edge
- Automatically detects the native Back-zone width when the Android resource is available
- Adjustable total Back-zone width and swipe distance
- Adjustable top and bottom exclusions to protect other gestures
- Optional haptic feedback
- Test mode that makes the otherwise transparent extra zones visible
- Rebuilds overlays after configuration changes and works again after reboot once the AccessibilityService is enabled
- Respects status-bar and display-cutout safe insets
- Dark Magic Swipe interface with cyan accent styling
- No root required
- No `INTERNET` permission
- No analytics or trackers
- No screen-content retrieval

## Languages

Magic Swipe follows the system language by default and supports Android 13+ per-app language selection.

The language set is intentionally broad because HONOR's international footprint is strongest across Latin America, Europe, the Middle East & Africa, and is expanding in South-Eastern Asia. Magic Swipe also covers major languages for China and South Asia.

Current translations include:

- **Global / Europe:** English, Dutch, German, French, Spanish, Italian, Portuguese, Polish, Romanian, Czech, Hungarian, Russian, Ukrainian, Turkish
- **Latin America:** Spanish, Brazilian Portuguese
- **Middle East & Africa:** Arabic, Persian, Turkish, English, French, Swahili
- **South Asia:** English, Hindi, Bengali, Marathi, Telugu, Urdu
- **South-Eastern Asia:** Indonesian, Malay, Thai, Vietnamese, Filipino
- **Greater China:** Simplified Chinese, Traditional Chinese

See [LANGUAGES.md](LANGUAGES.md) for the complete locale list.

## Why an AccessibilityService?

Magic Swipe uses an Android AccessibilityService because an accessibility overlay can intercept interaction in its own narrow edge zone and `performGlobalAction(GLOBAL_ACTION_BACK)` can trigger the system Back action without root.

The service is configured with `canRetrieveWindowContent=false`. It does not inspect the active app, window contents, messages, or typed text.

## Build on Windows

Requirements:

- JDK 17+
- Android SDK Platform 36 and Build-Tools 36.x
- ADB only when using the build-and-install workflow

Build and install:

```text
BUILD_AND_INSTALL.bat
```

Build without a connected phone:

```text
BUILD_ONLY.bat
```

Install an already-built APK:

```text
INSTALL_ONLY.bat
```

The output APK is:

```text
MagicSwipe-v0.3.0.apk
```

### Signing and upgrades

The Android package ID remains `be.michiel.edgeback` and the AccessibilityService component remains unchanged. Those internal legacy identifiers are intentionally retained so existing Edge Back Extender installations can upgrade to Magic Swipe instead of becoming a second app.

The local build uses `edgeback-local.keystore`. Keep that signing key safe if you distribute builds that must upgrade over one another.

## Privacy

Magic Swipe is intentionally minimal:

- No Internet permission
- No account
- No analytics
- No trackers
- No screen-content retrieval
- No root
- No system APK or framework modification

## Market and compatibility sources

The compatibility rationale is based on the standard Android accessibility APIs used by the app. The regional language coverage is informed by current HONOR shipment trends, including Latin America as a major overseas volume contributor, strong growth in the Middle East & Africa, a top-five position in Europe in 2025, and expansion in South-Eastern Asia.

- [Android Developers: AccessibilityService / `GLOBAL_ACTION_BACK`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService)
- [Android Developers: `TYPE_ACCESSIBILITY_OVERLAY`](https://developer.android.com/reference/android/view/accessibility/AccessibilityWindowInfo)
- [HONOR Global phone comparison: MagicOS 10 based on Android 16](https://www.honor.com/global/phones/comparison/)
- [Omdia: HONOR overseas expansion and regional dynamics in 2025](https://omdia.tech.informa.com/om143052/honor-expands-overseas-in-2025-scale-portfolio-and-regional-dynamics)
- [Omdia: Latin America smartphone market, 2025](https://omdia.tech.informa.com/pr/2026/feb/omdia-latin-americas-smartphone-market-hits-record-140point5-million-units-in-2025-up-12percent-in-4q)
- [Omdia: Europe smartphone market, 2025](https://omdia.tech.informa.com/pr/2026/feb/apple-and-honor-claim-record-market-shares-as-europes-smartphone-shipment-dips-1percent-in-2025)

## Contributing

Device confirmations, bug reports, translations, and focused pull requests are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md).

## License

MIT License. See [LICENSE](LICENSE).
