# Profile background caller trace — TikTok 47.9.3

Static analysis only; the APK was not executed.

## Confirmed caller-side result handling

File: `com/ss/android/ugc/profile/business/profile/ui/v2/I18nMyProfileFragment.smali`

The fragment's activity-result handler checks the boolean extra `extra_bg_updated`. When it is true, it reads:

- `extra_bg_aweme_id` as a string.
- `extra_bg_offset_ratio` as a float, defaulting to `0.5`.

It then calls:

```smali
sget-object v0, LX/0PlW;->LIZ:LX/0PlW;
invoke-static {v1, v2}, LX/0PlW;->LIZ(FLjava/lang/String;)LX/0PlY;
```

The returned `LX/0PlY` contains a `User` and a `ProfileUser`. The fragment passes those objects into its profile refresh/update method `RZ(...)`.

## What this proves

The preview activity result is not merely a UI close signal. The caller expects a background update result carrying an Aweme ID and vertical offset, then obtains updated profile model objects through the `0PlW.LIZ(float, String)` helper and refreshes the profile.

## What this does not prove

The result key is an Aweme ID, not a local image URI. This flow appears to be tied to an existing TikTok media item/background selection path. The code shown does not establish that arbitrary gallery images can be uploaded as profile backgrounds.

The next inspection must determine:
1. What `0PlW.LIZ(float, String)` does and whether it performs a supported server request.
2. Which screen launches `ProfileBgImagePreviewActivity` and what media object it passes.
3. Whether the image picker is for selecting a local image or choosing an existing TikTok post/media item.

Do not substitute a guessed endpoint. If the existing flow only selects an Aweme, a separate local-only banner prototype or a separately operated sync service is needed for custom gallery images.

## Build status

No APK code has been modified or rebuilt.
