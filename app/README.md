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
Its activity lifecycle requests player expansion on creation and return from background without explicit navigation.
This also covers Android restoring an existing task without delivering a new launcher intent. Explicit new navigation
is consumed before resume so opening a feed or episode does not get replaced by the player.
Keep explicit feed/episode navigation ahead of the open-player intent branch, and do not start audio from navigation.

The Home navigation identifier now represents Player and expands the playback sheet. Legacy Home routes
fall back to Subscriptions when no episode is selected; MainActivity does not instantiate the Home dashboard.
Control rows are play/pause, big rewind/forward, then small rewind/forward. ChapterSeekBar draws a nominal
2.2 mm track using display metrics; its layout provides a 64 dp touch area and a 40 dp thumb.

AudioPlayerFragment consumes sticky SleepTimerUpdatedEvent state so the toolbar reflects an existing timer
when the screen is recreated. Its active timer action cancels through the Media3 session command and disables
SleepTimerPreferences automatic activation. A custom action view keeps the off action visible in portrait.
The active action view displays remaining minutes:seconds from timer events, or remaining episodes for episode timers.
The right-hand audio progress label always shows remaining / total time, using the existing playback-speed conversion.

Launcher resources are overridden in app/src/main/res so both debug variants use GarlicPod branding instead of
the library's upstream icon. Density-specific legacy PNGs support older Android; adaptive and monochrome layers
support newer launchers. Approved source artwork is retained under assets/branding/garlicpod-aqua.
