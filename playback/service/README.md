# :playback:service

The main service doing media playback.

`Media3PlaybackService` is the active implementation, built on AndroidX Media3/ExoPlayer.
`PlaybackService` is legacy and will throw an exception if started — it exists only during the transition period.

External callers should interact with the service through `PlaybackController`, which provides a
`bindToMedia3Service()` helper that connects a `MediaController` and runs a callback on it.
The `MediaController` exposes the standard Media3 `Player` interface: `seekTo(positionMs)`,
`play()`, `pause()`, `getCurrentPosition()`, `getPlaybackParameters()`, etc.
Each call to `bindToMedia3Service()` creates a short-lived connection that is released after the
callback returns.

Hardware next/previous key events are handled in `MediaLibrarySessionCallback`: one press waits 400 ms
before using the existing hardware action mapping, and a second press from the same controller on the
same episode uses the configured big skip instead. Key releases and repeats are consumed without seeking.
Widget next-episode commands bypass this gesture. Pending gestures are cancelled when the service stops
and discarded if the episode changes before execution. Direct controller commands retain their existing mapping.
