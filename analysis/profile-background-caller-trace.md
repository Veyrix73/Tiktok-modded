# Profile background caller trace — TikTok 47.9.3

Static analysis only; the APK was not executed.

## Video/Aweme background result

File: `com/ss/android/ugc/profile/business/profile/ui/v2/I18nMyProfileFragment.smali`

The fragment's activity-result handler checks `extra_bg_updated`. When true, it reads `extra_bg_aweme_id` and `extra_bg_offset_ratio`, then calls `LX/0PlW;->LIZ(FLjava/lang/String;)`. That helper updates local profile model state from an existing Aweme ID. This is a separate path from choosing a local gallery image.

## Gallery image path discovered in the shared handler

File: `X/0hbu.smali`, method `LJFF(IILandroid/content/Intent;)Z`.

- Request code `0x2738`: a successful picker result supplies a content `Uri`.
- The handler validates the URI through `LX/01Dc;->LJIIJ(Context, Uri)`.
- It launches `com/ss/android/ugc/profile/business/ur/ui/CropActivity` with the URI, background type metadata, and crop request code `0x2739`.
- Request code `0x2739`: the crop result supplies another URI. The helper resolves it to a local path with `LX/01L0;->LIZ(Context, Uri)`.
- The selected path is passed to `LX/0P4W` with the `LX/1D5q` upload controller and source metadata. The asynchronous worker and server response still need inspection to confirm the exact commit contract and persistence behavior.
- The handler has an explicit photo-failure path that logs a failed photo operation.

This proves the app already contains a gallery-selection and crop pipeline tied to its profile-background handler. It corrects the earlier incomplete conclusion that the app had no gallery route.

## Editor availability gate

The profile editor is guarded by `X/0Ngv.LJFF(true)`:

- `X/0Ngv.LIZ()` reads the current account UID and requests the remote AB/config value `profile_bg_in_allow_list` (config key ID `0x7c00`).
- It caches the value alongside the UID so an account switch causes the snapshot to be refreshed.
- `LJFF(true)` also calls `X/0Bbm.LIZLLL()`; both conditions must pass.
- `MusProfileEditFragment` checks this before building the editor and before forwarding picker/crop results.

This explains why the gallery flow can exist in the APK without being visible or usable for every account. The allowlist is server-controlled; this project will not patch around it.

## Remaining trace

1. Inspect `LX/0P4W`, `LX/1D5q`, and their direct dependencies to determine the upload endpoint/result handling.
2. Find the UI action that launches the system image picker with request code `0x2738`.
3. Check whether the action is controlled by a feature flag, server-returned profile capability, or ordinary UI state.
4. Only then decide whether TikTok+ needs code changes or a visibility/entry-point fix.

## Build status

No APK has been modified or rebuilt. Static inspection only. The latest workflow now collects the missing gate, uploader, and controller class references for follow-up.
