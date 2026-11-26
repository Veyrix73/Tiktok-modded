# Profile background caller trace — TikTok 47.9.3

Static analysis only; the APK was not executed.

## Gallery image path

The profile-background component's image-change callback is X/0hYa. It obtains X/0hbu and calls LJII(null, fragment). That method gets the profile upload controller X/1D5q and calls LJIILJJIL(40) to enter the picker/upload flow.

In X/0hbu.LJFF(I,I,Intent):

- Request 0x2738 reads and validates the selected gallery URI.
- The URI is passed to CropActivity with type=background and request code 0x2739.
- The crop result URI is converted to a local path.
- The handler attaches source=40 metadata and submits X/0P4W(path, X/1D5q, metadata) to the app executor.

## Upload callback and commit

X/0hbu.handleMsg(Message) handles worker callbacks:

- On AvatarUri, it resets retry state and starts X/0Plb.
- On selected transient errors (IOException, SocketTimeoutException, or service error codes -1, -2, -3), it retries up to four times with 2/4/6/8-second delays.
- Other or exhausted errors use the photo-failure UI path.

X/0Plb calls X/0het.LIZIZ(AvatarUri.uri) and receives a CommitUserBgResponse. X/0Pla checks response status, updates the current user's profile-background media type and uploaded URL list, clears the video background, rebuilds profile data, updates local profile state, and reports success. This confirms separate media-upload and profile-commit stages.

## Video/Aweme background result is a separate flow

I18nMyProfileFragment handles extra_bg_aweme_id and extra_bg_offset_ratio, then calls X/0PlW.LIZ(float, String). That path updates a background from an existing TikTok Aweme/video. It is not the gallery image path above.

## Editor availability gate

The profile editor is guarded by X/0Ngv.LJFF(true):

- X/0Ngv.LIZ() reads the current account UID and requests profile_bg_in_allow_list (AB/config key ID 0x7c00).
- The value is cached alongside the UID.
- LJFF(true) also requires X/0Bbm.LIZLLL() to pass.
- MusProfileEditFragment checks this during editor setup and before forwarding picker/crop results.

The gallery picker and server upload code therefore exist, but the option is not available to every account. This is a remote rollout/eligibility condition, not proof of a broken local picker. The allowlist will not be patched around.

## Build status

No APK has been modified or rebuilt. Static inspection only.
