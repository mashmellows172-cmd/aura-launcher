package dev.senda.launcher.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.ActiveAudioInfo
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.ui.theme.*
import kotlin.math.sin
import kotlinx.coroutines.delay

@Composable
fun ThemedAudioWidget(
    audioInfo: ActiveAudioInfo,
    theme: LauncherTheme,
    reduceMotion: Boolean,
    onOpenAudio: (String?) -> Unit,
    modifier: Modifier = Modifier,
    onPause: (String?) -> Unit = {},
    onPrevious: (String?) -> Unit = {},
    onNext: (String?) -> Unit = {},
    onEnableAccess: () -> Unit = {},
    preview: Boolean = false,
) {
    var retained by remember { mutableStateOf(audioInfo) }
    if (audioInfo.isPlaying) SideEffect { retained = audioInfo }
    val info = if (audioInfo.isPlaying) audioInfo else retained
    var recentlyPlaying by remember { mutableStateOf(false) }
    LaunchedEffect(audioInfo.isPlaying) {
        if (audioInfo.isPlaying) recentlyPlaying = true
        else { delay(2500); recentlyPlaying = false }
    }
    val powerSaver = rememberMotionPowerSaver()
    val expanded = audioInfo.isPlaying
    val motionDisabled = powerSaver || reduceMotion || dev.senda.launcher.data.LocalAppearance.current.highContrast || preview || !android.animation.ValueAnimator.areAnimatorsEnabled()
    val duration = if (motionDisabled) 0 else historicalProfile(theme)?.durationMs ?: 240
    AnimatedVisibility(audioInfo.isPlaying || recentlyPlaying, modifier = modifier,
        enter = fadeIn(tween(duration)),
        exit = fadeOut(tween(duration.coerceAtMost(200))) + shrinkVertically(tween(duration.coerceAtMost(240)))) {
        HistoricalPanel(theme, Modifier.fillMaxWidth().animateContentSize(tween(duration)), onClick = if (preview) null else ({ if (info.generic) onEnableAccess() else onOpenAudio(info.sessionId) })) {
            val scheme = MaterialTheme.colorScheme
            val compact = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE || LocalDensity.current.fontScale > 1.5f
            var menu by remember { mutableStateOf(false) }
            val title = info.title ?: info.appLabel ?: "Contenido en reproducción"
            Column {
            Row(Modifier.fillMaxWidth().heightIn(min = if (expanded) 80.dp else 56.dp).padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(if (!expanded) 36.dp else if (compact) 40.dp else 56.dp).clip(RoundedCornerShape(if (theme == LauncherTheme.XPERIA || theme == LauncherTheme.RAZR) 2.dp else 8.dp)), contentAlignment = Alignment.Center) {
                    val art = info.artwork?.takeUnless { it.isRecycled }
                    if (art != null) Image(art.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else ThemedAudioVisualizer(theme, scheme.primary, true, Modifier.fillMaxSize())
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(if (!expanded) "SIN REPRODUCCIÓN" else if (info.generic) "REPRODUCCIÓN DETECTADA" else when (theme) {
                        LauncherTheme.NOTHING -> "AUDIO / ON"
                        LauncherTheme.SENSE -> "Música y compañía"
                        LauncherTheme.TOUCHWIZ -> "Sonidos del día"
                        LauncherTheme.OPTIMUS -> "Tu momento musical"
                        LauncherTheme.XPERIA -> "En tu frecuencia"
                        LauncherTheme.RAZR -> "MEDIA PLAYER"
                        else -> "En reproducción"
                    }, style = MaterialTheme.typography.labelSmall, color = scheme.onSurfaceVariant,
                        fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(title, style = MaterialTheme.typography.titleSmall,
                        fontWeight = when(theme) { LauncherTheme.PS4->FontWeight.Medium; LauncherTheme.SENSE,LauncherTheme.TOUCHWIZ,LauncherTheme.OPTIMUS->FontWeight.Bold; else->FontWeight.Normal },
                        color = scheme.onSurface, maxLines = if (compact) 1 else 2, overflow = TextOverflow.Ellipsis)
                    if (expanded && !compact) Text(if (info.generic) "Modo básico · tocar para configurar" else listOfNotNull(info.artist, info.appLabel).distinct().joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall, color = scheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (expanded && !info.generic) {
                    IconButton(onClick = { onPause(info.sessionId) }, enabled = !preview && audioInfo.isPlaying && info.canPause,
                        modifier = Modifier.size(48.dp).semantics { contentDescription = "Pausar reproducción" }) {
                        MediaControlGlyph("pause", if (info.canPause) scheme.primary else scheme.onSurfaceVariant.copy(alpha = .4f))
                    }
                    Box {
                        IconButton(onClick = { menu = true }, enabled = !preview && audioInfo.isPlaying,
                            modifier = Modifier.size(48.dp).semantics { contentDescription = "Más controles multimedia" }) {
                            MediaControlGlyph("more", scheme.onSurface)
                        }
                        DropdownMenu(menu, { menu = false }) {
                            DropdownMenuItem(text = { Text("Anterior") }, enabled = info.canPrevious && audioInfo.isPlaying,
                                onClick = { menu = false; onPrevious(info.sessionId) })
                            DropdownMenuItem(text = { Text("Siguiente") }, enabled = info.canNext && audioInfo.isPlaying,
                                onClick = { menu = false; onNext(info.sessionId) })
                            DropdownMenuItem(text = { Text("Abrir ${info.appLabel ?: "reproductor"}") },
                                onClick = { menu = false; onOpenAudio(info.sessionId) })
                        }
                    }
                }
            }
            if (expanded) ThemedAudioVisualizer(theme, scheme.primary, motionDisabled,
                Modifier.fillMaxWidth().height(if (compact) 16.dp else 28.dp).padding(horizontal = 14.dp, vertical = 3.dp))
            }
        }
    }
}

@Composable
private fun MediaControlGlyph(kind: String, color: Color) {
    Canvas(Modifier.size(22.dp).clearAndSetSemantics {}) {
        if (kind == "pause") {
            drawRoundRect(color, Offset(size.width * .22f, size.height * .15f), Size(size.width * .18f, size.height * .7f), CornerRadius(1.dp.toPx()))
            drawRoundRect(color, Offset(size.width * .60f, size.height * .15f), Size(size.width * .18f, size.height * .7f), CornerRadius(1.dp.toPx()))
        } else for (i in 0..2) drawCircle(color, 1.7.dp.toPx(), Offset(size.width * .5f, size.height * (.22f + i * .28f)))
    }
}

@Composable
private fun ThemedAudioVisualizer(theme: LauncherTheme, accent: Color, reduceMotion: Boolean, modifier: Modifier = Modifier) {
    val phase = rememberHistoricalPhase(theme, moving = true, reduced = reduceMotion)
    val reusablePath = remember { Path() }
    Canvas(modifier.clearAndSetSemantics {}) {
        val angle = phase.value * (Math.PI * 2).toFloat()
        when (theme) {
            LauncherTheme.NOTHING -> drawNothingVisualizer(angle, accent)
            LauncherTheme.SENSE -> drawSenseVisualizer(angle, accent)
            LauncherTheme.TOUCHWIZ -> drawTouchWizVisualizer(angle, accent)
            LauncherTheme.OPTIMUS -> drawOptimusVisualizer(angle, accent)
            LauncherTheme.XPERIA -> drawXperiaVisualizer(angle, accent, reusablePath)
            LauncherTheme.RAZR -> drawRazrVisualizer(angle, accent)
            LauncherTheme.PS4 -> drawPs4Visualizer(angle, accent, reusablePath)
            else -> drawDefaultVisualizer(angle, accent)
        }
    }
}
private fun DrawScope.drawNothingVisualizer(phase: Float, accent: Color) {
    val bars = 6
    val barWidth = size.width / (bars * 1.6f)
    val dotRadius = minOf(barWidth * 0.42f, size.height / 14f)
    for (i in 0 until bars) {
        val hFactor = ((sin(phase + i * 1.1f) + 1f) / 2f).coerceIn(0.2f, 1f)
        val numDots = (hFactor * 6).toInt().coerceIn(1, 6)
        val x = i * barWidth * 1.6f + barWidth / 2f
        for (d in 0 until numDots) {
            val y = size.height - (d + 0.5f) * (size.height / 6f)
            val isTop = d == numDots - 1
            val dotColor = if (isTop && i % 2 == 1) accent else Color.White
            val intensity = (hFactor * 6 - d).coerceIn(.15f, 1f)
            drawCircle(dotColor.copy(alpha = intensity), dotRadius, Offset(x, y))
        }
    }
}

private fun DrawScope.drawSenseVisualizer(phase: Float, accent: Color) {
    val bars = 5
    val barWidth = size.width / (bars * 1.5f)
    val green = Color(0xFF98C846)
    val amber = Color(0xFFFFB834)
    for (i in 0 until bars) {
        val hFactor = ((sin(phase + i * 0.9f) + 1f) / 2f).coerceIn(0.25f, 1f)
        val barHeight = size.height * hFactor
        val x = i * barWidth * 1.5f
        val y = size.height - barHeight
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(amber, green), startY = y, endY = size.height),
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
    }
}

private fun DrawScope.drawTouchWizVisualizer(phase: Float, accent: Color) {
    // Ondas de agua / rizos concéntricos suaves de naturaleza
    val cx = size.width / 2f
    val cy = size.height / 2f
    val baseColor = Color(0xFF38B6FF)
    for (r in 1..4) {
        val radiusFactor = ((phase / (Math.PI * 2).toFloat() + r * 0.25f) % 1f)
        val currentRadius = radiusFactor * size.minDimension * 0.48f
        val alpha = (1f - radiusFactor).coerceIn(0f, 0.8f)
        drawCircle(
            color = baseColor.copy(alpha = alpha),
            radius = currentRadius,
            center = Offset(cx, cy),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
    drawCircle(color = baseColor, radius = 3.dp.toPx(), center = Offset(cx, cy))
}

private fun DrawScope.drawOptimusVisualizer(phase: Float, accent: Color) {
    // A small rotating record, not an imitation of microphone or spectrum analysis.
    val center = Offset(size.width / 2, size.height / 2)
    val radius = minOf(size.width, size.height) * .43f
    drawCircle(Color(0xFF38312C), radius, center)
    for (ring in 1..3) drawCircle(Color(0xFFAD9980).copy(alpha = .5f), radius * ring / 4, center, style = Stroke(.6.dp.toPx()))
    drawArc(Color(0xFFDCAA58), phase * 180f / Math.PI.toFloat(), 72f, false,
        Offset(center.x - radius * .8f, center.y - radius * .8f), Size(radius * 1.6f, radius * 1.6f), style = Stroke(2.dp.toPx()))
    drawCircle(Color(0xFFF2DAAE), radius * .23f, center)
    drawCircle(accent, radius * .08f, center)
}
private fun DrawScope.drawXperiaVisualizer(phase: Float, accent: Color, path: Path) {
    val violet = Color(0xFFA370F7)
    val blue = Color(0xFF4361EE)
    path.reset()
    val midY = size.height / 2f
    val points = 20
    for (i in 0..points) {
        val x = (i.toFloat() / points) * size.width
        val wave = sin(phase + (i * 0.5f)) * (size.height * 0.35f)
        val y = midY + wave
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    drawPath(
        path = path,
        brush = Brush.horizontalGradient(listOf(violet, blue, violet)),
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun DrawScope.drawRazrVisualizer(phase: Float, accent: Color) {
    val lcdCyan = Color(0xFF62D0FF)
    val bars = 5
    val segments = 5
    val barWidth = size.width / (bars * 1.4f)
    val segHeight = (size.height - (segments - 1) * 2.dp.toPx()) / segments
    for (b in 0 until bars) {
        val hFactor = ((sin(phase + b * 1.2f) + 1f) / 2f).coerceIn(0.2f, 1f)
        val activeSegs = (hFactor * segments).toInt().coerceIn(1, segments)
        val x = b * barWidth * 1.4f
        for (s in 0 until segments) {
            val y = size.height - (s + 1) * (segHeight + 2.dp.toPx())
            val isActive = s < activeSegs
            val color = if (isActive) lcdCyan else lcdCyan.copy(alpha = 0.15f)
            drawRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, segHeight)
            )
        }
    }
}

private fun DrawScope.drawDefaultVisualizer(phase: Float, accent: Color) {
    val bars = 4
    val barWidth = size.width / (bars * 1.5f)
    for (i in 0 until bars) {
        val hFactor = ((sin(phase + i) + 1f) / 2f).coerceIn(0.25f, 1f)
        val h = size.height * hFactor
        drawRoundRect(
            color = accent,
            topLeft = Offset(i * barWidth * 1.5f, size.height - h),
            size = Size(barWidth, h),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
    }
}

/** Console light-wave bars, distinct from Xperia's orbital visualizer. */
private fun DrawScope.drawPs4Visualizer(phase:Float,accent:Color,path:Path) {
    for(i in 0..3){
        path.reset();val y=size.height*(.25f+i*.17f)
        path.moveTo(0f,y)
        path.cubicTo(size.width*.3f,y+sin(phase+i)*size.height*.14f,size.width*.65f,y-sin(phase+i)*size.height*.14f,size.width,y)
        drawPath(path,accent.copy(alpha=.35f+i*.15f),style=Stroke((1f+i*.25f).dp.toPx()))
    }
}
