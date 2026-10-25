# Project: GarlicPod - Custom Android Podcast Player

## Project Overview

I want to create a customized Android podcast player called **GarlicPod**, based on a fork of the open-source AntennaPod application.

Official source repository:
https://github.com/AntennaPod/AntennaPod

My GitHub organization is **GarlicSoftware**.

This is intended primarily as a personal-use application for my Samsung Galaxy S22 Ultra, running Android 16.

**The objective is NOT to rewrite AntennaPod.** I want to preserve its existing, mature podcast management functionality while modifying its interface and certain playback behaviors to suit my preferences.

The primary motivation is making podcast playback significantly easier to control, particularly while driving, without needing to look at or interact extensively with the phone.

I develop with assistance from Codex. You should handle the programming, repository investigation, configuration, and implementation while explaining important decisions to me in straightforward terms.

## 1. Development Strategy

Fork AntennaPod rather than building a new application.

Preserve its existing capabilities wherever possible, including:

- Podcast subscriptions and RSS feed management.
- Automatic discovery and downloading of new episodes.
- Episode lists, descriptions, and show notes.
- Offline playback.
- Playback position tracking.
- Playback speed adjustment.
- Background playback and Android media notifications.
- Bluetooth, headphone, and external media controls.
- All necessary database and storage management.

Use the existing AntennaPod architecture and technologies unless there is a compelling reason to introduce something different.

Keep our changes focused and maintainable.

**Important:** This is intended to become a relatively static personal application. We do not need to continuously merge new AntennaPod releases. Once GarlicPod works reliably, we can leave it alone unless a significant bug, compatibility issue, or security vulnerability requires attention.

Use a stable upstream release as the starting point rather than automatically using potentially unstable development code. Record the exact upstream tag and commit.

## 2. Primary Modification: Playback Screen

The existing AntennaPod playback interface is my biggest source of frustration.

Currently, opening the application usually shows an episode list or another navigation screen, with the active player reduced to a miniature bar near the bottom.

To access the actual playback controls, I must tap that miniature player to expand the full playback screen.

Even when expanded, the playback buttons are unnecessarily small, making them difficult to operate without looking closely.

I want to change this behavior.

### New startup behavior

Whenever I open GarlicPod normally:

- If a podcast is currently playing or has a saved playback position, open directly to the full playback screen.
- Do not default to the episode list with the small playback bar.
- Restore the last podcast and playback position.
- Do not automatically start audio playback merely because the application was opened.
- If there is no previously selected podcast, open an appropriate podcast browsing screen.

The playback screen should be the primary home interface when an episode is available.

All other existing podcast management functions should remain accessible through secondary navigation.

### Redesigned playback interface

I want the playback screen to prioritize large, easily identifiable controls.

The layout should contain:

**Top section:**
- Small podcast artwork or thumbnail.
- Podcast name.
- Current episode title.
- Playback progress bar.
- Elapsed time and remaining time.

**Main section:**
- Very large REWIND button.
- Very large PLAY/PAUSE button.
- Very large FAST FORWARD button.

These three buttons should occupy a substantial portion of the available screen, particularly the lower portion that is easy to reach.

Unlike the original AntennaPod layout, do not devote most of the screen to oversized podcast artwork.

Button requirements:

- Large touch targets covering their entire button area, not merely the visible icon.
- Obvious and unambiguous visual indicators.
- High contrast and readable labels.
- Enough spacing to avoid accidentally activating adjacent buttons.
- No tiny controls that require precise finger positioning.
- Keep unnecessary controls away from the main playback area.

Initially, use these behaviors:

- REWIND: Back 10 seconds.
- PLAY/PAUSE: Toggle playback.
- FAST FORWARD: Forward 30 seconds.

These values and the exact button sizes and positions may be adjusted as we develop and test the interface.

### Important distinction: Fixed customization

I do NOT need an in-app interface for customizing buttons.

I do not want a drag-and-drop button editor, user-configurable layouts, or a separate customization system.

Instead, we will determine the desired button sizes, positions, and actions during development, and implement them directly in the application.

If I later want something moved or resized, we can edit the source code.

This should simplify the implementation substantially.

## 3. Bluetooth and External Media Controls

This is an extremely important requirement.

I regularly listen to podcasts in my vehicle and want to operate the player using existing Bluetooth media controls.

