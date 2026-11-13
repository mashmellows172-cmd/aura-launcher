package dev.senda.launcher.ui

import android.os.SystemClock
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.ActiveAudioInfo
import kotlinx.coroutines.delay

/** Extrapolate only a real reported position, anchored to Android's elapsed-realtime clock. */
private fun positionAt(info: ActiveAudioInfo): Long? {
    val position = info.positionMs ?: return null
    val elapsed = if (info.isPlaying && info.positionUpdatedAt > 0) (SystemClock.elapsedRealtime() - info.positionUpdatedAt).coerceAtLeast(0) else 0L
    return (position.toDouble() + elapsed * info.speed.toDouble()).toLong().coerceIn(0L, info.durationMs ?: Long.MAX_VALUE)
}
private fun mediaTime(ms: Long): String = "%d:%02d".format(ms / 60000, ms / 1000 % 60)

@Composable
internal fun MediaDetails(info: ActiveAudioInfo, origin: Rect?, reduced: Boolean, onDismiss: () -> Unit,
    onSeek: (String?, Long) -> Unit, onPause: (String?) -> Unit, onResume: (String?) -> Unit, onOpen: (String?) -> Unit,
    onPrevious: (String?) -> Unit, onNext: (String?) -> Unit,
) {
    val motion = rememberPs4PanelMotion(reduced)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var position by remember(info.sessionId) { mutableStateOf(positionAt(info)) }
    var drag by remember(info.sessionId, info.title, info.durationMs) { mutableStateOf<Float?>(null) }
    var artTarget by remember { mutableStateOf<Rect?>(null) }
    var rootPosition by remember { mutableStateOf(Offset.Zero) }
    val close = { motion.close(reduced, onDismiss) }
    val expandArt = motion.effect == "SPRING" && origin != null
    val art = info.artwork?.takeUnless { it.isRecycled }
    val artwork: @Composable () -> Unit = {
        Crossfade(art, modifier = Modifier.fillMaxSize(),
            animationSpec = tween(if (motion.effect == "NONE") 0 else 320), label = "media-artwork") { image ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (image != null && !image.isRecycled)
                    Image(image.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Ps4ChromeIcon("play", Modifier.size(48.dp))
            }
        }
    }
    val trackCue = dev.senda.launcher.ui.theme.rememberClockChange(
        "${info.sessionId}:${info.title}:${info.artist}", dev.senda.launcher.data.LauncherTheme.PS4,
        motion.effect == "NONE", durationMs = 380,
    )
    val accent = MaterialTheme.colorScheme.primary
    LaunchedEffect(info, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            do { position = positionAt(info); if (info.isPlaying) delay(500) } while (info.isPlaying)
        }
    }
    Dialog(onDismissRequest = close, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        BoxWithConstraints(Modifier.fillMaxSize().onGloballyPositioned { rootPosition = it.positionOnScreen() }) {
        Box(Modifier.matchParentSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = .5f))
            .clickable(onClickLabel = "Cerrar detalle multimedia", onClick = close))
        Surface(Modifier.align(Alignment.Center).safeDrawingPadding().padding(24.dp).widthIn(max = 560.dp).fillMaxWidth()
            .heightIn(max = (maxHeight - 48.dp).coerceAtLeast(120.dp))
            .ps4PanelTransform(motion.progress.asState(), if (motion.effect == "SPRING") "FADE" else motion.effect)
            .pointerInput(Unit) { detectTapGestures {} }, shape = MaterialTheme.shapes.large) {
        Column(Modifier.fillMaxWidth().ps4HeaderSweep(trackCue, accent, .65f)
            .verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(info.title ?: info.appLabel ?: "Reproducción", style = MaterialTheme.typography.titleLarge)
            Box(Modifier.size(120.dp).then(if (expandArt) Modifier.onGloballyPositioned {
                val p = it.positionOnScreen()
                artTarget = Rect(p.x, p.y, p.x + it.size.width, p.y + it.size.height)
            } else Modifier), contentAlignment = Alignment.Center) {
                if (!expandArt) artwork()
            }
            Column(Modifier.fillMaxWidth().graphicsLayer {
                if (expandArt) {
                    val p = motion.progress.value.coerceIn(0f, 1f)
                    translationY = -18.dp.toPx() * (1f - p)
                    alpha = ((p - .18f) / .82f).coerceIn(0f, 1f)
                }
            }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(info.artist ?: info.appLabel ?: "", style = MaterialTheme.typography.bodyMedium)
            val duration = info.durationMs
            val current = position
            if (duration != null && current != null) {
                Slider(value = drag ?: (current.toDouble() / duration).toFloat().coerceIn(0f, 1f),
                    onValueChange = { drag = it }, onValueChangeFinished = {
                        drag?.let { onSeek(info.sessionId, (duration * it).toLong()) }; drag = null
                    }, enabled = info.canSeek)
                Text("${mediaTime(drag?.let { (duration * it).toLong() } ?: current)} / ${mediaTime(duration)}")
                if (!info.canSeek) Text("Este reproductor no permite buscar dentro de la pista", style = MaterialTheme.typography.bodySmall)
            } else Text("El reproductor no comunica duración o progreso", style = MaterialTheme.typography.bodySmall)
            Button(onClick = { if (info.isPlaying) onPause(info.sessionId) else onResume(info.sessionId) },
                enabled = if (info.isPlaying) info.canPause else info.canPlay) {
                Text(if (info.isPlaying) "Pausar" else "Reanudar")
            }
            if (info.isPlaying && (info.canPrevious || info.canNext)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { onPrevious(info.sessionId) }, enabled = info.canPrevious) { Text("Anterior") }
                    TextButton(onClick = { onNext(info.sessionId) }, enabled = info.canNext) { Text("Siguiente") }
                }
            }
            TextButton(onClick = { onOpen(info.sessionId) }) { Text("Abrir reproductor") }
            TextButton(onClick = close) { Text("Cerrar") }
            }
        }
        }
        val target = artTarget
        if (expandArt && origin != null && target != null) Box(Modifier.size(120.dp).graphicsLayer {
            val p = motion.progress.value.coerceIn(0f, 1f)
            val start = origin
            alpha = p
            transformOrigin = TransformOrigin(0f, 0f)
            translationX = start.left + (target.left - start.left) * p - rootPosition.x
            translationY = start.top + (target.top - start.top) * p - rootPosition.y
            scaleX = (start.width + (target.width - start.width) * p) / size.width.coerceAtLeast(1f)
            scaleY = (start.height + (target.height - start.height) * p) / size.height.coerceAtLeast(1f)
        }, contentAlignment = Alignment.Center) {
            artwork()
        }
        }
    }
}
