# GarlicPod

Personal Android podcast player derived from AntennaPod, maintained by GarlicSoftware.
The first milestone preserves the upstream interface and playback functionality while installing independently.
Large playback controls, player-first startup, Bluetooth skip remapping, and smart resume are later phases.

## Upstream baseline

- Repository: https://github.com/AntennaPod/AntennaPod
- Stable tag: `3.12.3`
- Commit: `175e5ef8fe3e125a5014d70ef0c57cf85661c71b`
- License: GPL-3.0; see LICENSE and retained source copyright notices.
- Upstream README is preserved in README_UPSTREAM.md.
- No automatic upstream merges. Review significant fixes individually.

## Build

Install JDK 21 and Android SDK platform 36 with build tools 35.0.0.
Set JAVA_HOME to JDK 21 and ANDROID_HOME to your SDK directory, or configure SDK location in ignored local.properties.
Use the checked-in Gradle 8.13 wrapper, not a system Gradle installation.

```powershell
.\gradlew.bat :app:assembleDebug
```

Outputs are under `app/build/outputs/apk/`. Prefer the free debug variant for the first phone test.
Do not install the unmodified upstream APK on the phone.
See PROJECT_STATUS.md for the actual build and device validation state.

## Two-computer workflow

GitHub is the source of truth: https://github.com/GarlicSoftware/GarlicPod

```powershell
git clone https://github.com/GarlicSoftware/GarlicPod.git
```

Before editing, read AGENTS.md and PROJECT_STATUS.md and inspect `git status`.
Before switching computers, commit focused changes, update PROJECT_STATUS.md, and push main.
On the other computer, preserve local edits, then fetch origin and merge origin/main with --ff-only.
Git transfers source and project documentation, not Codex conversation history or local SDK configuration.

Debug signing keys are local and must never be committed. Different laptop keys cannot update the same installed APK.
Keep one laptop responsible for installed builds initially. A future release key needs a separate secure transfer plan.
Never uninstall an existing app to work around a signing mismatch without backing up its data and obtaining permission.

## Data transfer and attribution

Use AntennaPod's native OPML export/import to transfer subscriptions first. OPML does not transfer playback positions,
settings, or downloaded audio. Upstream also supports full database backup, but cross-application restore has not been
validated. Do not copy private app databases directly.

GarlicPod is an independent derivative, not an official AntennaPod release. Preserve GPL notices and provide matching
source when distributing APKs. Modifications and validation are tracked in PROJECT_STATUS.md and Git history.


## GarlicPod player

Normal launcher startup opens the full player when an episode is selected, without autoplay.
The three large control rows are play/pause, 60-second rewind/forward, then 10-second rewind/forward.
Hold any skip button to choose its seconds. The progress track targets 5.5 mm with a larger handle and touch area.
Big rewind and big forward have independent saved settings. Small skips reuse AntennaPod's original interval settings.
Artwork also toggles play/pause. Player replaces Home in navigation; the old dashboard routes open playback.
Collapse the player with the down arrow to manage podcasts normally. With no selected episode, Subscriptions opens.
