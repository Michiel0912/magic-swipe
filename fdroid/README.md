# F-Droid packaging

This directory contains the upstream F-Droid packaging preparation for Magic Swipe.

The authoritative application ID is `be.michiel.edgeback`. The legacy package ID is intentionally kept so existing installations can upgrade without becoming a separate app.

The current packaging draft targets Magic Swipe **v0.3.0** (`versionCode 6`). Before submitting to `fdroiddata`:

1. Build and test the release on real hardware.
2. Tag the exact validated release commit as `v0.3.0`.
3. Copy `be.michiel.edgeback.yml` into a fork of `fdroiddata` as `metadata/be.michiel.edgeback.yml`.
4. Run `fdroid readmeta`, `fdroid rewritemeta be.michiel.edgeback`, `fdroid checkupdates be.michiel.edgeback`, `fdroid lint be.michiel.edgeback`, and `fdroid build -v -l be.michiel.edgeback` in an F-Droid build environment or use fdroiddata CI.
5. Add real screenshots under `fastlane/metadata/android/en-US/images/phoneScreenshots/` before the inclusion request.

See `FDROID_SUBMISSION.md` in the repository root for the complete checklist.
