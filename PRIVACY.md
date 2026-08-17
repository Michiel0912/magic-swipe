# Magic Swipe Privacy Policy

Last updated: 2026-08-17

Magic Swipe is designed to work without accounts, analytics, advertising, trackers, or collection of screen content.

## AccessibilityService

Magic Swipe uses Android's AccessibilityService only to detect swipes inside its own narrow transparent edge overlays and to trigger the system Back action.

The AccessibilityService:

- does not retrieve or inspect window or screen content;
- does not read messages, passwords, typed text, or other app data;
- does not create a user profile;
- does not use the network.

The service is configured with `canRetrieveWindowContent=false`.

## Network access

Magic Swipe has the Android `INTERNET` permission only for its optional GitHub update checker in the settings screen.

Manual update checks happen only when the user presses the update-check button. Automatic GitHub update checks are disabled by default. They can be enabled only after an explicit disclosure explaining what the feature does and that downloading an APK from GitHub happens outside F-Droid and bypasses F-Droid's build verification. Automatic checks can be disabled again at any time.

When an update check is performed, Magic Swipe sends a standard HTTPS request to the public GitHub Releases API for the Magic Swipe repository. Magic Swipe does not add an account identifier, advertising identifier, device identifier, location, contact data, or app-usage data to that request. As with any HTTPS connection, GitHub may receive ordinary connection information such as the IP address and user-agent and processes that information under GitHub's own privacy terms.

Magic Swipe itself does not store or transmit the response anywhere other than the device and does not operate a server that receives update-check data.

## External APK downloads

If a newer GitHub release is available, Magic Swipe can open the official GitHub APK download in the user's browser. It does not silently download or install applications and does not request package-installer privileges.

Before opening an external APK from the F-Droid-compatible v0.4.1 release onward, Magic Swipe explains that the GitHub APK is distributed outside F-Droid and bypasses F-Droid's build verification.

## Data collection and sharing

Magic Swipe does not collect, sell, rent, or share personal data. It contains no analytics SDKs, advertising SDKs, crash-reporting SDKs, social SDKs, or Google Play Services dependencies.

Settings such as gesture width, appearance, and whether automatic update checks are enabled are stored locally on the device using Android SharedPreferences.

## Source code and contact

Magic Swipe is free and open-source software. The complete source code and issue tracker are available at:

https://github.com/Michiel0912/magic-swipe

Privacy questions can be raised through the public GitHub issue tracker.
