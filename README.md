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

Static decode confirms the upstream app has a profile-background data path:

- `ProfileUser.profileBackground` is a serialized profile component (`profile_background`).
- The profile background renderer consumes `ProfileBgInfo` data, including video/image variants.
- The profile preview activity is `ProfileBgImagePreviewActivity`.
- Its `onActivityResult` handles result code `0x7d2` and returns an `extra_bg_updated` flag to its caller.
- The coroutine named `ProfileBgImagePreviewActivity$performSave$1` creates a timestamped `profile_background_*.png`, opens an output stream through the app context's content resolver, and compresses the preview bitmap as PNG.

**Critical distinction:** that `performSave` routine saves the preview bitmap into the device's media storage. The inspected method does not call a network API or upload a profile-background image. So it is a local export/save action, not proof of a publish-to-profile feature. The `profile_background` profile component is populated through profile data; custom publishing still needs a supported server endpoint or a separate sync service.

## Implementation stages

1. Trace the activity that launches the preview, the image selection/crop flow, and the caller of `performSave` to identify the intended feature boundary.
2. Determine whether an existing supported TikTok API publishes profile background images; do not guess endpoint names or fabricate server behavior.
3. If no supported publishing endpoint exists, prototype a local-only TikTok+ background using app-private storage and the existing renderer, clearly labelled local-only.
4. Add shared visibility only with a secure, documented sync service and user opt-in.
5. Test install, launch, profile loading, and regressions on Android.

A locally selected image cannot automatically appear on another person's device. Shared banners require a server-side service or an upstream TikTok feature that accepts and returns the image URL.

## Compatibility and limitations

- TikTok's servers control account access and server-side availability of music catalogs, LIVE, effects, and regional content.
- TikTok+ can avoid adding its own arbitrary country restrictions, but cannot guarantee overriding upstream regional availability.
- This project will not bypass account restrictions, paid features, or platform security controls.

## Build status

No modified APK has been produced. Current workflows perform static inspection only; they do not execute the app.
