package dev.senda.launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.Ps4Palette
import dev.senda.launcher.ui.theme.Ps4PaletteColors

/** Original static scenes carried by the wallpaper's retained, reactive graphics layer. */
@Composable
internal fun Ps4IdentityBackdrop(
    identity: Ps4Palette,
    palette: Ps4PaletteColors,
    modifier: Modifier,
    strength: Float,
) {
    val constrained = LocalDecorationConstrained.current
    Box(modifier.drawWithCache {
        val w = size.width
        val h = size.height
        val readingShade = Brush.horizontalGradient(
            0f to palette.bottom.copy(alpha = .70f),
            .52f to palette.bottom.copy(alpha = .48f),
            1f to Color.Transparent,
        )
        val light = Brush.radialGradient(
            listOf(palette.accent.copy(alpha = .13f), Color.Transparent),
            center = Offset(w * .94f, h * .38f), radius = h * .65f,
        )
        // Deterministic positions and radii: generated only with the size/theme cache.
        val particles = List(48) { index ->
            val x = ((index * 37 + 19) % 101) / 100f
            val y = ((index * 61 + 7) % 103) / 102f
            IdentityParticle(Offset(w * (.20f + x * .88f), h * y),
                (.45f + index % 5 * .28f).dp.toPx(),
                if (index % 5 == 0) Color(0xFFD5C7A3) else palette.ink,
                .12f + index % 4 * .055f)
        }
        val shaft = Brush.horizontalGradient(listOf(Color.Transparent,
            palette.ink.copy(alpha = .06f), Color.Transparent))
        val shaftPath = Path().apply {
            moveTo(w * .84f, -h * .08f)
            cubicTo(w * .69f, h * .30f, w * .91f, h * .65f, w * .78f, h * 1.08f)
            lineTo(w * .83f, h * 1.08f)
            cubicTo(w * .97f, h * .65f, w * .74f, h * .30f, w * .89f, -h * .08f)
            close()
        }
        // Vita's blue flow and glass bands belong behind content; bubbles belong to app plates.
        val bands = List(3) { index ->
            Path().apply {
                val y = h * (.28f + index * .25f)
                moveTo(-w * .12f, y)
                cubicTo(w * .25f, y + h * .18f, w * .64f, y - h * .14f, w * 1.12f, y + h * .10f)
                lineTo(w * 1.12f, y + h * .15f)
                cubicTo(w * .63f, y - h * .09f, w * .25f, y + h * .21f, -w * .12f, y + h * .04f)
                close()
            }
        }
        val bandFill = Brush.linearGradient(listOf(Color.Transparent,
            palette.accent.copy(alpha = .15f), Color.White.copy(alpha = .09f), Color.Transparent),
            start = Offset(w, 0f), end = Offset(0f, h))
        val horizon = Brush.verticalGradient(listOf(Color.Transparent,
            palette.accent.copy(alpha = .08f), Color.Transparent), startY = h * .35f, endY = h * .85f)
        onDrawBehind {
            if (strength <= 0f) return@onDrawBehind
            drawRect(light, alpha = strength)
            if (identity == Ps4Palette.MIDNIGHT) {
                drawPath(shaftPath, shaft, alpha = strength)
                if (!constrained.value) particles.forEach { particle ->
                    drawCircle(particle.color, particle.radius, particle.center,
                        alpha = particle.alpha * strength)
                }
            } else {
                drawRect(horizon, alpha = strength)
                bands.forEachIndexed { index, path ->
                    if (!constrained.value || index == 1) drawPath(path, bandFill, alpha = strength)
                }
            }
            drawRect(readingShade)
        }
    })
}

private data class IdentityParticle(val center: Offset, val radius: Float, val color: Color, val alpha: Float)