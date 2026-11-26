# Gallery profile-background flow — TikTok 47.9.3

**Method:** Apktool decompilation and static Smali inspection. The APK was not executed on a device.

## Confirmed UI entry point

The profile-background component wires its image-change action to X/0hYa. That callback retrieves the component's X/0hbu image handler and calls LJII(null, fragment). The handler obtains or creates the profile upload controller X/1D5q and calls its LJIILJJIL(40) entry method. This is the UI route into the existing picker/upload workflow.

## Confirmed gallery, crop, and media-upload path

Class: X/0hbu

Method: LJFF(IILandroid/content/Intent;)Z

1. Request 0x2738 handles the Android image-picker result and reads the selected content URI.
2. The URI is validated by X/01Dc.LJIIJ(Context, Uri).
3. The helper launches com.ss.android.ugc.profile.business.ur.ui.CropActivity with type=background and crop request code 0x2739.
4. Request 0x2739 handles the crop result. The output URI is resolved to a local file path using X/01L0.LIZ(Context, Uri).
5. The handler records source metadata (source=40), starts its progress UI, obtains the profile upload controller X/1D5q, constructs X/0P4W(path, controller, metadata), and submits that runnable to the app executor.
6. The upload worker reports back through the handler's WeakHandler. A successful result is an AvatarUri; the handler resets its retry counter and starts the profile-commit coroutine. Failures go through a photo-upload error path.

This is an actual media-upload operation, not merely a local gallery preview or PNG export.

## Confirmed second-stage profile commit

Coroutine: X/0Plb, named ImageBackgroundHandler$commitProfileBackground$1.

- It takes the uploaded AvatarUri.uri and calls X/0het.LIZIZ(String).
- The result is parsed as CommitUserBgResponse.
- The follow-up coroutine X/0Pla checks the response status and user model.
- On success, the current User is updated with profile-background media type 1, the uploaded image URL list, and a cleared video/Aweme background. The account service is notified, the profile model is rebuilt through X/0OSC.LJIILIIL(...), and local profile state is refreshed through X/0Ne0.LJ(...).
- The handler reports photo success in its analytics path. A failed commit uses the response status message and the normal photo-failure UI path.

The static code therefore confirms a two-stage upstream path: upload the cropped media, then commit the returned URI as the profile background. The returned profile data is what allows the app's profile renderer to show the image. This is stronger evidence than the separate preview activity's performSave routine, which only exports a PNG to device media storage.

## Retry behavior

The handler retries transient upload failures while its retry counter is below four. The inspected conditions include IOException, SocketTimeoutException, and selected service errors (-1, -2, -3). The delays are calculated as twice the retry count in seconds (2, 4, 6, and 8 seconds). Other failures, or exhausted retries, are sent to the visible failure handler.

## Confirmed account eligibility gate

The profile editor is guarded by X/0Ngv.LJFF(true):

- X/0Ngv.LIZ() retrieves the current account UID.
- It reads the AB/remote-config value profile_bg_in_allow_list using config key ID 0x7c00, then caches that value with the account UID.
- LJFF(true) returns true only when the remote value is 1 and X/0Bbm.LIZLLL() also passes.
- MusProfileEditFragment checks this during editor setup and again before forwarding gallery/crop activity results.

This explains why the native gallery route can be compiled into the app but unavailable in the UI for some accounts. The allowlist and second eligibility condition are controlled by TikTok's configuration and account state. This project must not force the flag, bypass the service rollout, or fabricate a successful server response.

## Engineering decision

**Do not delete or replace the existing TikTok image-background code.** It already implements the desired gallery -> crop -> upload -> commit workflow for eligible accounts. Duplicating it would add risk and likely be less reliable than preserving the official path.

If the option is missing for a particular account, the code inspection does not reveal a legitimate client-side fix. TikTok's service must enable the capability for that account. TikTok+ can keep its branding and improve unrelated UI only; it cannot turn a server-denied rollout into an enabled account.

## Current build status

No application bytes have been modified. No APK has been rebuilt or installed. This investigation is static-only; a real account's rollout status and a successful device-side test have not been verified.
