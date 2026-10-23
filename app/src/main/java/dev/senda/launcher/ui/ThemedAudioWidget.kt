package dev.senda.launcher.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.ActiveAudioInfo
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.ui.theme.IndustrialTokens
import dev.senda.launcher.ui.theme.LocalIndustrialStyle
import dev.senda.launcher.ui.theme.LocalRetroStyle
import dev.senda.launcher.ui.theme.LocalSkinStyle
import kotlin.math.sin

@Composable
fun ThemedAudioWidget(
    audioInfo: ActiveAudioInfo,
    theme: LauncherTheme,
    reduceMotion: Boolean,
    onOpenAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = audioInfo.isPlaying,
        enter = fadeIn(tween(280)) + expandVertically(tween(320)),
        exit = fadeOut(tween(220)) + shrinkVertically(tween(280)),
        modifier = modifier
    ) {
        val scheme = MaterialTheme.colorScheme
        val skin = LocalSkinStyle.current
        val industrial = LocalIndustrialStyle.current.enabled
        val retro = LocalRetroStyle.current.enabled

        val shape = when (theme) {
            LauncherTheme.NOTHING -> RoundedCornerShape(18.dp)
            LauncherTheme.SENSE -> RoundedCornerShape(10.dp)
            LauncherTheme.TOUCHWIZ -> RoundedCornerShape(18.dp)
            LauncherTheme.OPTIMUS -> RoundedCornerShape(12.dp)
            LauncherTheme.XPERIA -> RoundedCornerShape(4.dp)
            LauncherTheme.RAZR -> RoundedCornerShape(3.dp)
            else -> RoundedCornerShape(12.dp)
        }

        val border = when (theme) {
            LauncherTheme.NOTHING -> BorderStroke(1.dp, IndustrialTokens.outline.copy(alpha = 0.5f))
            LauncherTheme.RAZR -> BorderStroke(1.dp, Color(0xFF62D0FF).copy(alpha = 0.6f))
            LauncherTheme.XPERIA -> BorderStroke(0.8.dp, Color(0xFFA370F7).copy(alpha = 0.5f))
            LauncherTheme.TOUCHWIZ -> BorderStroke(1.dp, Color(0xFF38B6FF).copy(alpha = 0.45f))
            LauncherTheme.SENSE -> BorderStroke(1.dp, Color(0xFF98C846).copy(alpha = 0.4f))
            LauncherTheme.OPTIMUS -> BorderStroke(1.dp, Color(0xFF00D4BB).copy(alpha = 0.45f))
            else -> BorderStroke(1.dp, scheme.outline.copy(alpha = 0.4f))
        }

        val cardBg = when (theme) {
            LauncherTheme.NOTHING -> Color(0xFF0F0F0F)
            LauncherTheme.RAZR -> Color(0xFF0D1722)
            LauncherTheme.XPERIA -> Color(0xFF161226)
            LauncherTheme.TOUCHWIZ -> Color(0xFF0A223D)
            LauncherTheme.SENSE -> Color(0xFF1E2625)
            LauncherTheme.OPTIMUS -> Color(0xFF1A1728)
            else -> scheme.surface
        }

        val trackTitle = audioInfo.title ?: audioInfo.appLabel ?: "Reproduciendo audio"
        val subtitle = if (audioInfo.artist != null) {
            "${audioInfo.artist} · ${audioInfo.appLabel ?: "Audio"}"
        } else {
            audioInfo.appLabel ?: "Tocar para abrir reproductor"
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenAudio)
                .semantics {
                    role = Role.Button
                    contentDescription = "Reproductor de audio: $trackTitle. $subtitle"
                },
            shape = shape,
            color = cardBg,
            border = border,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Visualizador temático animado
                ThemedAudioVisualizer(
                    theme = theme,
                    accent = skin.accent,
                    reduceMotion = reduceMotion,
                    modifier = Modifier.size(width = 44.dp, height = 36.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = trackTitle,
                        style = if (theme == LauncherTheme.RAZR) {
                            MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        } else if (theme == LauncherTheme.XPERIA) {
                            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Light)
                        } else {
                            MaterialTheme.typography.titleMedium
                        },
                        color = if (theme == LauncherTheme.RAZR) Color(0xFF62D0FF) else scheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = if (theme == LauncherTheme.RAZR) {
                            MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                        } else {
                            MaterialTheme.typography.bodySmall
                        },
                        color = scheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Indicador de estado
                Box(
                    modifier = Modifier
                        .background(
                            color = skin.accent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "EN VIVO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = skin.accent,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemedAudioVisualizer(
    theme: LauncherTheme,
    accent: Color,
    reduceMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "audio-vis")
    val phase by if (reduceMotion) {
        remember { mutableFloatStateOf(0.5f) }
    } else {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = (Math.PI * 2).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "vis-phase"
        )
    }

    Canvas(modifier = modifier) {
        when (theme) {
            LauncherTheme.NOTHING -> drawNothingVisualizer(phase, accent)
            LauncherTheme.SENSE -> drawSenseVisualizer(phase, accent)
            LauncherTheme.TOUCHWIZ -> drawTouchWizVisualizer(phase, accent)
            LauncherTheme.OPTIMUS -> drawOptimusVisualizer(phase, accent)
            LauncherTheme.XPERIA -> drawXperiaVisualizer(phase, accent)
            LauncherTheme.RAZR -> drawRazrVisualizer(phase, accent)
            else -> drawDefaultVisualizer(phase, accent)
        }
    }
}

private fun DrawScope.drawNothingVisualizer(phase: Float, accent: Color) {
    val bars = 6
    val barWidth = size.width / (bars * 1.6f)
    val dotRadius = barWidth * 0.42f
    for (i in 0 until bars) {
        val hFactor = ((sin(phase + i * 1.1f) + 1f) / 2f).coerceIn(0.2f, 1f)
        val numDots = (hFactor * 6).toInt().coerceIn(1, 6)
        val x = i * barWidth * 1.6f + barWidth / 2f
        for (d in 0 until numDots) {
            val y = size.height - (d + 0.5f) * (size.height / 6f)
            val isTop = d == numDots - 1
            val dotColor = if (isTop && i % 2 == 1) IndustrialTokens.signal else Color.White
            drawCircle(dotColor, dotRadius, Offset(x, y))
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
        val currentRadius = radiusFactor * size.width * 0.48f
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
    val cyan = Color(0xFF00D4BB)
    val coral = Color(0xFFFF5277)
    val bars = 5
    val barWidth = size.width / (bars * 1.4f)
    for (i in 0 until bars) {
        val hFactor = ((sin(phase + i * 1.3f) + 1f) / 2f).coerceIn(0.2f, 1f)
        val barHeight = size.height * hFactor
        val x = i * barWidth * 1.4f
        val y = size.height - barHeight
        val color = if (i % 2 == 0) cyan else coral
        drawRoundRect(
            color = color,
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
    }
}

private fun DrawScope.drawXperiaVisualizer(phase: Float, accent: Color) {
    val violet = Color(0xFFA370F7)
    val blue = Color(0xFF4361EE)
    val path = Path()
    val midY = size.height / 2f
    val points = 20
    for (i in 0..points) {
        val x = (i.toFloat() / points) * size.width
        val wave = sin(phase * 1.5f + (i * 0.5f)) * (size.height * 0.35f)
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
