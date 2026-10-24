package dev.senda.launcher.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance

enum class HistoricalMaterial { DOTS, CHROME, WATER, PAPER, FLOW, LCD }
enum class HistoricalMotion { DOT_REVEAL, HINGE, RIPPLE, LAYERS, GLIDE, SEGMENTS }

/** Original material roles. Personal archive artwork and manufacturer fonts are never bundled. */
data class HistoricalProfile(
    val reference: String,
    val era: String,
    val identity: String,
    val material: HistoricalMaterial,
    val motion: HistoricalMotion,
    val top: Color,
    val bottom: Color,
    val ink: Color,
    val muted: Color,
    val frame: Color,
    val signal: Color,
    val radius: Int,
    val durationMs: Int,
) {
    fun readableAccent(requested: Color): Color {
        fun contrast(a: Color, b: Color) = (maxOf(a.luminance(), b.luminance()) + .05f) / (minOf(a.luminance(), b.luminance()) + .05f)
        return requested.takeIf { contrast(it, top) >= 4.5f && contrast(it, bottom) >= 4.5f } ?: signal
    }
}

fun historicalProfile(theme: LauncherTheme): HistoricalProfile? = when (theme) {
    LauncherTheme.NOTHING -> HistoricalProfile("Nothing / CMF", "AMOLED · matriz luminosa", "Puntos, negro puro y señal", HistoricalMaterial.DOTS, HistoricalMotion.DOT_REVEAL,
        Color(0xFF131313), Color(0xFF090909), Color(0xFFF5F5F2), Color(0xFFB9B9B3), Color(0xFF595956), Color(0xFFFF8D79), 22, 240)
    LauncherTheme.SENSE -> HistoricalProfile("HTC Sense clásico", "Reloj flip · 2009–2013", "Grafito, hojas mecánicas y clima", HistoricalMaterial.CHROME, HistoricalMotion.HINGE,
        Color(0xFF343A36), Color(0xFF171D1A), Color(0xFFF8F7EE), Color(0xFFD1D5C9), Color(0xFF80907B), Color(0xFFB7E591), 6, 420)
    LauncherTheme.TOUCHWIZ -> HistoricalProfile("Galaxy S III · Nature UX", "2012 · agua y luz", "Cristal orgánico y ondas", HistoricalMaterial.WATER, HistoricalMotion.RIPPLE,
        Color(0xFF174766), Color(0xFF071F36), Color(0xFFF3FAFF), Color(0xFFCAE1EE), Color(0xFF72B9D9), Color(0xFFB9EDB8), 18, 360)
    LauncherTheme.OPTIMUS -> HistoricalProfile("LG G2 · Optimus UX", "2013 · notas y capas", "Papel cálido, relieve y color", HistoricalMaterial.PAPER, HistoricalMotion.LAYERS,
        Color(0xFFFFF9EC), Color(0xFFEAE4D7), Color(0xFF282B2D), Color(0xFF55544F), Color(0xFF827769), Color(0xFF8E4619), 3, 300)
    LauncherTheme.XPERIA -> HistoricalProfile("Xperia Z1 · Flow", "2013 · cristal y luz", "Transparencia y líneas continuas", HistoricalMaterial.FLOW, HistoricalMotion.GLIDE,
        Color(0xFF25233E), Color(0xFF101323), Color(0xFFF5F3FF), Color(0xFFD0CCEC), Color(0xFF8C86B1), Color(0xFFC4C0FF), 2, 480)
    LauncherTheme.RAZR -> HistoricalProfile("Motorola RAZR V3", "2004 · metal y LCD", "Biseles, segmentos y azul eléctrico", HistoricalMaterial.LCD, HistoricalMotion.SEGMENTS,
        Color(0xFF142A3B), Color(0xFF081824), Color(0xFFD5F1FF), Color(0xFFACD2E5), Color(0xFFB3C2CF), Color(0xFF9DE4FF), 2, 180)
    else -> null
}

/** Keep nested Material menus and controls coherent, including light paper inside a dark home. */
internal fun historicalColors(profile: HistoricalProfile, base: ColorScheme, requested: Color): ColorScheme {
    val accent = profile.readableAccent(requested)
    val onAccent = if (accent.luminance() > .179f) Color.Black else Color.White
    val paper = profile.material == HistoricalMaterial.PAPER
    return base.copy(
        primary = accent, onPrimary = onAccent, primaryContainer = profile.top, onPrimaryContainer = profile.ink,
        secondary = accent, onSecondary = onAccent, secondaryContainer = profile.top, onSecondaryContainer = profile.ink,
        tertiary = accent, onTertiary = onAccent, tertiaryContainer = profile.top, onTertiaryContainer = profile.ink,
        surface = profile.bottom, onSurface = profile.ink, surfaceVariant = profile.top, onSurfaceVariant = profile.muted,
        surfaceContainerLowest = profile.bottom, surfaceContainerLow = profile.bottom,
        surfaceContainer = profile.top, surfaceContainerHigh = profile.top, surfaceContainerHighest = profile.top,
        surfaceBright = profile.top, surfaceDim = profile.bottom, surfaceTint = Color.Transparent,
        outline = profile.frame, outlineVariant = profile.frame,
        error = if (paper) Color(0xFF9B1C1C) else base.error,
        onError = if (paper) Color.White else base.onError,
        errorContainer = if (paper) Color(0xFFFFDAD6) else base.errorContainer,
        onErrorContainer = if (paper) Color(0xFF410002) else base.onErrorContainer,
    )
}

