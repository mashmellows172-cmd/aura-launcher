package dev.senda.launcher.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import dev.senda.launcher.MediaAccessService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlin.math.roundToInt

data class ActiveAudioInfo(
    val isPlaying: Boolean = false,
    val title: String? = null,
    val artist: String? = null,
    val packageName: String? = null,
    val appLabel: String? = null,
    val sessionId: String? = null,
    val mediaAccess: Boolean = false,
    val generic: Boolean = false,
    val canPause: Boolean = false,
    val canPrevious: Boolean = false,
    val canNext: Boolean = false,
    val artwork: Bitmap? = null,
    val positionMs: Long? = null,
    val durationMs: Long? = null,
    val positionUpdatedAt: Long = 0L,
    val speed: Float = 0f,
    val canSeek: Boolean = false,
    val canPlay: Boolean = false,
    val isBuffering: Boolean = false,
    val mediaId: String? = null,
) {
    val isYouTubeVideo: Boolean get() = packageName == "com.google.android.youtube"
    val playbackLabel: String get() = when {
        isBuffering -> "Cargando…"
        isPlaying -> "En reproducción"
        else -> "En pausa"
    }
}

/**
 * Monitor autonomo de reproduccion de audio.
 * Consent enables callbacks for public media sessions, including silent video.
 * Without it, AudioManager reports only generic local music activity, without metadata.
 * Home visibility and the user's widget preference own this subscriber's lifetime.
 */
