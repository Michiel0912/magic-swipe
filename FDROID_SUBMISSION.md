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
- Android build toolchain pinned to Build-Tools 36.0.0 for aapt2/D8/zipalign
- Current pinned Linux unsigned APK SHA-256: `c01e5922d5fd55418051d8ae8ac4edaf67a35eaa0d6b17d2a43da4466119b803`
- Windows upstream APK and pinned Linux source build contain byte-identical non-signature APK entries: confirmed
- Entry order, compression method, ZIP timestamps and payload bytes match for all 12 unsigned APK entries: confirmed
- Real-device v0.4.1 validation: complete on HONOR Magic8 Pro / MagicOS 10 / Android 16
- Definitive Magic Swipe upstream signing identity: confirmed unchanged
- Windows release signer subject: `C=BE, O=Local, CN=Magic Swipe`
- Windows release certificate SHA-256: `3B:8A:F9:D1:F2:D2:FF:AD:81:45:A3:4A:05:A8:05:BB:D4:C5:A5:08:32:72:0D:E2:76:25:3F:CD:B8:CA:AD:A6`
- Reproducible-signature compatibility hardening: Windows build now uses Build-Tools 34.0.0 `apksigner` while retaining Build-Tools 36.0.0 for compilation/packaging
- Real app screenshots for the store listing: pending
- Final v0.4.1 release tag and reproducibly signed APK: pending
- Official fdroiddata inclusion merge-request pipeline: pending
- Final F-Droid signature-copy verification against the published upstream APK: pending
- F-Droid inclusion merge request: pending

## Why v0.4.1 is the F-Droid candidate

F-Droid's inclusion policy requires executable downloads such as external auto-updates to be explicit opt-in and to explain that enabling them bypasses F-Droid's checks. v0.4.0 enabled automatic GitHub update checks by default, so v0.4.1 changes that behavior before submission.

On upgrade from v0.4.0, v0.4.1 resets automatic GitHub checks to disabled. Enabling them presents an informed-consent dialog. The update-available dialog also warns before opening an external GitHub APK. Manual update checks remain available as an explicit user action.

## Validation completed

The candidate builds successfully on a clean Ubuntu GitHub Actions runner using JDK 17, Android API 36 and Android Build-Tools 36.0.0. CI validates the resulting unsigned APK's package ID, versionName and versionCode before uploading it as an artifact.

An initial reproducibility check found that repeated builds contained byte-identical APK entries but produced different APK hashes because the `classes.dex` ZIP entry inherited the wall-clock build timestamp. The Linux and Windows build scripts now normalize that timestamp before packaging.

A later Windows/Linux comparison exposed a second toolchain issue: the build scripts had been selecting different installed D8 versions. `classes.dex` was the only payload entry that differed and its embedded D8 metadata showed the mismatch. Both build paths are now pinned to Android Build-Tools 36.0.0.

After pinning, the current Linux source build produced:

`c01e5922d5fd55418051d8ae8ac4edaf67a35eaa0d6b17d2a43da4466119b803  MagicSwipe-v0.4.1-unsigned.apk`

The uploaded, device-tested Windows APK was then compared entry-by-entry against that pinned Linux build. Excluding only the three v1 signature files (`META-INF/MANIFEST.MF`, `META-INF/EDGEBACK.SF`, `META-INF/EDGEBACK.RSA`), all 12 APK entries match byte-for-byte. Their ordering, compression type, ZIP timestamps and other inspected ZIP metadata also match. This establishes cross-platform payload reproducibility for the candidate.

The uploaded Windows candidate itself has SHA-256 `968d86a461e26a38ce0c8645e08e64ad6e0a94041275decd7a576227a3203159` and is signed with the established Magic Swipe certificate. That APK is a validation candidate, not the final v0.4.1 release asset, because the release signing step is being hardened for F-Droid signature-copy compatibility.

Current F-Droid/apksigcopier guidance documents compatibility problems with APKs signed by `apksigner` from Build-Tools 35 and newer unless special alignment handling is used. To use the conservative, documented-compatible route, the Windows release script now keeps Build-Tools 36.0.0 for aapt2/D8/zipalign but uses `apksigner` from Build-Tools 34.0.0. This does not change the private key or signing certificate.

The candidate metadata has been read and linted successfully using the current fdroidserver against a fresh clone of the current fdroiddata configuration.

A second CI workflow ran the candidate through the real fdroidserver path using `fdroid build --test --no-tarball --stop --verbose be.michiel.edgeback:9`. fdroidserver cloned the Magic Swipe source from GitHub, checked out the candidate commit, installed the declared Android SDK components, scanned the source for common problems, executed the metadata build recipe and successfully validated the resulting APK. The run finished with `1 build succeeded`.

The test runner was not a dedicated F-Droid build-server VM, so fdroidserver intentionally skipped the metadata `sudo` provisioning commands. JDK 17 was already available on the runner, and the app build itself completed successfully. The official build server will execute those provisioning commands.

## Remaining steps before submission

1. Rebuild the already device-validated v0.4.1 source using the updated Windows build script so the APK is signed with the same established key through Build-Tools 34.0.0 `apksigner`.
2. Reconfirm the final APK certificate fingerprint and SHA-256.
3. Confirm that its unsigned payload still matches the pinned Linux source build and perform the final F-Droid signature-copy verification.
4. Publish the signed v0.4.1 GitHub APK and checksum, then create the final `v0.4.1` tag from the exact release source commit.
5. If signature-copy verification succeeds, add `Binaries` and `AllowedAPKSigningKeys` to use the upstream-signed F-Droid route.
6. Add real app screenshots under `fastlane/metadata/android/en-US/images/phoneScreenshots/` if available. They are desirable store metadata but should not block technical build validation.
7. Copy `fdroid/be.michiel.edgeback.yml` into a public fork of `fdroiddata` as `metadata/be.michiel.edgeback.yml`.
8. Run/confirm the official fdroiddata inclusion merge-request pipeline and open the official app-inclusion merge request after it passes.

## Reproducible builds

Cross-platform payload reproducibility between the Windows upstream build and the pinned Linux F-Droid-style build is now confirmed. The remaining reproducibility gate is signature copying/verifiability for the final upstream-signed APK.

Reproducible upstream-signed builds are preferred because users can then move between the GitHub and F-Droid APKs without changing signing identities. Magic Swipe is a strong candidate because it is a small Java/resources-only app with no native libraries or third-party dependency tree.

Do not add `Binaries`/`AllowedAPKSigningKeys` to the official fdroiddata submission until the final v0.4.1 upstream APK has passed signature-copy verification.
