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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
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
) {
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
    val compact = LocalConfiguration.current.screenWidthDp < 360 ||
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val artSize = if (compact) 72.dp else 98.dp
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
                if (info.generic) onEnableAccess() else onOpenAudio(info.sessionId)
            })) {
            Column(
                Modifier.fillMaxWidth().then(if (still) Modifier else Modifier.ps4MediaScene(change))
                    .padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        Modifier.size(artSize).graphicsLayer {
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
                        if (art != null) Image(art.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        else Ps4ChromeIcon("play", Modifier.size(38.dp))
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(if (!audioInfo.isPlaying) "Audio detenido" else if (info.generic) "Reproducción detectada" else "En reproducción",
                            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
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
                            enabled = !preview && audioInfo.isPlaying && info.canPrevious,
                            modifier = Modifier.weight(1f).heightIn(min = 56.dp).semantics { contentDescription = "Pista anterior" }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Ps4ChromeIcon("previous")
                                Text("Anterior", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        FilledTonalButton(onClick = { onPause(info.sessionId) },
                            enabled = !preview && audioInfo.isPlaying && info.canPause,
                            modifier = Modifier.weight(1f).heightIn(min = 56.dp).semantics { contentDescription = "Pausar reproducción" },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Ps4ChromeIcon("pause")
                                Text("Pausar", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        TextButton(onClick = { onNext(info.sessionId) },
                            enabled = !preview && audioInfo.isPlaying && info.canNext,
                            modifier = Modifier.weight(1f).heightIn(min = 56.dp).semantics { contentDescription = "Pista siguiente" }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Ps4ChromeIcon("next")
                                Text("Siguiente", style = MaterialTheme.typography.labelMedium)
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
    var replay by remember { mutableIntStateOf(0) }
    TextButton(onClick = { expanded = !expanded }) {
        Text(if (expanded) "Cerrar muestra multimedia" else "Ver muestra multimedia PS4")
    }
    if (expanded) {
        key(replay) {
            Ps4MediaWidget(
                ActiveAudioInfo(isPlaying = true, title = "Tu música, en Aura", artist = "Vista previa PS4",
                    appLabel = "Reproductor"),
                reduced, {}, onPause = {}, onPrevious = {}, onNext = {}, onEnableAccess = {},
                preview = true, sampleMotion = true,
            )
        }
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
    val primary = MaterialTheme.colorScheme.primary
    val ink = MaterialTheme.colorScheme.onSurface
    return drawWithCache {
        val paths = List(3) { Path() }
        val glow = Brush.horizontalGradient(listOf(Color.Transparent, primary.copy(alpha = .20f), Color.Transparent))
        val bandWidth = size.width * .32f
        val reflection = Brush.horizontalGradient(listOf(Color.Transparent, ink.copy(alpha = .10f), Color.Transparent),
            startX = 0f, endX = bandWidth.coerceAtLeast(1f))
        val stroke = Stroke(.8.dp.toPx())
        onDrawBehind {
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
