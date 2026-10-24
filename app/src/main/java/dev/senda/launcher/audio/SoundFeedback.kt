package dev.senda.launcher.audio

import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import androidx.compose.runtime.staticCompositionLocalOf
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.SoundEvent
import dev.senda.launcher.data.SoundSettings

enum class SoundResult { PLAYED, LOADING, DISABLED, SYSTEM_MUTED, MEDIA_ACTIVE, UNAVAILABLE }

interface SoundFeedback {
    fun play(event: SoundEvent): SoundResult
    fun beginPreview() {}
    fun endPreview() {}
    fun preview(theme: LauncherTheme): SoundResult = SoundResult.UNAVAILABLE
}

val LocalSoundFeedback = staticCompositionLocalOf<SoundFeedback> {
    object : SoundFeedback { override fun play(event: SoundEvent) = SoundResult.DISABLED }
}

/** Main-thread confined; no service, polling, recording, stream-volume change or background playback. */
class SoundFeedbackController(context: Context) : SoundFeedback {
    private val app = context.applicationContext
    private val audio = app.getSystemService(AudioManager::class.java)
    private val notifications = app.getSystemService(NotificationManager::class.java)
    private val handler = Handler(Looper.getMainLooper())
    private var pool: SoundPool? = null
    private val samples = mutableMapOf<Int, Int>()
    private val loaded = mutableSetOf<Int>()
    private var settings = SoundSettings()
    private var theme = LauncherTheme.NOTHING
    private var visible = false
    private var previewVisible = false
    private var previewStream = 0
    private var pendingPreview: Pair<Int, Long>? = null
    private var lastAction = Long.MIN_VALUE
    private var lastIndex = Long.MIN_VALUE

    fun configure(value: SoundSettings, theme: LauncherTheme) {
        settings = value.bounded(); this.theme = theme
        reconcile()
    }

    fun setVisible(value: Boolean) { visible = value; reconcile() }

    private fun reconcile() {
        if (!visible || !settings.enabled && !previewVisible) release()
        else prepare()
    }

    private fun prepare() {
        if (pool != null || !visible) return
        val current = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).build()
        pool = current
        current.setOnLoadCompleteListener { source, id, status -> handler.post {
            if (pool !== source) return@post
            if (status == 0) loaded += id
            val pending = pendingPreview
            if (pending?.first == id) {
                pendingPreview = null
                if (status == 0 && previewVisible && SystemClock.uptimeMillis() - pending.second <= 1500 && policy() == null) {
                    previewStream = start(id)
                }
            }
        } }
        SoundAssets.allResources.forEach { resource -> samples[resource] = current.load(app, resource, 1) }
    }

    private fun policy(): SoundResult? {
        if (!visible || audio == null) return SoundResult.UNAVAILABLE
        return runCatching {
            when {
                audio.ringerMode != AudioManager.RINGER_MODE_NORMAL -> SoundResult.SYSTEM_MUTED
                audio.getStreamVolume(AudioManager.STREAM_SYSTEM) == 0 -> SoundResult.SYSTEM_MUTED
                Settings.System.getInt(app.contentResolver, Settings.System.SOUND_EFFECTS_ENABLED, 1) == 0 -> SoundResult.SYSTEM_MUTED
                notifications?.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL -> SoundResult.SYSTEM_MUTED
                settings.muteDuringMedia && audio.isMusicActive -> SoundResult.MEDIA_ACTIVE
                settings.volume == 0 -> SoundResult.SYSTEM_MUTED
                else -> null
            }
        }.getOrDefault(SoundResult.SYSTEM_MUTED)
    }

    override fun play(event: SoundEvent): SoundResult {
        if (!settings.enabled || !settings.allows(event)) return SoundResult.DISABLED
        policy()?.let { return it }
        val now = SystemClock.uptimeMillis()
        val last = if (event == SoundEvent.INDEX) lastIndex else lastAction
        val gap = if (event == SoundEvent.INDEX) 140 else 65
        if (last != Long.MIN_VALUE && now - last < gap) return SoundResult.DISABLED
        val id = samples[SoundAssets.resource(settings.family.resolve(theme), event)] ?: return SoundResult.UNAVAILABLE
        // Automatic feedback is dropped while loading; it must never sound late after an old action.
        if (id !in loaded) return SoundResult.LOADING
        if (event == SoundEvent.INDEX) lastIndex = now else lastAction = now
        return if (start(id) != 0) SoundResult.PLAYED else SoundResult.UNAVAILABLE
    }

    override fun beginPreview() { previewVisible = true; reconcile() }
    override fun endPreview() {
        previewVisible = false; pendingPreview = null
        if (previewStream != 0) pool?.stop(previewStream)
        previewStream = 0; reconcile()
    }

    override fun preview(theme: LauncherTheme): SoundResult {
        policy()?.let { return it }
        if (!previewVisible) return SoundResult.UNAVAILABLE
        val id = samples[SoundAssets.resource(settings.family.resolve(theme), SoundEvent.SELECT)] ?: return SoundResult.UNAVAILABLE
        if (previewStream != 0) pool?.stop(previewStream)
        pendingPreview = null
        if (id !in loaded) {
            pendingPreview = id to SystemClock.uptimeMillis()
            return SoundResult.LOADING
        }
        previewStream = start(id)
        return if (previewStream != 0) SoundResult.PLAYED else SoundResult.UNAVAILABLE
    }

    private fun start(id: Int): Int {
        val volume = settings.volume / 100f
        return pool?.play(id, volume, volume, 1, 0, 1f) ?: 0
    }

    private fun release() {
        pendingPreview = null; previewStream = 0
        handler.removeCallbacksAndMessages(null)
        pool?.setOnLoadCompleteListener(null)
        pool?.release(); pool = null; samples.clear(); loaded.clear()
    }

    fun close() { visible = false; previewVisible = false; release() }
}
