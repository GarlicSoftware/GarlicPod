# GarlicPod project status

## Baseline and scope

Started 2026-10-25 from stable AntennaPod 3.12.3, commit 175e5ef8fe3e125a5014d70ef0c57cf85661c71b.
Phase 1 only. Preserve existing player, subscriptions, downloads, and storage behavior.

## Completed setup

- Created GitHub fork GarlicSoftware/GarlicPod.
- Checked out baseline on main; origin points to GarlicPod and upstream to AntennaPod.
- Read upstream AGENTS.md, CONTRIBUTING.md, build scripts, and module architecture.
- Found Android Studio, SDK, and an authorized Galaxy S22 Ultra (SM-S908U1), Android 16.
- Downloaded checksum-verified Temurin JDK 21 separately from Android Studio's Java 25.

## Build results and work in progress

- Unmodified build with Java 25 failed: Unsupported class file major version 69.
- Unmodified build retried with JDK 21. Outcome pending.
- No phone installation or playback verification yet.
- App identity changes pending the stock build result.

## Decisions

Use JDK 21, Gradle wrapper 8.13, AGP 8.11.0, SDK/target 36, min SDK 23.
Retain Java package namespaces to keep changes focused; give application and provider separate identifiers.
First device artifact will be free debug; Google Play/Cast and Wear OS are outside this initial validation.
Do not automatically merge upstream releases. Preserve GPL licensing and attribution.
Native OPML import/export exists; full backup transfer between applications remains untested.
Debug signing differs between machines unless handled separately and securely. Never commit signing keys.

## Next steps

1. Finish stock build and resolve actual dependency or SDK failures.
2. Set GarlicPod application identity, label, and provider; audit merged manifest and app-specific links.
3. Build GarlicPod, install separately, and verify launch and ordinary podcast playback.
4. Commit and push the documented baseline; set GitHub default branch to main.
5. Only after phone validation begin Phase 2 playback UI changes.

## Later requirements

Player-first startup without autoplay; small artwork and large 10s back / play-pause / 30s forward controls.
Map hardware previous/next at media-session layer, preserving manual episode selection.
Reuse upstream resume logic where possible: 10-minute pause threshold, 30-second rewind, persistent state,
nonnegative position, and no false pause caused by seeking. Test actual Bluetooth hardware early.
