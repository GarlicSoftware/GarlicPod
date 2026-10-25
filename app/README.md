# :app

The main application module that integrates all features and hosts app-specific UI screens not large enough for their own module.
`PodcastApp` initializes the app; `ClientConfigurator` registers service implementations (download, sync) at startup.

The miniplayer (the collapsed player bar at the bottom of the screen) is implemented in `ExternalPlayerFragment`.
It is hosted in `MainActivity` as a bottom sheet. `MainActivity` controls its visibility via `setPlayerVisible()` based on playback state.

GarlicPod's full player layout is `audioplayer_fragment.xml`; `AudioPlayerFragment` wires its controls.
`CoverFragment` owns the artwork toggle and metadata. Launcher-only player expansion is requested in `SplashActivity`
using `MainActivityStarter.EXTRA_OPEN_PLAYER`, so explicit feed/episode navigation keeps its normal behavior.
Skip interval dialogs share `SkipPreferenceDialog`; preserve independent big-skip preferences when modifying controls.
