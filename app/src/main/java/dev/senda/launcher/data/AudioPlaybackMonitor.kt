package dev.senda.launcher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.provider.MediaStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ActiveAudioInfo(
    val isPlaying: Boolean = false,
    val title: String? = null,
    val artist: String? = null,
    val packageName: String? = null,
    val appLabel: String? = null
)

/**
 * Monitor autónomo de reproducción de audio.
 * Detecta si el dispositivo está reproduciendo sonido mediante AudioManager y,
 * si hay sesiones activas disponibles, extrae el título, artista y app.
 */
class AudioPlaybackMonitor(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager

    private val _state = MutableStateFlow(ActiveAudioInfo())
    val state: StateFlow<ActiveAudioInfo> = _state.asStateFlow()

    private var pollJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var activeController: MediaController? = null

    private val controllerCallback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            updatePlayback()
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            updatePlayback()
        }
    }

    fun start() {
        if (pollJob?.isActive == true) return
        attachActiveSession()
        pollJob = scope.launch {
            while (isActive) {
                updatePlayback()
                delay(2000) // Verificación periódica ligera de bajo impacto
            }
        }
    }

    fun stop() {
        pollJob?.cancel()
        pollJob = null
        activeController?.unregisterCallback(controllerCallback)
        activeController = null
    }

    private fun attachActiveSession() {
        try {
            // Intentar enlazar la sesión de medios más reciente si está disponible
            val controllers = mediaSessionManager?.getActiveSessions(null)
            val controller = controllers?.firstOrNull()
            if (controller != activeController) {
                activeController?.unregisterCallback(controllerCallback)
                activeController = controller
                activeController?.registerCallback(controllerCallback)
            }
        } catch (_: SecurityException) {
            // Requiere permiso especial para leer sesiones de terceros; en su defecto se usa AudioManager
        } catch (_: Exception) {
            // Manejo de fallos en dispositivos específicos
        }
    }

    fun updatePlayback() {
        attachActiveSession()
        val controller = activeController
        val pbState = controller?.playbackState?.state
        val isControllerPlaying = pbState == PlaybackState.STATE_PLAYING

        // AudioManager.isMusicActive detecta reproducción activa con 0 permisos requeridos
        val isAudioActive = audioManager?.isMusicActive == true || isControllerPlaying

        if (!isAudioActive) {
            if (_state.value.isPlaying) {
                _state.value = ActiveAudioInfo(isPlaying = false)
            }
            return
        }

        val metadata = controller?.metadata
        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
        val pkg = controller?.packageName

        val label = if (pkg != null) {
            try {
                val pm = context.packageManager
                val appInfo = pm.getApplicationInfo(pkg, 0)
                pm.getApplicationLabel(appInfo).toString()
            } catch (_: Exception) {
                null
            }
        } else null

        _state.value = ActiveAudioInfo(
            isPlaying = true,
            title = title?.takeIf { it.isNotBlank() },
            artist = artist?.takeIf { it.isNotBlank() },
            packageName = pkg,
            appLabel = label
        )
    }

    fun openAudioApp() {
        val pkg = _state.value.packageName
        if (pkg != null) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                runCatching { context.startActivity(launchIntent) }
                return
            }
        }
        // Intent general para abrir el reproductor de música del sistema
        val intents = listOf(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC),
            Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)
        )
        for (intent in intents) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (runCatching { context.startActivity(intent); true }.getOrDefault(false)) return
        }
    }
}
