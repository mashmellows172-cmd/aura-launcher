package dev.senda.launcher.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
)

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
        val selected = playing.firstOrNull { it.id == current?.id } ?: playing.firstOrNull()
        if (selected == null) {
            current = null; artworkJob?.cancel(); artRevision++
            publishGeneric(); ensureFallback(); return
        }
        fallbackJob?.cancel(); fallbackJob = null
        val changed = selected.id != current?.id
        current = selected
        val controller = selected.controller
        val metadata = runCatching { controller.metadata }.getOrNull()
        val actions = runCatching { controller.playbackState?.actions ?: 0L }.getOrDefault(0L)
        fun supports(action: Long) = actions and action != 0L
        val label = labels.getOrPut(controller.packageName) {
            runCatching { app.packageManager.getApplicationLabel(app.packageManager.getApplicationInfo(controller.packageName, 0)).toString() }
                .getOrDefault("Reproductor")
        }
        fun text(key: String) = runCatching { metadata?.getString(key)?.takeIf(String::isNotBlank)?.take(240) }.getOrNull()
        mutableState.value = ActiveAudioInfo(
            isPlaying = true,
            title = text(MediaMetadata.METADATA_KEY_DISPLAY_TITLE) ?: text(MediaMetadata.METADATA_KEY_TITLE),
            artist = text(MediaMetadata.METADATA_KEY_ARTIST), packageName = controller.packageName, appLabel = label,
            sessionId = selected.id, mediaAccess = true,
            canPause = supports(PlaybackState.ACTION_PAUSE) || supports(PlaybackState.ACTION_PLAY_PAUSE),
            canPrevious = supports(PlaybackState.ACTION_SKIP_TO_PREVIOUS), canNext = supports(PlaybackState.ACTION_SKIP_TO_NEXT),
            artwork = if (changed || artChanged) null else mutableState.value.artwork,
        )
        if (changed || artChanged) loadArtwork(selected.id, metadata)
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

    private fun loadArtwork(id: String, metadata: MediaMetadata?) {
        artworkJob?.cancel()
        val revision = ++artRevision
        artworkJob = scope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                runCatching {
                    val source = metadata?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)
                        ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                        ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                    source?.takeUnless { it.isRecycled || it.width.toLong() * it.height > 16_000_000L }?.let {
                        val factor = 384f / max(it.width, it.height).coerceAtLeast(1)
                        if (factor < 1f) Bitmap.createScaledBitmap(it, (it.width * factor).roundToInt().coerceAtLeast(1), (it.height * factor).roundToInt().coerceAtLeast(1), true) else it
                    }
                }.getOrNull()
            }
            if (visible && current?.id == id && revision == artRevision) mutableState.value = mutableState.value.copy(artwork = bitmap)
        }
    }

    private fun control(id: String?, action: Long, command: (MediaController.TransportControls) -> Unit) {
        val selected = current?.takeIf { id != null && it.id == id } ?: return
        runCatching {
            val state = selected.controller.playbackState ?: return@runCatching
            if (state.state != PlaybackState.STATE_PLAYING || state.actions and action == 0L) return@runCatching
            command(selected.controller.transportControls)
        }
    }

    fun pause(id: String?) = control(id, PlaybackState.ACTION_PAUSE or PlaybackState.ACTION_PLAY_PAUSE) { it.pause() }
    fun previous(id: String?) = control(id, PlaybackState.ACTION_SKIP_TO_PREVIOUS) { it.skipToPrevious() }
    fun next(id: String?) = control(id, PlaybackState.ACTION_SKIP_TO_NEXT) { it.skipToNext() }

    fun openAudioApp(id: String? = mutableState.value.sessionId): Boolean {
        val selected = current?.takeIf { it.id == id } ?: return false
        val activity = selected.controller.sessionActivity
        if (activity != null && runCatching { activity.send(); true }.getOrDefault(false)) return true
        return runCatching {
            val intent = app.packageManager.getLaunchIntentForPackage(selected.controller.packageName) ?: return@runCatching false
            app.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); true
        }.getOrDefault(false)
    }
}
