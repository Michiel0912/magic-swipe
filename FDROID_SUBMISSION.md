# F-Droid submission status

Magic Swipe is being prepared for the official F-Droid repository. The current candidate is v0.4.1 / versionCode 9.

## Ready

- Public source repository and MIT license
- No proprietary runtime dependencies, analytics, trackers, Google Play Services, or screen-content retrieval
- AccessibilityService network use: none
- GitHub update checks are optional; automatic checks are disabled by default and require explicit informed consent
- External GitHub APK downloads are clearly disclosed as outside F-Droid verification
- Privacy policy: `PRIVACY.md`
- Fastlane/Triple-T English metadata, store icon, and three real phone screenshots
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
- Public GitHub release `v0.4.1` points to release commit `6ba3ae764c36805143b29d16e08702fe0561f067`
- Public release APK SHA-256: `056ecd7dbb3823aacbd7d14c1b585fad58696cdf25edb9a555f95fe4b0fdedb8`
- Signing certificate subject: `C=BE, O=Local, CN=Magic Swipe`
- Signing certificate SHA-256: `3B:8A:F9:D1:F2:D2:FF:AD:81:45:A3:4A:05:A8:05:BB:D4:C5:A5:08:32:72:0D:E2:76:25:3F:CD:B8:CA:AD:A6`
- `Binaries` points to the public versioned GitHub release APK
- `AllowedAPKSigningKeys` is set to `3b8af9d1f2d2ffad8145a34a05a805bbd4c5a50832720de276253fcdb8caada6`
- Windows upstream APK and pinned Linux source build have byte-identical non-signature APK entries
- All 12 payload entries match in bytes, entry order, compression, offsets, and ZIP timestamps
- Reapplying the final APK's signing material to the Linux unsigned APK with the apksigcopier signing-copy algorithm reconstructs the final signed APK bit-for-bit with the same SHA-256
- Current fdroidserver fetched the public GitHub `v0.4.1` APK during CI and verified it against the source-built APK successfully
- Current fdroidserver verified the public APK uses the configured allowed signing certificate
- Public upstream-signed reproducible-build gate: passed

## Why v0.4.1

F-Droid's inclusion policy requires external executable downloads such as a GitHub APK updater to be explicit opt-in and to explain that enabling them bypasses F-Droid's checks. v0.4.1 changes v0.4.0's updater behavior accordingly: automatic GitHub checks are disabled by default, upgrades from v0.4.0 reset them to disabled, and enabling them requires informed consent. Manual checks remain available as an explicit user action.

## Reproducible build status

The upstream-signed reproducible-build path is now fully validated with the current public release.

The final Windows APK and the Linux F-Droid-style source build match in all non-signature APK entries. The APK is signed with `apksigner` from Build-Tools 34.0.0, while compilation and packaging remain pinned to Build-Tools 36.0.0.

After the GitHub release was published, current fdroidserver 2.4.2 was run against a fresh current fdroiddata checkout using the candidate metadata. It checked out tag `v0.4.1` at commit `6ba3ae764c36805143b29d16e08702fe0561f067`, built the unsigned APK, downloaded the public GitHub release APK through `Binaries`, copied/verified its v2/v3 signing material against the source build, and reported:

- `compared built binary to supplied reference binary successfully`
- `supplied reference binary has allowed signer 3b8af9d1f2d2ffad8145a34a05a805bbd4c5a50832720de276253fcdb8caada6`
- `1 build succeeded`

This is the same core reproducible-binary verification path used by fdroidserver when `Binaries` and `AllowedAPKSigningKeys` are configured.

## Remaining steps

1. Put the prepared metadata into a public fork of `fdroiddata` as `metadata/be.michiel.edgeback.yml`.
2. Run/confirm the official fdroiddata app-inclusion merge-request pipeline.
3. Open the official app-inclusion merge request and respond to reviewer feedback if any.

Magic Swipe is not yet available from the official F-Droid repository.
