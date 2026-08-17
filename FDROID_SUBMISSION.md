# F-Droid submission status

Magic Swipe is being prepared for the official F-Droid repository. The current candidate is v0.4.1 / versionCode 9.

## Ready

- Public source repository and MIT license
- No proprietary runtime dependencies, analytics, trackers, Google Play Services, or screen-content retrieval
- AccessibilityService network use: none
- GitHub update checks are optional; automatic checks are disabled by default and require explicit informed consent
- External GitHub APK downloads are clearly disclosed as outside F-Droid verification
- Privacy policy: `PRIVACY.md`
- Fastlane/Triple-T English metadata and store icon
- F-Droid build recipe: `fdroid/be.michiel.edgeback.yml`
- Linux source-build helper: `fdroid/build.sh`
- GitHub Actions Linux source build: passing
- Current fdroidserver/current fdroiddata metadata read/lint: passing
- Full `fdroid build --test --no-tarball --stop --verbose be.michiel.edgeback:9`: passing
- Source scan in the fdroidserver candidate build: passing
- Package ID: `be.michiel.edgeback`
- Version: `0.4.1` / versionCode 9 / targetSdk 36
- Build toolchain pinned to Android Build-Tools 36.0.0 for aapt2/D8/zipalign
- Upstream signing uses Build-Tools 34.0.0 `apksigner` for F-Droid reproducible-signature compatibility
- Real-device v0.4.1 validation completed on HONOR Magic8 Pro / MagicOS 10 / Android 16
- Final APK SHA-256: `056ecd7dbb3823aacbd7d14c1b585fad58696cdf25edb9a555f95fe4b0fdedb8`
- Signing certificate subject: `C=BE, O=Local, CN=Magic Swipe`
- Signing certificate SHA-256: `3B:8A:F9:D1:F2:D2:FF:AD:81:45:A3:4A:05:A8:05:BB:D4:C5:A5:08:32:72:0D:E2:76:25:3F:CD:B8:CA:AD:A6`
- Windows upstream APK and pinned Linux source build have byte-identical non-signature APK entries
- All 12 payload entries match in bytes, entry order, compression, offsets, and ZIP timestamps
- Reapplying the final APK's signing material to the Linux unsigned APK with the apksigcopier signing-copy algorithm reconstructs the final signed APK bit-for-bit with the same SHA-256

## Why v0.4.1

F-Droid's inclusion policy requires external executable downloads such as a GitHub APK updater to be explicit opt-in and to explain that enabling them bypasses F-Droid's checks. v0.4.1 changes v0.4.0's updater behavior accordingly: automatic GitHub checks are disabled by default, upgrades from v0.4.0 reset them to disabled, and enabling them requires informed consent. Manual checks remain available as an explicit user action.

## Reproducible build status

The cross-platform payload reproducibility gate is passed. The final Windows APK and the Linux F-Droid-style source build match in all non-signature APK entries. The signing route is also compatible with F-Droid's documented conservative path: the APK is signed with `apksigner` from Build-Tools 34.0.0, while compilation and packaging remain pinned to Build-Tools 36.0.0.

A local signature-copy reconstruction using the same algorithm as apksigcopier reproduces the final signed APK bit-for-bit. The remaining official verification step is F-Droid fetching the public GitHub release APK via `Binaries` and validating it against the source build and `AllowedAPKSigningKeys`.

## Remaining steps

1. Publish the v0.4.1 GitHub release from the exact `main` commit used for the release source.
2. Upload `MagicSwipe-v0.4.1.apk` and `MagicSwipe-v0.4.1-SHA256.txt`.
3. Add `Binaries` pointing to the published GitHub release APK and `AllowedAPKSigningKeys` with the lowercase certificate SHA-256.
4. Run the fdroidserver candidate build again so the public upstream-signed APK is verified through the actual reproducible-build path.
5. Add real app screenshots under `fastlane/metadata/android/en-US/images/phoneScreenshots/`.
6. Put `metadata/be.michiel.edgeback.yml` into a public fdroiddata fork and run the official inclusion pipeline.
7. Open the official fdroiddata app-inclusion merge request after the pipeline passes.

Magic Swipe is not yet available from the official F-Droid repository.