class AudioPlaybackMonitor(context: Context) {
    private val app = context.applicationContext
    private val audio = app.getSystemService(AudioManager::class.java)
    private val sessions = app.getSystemService(MediaSessionManager::class.java)
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val mutableState = MutableStateFlow(ActiveAudioInfo())
    val state: StateFlow<ActiveAudioInfo> = mutableState.asStateFlow()
    private data class Entry(val id: String, val controller: MediaController, val callback: MediaController.Callback)
    private val entries = linkedMapOf<MediaSession.Token, Entry>()
    private val labels = mutableMapOf<String, String>()
    private var nextId = 0L
    private var visible = false
    private var observing = false
    private var current: Entry? = null
    private var connectionJob: Job? = null
    private var fallbackJob: Job? = null
    private var artworkJob: Job? = null
    private var artRevision = 0L
    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        if (visible) attach(controllers.orEmpty())
    }

    fun start() {
        if (visible) return
        visible = true
        refreshAccess()
        connectionJob = scope.launch { MediaAccessService.connection.collect { if (visible) refreshAccess() } }
    }

    fun stop() {
        visible = false
        connectionJob?.cancel(); connectionJob = null
        fallbackJob?.cancel(); fallbackJob = null
        disconnect()
        mutableState.value = ActiveAudioInfo(mediaAccess = MediaAccessService.isEnabled(app))
    }

    fun close() { stop(); scope.cancel() }

    private fun disconnect() {
        if (observing) runCatching { sessions?.removeOnActiveSessionsChangedListener(sessionListener) }
        observing = false
        entries.values.forEach { runCatching { it.controller.unregisterCallback(it.callback) } }
        entries.clear(); current = null
        artworkJob?.cancel(); artworkJob = null; artRevision++
    }

    private fun refreshAccess() {
        if (!visible) return
        if (MediaAccessService.isEnabled(app) && sessions != null) {
            try {
                if (!observing) {
                    sessions.addOnActiveSessionsChangedListener(sessionListener, MediaAccessService.component(app), handler)
                    observing = true
                }
                attach(sessions.getActiveSessions(MediaAccessService.component(app)))
                return
            } catch (_: SecurityException) { disconnect() }
            catch (_: RuntimeException) { disconnect() }
        } else if (observing || entries.isNotEmpty()) disconnect()
        publishGeneric()
        ensureFallback()
    }

    private fun attach(controllers: List<MediaController>) {
        val tokens = controllers.map { it.sessionToken }.toSet()
        entries.keys.filter { it !in tokens }.forEach { token ->
            entries.remove(token)?.let { runCatching { it.controller.unregisterCallback(it.callback) } }
        }
        controllers.forEach { controller ->
            if (controller.sessionToken !in entries) {
                val callback = object : MediaController.Callback() {
                    override fun onPlaybackStateChanged(state: PlaybackState?) { if (visible) publish() }
                    override fun onMetadataChanged(metadata: MediaMetadata?) { if (visible) publish(artChanged = true) }
                    override fun onSessionDestroyed() {
                        entries.remove(controller.sessionToken)?.let { runCatching { it.controller.unregisterCallback(it.callback) } }
                        if (visible) publish()
                    }
                }
                val entry = Entry("media-${++nextId}", controller, callback)
                runCatching { controller.registerCallback(callback, handler) }.onSuccess { entries[controller.sessionToken] = entry }
            }
        }
        publish()
    }

    private fun publish(artChanged: Boolean = false) {
        if (!visible) return
        if (!MediaAccessService.isEnabled(app)) { disconnect(); publishGeneric(); ensureFallback(); return }
        // Never lend a paused player's metadata or controls to another application's audio.
        val playing = entries.values.filter { runCatching { it.controller.playbackState?.state == PlaybackState.STATE_PLAYING }.getOrDefault(false) }
        val canUsePaused = runCatching { audio?.isMusicActive != true }.getOrDefault(false)
        val waiting = entries.values.filter { entry ->
            runCatching {
                val state = entry.controller.playbackState ?: return@runCatching false
                when (state.state) {
                    PlaybackState.STATE_BUFFERING, PlaybackState.STATE_CONNECTING -> true
                    PlaybackState.STATE_PAUSED -> canUsePaused &&
                        state.actions and (PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PLAY_PAUSE) != 0L &&
                        entry.controller.metadata?.description?.title?.isNotBlank() == true
                    else -> false
                }
            }.getOrDefault(false)
        }
        val selected = playing.firstOrNull { it.id == current?.id } ?: playing.firstOrNull()
            ?: waiting.firstOrNull { it.id == current?.id } ?: waiting.firstOrNull()
        if (selected == null) {
            current = null; artworkJob?.cancel(); artRevision++
            publishGeneric(); ensureFallback(); return
        }
        fallbackJob?.cancel(); fallbackJob = null
        val changed = selected.id != current?.id
        current = selected
        val controller = selected.controller
        val metadata = runCatching { controller.metadata }.getOrNull()
        val playback = runCatching { controller.playbackState }.getOrNull()
        val actions = playback?.actions ?: 0L
        val duration = runCatching { metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)?.takeIf { it > 0L } }.getOrNull()
        fun supports(action: Long) = actions and action != 0L
        val canTransport = playback?.state == PlaybackState.STATE_PLAYING || playback?.state == PlaybackState.STATE_PAUSED
        val label = labels.getOrPut(controller.packageName) {
            runCatching { app.packageManager.getApplicationLabel(app.packageManager.getApplicationInfo(controller.packageName, 0)).toString() }
                .getOrDefault("Reproductor")
        }
        fun text(key: String) = runCatching { metadata?.getString(key)?.takeIf(String::isNotBlank)?.take(240) }.getOrNull()
        val title = text(MediaMetadata.METADATA_KEY_DISPLAY_TITLE) ?: text(MediaMetadata.METADATA_KEY_TITLE)
        val mediaId = text(MediaMetadata.METADATA_KEY_MEDIA_ID)
        val contentChanged = changed || mediaId != mutableState.value.mediaId || title != mutableState.value.title
        mutableState.value = ActiveAudioInfo(
            isPlaying = playback?.state == PlaybackState.STATE_PLAYING,
            title = title,
            artist = text(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE) ?: text(MediaMetadata.METADATA_KEY_ARTIST)
                ?: text(MediaMetadata.METADATA_KEY_AUTHOR), packageName = controller.packageName, appLabel = label,
            sessionId = selected.id, mediaAccess = true,
            canPause = supports(PlaybackState.ACTION_PAUSE) || supports(PlaybackState.ACTION_PLAY_PAUSE),
            canPrevious = canTransport && supports(PlaybackState.ACTION_SKIP_TO_PREVIOUS),
            canNext = canTransport && supports(PlaybackState.ACTION_SKIP_TO_NEXT),
            artwork = if (contentChanged) null else mutableState.value.artwork,
            durationMs = duration, positionMs = playback?.position?.takeIf { it >= 0L },
            positionUpdatedAt = playback?.lastPositionUpdateTime ?: 0L,
            speed = playback?.playbackSpeed?.takeIf { it.isFinite() } ?: 0f,
            canSeek = canTransport && duration != null && supports(PlaybackState.ACTION_SEEK_TO),
            isBuffering = playback?.state == PlaybackState.STATE_BUFFERING || playback?.state == PlaybackState.STATE_CONNECTING,
            mediaId = mediaId,
            canPlay = playback?.state == PlaybackState.STATE_PAUSED && (supports(PlaybackState.ACTION_PLAY) || supports(PlaybackState.ACTION_PLAY_PAUSE)),
        )
        if (contentChanged || artChanged) loadArtwork(selected.id, metadata, controller.packageName)
    }

    private fun publishGeneric() {
        val playing = runCatching { audio?.isMusicActive == true }.getOrDefault(false)
        mutableState.value = ActiveAudioInfo(isPlaying = playing, generic = playing, mediaAccess = MediaAccessService.isEnabled(app))
    }

    private fun ensureFallback() {
        if (!visible || fallbackJob?.isActive == true) return
        fallbackJob = scope.launch {
            // Ordinary apps have no public cross-app audio callback for the limited mode.
            // Poll only in the visible, enabled, no-exposed-session state.
            while (isActive && visible) {
                delay(3000)
                if (!observing && MediaAccessService.isEnabled(app)) { refreshAccess(); continue }
                if (observing) publish() else publishGeneric()
            }
        }
    }

    private fun loadArtwork(id: String, metadata: MediaMetadata?, packageName: String) {
        artworkJob?.cancel()
        val revision = ++artRevision
        artworkJob = scope.launch {
            val edge = if (packageName == "com.google.android.youtube") 768 else 384
            val bitmap = withContext(Dispatchers.IO) {
                runCatching {
                    val source = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                        ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                        ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)
                    val safeSource = source?.takeUnless { it.isRecycled || it.width.toLong() * it.height > 16_000_000L }
                    if (safeSource != null) {
                        val factor = edge.toFloat() / max(safeSource.width, safeSource.height).coerceAtLeast(1)
                        if (factor < 1f) Bitmap.createScaledBitmap(safeSource,
                            (safeSource.width * factor).roundToInt().coerceAtLeast(1),
                            (safeSource.height * factor).roundToInt().coerceAtLeast(1), true) else safeSource
                    } else {
                        val uri = sequenceOf(MediaMetadata.METADATA_KEY_ART_URI,
                            MediaMetadata.METADATA_KEY_ALBUM_ART_URI, MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI)
                            .mapNotNull { key -> metadata?.getString(key)?.let(Uri::parse) }
                            .firstOrNull { it.scheme == "content" }
                        if (uri == null) null else ImageDecoder.decodeBitmap(ImageDecoder.createSource(app.contentResolver, uri)) { decoder, info, _ ->
                            val longest = max(info.size.width, info.size.height).coerceAtLeast(1)
                            val factor = minOf(1f, edge.toFloat() / longest)
                            decoder.setTargetSize((info.size.width * factor).roundToInt().coerceAtLeast(1),
                                (info.size.height * factor).roundToInt().coerceAtLeast(1))
                            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        }
                    }
                }.getOrNull()
            }
            if (visible && current?.id == id && revision == artRevision) mutableState.value = mutableState.value.copy(artwork = bitmap)
        }
    }

    private fun control(id: String?, action: Long, allowPaused: Boolean = false, command: (MediaController.TransportControls) -> Unit) {
        val selected = current?.takeIf { id != null && it.id == id } ?: return
        runCatching {
            val state = selected.controller.playbackState ?: return@runCatching
            val acceptedState = state.state == PlaybackState.STATE_PLAYING ||
                allowPaused && state.state == PlaybackState.STATE_PAUSED
            if (!acceptedState || state.actions and action == 0L) return@runCatching
            command(selected.controller.transportControls)
        }
    }

    fun seek(id: String?, position: Long) {
        val entry = current?.takeIf { it.id == id && id != null } ?: return
        runCatching {
            val state = entry.controller.playbackState ?: return@runCatching
            val duration = entry.controller.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: return@runCatching
            if (duration <= 0L || state.actions and PlaybackState.ACTION_SEEK_TO == 0L ||
                state.state !in setOf(PlaybackState.STATE_PLAYING, PlaybackState.STATE_PAUSED)) return@runCatching
            entry.controller.transportControls.seekTo(position.coerceIn(0L, duration))
        }
    }
    fun resume(id: String?) {
        val entry = current?.takeIf { it.id == id && id != null } ?: return
        runCatching {
            val state = entry.controller.playbackState ?: return@runCatching
            if (state.state == PlaybackState.STATE_PAUSED &&
                state.actions and (PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PLAY_PAUSE) != 0L) entry.controller.transportControls.play()
        }
    }

    fun pause(id: String?) = control(id, PlaybackState.ACTION_PAUSE or PlaybackState.ACTION_PLAY_PAUSE) { it.pause() }
    fun previous(id: String?) = control(id, PlaybackState.ACTION_SKIP_TO_PREVIOUS, allowPaused = true) { it.skipToPrevious() }
    fun next(id: String?) = control(id, PlaybackState.ACTION_SKIP_TO_NEXT, allowPaused = true) { it.skipToNext() }

    fun openAudioApp(id: String? = mutableState.value.sessionId): Boolean {
        val selected = current?.takeIf { it.id == id } ?: return false
        val activity = selected.controller.sessionActivity
        if (activity != null && runCatching { activity.send(); true }.getOrDefault(false)) return true
        if (selected.controller.packageName == "com.google.android.youtube") {
            val mediaUri = runCatching { selected.controller.metadata?.description?.mediaUri }.getOrNull()
            val host = mediaUri?.host?.lowercase(java.util.Locale.ROOT)
            val trustedHost = host in setOf("youtube.com", "www.youtube.com", "m.youtube.com", "youtu.be")
            if (mediaUri?.scheme == "https" && trustedHost && runCatching {
                app.startActivity(Intent(Intent.ACTION_VIEW, mediaUri)
                    .setPackage(selected.controller.packageName).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                true
            }.getOrDefault(false)) return true
        }
        return runCatching {
            val intent = app.packageManager.getLaunchIntentForPackage(selected.controller.packageName) ?: return@runCatching false
            app.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); true
        }.getOrDefault(false)
    }
}