The next-track and previous-track commands from my Bluetooth controller should NOT normally switch to another podcast episode.

Instead:

- Bluetooth PREVIOUS: Rewind the current episode 10 seconds.
- Bluetooth NEXT: Advance the current episode 30 seconds.
- Bluetooth PLAY/PAUSE: Play or pause the current episode.

These durations may be adjusted during development.

Investigate AntennaPod's existing hardware media-button handling first. Reuse its existing remapping support if practical.

Make the changes at the appropriate media-session or playback-control layer rather than creating a fragile workaround tied to the visible activity.

Ensure the behavior works when:

- The phone screen is turned off.
- The application is in the background.
- The phone is connected to my vehicle's Bluetooth audio system.
- Playback is controlled by Bluetooth headphones or another media controller.
- The application has been suspended or its interface is not currently visible, provided its playback service remains active.

Distinguish hardware next/previous commands from normal in-app episode selection so that manually selecting another episode continues to work.

We should test the behavior with my actual Bluetooth controller as early as possible because manufacturers sometimes implement media commands differently.

**The objective is to control playback without interacting with the phone while driving.**

## 4. Automatic Rewind When Resuming Playback

I want a feature that automatically rewinds slightly after playback has been paused for a substantial period.

Example:

- I pause a podcast at 42:15.
- I stop listening for 30 minutes.
- When I resume, playback starts at 41:45 instead of 42:15.

This allows me to hear the previous conversation again and remember what was happening.

Initial desired behavior:

- Pause duration threshold: 10 minutes.
- Automatic rewind: 30 seconds.
- If the pause is shorter than the threshold, resume normally.

Ideally, this should work even if Android terminates the application between stopping and resuming.

Persist whatever timestamp and playback position information is necessary.

Check whether AntennaPod already provides this feature before implementing anything new. If it exists, configure or adapt the existing implementation rather than duplicating it.

Prevent the rewind from producing invalid negative playback positions.

Also avoid treating manual seeks as long pauses.

## 5. Preserve Existing Podcast Functionality

I am generally satisfied with AntennaPod's podcast management features.

I want to continue being able to:

- Find and subscribe to podcasts.
- Download new episodes automatically.
- Browse previously published episodes.
- Download an episode manually.
- Listen without an internet connection.
- Resume partially played episodes.
- Manage downloads and delete completed episodes.
- Access episode descriptions and show notes.

Do not unnecessarily redesign these portions of the application.

The main changes should be the primary playback interface, startup navigation, Bluetooth media controls, and smart-resume behavior.

## 6. Independent Installation

GarlicPod must be installable alongside the original AntennaPod.

Do not overwrite, uninstall, or modify my existing installation during development.

Use a unique Android application ID and appropriate application name, ensuring that the two applications have independent storage and databases.

Check for manifest authorities, deep links, file providers, and other identifiers that could conflict between the two installed applications.

For now, prioritize producing a debug APK that I can install directly on my phone.

Google Play Store publication is not required.

Investigate whether existing subscriptions and settings can be transferred using AntennaPod's native export/import functionality. Do not assume direct database copying between applications will work.

## 7. GitHub and Two-Computer Development

I regularly alternate between two Windows laptops:

1. A mobile laptop that I use when away from my primary workspace.
2. My main development laptop.

I want to be able to start working on one laptop and continue seamlessly on the other.

Use GitHub as the central source of truth.

Preferred repository:

**GarlicSoftware/GarlicPod**

If the repository doesn't exist, help me create the fork or an appropriately configured derivative repository in that organization.

Set up the necessary Git remotes and explain the workflow.

Before switching machines, I should be able to commit and push the work.

On the second machine, I should be able to clone or pull the repository and resume development.

### Persistent development context

The project must be understandable to a fresh Codex conversation on either laptop.

AntennaPod may already contain an AGENTS.md file. Inspect and preserve its useful development instructions.

Extend it appropriately, or add separate project-specific documentation, rather than blindly replacing existing instructions.

Maintain these project files:

**AGENTS.md**
- Project purpose and overall architecture.
- Coding instructions for Codex.
- Important project-specific conventions.
- Requirement to preserve existing AntennaPod functionality.
- Instructions for running builds and tests.
- Rules for changing the user interface and playback behaviors.

**PROJECT_STATUS.md**
- Current project state.
- Completed modifications.
- Current work in progress.
- Known problems.
- Important decisions.
- Next development steps.
- Test results and build status.

