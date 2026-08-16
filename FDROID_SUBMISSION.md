# F-Droid submission status

Magic Swipe is being prepared for the official F-Droid repository.

## Current state

- Public source repository: ready
- FOSS license (MIT): ready
- No proprietary runtime dependencies: ready
- No Internet permission, analytics, trackers, or Google Play Services: ready
- Upstream Fastlane/Triple-T text metadata: ready for English
- F-Droid build recipe: added under `fdroid/`
- Linux source-build helper: added as `fdroid/build.sh`
- Store icon PNG under Fastlane metadata: pending
- Real screenshots for the store listing: pending
- Real device validation for the release: pending
- `v0.3.0` release tag: pending until the build is validated
- `fdroiddata` lint/build CI: pending
- F-Droid inclusion merge request: pending

## Required validation before submission

1. Build and install Magic Swipe v0.3.0 on the HONOR Magic8 Pro.
2. Confirm that it upgrades the existing installation, preserves settings, and keeps the AccessibilityService enabled.
3. Confirm left and right Back swipes, reboot persistence, language selection, and display-cutout layout.
4. Export the existing Magic Swipe logo as `fastlane/metadata/android/en-US/images/icon.png`.
5. Capture at least two real screenshots of the app UI under `fastlane/metadata/android/en-US/images/phoneScreenshots/`.
6. Tag the exact validated commit as `v0.3.0`.
7. Copy `fdroid/be.michiel.edgeback.yml` into a fork of `fdroiddata` as `metadata/be.michiel.edgeback.yml`.
8. Run the F-Droid checks (`readmeta`, `rewritemeta`, `checkupdates`, `lint`, and `build`) or use fdroiddata CI.
9. Open a merge request to the official `fdroiddata` repository.

## Reproducible builds

Reproducible upstream-signed builds are desirable but are not required for initial inclusion. Magic Swipe is a small Java/resources-only app, so reproducibility is worth attempting after the unsigned F-Droid build recipe is confirmed. Do not change signing strategy until that test is complete, because Android update compatibility depends on the signing key.
