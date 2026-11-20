package dev.senda.launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
        val hairline = Stroke(.7.dp.toPx())
        val controlStroke = Stroke(1.dp.toPx())
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
                if (index % 4 == 0) palette.ink else palette.accent,
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
        val orbs = listOf(
            Triple(.96f, .24f, .31f), Triple(.77f, .52f, .19f),
            Triple(1.02f, .88f, .39f), Triple(.45f, .91f, .12f),
            Triple(.80f, .10f, .065f),
        ).map { (x, y, radius) ->
            val center = Offset(w * x, h * y)
            val r = w * radius
            IdentityOrb(center, r, Brush.radialGradient(
                0f to palette.ink.copy(alpha = .32f),
                .19f to palette.accent.copy(alpha = .27f),
                .58f to palette.raised.copy(alpha = .38f),
                1f to palette.bottom.copy(alpha = .72f),
                center = center - Offset(r * .34f, r * .40f), radius = r * 1.5f,
            ), Brush.radialGradient(
                listOf(palette.ink.copy(alpha = .50f), Color.Transparent),
                center = center - Offset(r * .36f, r * .40f), radius = r * 1.35f,
            ))
        }
        // A quiet, non-interactive handheld control cluster stays in the right margin.
        val controlCenter = Offset(w * .91f, h * .70f)
        val controlRadius = w * .027f
        val controlGap = w * .047f
        val buttons = listOf(Offset(0f, -controlGap), Offset(controlGap, 0f),
            Offset(0f, controlGap), Offset(-controlGap, 0f)).map { controlCenter + it }
        val padCenter = Offset(w * .91f, h * .80f)
        val padArm = w * .035f
        val padNeck = w * .012f
        val pad = Path().apply {
            moveTo(padCenter.x - padNeck, padCenter.y - padArm)
            lineTo(padCenter.x + padNeck, padCenter.y - padArm)
            lineTo(padCenter.x + padNeck, padCenter.y - padNeck)
            lineTo(padCenter.x + padArm, padCenter.y - padNeck)
            lineTo(padCenter.x + padArm, padCenter.y + padNeck)
            lineTo(padCenter.x + padNeck, padCenter.y + padNeck)
            lineTo(padCenter.x + padNeck, padCenter.y + padArm)
            lineTo(padCenter.x - padNeck, padCenter.y + padArm)
            lineTo(padCenter.x - padNeck, padCenter.y + padNeck)
            lineTo(padCenter.x - padArm, padCenter.y + padNeck)
            lineTo(padCenter.x - padArm, padCenter.y - padNeck)
            lineTo(padCenter.x - padNeck, padCenter.y - padNeck)
            close()
        }
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
                orbs.forEachIndexed { index, orb ->
                    if (!constrained.value || index == 0 || index == 2) {
                        drawCircle(orb.fill, orb.radius, orb.center, alpha = strength * .84f)
                        if (!constrained.value) drawCircle(orb.edge, orb.radius, orb.center,
                            alpha = strength * .65f, style = hairline)
                    }
                }
                if (!constrained.value) {
                    buttons.forEach { center ->
                        drawCircle(palette.bottom, controlRadius, center, alpha = .34f * strength)
                        drawCircle(palette.accent, controlRadius, center,
                            alpha = .22f * strength, style = controlStroke)
                    }
                    drawPath(pad, palette.bottom, alpha = .34f * strength)
                    drawPath(pad, palette.accent, alpha = .22f * strength, style = controlStroke)
                    drawCircle(palette.accent, w * .034f, Offset(w * .91f, h * .87f),
                        alpha = .17f * strength, style = controlStroke)
                }
            }
            drawRect(readingShade)
        }
    })
}

private data class IdentityParticle(val center: Offset, val radius: Float, val color: Color, val alpha: Float)
private data class IdentityOrb(val center: Offset, val radius: Float, val fill: Brush, val edge: Brush)