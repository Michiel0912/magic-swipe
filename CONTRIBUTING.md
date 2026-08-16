# Contributing

Small, focused pull requests are preferred.

Please keep the privacy and safety model intact:

- Do not add Internet access unless there is a compelling, documented reason.
- Do not enable accessibility window-content retrieval.
- Do not add analytics or trackers.
- Keep changes reversible and non-root where possible.
- Do not modify system APKs or framework resources as part of the normal app workflow.

## Device compatibility reports

Compatibility claims should distinguish between **tested**, **expected**, and **experimental**. The only confirmed development device at the time of v0.3.0 is the HONOR Magic8 Pro on MagicOS 10 / Android 16.

When confirming another device, include the model, MagicOS/Magic UI version, Android version, and whether left/right Back zones and reboot persistence work correctly.

## Translations

When adding or correcting a translation:

1. Update the matching `values-*` resource directory.
2. Keep `app/src/main/res/xml/locales_config.xml` in sync.
3. Prefer natural UI wording over literal word-for-word translation.
4. Keep the product name **Magic Swipe** untranslated.
