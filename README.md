# TikTok+

TikTok+ is an experimental Android customization project based on the archived TikTok 47.9.3 package.

## Project goals

- Use the TikTok+ name and supplied logo for project branding.
- Preserve the upstream TikTok experience wherever its app and services allow.
- Add optional profile customization, starting with custom profile backgrounds.
- Keep TikTok+ additions language- and country-neutral; do not hard-code a country allowlist.
- Keep changes isolated and reversible.

## Current technical baseline

- Upstream package: `com.zhiliaoapp.musically`
- Upstream version: `47.9.3` (version code `2024709030`)
- Minimum Android SDK: 23
- Target Android SDK: 36
- Repository contains the original app archive and static-inspection workflows. It is **not yet a rebuilt TikTok+ APK**.

## Profile background code findings

Static decode confirms the upstream app already has a real profile-background data path:

- `User.profileBackgroundMediaType`
- `User.profileBackgroundImageUrlList`
- `User.profileBackgroundAweme`
- `User.profileBackgroundOffsetRatio`
- `ProfileBgInfo.setImageUrlList(...)`
- `ProfileBackgroundComponent`
- `ProfileBgImagePreviewActivity`

The legacy-to-new profile conversion code in `X/0OSC.smali` copies the media type and image URL list into `ProfileBgInfo`. The profile renderer checks for media type `1` and a non-empty image URL list before creating the image background data. This is a concrete rendering path, not just a string or feature flag.

The remaining investigation is the source of those image URLs and whether the existing image preview/crop flow includes an upstream upload/save endpoint. A renderer accepting URLs does not itself implement upload or cross-user sharing.

## Implementation stages

1. Trace the existing image selection, crop, upload, and save flow.
2. Prototype local-only custom background selection and persistence.
3. Add shared visibility only with a secure, documented sync service and user opt-in, unless the upstream app already provides a supported publishing endpoint.
4. Test install, launch, profile loading, and regressions on Android.

A locally selected image cannot automatically appear on another person's device. Shared banners require a server-side service or an upstream TikTok feature that accepts and returns the image URL.

## Compatibility and limitations

- TikTok's servers control account access and server-side availability of music catalogs, LIVE, effects, and regional content.
- TikTok+ can avoid adding its own arbitrary country restrictions, but cannot guarantee overriding upstream regional availability.
- This project will not bypass account restrictions, paid features, or platform security controls.

## Build status

No modified APK has been produced. Current workflows perform static inspection only; they do not execute the app.
