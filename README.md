# TikTok+ project notes

This repository contains a user-provided TikTok 47.9.3 archive and static-analysis notes. It is **not** a source project for TikTok and does not contain a legitimate way to build an independently maintained TikTok client.

## Current state

- The original archive is retained as the supplied input.
- Static-inspection workflows and reports document the profile gallery, crop, upload and server commit flow.
- No modified TikTok APK is published by this repository; only the separate, original-source Android prototype is built and released.
- The temporary APK-repackaging workflow and patch script were removed. They attempted to rebuild and rebrand TikTok's proprietary binary, which is not an appropriate deliverable.

## Profile background investigation

Static inspection of the supplied 47.9.3 archive found that TikTok already contains a gallery-image background flow:

- The profile-background image action receives a gallery URI and opens TikTok's crop screen.
- The cropped image is passed to TikTok's upload handler.
- A profile-background commit request returns data that the app uses to refresh the profile.
- An eligibility gate checks the remote `profile_bg_in_allow_list` configuration and a second account eligibility condition. Those checks are controlled by TikTok's service. This project does not alter or bypass them.
- The video/Aweme background path is separate from the gallery-image upload path.

These are static-analysis findings only; the app was not executed or tested against an account.

## Safe development direction

A testable Android app should be built from original source code, with its own name, UI, backend and assets. It can prototype general short-video concepts such as a local sample feed, profile editing, likes and comments without bundling TikTok's proprietary app or pretending to connect to TikTok's servers.

A separate prototype cannot log into TikTok, upload to TikTok, display TikTok's server feed, or inherit TikTok's account permissions. Any such integration must use officially supported interfaces and comply with their terms.

## Android prototype updater

The independent Android prototype includes a **Check for app updates** button on the Profile screen. It checks the latest published GitHub Release, downloads the attached `test-Tiktok-plus-prototype.apk`, and hands it to Android's package installer. Android still asks the user to approve installation; the app does not silently install updates.

Every push to `main` builds a versioned test APK and publishes it as a GitHub Release (`v0.3.<run number>`). Version codes increase with each workflow run. A scheduled build every five days refreshes the cached prototype-only Android debug signing key but does not publish another release. This keeps successive test APKs installable as updates. The signing setup is only for this public prototype; do not reuse it for a production app.

The updater only applies to this independent prototype. It does not update or patch the original TikTok application.

## Build status

The Android workflow builds and verifies the original-source prototype APK and uploads a 14-day Actions artifact. Pushes to `main` and manual runs on `main` also publish the APK as a GitHub Release. Scheduled builds only refresh the signing-key cache.
