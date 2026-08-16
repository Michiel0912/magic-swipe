# Signing

Magic Swipe uses Android package ID `be.michiel.edgeback`. Android requires every update for that package to be signed by the same key.

## Current project policy

- Signing keys are **never committed to GitHub**.
- The key used for direct GitHub APK builds must be backed up outside the project directory.
- Deleting or replacing the signing key breaks in-place updates for existing direct-install users.
- F-Droid may use its own repository signing key unless reproducible upstream-signed builds are configured later.
- Google Play App Signing should be treated as a separate distribution/signing workflow.

## Local backup

For the current Windows build workflow, keep at least two private backups of the signing keystore used for direct APK releases. A recommended local location is:

```text
%USERPROFILE%\.magic-swipe\magic-swipe-release.keystore
```

A second offline or encrypted backup is strongly recommended.

## Important

Never publish the keystore, private key, keystore password, or signing secrets in this repository, an issue, a pull request, a release asset, or a public CI log.

Before publishing the first public APK release, record the certificate SHA-256 fingerprint and keep it with the private release notes. That fingerprint can later be used to verify that an APK was signed with the expected certificate.