/** Shared by information, media and previews; a real clickable Surface owns input semantics. */
@Composable
fun HistoricalPanel(
    theme: LauncherTheme?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val profile = theme?.let(::historicalProfile)?.takeUnless { LocalAppearance.current.highContrast }
    val scheme = MaterialTheme.colorScheme
    val customShape = LocalRetroStyle.current.customHolder
    val shape = when {
        customShape -> when (LocalSkinStyle.current.iconHolder) {
            IconHolder.CIRCLE, IconHolder.PILL -> RoundedCornerShape(50)
            IconHolder.SQUIRCLE -> RoundedCornerShape(22.dp)
            IconHolder.ROUNDED -> RoundedCornerShape(14.dp)
            IconHolder.BEVELED -> androidx.compose.foundation.shape.CutCornerShape(6.dp)
        }
        profile == null -> MaterialTheme.shapes.large
        else -> RoundedCornerShape(profile.radius.dp)
    }
    val top = profile?.top ?: scheme.surface
    val bottom = profile?.bottom ?: scheme.surface
    val foreground = profile?.ink ?: scheme.onSurface
    val muted = profile?.muted ?: scheme.onSurfaceVariant
    val accent = profile?.readableAccent(LocalSkinStyle.current.accent) ?: scheme.primary
    val frame = if (selected) accent else profile?.frame ?: scheme.outline
    val panelScheme = profile?.let { historicalColors(it, scheme, accent) } ?: scheme
    val inner: @Composable () -> Unit = {
        MaterialTheme(colorScheme = panelScheme) {
            CompositionLocalProvider(LocalContentColor provides foreground) {
                Box(Modifier.background(Brush.verticalGradient(listOf(top, bottom)))
                    .drawWithContent {
                        // Material is decorative, stays clipped by Surface and cannot intercept taps.
                        when (profile?.material) {
                            HistoricalMaterial.CHROME -> {
                                drawLine(Color.White.copy(alpha = .25f), Offset(3.dp.toPx(), 1.dp.toPx()), Offset(size.width - 3.dp.toPx(), 1.dp.toPx()), 1.dp.toPx())
                                drawLine(Color.Black.copy(alpha = .45f), Offset(0f, size.height - 2.dp.toPx()), Offset(size.width, size.height - 2.dp.toPx()), 2.dp.toPx())
                            }
                            HistoricalMaterial.WATER -> {
                                val gloss = Path().apply {
                                    moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width, size.height * .18f)
                                    quadraticTo(size.width * .45f, size.height * .45f, 0f, size.height * .25f); close()
                                }
                                drawPath(gloss, Color.White.copy(alpha = .07f))
                            }
                            HistoricalMaterial.PAPER -> {
                                drawRect(Color(0xFFC87F36), Offset(0f, 0f), Size(3.dp.toPx(), size.height))
                                for (row in 1..3) drawLine(Color(0xFF8E846E).copy(alpha = .08f), Offset(0f, size.height * row / 4), Offset(size.width, size.height * row / 4), .5.dp.toPx())
                            }
                            HistoricalMaterial.FLOW -> {
                                drawLine(Color(0xFFCDC7FF).copy(alpha = .32f), Offset(0f, 1.dp.toPx()), Offset(size.width, 1.dp.toPx()), .6.dp.toPx())
                                drawLine(Color(0xFF70B8E3).copy(alpha = .22f), Offset(size.width * .55f, size.height - 1.dp.toPx()), Offset(size.width, size.height - 1.dp.toPx()), .6.dp.toPx())
                            }
                            HistoricalMaterial.LCD -> {
                                drawRect(Color(0xFF8396A5).copy(alpha = .22f), style = Stroke(4.dp.toPx()))
                                for (row in 1..6) drawLine(Color.Black.copy(alpha = .10f), Offset(3.dp.toPx(), size.height * row / 7), Offset(size.width - 3.dp.toPx(), size.height * row / 7), .5.dp.toPx())
                            }
                            else -> Unit
                        }
                        drawContent()
                    }, content = content)
            }
        }
    }
    if (onClick == null) Surface(modifier, shape, bottom, foreground, border = BorderStroke(if (selected) 2.dp else 1.dp, frame), content = inner)
    else Surface(onClick = onClick, modifier = modifier, shape = shape, color = bottom, contentColor = foreground,
        border = BorderStroke(if (selected) 2.dp else 1.dp, frame), content = inner)
}
