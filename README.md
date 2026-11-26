# TikTok+ project notes

This repository contains a user-provided TikTok 47.9.3 archive and static-analysis notes. It is **not** a source project for TikTok and does not contain a legitimate way to build an independently maintained TikTok client.

## Current state

- The original archive is retained as the supplied input.
- Static-inspection workflows and reports document the profile gallery, crop, upload and server commit flow.
- No modified TikTok APK is published by this repository.
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

## Build status

No TikTok+ APK has been produced. The existing workflows are for static inspection only.
