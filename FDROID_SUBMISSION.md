# F-Droid submission status

Magic Swipe is being prepared for the official F-Droid repository. The F-Droid preparation version is v0.4.1 / versionCode 9.

## Current state

- Public source repository: ready
- FOSS license (MIT): ready
- No proprietary runtime dependencies: ready
- No analytics, trackers, Google Play Services, or screen-content retrieval: ready
- AccessibilityService network use: none
- INTERNET permission: used only by the optional settings-screen GitHub update checker
- Manual GitHub update checks: explicit user action
- Automatic GitHub update checks: disabled by default in v0.4.1
- Automatic GitHub update consent: explicit informed consent required
- External APK warning: clearly states that GitHub APK downloads are outside F-Droid and bypass F-Droid build verification
- Upstream Fastlane/Triple-T English text metadata: ready
- F-Droid build recipe: ready under `fdroid/`
- Linux source-build helper: ready as `fdroid/build.sh`
- F-Droid candidate metadata: ready as `fdroid/be.michiel.edgeback.yml`
- Real-device validation for the gesture service: complete on HONOR Magic8 Pro / MagicOS 10 / Android 16
- Definitive Magic Swipe upstream signing identity: established before public distribution
- Store icon PNG under Fastlane metadata: pending
- Real app screenshots for the store listing: pending
- v0.4.1 real-device validation: pending
- v0.4.1 release tag and signed APK: pending
- F-Droid/fdroiddata CI build: pending
- Reproducible-build comparison against the upstream-signed APK: pending
- F-Droid inclusion merge request: pending

## Why v0.4.1 is the F-Droid candidate

F-Droid's inclusion policy requires executable downloads such as external auto-updates to be explicit opt-in and to explain that enabling them bypasses F-Droid's checks. v0.4.0 enabled automatic GitHub update checks by default, so v0.4.1 changes that behavior before submission.

On upgrade from v0.4.0, v0.4.1 resets automatic GitHub checks to disabled. Enabling them presents an informed-consent dialog. The update-available dialog also warns before opening an external GitHub APK. Manual update checks remain available as an explicit user action.

## Remaining steps before submission

1. Build and install the v0.4.1 candidate on real hardware.
2. Verify the update-consent flow, manual update check, appearance modes, AccessibilityService behavior, keyboard safe zone, and upper-corner safe zone.
3. Build the unsigned APK with `fdroid/build.sh` on Linux/CI and verify package ID, versionName, and versionCode.
4. Publish the signed v0.4.1 GitHub APK using the established Magic Swipe signing identity and record its SHA-256/certificate fingerprint.
5. Attempt a reproducible-build comparison. If it matches, add `Binaries` and `AllowedAPKSigningKeys` so F-Droid can publish the upstream-signed APK. If it does not match initially, submit with normal F-Droid signing and continue reproducibility work separately.
6. Export the Magic Swipe logo to `fastlane/metadata/android/en-US/images/icon.png`.
7. Add at least two real app screenshots under `fastlane/metadata/android/en-US/images/phoneScreenshots/`.
8. Copy `fdroid/be.michiel.edgeback.yml` into a public fork of `fdroiddata` as `metadata/be.michiel.edgeback.yml`.
9. Run/confirm the fdroiddata pipeline (`lint`, source scan, metadata checks, and build).
10. Open the official app-inclusion merge request after the build pipeline passes.

## Reproducible builds

Reproducible upstream-signed builds are preferred because users can then move between the GitHub and F-Droid APKs without changing signing identities. Magic Swipe is a strong candidate because it is a small Java/resources-only app with no native libraries or third-party dependency tree.

Do not add `Binaries`/`AllowedAPKSigningKeys` to the official fdroiddata submission until a real v0.4.1 upstream APK has been compared successfully with the F-Droid-style source build.
