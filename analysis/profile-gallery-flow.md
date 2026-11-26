# Gallery profile-background flow — TikTok 47.9.3

**Method:** Apktool decompilation and static Smali inspection. The APK was not executed.

## Confirmed selection and crop handling

Class: `X/0hbu`

Method: `LJFF(IILandroid/content/Intent;)Z`

1. Request `0x2738` handles a successful Android picker result and reads the selected content URI.
2. The URI is validated by `X/01Dc.LJIIJ(Context, Uri)`.
3. The helper launches `com.ss.android.ugc.profile.business.ur.ui.CropActivity` using the selected URI, the `background` type metadata, and crop request code `0x2739`.
4. On the crop callback (`0x2739`), it reads the output URI and resolves a path using `X/01L0.LIZ(Context, Uri)`.
5. The resulting path is submitted to `X/0P4W` with an `X/1D5q` controller and source metadata.
6. Failure handling includes a photo-failure log path.

This is materially different from the preview activity's separate `performSave` PNG export. The app has a native gallery-to-crop-to-background-handler path.

## What is not yet established

The static call chain does not, by itself, reveal the final HTTP/API request, the server response contract, or whether the result is persisted and returned to the public profile. The exact UI entry point for request `0x2738` also remains to be located.

## Next engineering task

Inspect the upload worker, controller, and their dependencies; then trace the UI entry action and any remote feature gate. Preserve this upstream path if it already provides the required behavior, and patch only the missing entry point or presentation logic.

## Current build status

No application bytes have been modified. No APK has been rebuilt or installed.
