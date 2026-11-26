# Profile background trace — TikTok 47.9.3

Static-only analysis of the decoded archive. The app was not executed.

## Confirmed behavior

### Preview image save
Class: `com.ss.android.ugc.profile.business.profile.background.ProfileBgImagePreviewActivity`

Coroutine: `X/0hbm.smali`, source debug name `ProfileBgImagePreviewActivity$performSave$1`.

The coroutine:
1. Builds a filename beginning `profile_background_` and ending in `.png`.
2. Creates a URI using the activity context and that filename.
3. Opens an output stream through `ContentResolver`.
4. Compresses the preview bitmap as PNG at quality 100.
5. Reports save failure through the app's logging path.

This is an export/save-to-device operation. The inspected coroutine contains no network request and does not publish the image to the user's profile.

### Activity result
The preview activity's `onActivityResult` forwards results to its internal handler. For request code `0x7d2`, a successful result is returned to the caller with `extra_bg_updated=true` (or the same flag is added when the returned Intent is null). This confirms an upstream activity flow exists, but the inspected snippet does not identify a custom image upload endpoint.

### Profile data
`ProfileUser.profileBackground` is serialized under the `profile_background` key. The profile-background component consumes `ProfileBgInfo` data. This is the rendering/data-consumption side, not evidence that a client-selected local file can be published.

## Engineering conclusion

Do not patch the gallery-save coroutine and call it an upload feature. The next safe investigation target is the code that launches `ProfileBgImagePreviewActivity` and the data/ability object used to produce its input bitmap and result. Then inspect whether the caller invokes a documented upstream publishing operation.

If there is no supported upstream publishing operation, a local-only TikTok+ prototype can store a selected image in app-private storage and render it only on that device. Cross-user visibility requires a separate backend and explicit user opt-in; it cannot be achieved by changing the renderer alone.

## Build status

No APK modifications or APK build have been made.
