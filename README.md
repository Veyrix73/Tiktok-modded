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

Static inspection confirms the original TikTok 47.9.3 package already contains a complete gallery-image background flow:

- The profile-background component's image-change action runs through X/0hYa and X/0hbu.
- Request 0x2738 receives a gallery URI. TikTok validates it and opens CropActivity with request code 0x2739.
- The cropped file path is sent to upload worker X/0P4W through profile upload controller X/1D5q.
- The upload handler receives an AvatarUri result, then coroutine X/0Plb calls X/0het.LIZIZ(AvatarUri.uri), which returns CommitUserBgResponse.
- On an accepted response, X/0Pla updates the user's profile-background media type and image URL list, refreshes profile data, and reports success.
- The existing retry handler retries selected temporary upload failures up to four times.
- The video/Aweme background path is separate from the gallery-image path.
- The preview activity's performSave routine only exports a PNG to device media storage; it is not the profile upload operation.

**Availability gate confirmed in code:** X/0Ngv.LIZ() reads the current account ID and asks TikTok's remote AB/config manager for profile_bg_in_allow_list (config key ID 0x7c00). X/0Ngv.LJFF(true) enables the profile-editor route only when that value is 1 and a second eligibility check, X/0Bbm.LIZLLL(), also passes. The profile editor checks this during setup and before handling picker/crop results. The gallery upload code is present, but an account that fails either check cannot use the route. This is a server-controlled rollout/eligibility condition, not a missing gallery picker. Do not force the flag or bypass the rollout.

## Implementation stages

1. Preserve the existing upstream gallery -> crop -> upload -> profile commit flow; do not create a duplicate picker.
2. Keep the server-controlled eligibility checks intact.
3. Add TikTok+ branding/settings only where the base app is actually missing them.
4. Rebuild and sign a test APK in CI without committing signing keys.
5. Test install, launch, profile image selection, server response, and regressions on Android.

A local image only becomes visible to other users when the upstream service accepts it and returns it as profile data. Do not claim shared visibility before verifying that server round-trip.

## Compatibility and limitations

- TikTok's servers control account access and server-side availability of music catalogs, LIVE, effects, and regional content.
- TikTok+ can avoid adding its own arbitrary country restrictions, but cannot guarantee overriding upstream regional availability.
- This project will not bypass account restrictions, paid features, or platform security controls.

## Build status

No modified APK has been produced yet. Current workflows perform static inspection only; they do not execute the app. The gallery upload and commit code exists in the original package, but TikTok's remote allowlist and eligibility check determine whether a particular account can access it. A client-only rebrand cannot change that server decision.
