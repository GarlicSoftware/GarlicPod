package de.danoeh.antennapod.playback.service.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.KeyEvent;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaSession;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.model.feed.FeedItem;
import de.danoeh.antennapod.model.feed.FeedMedia;
import de.danoeh.antennapod.net.sync.serviceinterface.SynchronizationQueue;
import de.danoeh.antennapod.net.sync.serviceinterface.SynchronizationQueueStub;
import de.danoeh.antennapod.playback.base.MediaItemAdapter;
import de.danoeh.antennapod.storage.database.FeedDatabaseWriter;
import de.danoeh.antennapod.storage.database.PodDBAdapter;
import de.danoeh.antennapod.storage.preferences.PlaybackPreferences;
import de.danoeh.antennapod.storage.preferences.UserPreferences;
import de.danoeh.antennapod.ui.appstartintent.MediaButtonStarter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class MediaLibrarySessionCallbackTest {
    private static final String EPISODE_TITLE = "Episode Title";
    private Context context;
    private MediaLibrarySessionCallback callback;
    private final MediaSession session = mock(MediaSession.class);
    private final MediaSession.ControllerInfo controllerInfo = mock(MediaSession.ControllerInfo.class);

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        UserPreferences.init(context);
        PlaybackPreferences.init(context);
        PodDBAdapter.init(context);
        PodDBAdapter.deleteDatabase();
        SynchronizationQueue.setInstance(new SynchronizationQueueStub());
        callback = new MediaLibrarySessionCallback(context);
    }

    @After
    public void tearDown() {
        callback.cancelPendingHardwareSkip();
        PodDBAdapter.tearDownTests();
    }

    @Test
    public void singleHardwarePressWaitsForDoublePressAndIgnoresRelease() {
        Player player = hardwarePlayer();
        press(KeyEvent.KEYCODE_MEDIA_NEXT);
        assertTrue(callback.onMediaButtonEvent(session, controllerInfo,
                new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT,
                        new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT))));
        verify(player, never()).seekToNext();
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(401, TimeUnit.MILLISECONDS);
        verify(player).seekToNext();
        verify(player, never()).seekTo(80000L);
    }

    @Test
    public void doublePressUsesConfiguredBigSkipWithoutSmallSkip() {
        Player player = hardwarePlayer();
        UserPreferences.setBigFastForwardSecs(80);
        press(KeyEvent.KEYCODE_MEDIA_NEXT);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(200, TimeUnit.MILLISECONDS);
        press(KeyEvent.KEYCODE_MEDIA_NEXT);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(500, TimeUnit.MILLISECONDS);
        verify(player).seekTo(110000L);
        verify(player, never()).seekToNext();
    }

    @Test
    public void bigRewindClampsAtStartAndForwardClampsAtEnd() {
        Player player = hardwarePlayer();
        UserPreferences.setBigRewindSecs(60);
        UserPreferences.setBigFastForwardSecs(120);
        press(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        press(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        verify(player).seekTo(0L);
        press(KeyEvent.KEYCODE_MEDIA_NEXT);
        press(KeyEvent.KEYCODE_MEDIA_NEXT);
        verify(player).seekTo(120000L);
        verify(player, never()).seekToPrevious();
    }

    @Test
    public void oppositeDirectionsRemainSeparateSinglePresses() {
        Player player = hardwarePlayer();
        press(KeyEvent.KEYCODE_MEDIA_NEXT);
        press(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        verify(player).seekToNext();
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(401, TimeUnit.MILLISECONDS);
        verify(player).seekToPrevious();
    }

    @Test
    public void pendingSkipIsDiscardedWhenEpisodeChangesOrServiceStops() {
        Player player = hardwarePlayer();
        press(KeyEvent.KEYCODE_MEDIA_NEXT);
        when(player.getCurrentMediaItem()).thenReturn(MediaItem.fromUri("https://example.com/other"));
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(401, TimeUnit.MILLISECONDS);
        verify(player, never()).seekToNext();
        press(KeyEvent.KEYCODE_MEDIA_NEXT);
        callback.cancelPendingHardwareSkip();
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(401, TimeUnit.MILLISECONDS);
        verify(player, never()).seekToNext();
    }

    @Test
    public void widgetNextKeepsImmediateEpisodeSkip() {
        Player player = hardwarePlayer();
        callback.onMediaButtonEvent(session, controllerInfo,
                new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT,
                        new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT))
                        .putExtra(MediaButtonStarter.EXTRA_MEDIA_BUTTON_SOURCE,
                                MediaButtonStarter.MEDIA_BUTTON_SOURCE_WIDGET));
        verify(player).seekToNextMediaItem();
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(401, TimeUnit.MILLISECONDS);
        verify(player, never()).seekToNext();
    }

    private Player hardwarePlayer() {
        Player player = mock(Player.class);
        when(session.getPlayer()).thenReturn(player);
        when(player.getCurrentMediaItem()).thenReturn(MediaItem.fromUri("https://example.com/episode"));
        when(player.getCurrentPosition()).thenReturn(30000L);
        when(player.getDuration()).thenReturn(120000L);
        return player;
    }

    private void press(int keyCode) {
        assertTrue(callback.onMediaButtonEvent(session, controllerInfo,
                new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT,
                        new KeyEvent(KeyEvent.ACTION_DOWN, keyCode))));
    }

    @Test
    public void onSetMediaItemsStub() throws Exception {
        long mediaId = seedEpisode().getId();
        MediaItem browseItem = MediaItemAdapter.fromMediaIdStub(mediaId);
        MediaSession.MediaItemsWithStartPosition result = callback.onSetMediaItems(
                session, controllerInfo, Collections.singletonList(browseItem), C.INDEX_UNSET, C.TIME_UNSET)
                .get(5, TimeUnit.SECONDS);
        assertEquals(1, result.mediaItems.size());
        assertEquals(String.valueOf(mediaId), result.mediaItems.get(0).mediaId);
        assertEquals(EPISODE_TITLE, result.mediaItems.get(0).mediaMetadata.title);
    }

    @Test
    public void onPlaybackResumption() throws Exception {
        FeedMedia media = seedEpisode();
        PlaybackPreferences.writeMediaPlaying(media);
        MediaSession.MediaItemsWithStartPosition result = callback.onPlaybackResumption(session, controllerInfo)
                .get(5, TimeUnit.SECONDS);
        assertEquals(1, result.mediaItems.size());
        assertEquals(String.valueOf(media.getId()), result.mediaItems.get(0).mediaId);
    }

    @Test
    public void onAndroidAutoVoiceSearchQuery() throws Exception {
        long mediaId = seedEpisode().getId();
        MediaItem searchItem = MediaItem.EMPTY.buildUpon()
                .setRequestMetadata(new MediaItem.RequestMetadata.Builder().setSearchQuery(EPISODE_TITLE).build())
                .build();
        MediaSession.MediaItemsWithStartPosition result = callback.onSetMediaItems(session, controllerInfo,
                Collections.singletonList(searchItem), C.INDEX_UNSET, C.TIME_UNSET).get(5, TimeUnit.SECONDS);
        assertEquals(1, result.mediaItems.size());
        assertEquals(String.valueOf(mediaId), result.mediaItems.get(0).mediaId);

        // No match: nothing to play
        searchItem = MediaItem.EMPTY.buildUpon()
                .setRequestMetadata(new MediaItem.RequestMetadata.Builder().setSearchQuery("Unrelated").build())
                .build();
        result = callback.onSetMediaItems(session, controllerInfo,
                Collections.singletonList(searchItem), C.INDEX_UNSET, C.TIME_UNSET).get(5, TimeUnit.SECONDS);
        assertEquals(0, result.mediaItems.size());

        // Empty query ("play something"): fall back to playing something rather than nothing, per
        // Android Auto/Assistant voice action guidelines.
        searchItem = MediaItem.EMPTY.buildUpon()
                .setRequestMetadata(new MediaItem.RequestMetadata.Builder().setSearchQuery("").build())
                .build();
        result = callback.onSetMediaItems(session, controllerInfo,
                Collections.singletonList(searchItem), C.INDEX_UNSET, C.TIME_UNSET).get(5, TimeUnit.SECONDS);
        assertEquals(1, result.mediaItems.size());
        assertEquals(String.valueOf(mediaId), result.mediaItems.get(0).mediaId);
    }

    private FeedMedia seedEpisode() {
        Feed feed = new Feed("url", null, null);
        feed.setItems(new ArrayList<>());
        FeedItem item = new FeedItem();
        item.setItemIdentifier("id");
        item.setTitle(EPISODE_TITLE);
        item.setMedia(new FeedMedia(item, "http://example.com", 2, "mime"));
        item.setFeed(feed);
        feed.getItems().add(item);
        return FeedDatabaseWriter.updateFeed(context, feed, false).getItems().get(0).getMedia();
    }
}
