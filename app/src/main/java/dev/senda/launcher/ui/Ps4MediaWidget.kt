package dev.senda.launcher.ui

import android.animation.ValueAnimator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.ActiveAudioInfo
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.ui.theme.HistoricalPanel
import dev.senda.launcher.ui.theme.rememberClockChange
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
internal fun Ps4MediaWidget(
    audioInfo: ActiveAudioInfo, reduced: Boolean, onOpenAudio: (String?) -> Unit,
    modifier: Modifier = Modifier, onPause: (String?) -> Unit, onPrevious: (String?) -> Unit,
    onNext: (String?) -> Unit, onEnableAccess: () -> Unit, preview: Boolean,
    sampleMotion: Boolean = false,
    onSeek: (String?, Long) -> Unit = { _, _ -> },
    onResume: (String?) -> Unit = {},
) {
    var details by remember(audioInfo.sessionId) { mutableStateOf(false) }
    var artOrigin by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    if (details && audioInfo.sessionId != null && !audioInfo.generic) MediaDetails(audioInfo, artOrigin,
        reduced || LocalSystemPowerSaving.current || LocalAppearance.current.highContrast || !ValueAnimator.areAnimatorsEnabled(),
        { details = false }, onSeek, onPause, onResume, onOpenAudio, onPrevious, onNext)
    var retained by remember { mutableStateOf(audioInfo) }
    var recentlyPlaying by remember { mutableStateOf(false) }
    if (audioInfo.isPlaying) SideEffect { retained = audioInfo }
    LaunchedEffect(audioInfo.isPlaying) {
        if (audioInfo.isPlaying) recentlyPlaying = true
        else { delay(2500); recentlyPlaying = false }
    }
    val info = if (audioInfo.isPlaying) audioInfo else retained
    val still = reduced || LocalAppearance.current.highContrast || LocalSystemPowerSaving.current ||
        (preview && !sampleMotion) || LocalThemePreview.current || !ValueAnimator.areAnimatorsEnabled()
    val change = rememberClockChange(
        "${info.sessionId}|${info.title}|${info.artist}|${audioInfo.isPlaying}",
        LauncherTheme.PS4, still, durationMs = 2400, playOnEntry = true, ready = audioInfo.isPlaying,
    )
    val atmosphere = LocalMediaAtmosphere.current
    val title = info.title ?: info.appLabel ?: "Contenido en reproducción"
    val art = info.artwork?.takeUnless { it.isRecycled }
    AnimatedVisibility(
        audioInfo.isPlaying || recentlyPlaying,
        modifier = modifier,
        enter = fadeIn(tween(if (still) 0 else 220)) +
            scaleIn(tween(if (still) 0 else 220, easing = Ps4Motion.easing), initialScale = .985f),
        exit = fadeOut(tween(if (still) 0 else 150)),
    ) {
        HistoricalPanel(LauncherTheme.PS4, Modifier.fillMaxWidth(),
            onClick = if (preview) null else ({
                if (info.generic) onEnableAccess() else details = true
            })) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
            val compact = maxWidth < 340.dp || LocalDensity.current.fontScale > 1.3f
            val artSize = if (compact) 72.dp else 98.dp
            val previousEnabled = !preview && audioInfo.isPlaying && info.canPrevious
            val pauseEnabled = !preview && audioInfo.isPlaying && info.canPause
            val nextEnabled = !preview && audioInfo.isPlaying && info.canNext
            Column(
                Modifier.fillMaxWidth().ps4MediaAtmosphere(atmosphere).then(if (still) Modifier else Modifier.ps4MediaScene(change))
                    .padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        Modifier.size(artSize).onGloballyPositioned {
                            val p = it.positionOnScreen(); artOrigin = androidx.compose.ui.geometry.Rect(p.x, p.y, p.x + it.size.width, p.y + it.size.height)
                        }.graphicsLayer {
                            if (!still) {
                                val wave = sin(change.value * Math.PI).toFloat()
                                translationY = -3.dp.toPx() * wave
                                rotationZ = -.8f * wave
                                scaleX = 1f + .025f * wave
                                scaleY = scaleX
                            }
                        }.clip(RoundedCornerShape(5.dp)).ps4Material(),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.animation.Crossfade(art, animationSpec = tween(if (still) 0 else 320), label = "media-artwork") { cover ->
                            if (cover != null && !cover.isRecycled) Image(cover.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Ps4ChromeIcon("play", Modifier.size(38.dp)) }
                        }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(if (!audioInfo.isPlaying) "Audio detenido" else if (info.generic) "Reproducción detectada" else "En reproducción",
                            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(title, style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                            maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(if (info.generic) "Toca para conectar los controles"
                            else listOfNotNull(info.artist, info.appLabel).distinct().joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (!info.generic) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .25f))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { onPrevious(info.sessionId) },
                            enabled = previousEnabled,
                            modifier = Modifier.weight(1f).heightIn(min = 56.dp).semantics {
                                contentDescription = "Pista anterior"
                                if (!previousEnabled) disabled()
                            }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Ps4ChromeIcon("previous")
                                if (!compact) Text("Anterior", style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        FilledTonalButton(onClick = { onPause(info.sessionId) },
                            enabled = pauseEnabled,
                            modifier = Modifier.weight(1f).heightIn(min = 56.dp).semantics {
                                contentDescription = "Pausar reproducción"
                                if (!pauseEnabled) disabled()
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Ps4ChromeIcon("pause")
                                if (!compact) Text("Pausar", style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        TextButton(onClick = { onNext(info.sessionId) },
                            enabled = nextEnabled,
                            modifier = Modifier.weight(1f).heightIn(min = 56.dp).semantics {
                                contentDescription = "Pista siguiente"
                                if (!nextEnabled) disabled()
                            }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Ps4ChromeIcon("next")
                                if (!compact) Text("Siguiente", style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
internal fun Ps4MediaSample(reduced: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    var coverIndex by remember { mutableIntStateOf(0) }
    var replay by remember { mutableIntStateOf(0) }
    TextButton(onClick = { expanded = !expanded }) {
        Text(if (expanded) "Cerrar muestra multimedia" else "Ver muestra multimedia PS4")
    }
    if (expanded) {
        val covers = remember { listOf(sampleMediaCover(false), sampleMediaCover(true)) }
        val sample = ActiveAudioInfo(isPlaying = true, title = if (coverIndex == 0) "Horizonte azul" else "Luz de cobre",
            artist = "Vista previa PS4", appLabel = "Reproductor", artwork = covers[coverIndex])
        val sampleAtmosphere = rememberMediaAtmosphere(sample, reduced, enabled = true)
        CompositionLocalProvider(LocalMediaAtmosphere provides sampleAtmosphere) {
        key(replay) {
            Ps4MediaWidget(
                sample,
                reduced, {}, onPause = {}, onPrevious = {}, onNext = {}, onEnableAccess = {},
                preview = true, sampleMotion = true,
            )
        }
        }
        TextButton(onClick = { coverIndex = 1 - coverIndex }) { Text("Alternar carátula y ambiente") }
        Text("Muestra visual · controles desactivados", style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = { replay++ },
            enabled = !reduced && !LocalSystemPowerSaving.current && ValueAnimator.areAnimatorsEnabled()) {
            Text("Repetir animación multimedia")
        }
    }
}

/** Finite local-model concept, corrected to cached Compose drawing primitives. */
@Composable
private fun Modifier.ps4MediaScene(progress: State<Float>): Modifier {
    val constrained = LocalDecorationConstrained.current
    val primary = MaterialTheme.colorScheme.primary
    val ink = MaterialTheme.colorScheme.onSurface
    return drawWithCache {
        val paths = List(3) { Path() }
        val glow = Brush.horizontalGradient(listOf(Color.Transparent, primary.copy(alpha = .30f), Color.Transparent))
        val bandWidth = size.width * .32f
        val reflection = Brush.horizontalGradient(listOf(Color.Transparent, ink.copy(alpha = .10f), Color.Transparent),
            startX = 0f, endX = bandWidth.coerceAtLeast(1f))
        val stroke = Stroke(1.1.dp.toPx())
        onDrawBehind {
            if (constrained.value) return@onDrawBehind
            val p = progress.value
            val envelope = sin(p * Math.PI).toFloat().coerceAtLeast(0f)
            if (envelope > .001f) {
                for (index in 0..2) {
                    val y = size.height * (.68f + index * .09f)
                    val bend = sin(p * Math.PI * 2 + index).toFloat() * size.height * .09f
                    val path = paths[index]
                    path.reset()
                    path.moveTo(0f, y)
                    path.cubicTo(size.width * .3f, y + bend, size.width * .7f, y - bend, size.width, y)
                    drawPath(path, glow, alpha = envelope, style = stroke)
                }
                translate(left = (size.width + bandWidth) * p - bandWidth) {
                    drawRect(reflection, size = Size(bandWidth, size.height), alpha = envelope)
                }
            }
        }
    }
}

private fun Modifier.ps4MediaAtmosphere(atmosphere: MediaAtmosphere): Modifier = drawWithCache {
    onDrawBehind {
        val presence = atmosphere.presence.value
        if (presence > .001f) {
            val color = atmosphere.color.value
            drawRect(color, alpha = presence * .12f)
            drawLine(color, Offset(0f, size.height - 1.dp.toPx()), Offset(size.width, size.height - 1.dp.toPx()),
                2.dp.toPx(), alpha = presence * .65f)
        }
    }
}

/** Two small original preview tiles, generated only while this sample is open. */
private fun sampleMediaCover(warm: Boolean): android.graphics.Bitmap {
    val bitmap = android.graphics.Bitmap.createBitmap(64, 64, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    paint.shader = android.graphics.LinearGradient(0f, 0f, 64f, 64f,
        if (warm) 0xFFF7A14B.toInt() else 0xFF36D9F5.toInt(),
        if (warm) 0xFF6F2354.toInt() else 0xFF152B95.toInt(), android.graphics.Shader.TileMode.CLAMP)
    canvas.drawRect(0f, 0f, 64f, 64f, paint)
    paint.shader = null; paint.color = 0x66FFFFFF
    canvas.drawLine(0f, 52f, 64f, 12f, paint.apply { strokeWidth = 3f })
    canvas.drawLine(0f, 60f, 64f, 20f, paint)
    return bitmap
}
