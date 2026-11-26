#!/usr/bin/env python3
"""Apply the minimal TikTok+ test branding to an Apktool-decoded package.

This deliberately does not change TikTok feature gates, API calls, login flows,
or account/security checks.
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

OLD_APP_ID = "com.zhiliaoapp.musically"
TEST_APP_ID = "com.veyrix.tiktokplus.test"
TEST_LABEL = "TikTok+ Test"
TEXT_SUFFIXES = {".xml", ".smali", ".json", ".txt", ".properties", ".mf", ".cfg", ".ini"}


def patch_manifest(path: Path) -> None:
    text = path.read_text(encoding="utf-8")
    if f'package="{OLD_APP_ID}"' not in text:
        raise SystemExit(f"Expected package attribute not found in {path}")

    text = text.replace(f'package="{OLD_APP_ID}"', f'package="{TEST_APP_ID}"', 1)

    app_match = re.search(r"<application\\b[^>]*>", text, flags=re.DOTALL)
    if not app_match:
        raise SystemExit("Could not find the <application> element in AndroidManifest.xml")

    app_tag = app_match.group(0)
    label_pattern = re.compile(r'android:label=(["\']).*?\\1', flags=re.DOTALL)
    if label_pattern.search(app_tag):
        app_tag = label_pattern.sub(f'android:label="{TEST_LABEL}"', app_tag, count=1)
    else:
        app_tag = app_tag[:-1] + f' android:label="{TEST_LABEL}">'
    text = text[:app_match.start()] + app_tag + text[app_match.end():]
    path.write_text(text, encoding="utf-8")


def main() -> None:
    if len(sys.argv) != 2:
        raise SystemExit("Usage: brand_apk.py <apktool-decoded-directory>")

    root = Path(sys.argv[1]).resolve()
    manifest = root / "AndroidManifest.xml"
    if not root.is_dir() or not manifest.is_file():
        raise SystemExit(f"Not an Apktool-decoded directory: {root}")

    patch_manifest(manifest)

    changed_files = 0
    changed_refs = 0
    for path in root.rglob("*"):
        if not path.is_file() or path == manifest or path.suffix.lower() not in TEXT_SUFFIXES:
            continue
        try:
            content = path.read_text(encoding="utf-8")
        except (UnicodeDecodeError, OSError):
            continue
        count = content.count(OLD_APP_ID)
        if count:
            path.write_text(content.replace(OLD_APP_ID, TEST_APP_ID), encoding="utf-8")
            changed_files += 1
            changed_refs += count

    manifest_text = manifest.read_text(encoding="utf-8")
    if f'package="{TEST_APP_ID}"' not in manifest_text or f'android:label="{TEST_LABEL}"' not in manifest_text:
        raise SystemExit("Manifest branding verification failed")

    print(f"Application ID: {TEST_APP_ID}")
    print(f"Launcher label: {TEST_LABEL}")
    print(f"Updated package references in {changed_files} text files ({changed_refs} references).")
    print("No feature gates, login code, API endpoints, or account-security checks were changed.")


if __name__ == "__main__":
    main()
