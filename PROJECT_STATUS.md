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

1. Gather user feedback on the verified portrait player layout.
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
The latest layout uses three rows: full-width play/pause, big rewind/forward, then small rewind/forward.
Artwork is compact and remains a play/pause target. Existing speed, skip-episode, chapters, and show notes remain available.
Normal launcher entry requests expanded player when an episode is selected, without starting playback.
Long-press each skip target to use the existing interval dialog (5, 10, 15, 20, 30, 45, 60 seconds).
Big-skip settings are independent; small skips reuse upstream rewind/forward preferences (new forward default 10s).
Existing explicitly saved small skip times are preserved and can be adjusted by holding the button.
First implementation built both variants in 32s and installed as an update. Phone checks completed after unlock.
No data reset or subscription import was performed.

## Phase 2 phone validation

- Portrait layout visually inspected on Galaxy S22 Ultra; all five touch areas and metadata fit.
- Paused skip sequence verified by Android media-session position: -60000, -10000, +10000, +60000 ms.
- Long-pressed big forward and changed 60 to 45 seconds. The setting persisted through APK updates and cold starts.
- Restored big forward to 60 seconds. All four controls show 60 / 10 / 10 / 60.
- Artwork toggled PLAYING then PAUSED. The large play control successfully paused playback.
- Cold launcher entry and a hot return from the home screen both opened the full player without autoplay.
- Startup expansion waits for asynchronous media loading. MainActivity also observes process backgrounding because
  Android can bring an existing task forward without delivering a new launch intent.
- Added AndroidX lifecycle-process 2.8.3 for the process observer, matching existing cached lifecycle artifacts.
- A compile attempt without that direct dependency failed; adding it resolved the missing ProcessLifecycleOwner class.
- Final :app:assembleDebug passed for both variants in 42s. Updated free-debug APK installed successfully.
- No unit tests/lint run; validation used the real phone. Landscape layout and Bluetooth remain untested.
- Left the current episode paused on the full player.

## Player layout and Home replacement

- Rearranged controls to play/pause on top, 60-second rewind/forward in row two, and 10-second rewind/forward in row three.
- Progress track targets 5.5 mm through Android display metrics, with a 40 dp handle and 64 dp touch area.
- Replaced Home navigation with Player and redirected legacy Home routes. Subscriptions is the fallback with no selected episode.
- Both debug variants passed :app:assembleDebug; final incremental build completed in 7 seconds.
- Installed the free debug update and visually checked the three rows and thicker slider on the Galaxy S22 Ultra.
- Verified cold launch opens playback, Subscriptions remains accessible, and Player returns to full playback.
- Playback remained paused at 01:15:43 during navigation checks. Physical millimeter size was not measured with a ruler.

## Toolbar and sleep timer clarity

- Removed the down arrow and skip-interval instruction from the audio player.
- Moved playback speed to an icon beside Favorite, retaining the existing speed dialog.
- Off timer uses a grey icon; active timer uses an explicit blue Turn off timer action.
- Direct off cancels through the Media3 command and disables automatic timer activation.
- Audio player consumes sticky timer events to restore the visible state when recreated.
- Both debug variants passed :app:assembleDebug in 28 seconds; final action-view revision passed in 16 seconds.
- Installed the final free debug APK. On Galaxy S22 Ultra, checked the cleaned layout, speed dialog,
  timer activation, visible off action, and return to the grey icon after direct cancellation.
- Timer setup requires a running playback service, as upstream does. Started playback briefly to exercise setup.
- Left the episode paused at 01:17:18 with timer off. Landscape, video, and Bluetooth were not tested in this change.

## Countdown and next-button removal

- Removed the audio player's next-episode button and its click handler.
- Active sleep timer now displays hours/minutes/seconds remaining with Tap to turn off underneath.
- Episode-based timers display remaining episodes instead of a misleading time duration.
- Both debug variants passed :app:assembleDebug in 38 seconds. Installed the free debug APK as an update.
- Phone validation confirmed the next button is absent and the active countdown fits the toolbar.
- Countdown decreased from 00:14:53 to 00:14:49 during playback and held while paused, matching service behavior.
- Verified tapping the countdown cancels it. Left playback paused and the timer off.
- Episode-mode display, landscape, and Bluetooth were not device-tested in this change.

## Longer skip options and slimmer progress track

- Added 80, 100, and 120 seconds to the shared skip interval options.
- Reduced the progress track from nominal 5.5 mm to 2.2 mm, a 60 percent reduction, retaining the touch area and thumb.
- Enlarged elapsed and duration/remaining time labels to 18 sp.
- Final :app:assembleDebug passed for both variants in 13 seconds after an earlier 18-second build.
- Installed the final free debug APK. Visually verified the slimmer track, larger time labels, and all three new options
  in the Big forward dialog on the Galaxy S22 Ultra. Dismissed without changing the user's current skip settings.

## Remaining and total time together

- Right-hand progress label now always displays remaining / total, retaining playback-speed conversion and 18 sp size.
- Removed the former tap-to-toggle behavior and added an accessible remaining/total description.
- Both debug variants passed :app:assembleDebug in 36 seconds. Installed free debug update.
- Verified on Galaxy S22 Ultra: elapsed 01:19:11 and right label 00:10:45 / 01:29:56, with no overlap.
- Left the episode paused. Landscape was not tested for this label change.

## Compact sleep countdown

- Sleep countdown now shows total minutes and two-digit seconds, with no hours or visible instruction text.
- Retained tap-to-cancel and its accessible description; other playback time labels retain their format.
- Both debug variants passed :app:assembleDebug in 12 seconds; final text-only revision passed in 11 seconds.
- Installed final free debug update and visually verified 14:52 alone in the toolbar on Galaxy S22 Ultra.
- Left playback paused and tapped the countdown to turn the timer off after checking.

## Skip button capitalization

- Capitalized both words on all four skip buttons: Big Rewind, Big Forward, Small Rewind, Small Forward.
- Both debug variants passed :app:assembleDebug in 23 seconds; free debug update installed successfully.
- Visually confirmed labels on the Galaxy S22 Ultra. No playback or skip settings changed by this update.
- Car Bluetooth button behavior remains untested and needs validation with the actual car controls.
