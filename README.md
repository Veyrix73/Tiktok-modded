# TikTok+

TikTok+ is an experimental Android customization project based on the archived TikTok 47.9.3 package.

## Project goals

- Use the TikTok+ name and supplied logo for project branding.
- Preserve the upstream TikTok experience wherever its app and services allow.
- Improve profile customization and expose supported profile-image flows clearly.
- Keep TikTok+ additions language- and country-neutral; do not hard-code a country allowlist.
- Keep changes isolated and reversible.

## Current technical baseline

- Upstream package: `com.zhiliaoapp.musically`
- Upstream version: `47.9.3` (version code `2024709030`)
- Minimum Android SDK: 23
- Target Android SDK: 36
- Repository contains the original app archive and static-inspection workflows. It is **not yet a rebuilt TikTok+ APK**.

## Profile background findings

Static inspection of the original package confirms more than a renderer and preview:

- The app contains a profile-background image handler, `X/0hbu`.
- The handler receives a gallery result as an Android `Uri` for request code `0x2738`.
- It validates the selected URI and opens TikTok's `CropActivity` with request code `0x2739`.
- After the crop result, it resolves the selected file path and submits it to an asynchronous handler, `X/0P4W`, together with the profile upload controller `X/1D5q` and source metadata.
- The profile preview/export activity separately saves a PNG to device media storage. That save routine is not the same thing as publishing the selected profile image.
- The normal profile refresh path handles an Aweme/video background ID separately.

**Engineering implication:** do not build a duplicate gallery picker before testing the existing route. The immediate task is to finish tracing the existing upload worker and its server response, then determine whether the gallery action is feature-gated, hidden by server configuration, or already accessible in the normal profile UI.

## Implementation stages

1. Finish tracing the existing gallery -> crop -> upload worker -> profile refresh chain.
2. Identify the normal UI entry point and any server-side feature gate.
3. Preserve the upstream flow where it works; add TikTok+ UI/settings only where the base app is missing them.
4. Rebuild and sign a test APK in CI without committing signing keys.
5. Test install, launch, profile image selection, upload result, and regressions on Android.

A local image only becomes visible to other users when the upstream service accepts it and returns it as profile data. Do not claim shared visibility before verifying that server round-trip.

## Compatibility and limitations

- TikTok's servers control account access and server-side availability of music catalogs, LIVE, effects, and regional content.
- TikTok+ can avoid adding its own arbitrary country restrictions, but cannot guarantee overriding upstream regional availability.
- This project will not bypass account restrictions, paid features, or platform security controls.

## Build status

No modified APK has been produced yet. Current workflows perform static inspection only; they do not execute the app.