**README.md**
- Description of GarlicPod.
- Relationship to AntennaPod.
- Installation and development instructions.
- Basic instructions for continuing development on another computer.

Update the status documentation whenever significant progress occurs, so a new Codex session can understand the project without relying on previous conversation history.

Never commit signing secrets, passwords, personal tokens, or machine-specific paths.

## 8. Licensing

AntennaPod is released under GPL-3.0.

Preserve the appropriate original copyright notices, attribution, license text, and other required licensing information.

Document our modifications and ensure that our repository and any distribution comply with the upstream license.

## 9. Development Priorities

Proceed incrementally.

### Phase 1: Repository and build setup

1. Inspect the official AntennaPod repository and its development instructions.
2. Identify a suitable stable release to use as our baseline.
3. Establish the GarlicPod repository under GarlicSoftware.
4. Configure Git correctly.
5. Identify required Android SDK, Java, Gradle, and build dependencies.
6. Check the development laptop for the required tools and help configure anything missing.
7. Build the unmodified application successfully.
8. Configure a distinct application ID and GarlicPod branding.
9. Produce an installable APK that can coexist with AntennaPod.
10. Commit and push this baseline.
11. Establish the project documentation.

Do not assume the initial Android build will succeed. Investigate and resolve actual build errors instead of guessing.

### Phase 2: Playback interface

1. Locate the existing playback activity, fragment, or equivalent UI components.
2. Determine how the miniature player and full playback screen interact.
3. Change startup navigation to prioritize the full playback screen.
4. Reduce the amount of space consumed by podcast artwork.
5. Implement the large playback buttons.
6. Preserve normal navigation to podcast management screens.
7. Build and test these changes.

### Phase 3: Bluetooth controls

1. Identify existing media-session and hardware-button handling.
2. Implement or configure the 10-second rewind and 30-second forward commands.
3. Verify correct behavior during background playback.
4. Test on my actual phone and Bluetooth controller.
5. Resolve any incompatibilities discovered.

### Phase 4: Smart resume

1. Inspect the existing playback-resume behavior.
2. Reuse any existing automatic rewind implementation.
3. Implement the desired 10-minute threshold and 30-second rewind if necessary.
4. Ensure persistence across application restarts.
5. Test various pause durations and resume scenarios.

### Phase 5: Polish and stabilization

1. Refine the layout based on actual phone testing.
2. Adjust control dimensions and positions.
3. Check playback reliability.
4. Test downloads and RSS subscriptions.
5. Test background playback and Bluetooth reconnection.
6. Verify the original AntennaPod installation remains unaffected.
7. Produce a working personal-use release build.
8. Document the final implementation.

## 10. Working Style

Please follow these instructions throughout development:

- Prefer modifying existing code over rewriting functionality.
- Understand the relevant existing implementation before changing it.
- Keep changes small and logically separated.
- Build and test incrementally.
- Make meaningful Git commits.
- Avoid unnecessary dependencies and additional complexity.
- Never silently remove working podcast features.
- Tell me when a design choice involves a meaningful tradeoff.
- Explain technical problems in plain English.
- Do not invent functionality or claim something has been tested when it hasn't.
- Do not automatically merge future AntennaPod releases.
- Treat GarlicPod as a focused personal software project, not a commercial podcast platform.
- Preserve compatibility with Android 16 and my Galaxy S22 Ultra.
- Do not mistake Git synchronization for synchronization of Codex conversation histories.

I prefer practical, maintainable solutions rather than elaborate architecture for problems that don't require it.

## 11. Immediate Instructions

**Start working on Phase 1 now.**

First, inspect the official AntennaPod repository, its existing AGENTS.md, its build system, and its architecture.

Check what development tools are available on this computer.

Explain briefly what we need to do to establish the fork and get the first build running.

Then proceed with the setup and baseline build using the tools available to you.

If a step requires GitHub authorization, installation, or a decision that cannot safely be made automatically, tell me exactly what is required.

Do not immediately attempt a wholesale interface rewrite before establishing a clean, working baseline.

The first major milestone is:

**GarlicPod installs alongside AntennaPod on my phone, opens, and plays podcasts normally using the existing interface.**

Once that milestone is achieved, we can start modifying the playback screen and controls.

Let's get started.
