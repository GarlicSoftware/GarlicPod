# GarlicPod project status

## Baseline and scope

Started 2026-10-25 from stable AntennaPod 3.12.3, commit 175e5ef8fe3e125a5014d70ef0c57cf85661c71b.
Phase 1 validated by user; Phase 2 player UI in progress. Preserve subscriptions, downloads, and storage behavior.

## Completed setup

- Created GitHub fork GarlicSoftware/GarlicPod.
- Checked out baseline on main; origin points to GarlicPod and upstream to AntennaPod.
- Read upstream AGENTS.md, CONTRIBUTING.md, build scripts, and module architecture.
- Found Android Studio, SDK, and an authorized Galaxy S22 Ultra (SM-S908U1), Android 16.
- Downloaded checksum-verified Temurin JDK 21 separately from Android Studio's Java 25.

## Build results and work in progress

- Unmodified build with Java 25 failed: Unsupported class file major version 69.
- Unmodified free and Play debug APKs built successfully with JDK 21: 7m 48s, 1224 tasks executed.
- GarlicPod free debug installed successfully alongside AntennaPod and launched to MainActivity.
- Added The Daily public RSS feed using the normal interface; subscription and episode/show notes loaded.
- Streaming reached PLAYING with position advancing from 2968 ms to 15028 ms and error=null; paused afterward.
- User confirmed playback works and added TESD episodes. Bluetooth, downloads/offline playback, and full backup restore remain untested.
- Original AntennaPod remains installed at version 3.12.3 with unchanged lastUpdateTime (2026-10-13). Its private data was not accessed.
- Identity configured: com.garlicsoftware.garlicpod (debug suffix .debug), unique provider and launcher action, GarlicPod labels.
- Existing subscription schemes retained for parser compatibility; upstream-domain auto-verification disabled.
- Removed inherited upstream backup API registration metadata; native manual export/import retained.
- GitHub main branch exists, documentation is pushed, and main is the default branch.

## Decisions

Use JDK 21, Gradle wrapper 8.13, AGP 8.11.0, build tools 35.0.0, SDK/target 36, min SDK 23.
Retain Java package namespaces to keep changes focused; give application and provider separate identifiers.
First device artifact will be free debug; Google Play/Cast and Wear OS are outside this initial validation.
Do not automatically merge upstream releases. Preserve GPL licensing and attribution.
Native OPML import/export exists; full backup transfer between applications remains untested.
Debug signing differs between machines unless handled separately and securely. Never commit signing keys.

## Next steps

1. Inspect the three-row player on the unlocked phone and test skips/configuration/startup.
2. Refine layout from user feedback.
3. Test the real Bluetooth controller early in Phase 3.
4. Validate downloads/offline playback and native subscription import before stabilization.

## Later requirements

Player-first startup without autoplay; small artwork and large 10s back / play-pause / 30s forward controls.
Map hardware previous/next at media-session layer, preserving manual episode selection.
Reuse upstream resume logic where possible: 10-minute pause threshold, 30-second rewind, persistent state,
nonnegative position, and no false pause caused by seeking. Test actual Bluetooth hardware early.

## Investigation for later phases

Media3PlaybackService already maps hardware actions through UserPreferences and separates other-app requests.
RewindAfterPauseUtils and existing tests provide upstream automatic rewind; desired thresholds still need adaptation.
Generic RSS/OPML/share links remain supported and can show the normal Android app chooser. GarlicPod does not claim
verified ownership of AntennaPod's domain. Java namespaces and original icon are retained for the first baseline.

## GarlicPod build and artifact

Both debug variants passed :app:assembleDebug in 52s (57 executed, 1167 up-to-date).
Free APK: app/build/outputs/apk/free/debug/app-free-debug.apk.
Installed ID: com.garlicsoftware.garlicpod.debug; label: GarlicPod Debug.
FileProvider: com.garlicsoftware.garlicpod.debug.provider; startup provider uses the same app prefix.
Merged manifest and APK metadata inspected before installation. Remaining compiler/resource warnings also occur upstream.
Unit tests and lint were not run for the identity-only change; device launch/subscription/stream/pause checks were used.
The first attempted BBC test feed returned HTTP 404; switched to a verified live Simplecast feed.

Final link-compatibility build also passed in 18s (24 executed, 1200 up-to-date).
The final free APK was installed as an update and launched successfully. Original subscription schemes are
retained because the upstream URL normalizer depends on them; Android may offer an app chooser.
Repository is committed on main; no UI or playback-behavior customizations have been made yet.

## Phase 2 work

User requested 60s back / 10s back / play-pause / 10s forward / 60s forward, with adjustable times.
The latest layout uses three rows: two rewind targets, full-width play/pause, two forward targets.
Artwork is compact and remains a play/pause target. Existing speed, skip-episode, chapters, and show notes remain available.
Normal launcher entry requests expanded player when an episode is selected, without starting playback.
Long-press each skip target to use the existing interval dialog (5, 10, 15, 20, 30, 45, 60 seconds).
Big-skip settings are independent; small skips reuse upstream rewind/forward preferences (new forward default 10s).
Existing explicitly saved small skip times are preserved and can be adjusted by holding the button.
First implementation built both variants in 32s and installed as an update. Phone currently locked; visual and behavior
checks pending unlock. No data reset or subscription import was performed.
