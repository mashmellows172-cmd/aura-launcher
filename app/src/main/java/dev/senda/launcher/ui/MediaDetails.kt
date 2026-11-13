package dev.senda.launcher.ui

import android.os.SystemClock
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.ActiveAudioInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

/** Extrapolate only a real reported position, anchored to Android's elapsed-realtime clock. */
private fun positionAt(info: ActiveAudioInfo): Long? {
    val position = info.positionMs ?: return null
    val elapsed = if (info.isPlaying && info.positionUpdatedAt > 0) (SystemClock.elapsedRealtime() - info.positionUpdatedAt).coerceAtLeast(0) else 0L
    return (position.toDouble() + elapsed * info.speed.toDouble()).toLong().coerceIn(0L, info.durationMs ?: Long.MAX_VALUE)
}
private fun mediaTime(ms: Long): String = "%d:%02d".format(ms / 60000, ms / 1000 % 60)

private data class MediaSeekRequest(val target: Long, val sentAt: Long)

@Composable
internal fun MediaDetails(info: ActiveAudioInfo, origin: Rect?, reduced: Boolean, onDismiss: () -> Unit,
    onSeek: (String?, Long) -> Unit, onPause: (String?) -> Unit, onResume: (String?) -> Unit, onOpen: (String?) -> Unit,
    onPrevious: (String?) -> Unit, onNext: (String?) -> Unit,
) {
    val still = reduced || !LocalMotionSettings.current.mediaDetailsMotionEnabled
    val motion = rememberPs4PanelMotion(still)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var position by remember(info.sessionId) { mutableStateOf(positionAt(info)) }
    var drag by remember(info.sessionId, info.title, info.durationMs) { mutableStateOf<Float?>(null) }
    var pendingSeek by remember(info.sessionId, info.title, info.durationMs) { mutableStateOf<MediaSeekRequest?>(null) }
    var seekResult by remember(info.sessionId, info.title, info.durationMs) { mutableStateOf<String?>(null) }
    var artTarget by remember { mutableStateOf<Rect?>(null) }
    var rootPosition by remember { mutableStateOf(Offset.Zero) }
    var dragLayer by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val close = { motion.close(still, onDismiss) }
    val currentClose by rememberUpdatedState(close)
    val scope = rememberCoroutineScope()
    val dismissDistance = with(LocalDensity.current) { 96.dp.toPx() }
    var dismissOffset by remember { mutableFloatStateOf(0f) }
    var returnJob by remember { mutableStateOf<Job?>(null) }
    val animateDismiss = motion.effect != "NONE"
    fun returnToCenter() {
        returnJob?.cancel()
        if (!animateDismiss) dismissOffset = 0f
        else returnJob = scope.launch {
            animate(dismissOffset, 0f, animationSpec = tween(180, easing = Ps4Motion.easing)) { value, _ ->
                dismissOffset = value
            }
        }
    }
    val expandArt = motion.effect == "SPRING" && origin != null
    val art = info.artwork?.takeUnless { it.isRecycled }
    val artwork: @Composable () -> Unit = {
        Crossfade(art, modifier = Modifier.fillMaxSize(),
            animationSpec = tween(if (motion.effect == "NONE") 0 else 320), label = "media-artwork") { image ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (image != null && !image.isRecycled)
                    Image(image.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else MediaCoverFallback(info, dev.senda.launcher.data.LauncherTheme.PS4, Modifier.size(48.dp))
            }
        }
    }
    val trackCue = dev.senda.launcher.ui.theme.rememberClockChange(
        "${info.sessionId}:${info.title}:${info.artist}", dev.senda.launcher.data.LauncherTheme.PS4,
        motion.effect == "NONE", durationMs = 380,
    )
    val atmosphere = LocalMediaAtmosphere.current
    val accent = MaterialTheme.colorScheme.primary
    val panelColor = MaterialTheme.colorScheme.surface
    val sessionId = info.sessionId
    val duration = info.durationMs?.takeIf { it > 0L }
    LaunchedEffect(info.positionMs, info.positionUpdatedAt, pendingSeek) {
        val request = pendingSeek ?: return@LaunchedEffect
        val reported = info.positionMs
        if (reported != null && info.positionUpdatedAt >= request.sentAt &&
            abs(reported - request.target) <= 2_000L
        ) {
            position = positionAt(info)
            pendingSeek = null
            seekResult = "Posición actualizada"
        }
    }
    LaunchedEffect(pendingSeek) {
        if (pendingSeek == null) return@LaunchedEffect
        delay(2200)
        pendingSeek = null
        seekResult = "El reproductor no confirmó el cambio"
    }
    LaunchedEffect(seekResult) {
        if (seekResult == null) return@LaunchedEffect
        delay(2400)
        seekResult = null
    }
    LaunchedEffect(info, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            do { position = positionAt(info); if (info.isPlaying) delay(500) } while (info.isPlaying)
        }
    }
    Dialog(onDismissRequest = close, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        BoxWithConstraints(Modifier.fillMaxSize().onGloballyPositioned { rootPosition = it.positionOnScreen() }) {
        Box(Modifier.matchParentSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = .5f))
            .clickable(onClickLabel = "Cerrar detalle multimedia", onClick = close))
        val panelMaxHeight = (maxHeight - 48.dp).coerceAtLeast(120.dp)
        Box(Modifier.fillMaxSize().graphicsLayer {
            translationY = if (animateDismiss) dismissOffset else 0f
        }.onGloballyPositioned { dragLayer = it }) {
        Surface(Modifier.align(Alignment.Center).safeDrawingPadding().padding(24.dp).widthIn(max = 560.dp).fillMaxWidth()
            .heightIn(max = panelMaxHeight)
            .ps4PanelTransform(motion.progress.asState(), if (motion.effect == "SPRING") "FADE" else motion.effect)
            .pointerInput(Unit) { detectTapGestures {} }, shape = MaterialTheme.shapes.large, color = panelColor) {
        Column(Modifier.fillMaxWidth().drawBehind {
            drawRect(lerp(panelColor, atmosphere.color.value, .11f * atmosphere.presence.value))
        }) {
            val handleTint = MaterialTheme.colorScheme.onSurfaceVariant
            TextButton(
                onClick = close,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).pointerInput(dismissDistance, animateDismiss) {
                    detectVerticalDragGestures(
                        onDragStart = { returnJob?.cancel() },
                        onVerticalDrag = { _, delta ->
                            if (!motion.closing) dismissOffset = (dismissOffset + delta).coerceIn(0f, dismissDistance * 2f)
                        },
                        onDragCancel = { returnToCenter() },
                        onDragEnd = {
                            if (dismissOffset >= dismissDistance) currentClose() else returnToCenter()
                        },
                    )
                },
                colors = ButtonDefaults.textButtonColors(contentColor = handleTint),
            ) { Text("Minimizar") }
            Column(Modifier.fillMaxWidth().ps4HeaderSweep(trackCue, accent, .65f)
                .verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(info.title ?: info.appLabel ?: "Reproducción", style = MaterialTheme.typography.titleLarge)
            Box(Modifier.size(120.dp).then(if (expandArt) Modifier.onGloballyPositioned {
                val parent = dragLayer?.takeIf { coordinates -> coordinates.isAttached }
                if (parent != null) {
                    val p = parent.localPositionOf(it, Offset.Zero)
                    artTarget = Rect(p.x, p.y, p.x + it.size.width, p.y + it.size.height)
                }
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
            val current = position
            if (duration != null && current != null) {
                val displayed = pendingSeek?.target ?: current
                val fraction = (displayed.toDouble() / duration).toFloat().coerceIn(0f, 1f)
                val smoothPosition by animateFloatAsState(fraction,
                    if (!animateDismiss || drag != null || pendingSeek != null) snap()
                    else tween(420, easing = LinearEasing), label = "media-progress")
                val scrubTime = drag?.let { mediaTime((duration * it).toLong()) }
                if (info.canSeek && sessionId != null) {
                    Box(Modifier.fillMaxWidth().heightIn(min = 20.dp), contentAlignment = Alignment.Center) {
                        if (scrubTime != null) Text(scrubTime, style = MaterialTheme.typography.labelLarge)
                    }
                }
                Slider(value = drag ?: smoothPosition,
                    onValueChange = { drag = it }, onValueChangeFinished = {
                        val target = drag?.let { (duration * it).toLong().coerceIn(0L, duration) }
                        drag = null
                        if (target != null && sessionId != null && info.canSeek) {
                            seekResult = null
                            pendingSeek = MediaSeekRequest(target, SystemClock.elapsedRealtime())
                            onSeek(sessionId, target)
                        }
                    }, enabled = sessionId != null && info.canSeek)
                Text("${mediaTime(drag?.let { (duration * it).toLong() } ?: displayed)} / ${mediaTime(duration)}")
                val status = if (pendingSeek != null) "Esperando al reproductor…" else seekResult
                if (status != null) Text(status, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    style = MaterialTheme.typography.bodySmall)
                if (!info.canSeek) Text("Este reproductor no permite buscar dentro de la pista", style = MaterialTheme.typography.bodySmall)
            } else Text("El reproductor no comunica duración o progreso", style = MaterialTheme.typography.bodySmall)
            Button(onClick = { if (info.isPlaying) onPause(info.sessionId) else onResume(info.sessionId) },
                enabled = sessionId != null && (if (info.isPlaying) info.canPause else info.canPlay),
                modifier = Modifier.widthIn(min = 136.dp).semantics {
                    contentDescription = if (info.isPlaying) "Pausar reproducción" else "Reanudar reproducción"
                }) {
                Crossfade(info.isPlaying, animationSpec = tween(if (animateDismiss) 180 else 0),
                    label = "confirmed-playback") { playing ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeSystemIcon(if (playing) "pause" else "play", dev.senda.launcher.data.LauncherTheme.PS4)
                        Text(if (playing) "Pausar" else "Reanudar")
                    }
                }
            }
            if (sessionId != null && info.isPlaying && (info.canPrevious || info.canNext)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { onPrevious(info.sessionId) }, enabled = info.canPrevious) { Text("Anterior") }
                    TextButton(onClick = { onNext(info.sessionId) }, enabled = info.canNext) { Text("Siguiente") }
                }
            }
            TextButton(onClick = { onOpen(info.sessionId) }, enabled = sessionId != null) { Text("Abrir reproductor") }
            }
        }
        }
        val target = artTarget
        if (expandArt && origin != null && target != null) Box(Modifier.size(120.dp).graphicsLayer {
            val p = motion.progress.value.coerceIn(0f, 1f)
            val start = origin
            alpha = p
            transformOrigin = TransformOrigin(0f, 0f)
            val startX = start.left - rootPosition.x
            val startY = start.top - rootPosition.y
            translationX = startX + (target.left - startX) * p
            translationY = startY + (target.top - startY) * p
            scaleX = (start.width + (target.width - start.width) * p) / size.width.coerceAtLeast(1f)
            scaleY = (start.height + (target.height - start.height) * p) / size.height.coerceAtLeast(1f)
        }, contentAlignment = Alignment.Center) {
            artwork()
        }
        }
        }
        }
    }
}
