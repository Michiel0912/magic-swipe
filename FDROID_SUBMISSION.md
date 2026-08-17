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
- Privacy policy: ready as `PRIVACY.md`
- Upstream Fastlane/Triple-T English text metadata: ready
- F-Droid build recipe: ready under `fdroid/`
- Linux source-build helper: ready as `fdroid/build.sh`
- F-Droid candidate metadata: ready as `fdroid/be.michiel.edgeback.yml`
- Store icon PNG: ready at `fastlane/metadata/android/en-US/images/icon.png`
- GitHub Actions Linux source build: passing
- Current fdroidserver + current fdroiddata metadata read/lint: passing
- Full `fdroid build --test` through current fdroidserver/current fdroiddata: passing
- F-Droid source scan in that build: passing
- CI-verified package ID: `be.michiel.edgeback`
- CI-verified candidate version: `0.4.1` / versionCode 9 / compileSdk 36
- Repeatable Linux unsigned APK SHA-256: `c0f1016b2e1b76175ae4dcbbca7cfc4792176adbe8615a3831efee488a5efc82`
- Consecutive normalized Linux builds produce the same APK SHA-256: confirmed
- Real-device validation for the gesture service: complete on HONOR Magic8 Pro / MagicOS 10 / Android 16
- Definitive Magic Swipe upstream signing identity: established before public distribution
- Real app screenshots for the store listing: pending
- v0.4.1 real-device validation: pending
- v0.4.1 release tag and signed APK: pending
- Official fdroiddata inclusion merge-request pipeline: pending
- Cross-platform reproducible-build comparison against the upstream-signed APK: pending
- F-Droid inclusion merge request: pending

## Why v0.4.1 is the F-Droid candidate

F-Droid's inclusion policy requires executable downloads such as external auto-updates to be explicit opt-in and to explain that enabling them bypasses F-Droid's checks. v0.4.0 enabled automatic GitHub update checks by default, so v0.4.1 changes that behavior before submission.

On upgrade from v0.4.0, v0.4.1 resets automatic GitHub checks to disabled. Enabling them presents an informed-consent dialog. The update-available dialog also warns before opening an external GitHub APK. Manual update checks remain available as an explicit user action.

## Validation completed

The candidate builds successfully on a clean Ubuntu GitHub Actions runner using JDK 17, Android API 36 and Android Build-Tools 36.0.0. CI validates the resulting unsigned APK's package ID, versionName and versionCode before uploading it as an artifact.

An initial reproducibility check found that repeated builds contained byte-identical APK entries but produced different APK hashes because the `classes.dex` ZIP entry inherited the wall-clock build timestamp. The Linux and Windows build scripts now normalize that timestamp before packaging.

After normalization, two consecutive Linux source builds produced the same unsigned APK SHA-256:

`c0f1016b2e1b76175ae4dcbbca7cfc4792176adbe8615a3831efee488a5efc82`

This confirms byte-repeatability of the Linux source build. It is a source-build validation hash, not the final signed release hash.

The candidate metadata has been read and linted successfully using the current fdroidserver against a fresh clone of the current fdroiddata configuration.

A second CI workflow then ran the candidate through the real fdroidserver path using `fdroid build --test --no-tarball --stop --verbose be.michiel.edgeback:9`. fdroidserver cloned the Magic Swipe source from GitHub, checked out the candidate commit, installed the declared Android SDK components, scanned the source for common problems, executed the metadata build recipe and successfully validated the resulting APK. The run finished with `1 build succeeded`.

The test runner was not a dedicated F-Droid build-server VM, so fdroidserver intentionally skipped the metadata `sudo` provisioning commands. JDK 17 was already available on the runner, and the app build itself completed successfully. The official build server will execute those provisioning commands.

## Remaining steps before submission

1. Build and install the v0.4.1 candidate on real hardware.
2. Verify the update-consent flow, manual update check, appearance modes, AccessibilityService behavior, keyboard safe zone, and upper-corner safe zone.
3. Publish the signed v0.4.1 GitHub APK using the established Magic Swipe signing identity and record its SHA-256/certificate fingerprint.
4. Compare the unsigned payload of the Windows upstream build with the repeatable Linux source build. If it matches, add `Binaries` and `AllowedAPKSigningKeys` so F-Droid can publish the upstream-signed APK. If it does not match initially, submit with normal F-Droid signing and continue cross-platform reproducibility work separately.
5. Add real app screenshots under `fastlane/metadata/android/en-US/images/phoneScreenshots/` if available. They are desirable store metadata but should not block technical build validation.
6. Copy `fdroid/be.michiel.edgeback.yml` into a public fork of `fdroiddata` as `metadata/be.michiel.edgeback.yml`.
7. Run/confirm the official fdroiddata inclusion merge-request pipeline.
8. Open the official app-inclusion merge request after the pipeline passes.

## Reproducible builds

Repeatability of the Linux source build is now confirmed. The remaining reproducibility question is cross-platform equivalence between the final Windows-built upstream APK payload and the F-Droid-style Linux source build.

Reproducible upstream-signed builds are preferred because users can then move between the GitHub and F-Droid APKs without changing signing identities. Magic Swipe is a strong candidate because it is a small Java/resources-only app with no native libraries or third-party dependency tree.

Do not add `Binaries`/`AllowedAPKSigningKeys` to the official fdroiddata submission until a real v0.4.1 upstream APK has been compared successfully with the F-Droid-style source build.
