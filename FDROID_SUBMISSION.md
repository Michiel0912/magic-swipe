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
- Real device validation for Magic Swipe v0.3.0: complete on HONOR Magic8 Pro / MagicOS 10 / Android 16
- Definitive Magic Swipe upstream signing identity: established before public distribution
- Release artifact SHA-256 and certificate fingerprint: documented in `docs/releases/v0.3.0.md`
- Store icon PNG under Fastlane metadata: pending
- Real screenshots for the store listing: pending
- `v0.3.0` release tag: pending
- `fdroiddata` lint/build CI: pending
- F-Droid inclusion merge request: pending

## Validation completed

The validated v0.3.0 installation successfully passed left and right Back swipes, test mode, per-app language selection, AccessibilityService operation, and reboot persistence on the HONOR Magic8 Pro.

The older pre-release Edge Back Extender signing key was unavailable. The development installation therefore required a one-time uninstall before the definitive Magic Swipe signing identity was established. This happened before public distribution, so v0.3.0 is the baseline signing identity for direct upstream APK releases.

## Remaining steps before submission

1. Export the existing Magic Swipe logo as `fastlane/metadata/android/en-US/images/icon.png`.
2. Capture at least two real screenshots of the app UI under `fastlane/metadata/android/en-US/images/phoneScreenshots/`.
3. Create the GitHub `v0.3.0` release/tag for the validated source and publish the verified APK artifact.
4. Copy `fdroid/be.michiel.edgeback.yml` into a fork of `fdroiddata` as `metadata/be.michiel.edgeback.yml`.
5. Run the F-Droid checks (`readmeta`, `rewritemeta`, `checkupdates`, `lint`, and `build`) or use fdroiddata CI.
6. Open a merge request to the official `fdroiddata` repository.

## Reproducible builds

Reproducible upstream-signed builds are desirable but are not required for initial inclusion. Magic Swipe is a small Java/resources-only app, so reproducibility is worth attempting after the unsigned F-Droid build recipe is confirmed. Do not change signing strategy until that test is complete, because Android update compatibility depends on the signing key.
