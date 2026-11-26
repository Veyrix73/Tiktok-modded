# TikTok+

TikTok+ is an experimental Android customization project based on the archived TikTok 47.9.3 package.

## Project goals

- Use the TikTok+ name and the supplied TikTok+ logo for project branding.
- Keep the original TikTok experience intact wherever the upstream app and its services allow it.
- Add optional profile customization, starting with custom profile backgrounds.
- Make TikTok+ additions language- and country-neutral; do not hard-code a country allowlist.
- Keep modifications isolated and reversible so a feature can be disabled if it causes crashes.

## Current technical baseline

- Upstream package: `com.zhiliaoapp.musically`
- Upstream version: `47.9.3` (version code `2024709030`)
- Minimum Android SDK: 23
- Target Android SDK: 36
- The repository currently contains the original app archive and static-inspection workflows. It is **not yet a rebuilt TikTok+ APK**.

## Profile background investigation

The decoded app contains profile-background UI and model code. The renderer uses image URL data; this alone does not provide a custom upload or cross-user sync service.

Implementation is staged:

1. Trace the existing profile-background model and rendering path.
2. Add a local-only custom background prototype and verify persistence.
3. Add shared visibility only after a secure, documented sync service exists and users can opt in.
4. Test install, launch, profile loading, and regression behavior on Android.

A locally selected image cannot automatically appear on another person's device. Shared banners require a server-side service or an upstream TikTok feature that accepts the image.

## Compatibility and limitations

- TikTok's servers control account access and availability of server-side features such as music catalogs, LIVE, effects, and regional content.
- TikTok+ can avoid adding its own arbitrary country restrictions, but cannot promise to override upstream regional availability.
- This project will not bypass account restrictions, paid features, or platform security controls.

## Build status

No modified APK has been produced yet. The current workflows perform static inspection only; they do not execute the app.
