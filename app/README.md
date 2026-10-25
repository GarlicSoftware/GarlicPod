# :app

The main application module that integrates all features and hosts app-specific UI screens not large enough for their own module.
`PodcastApp` initializes the app; `ClientConfigurator` registers service implementations (download, sync) at startup.

The miniplayer (the collapsed player bar at the bottom of the screen) is implemented in `ExternalPlayerFragment`.
It is hosted in `MainActivity` as a bottom sheet. `MainActivity` controls its visibility via `setPlayerVisible()` based on playback state.

GarlicPod's full player layout is `audioplayer_fragment.xml`; `AudioPlayerFragment` wires its controls.
`CoverFragment` owns the artwork toggle and metadata. Launcher-only player expansion is requested in `SplashActivity`
using `MainActivityStarter.EXTRA_OPEN_PLAYER`, so explicit feed/episode navigation keeps its normal behavior.
Skip interval dialogs share `SkipPreferenceDialog`; preserve independent big-skip preferences when modifying controls.

MainActivity defers player expansion until `setPlayerVisible(true)` signals media is ready.
Its process lifecycle observer requests player expansion when returning from background without a new launch intent.
Keep explicit feed/episode navigation ahead of the open-player intent branch, and do not start audio from navigation.

The Home navigation identifier now represents Player and expands the playback sheet. Legacy Home routes
fall back to Subscriptions when no episode is selected; MainActivity does not instantiate the Home dashboard.
Control rows are play/pause, big rewind/forward, then small rewind/forward. ChapterSeekBar draws a nominal
5.5 mm track using display metrics; its layout provides a 64 dp touch area and a 40 dp thumb.

AudioPlayerFragment consumes sticky SleepTimerUpdatedEvent state so the toolbar reflects an existing timer
when the screen is recreated. Its active timer action cancels through the Media3 session command and disables
SleepTimerPreferences automatic activation. A custom action view keeps the off action visible in portrait.
