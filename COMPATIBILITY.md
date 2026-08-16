# Compatibility

## Confirmed testing

Magic Swipe is currently **tested only on one device configuration**:

- HONOR Magic8 Pro
- MagicOS 10
- Android 16

The project started as a workaround for the relatively narrow native Back-gesture start zone on that device when using a protective case with raised side lips.

## Expected compatibility

Magic Swipe is built on standard Android APIs rather than HONOR-private APIs:

- `TYPE_ACCESSIBILITY_OVERLAY` for the transparent edge touch zones
- `AccessibilityService.performGlobalAction(GLOBAL_ACTION_BACK)` for Back

Because of that architecture, the app is **expected** to work on many HONOR devices using gesture navigation, including devices running MagicOS 6 through MagicOS 10. This expectation is not a substitute for real-device testing.

Older Magic UI / MagicOS versions and non-HONOR Android devices are considered experimental until confirmed by users.

## Foldables

The AccessibilityService rebuilds its overlays after Android configuration changes. This should help on Magic V-series foldables when screen configuration changes, but folded/unfolded behavior has not yet been validated on real hardware.

## Device report

When reporting compatibility, include:

- Device model
- MagicOS / Magic UI version
- Android version
- Left edge works: yes/no
- Right edge works: yes/no
- Reboot persistence works: yes/no
- Folded/unfolded state if applicable
- Any app or keyboard where the overlay interferes with normal use

A successful report can be added to the confirmed-device table in a future release.
