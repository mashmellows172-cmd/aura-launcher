package dev.senda.launcher.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import dev.senda.launcher.data.Ps4Palette
import dev.senda.launcher.ui.theme.LocalPs4Palette
import dev.senda.launcher.ui.theme.ps4PaletteColors
import dev.senda.launcher.ui.theme.ps4Design
import dev.senda.launcher.ui.theme.LocalRetroStyle
import dev.senda.launcher.ui.theme.LocalSkinStyle
import dev.senda.launcher.ui.theme.animatedIconHolderShape
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LocalAppearance

@Composable
internal fun Modifier.ps4TilePlate(): Modifier {
    val colors = MaterialTheme.colorScheme
    val palette = LocalPs4Palette.current
    val design = ps4Design(palette)
    val customShape = LocalRetroStyle.current.customHolder
    val shape = if (customShape) animatedIconHolderShape(LocalSkinStyle.current.iconHolder, design.motionMillis)
        else if (palette == Ps4Palette.WHITE) CircleShape else RoundedCornerShape(design.tileRadius.dp)
    return drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        val outlinePath = Path().apply {
            when (outline) {
                is Outline.Rectangle -> addRect(outline.rect)
                is Outline.Rounded -> addRoundRect(outline.roundRect)
                is Outline.Generic -> addPath(outline.path)
            }
        }
        val base = if (palette == Ps4Palette.WHITE) Brush.radialGradient(
            listOf(colors.primary.copy(alpha = .48f), colors.surfaceVariant, colors.surface),
            center = Offset(size.width * .35f, size.height * .2f), radius = size.maxDimension,
        ) else Brush.linearGradient(
            listOf(colors.primary.copy(alpha = if (palette == Ps4Palette.MIDNIGHT) .08f else .23f),
                colors.surfaceVariant, colors.surface),
            end = Offset(size.width, size.height),
        )
        val edge = Brush.linearGradient(
            listOf(colors.primary.copy(alpha = .68f), colors.outline.copy(alpha = .28f)),
            end = Offset(size.width, size.height),
        )
        val gloss = Brush.radialGradient(
            listOf(Color.White.copy(alpha = .25f), Color.Transparent),
            center = Offset(size.width * .32f, size.height * .16f), radius = size.width * .7f,
        )
        val stroke = Stroke(1.dp.toPx())
        onDrawBehind {
            drawPath(outlinePath, base)
            if (palette == Ps4Palette.WHITE) drawPath(outlinePath, gloss)
            drawPath(outlinePath, edge, style = stroke)
        }
    }
}

/** Local Ollama proposal corrected to Material 3 and draw-only state observation. */
@Composable
internal fun Modifier.ps4Material(highlight: State<Float>? = null): Modifier {
    if (LocalAppearance.current.highContrast) return this
    val scheme = MaterialTheme.colorScheme
    val palette = LocalPs4Palette.current
    val tokens = ps4PaletteColors(palette)
    return drawWithCache {
        val base = when (palette) {
            Ps4Palette.CLASSIC -> Brush.verticalGradient(listOf(scheme.surfaceVariant.copy(alpha = tokens.panelAlpha), scheme.surface))
            Ps4Palette.MIDNIGHT -> Brush.verticalGradient(listOf(scheme.surfaceVariant.copy(alpha = .98f), scheme.surface.copy(alpha = .98f)))
            Ps4Palette.WHITE -> Brush.radialGradient(listOf(tokens.accent.copy(alpha = .24f), scheme.surfaceVariant.copy(alpha = tokens.panelAlpha), scheme.surface))
        }
        val reflection = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * .14f)
            cubicTo(size.width * .7f, size.height * .08f,
                size.width * .3f, size.height * .4f, 0f, size.height * .25f)
            close()
        }
        val facet = Path().apply {
            moveTo(size.width * .72f, 0f); lineTo(size.width, 0f)
            lineTo(size.width, size.height * .48f); close()
        }
        val sheen = Brush.verticalGradient(
            listOf(scheme.onSurface.copy(alpha = tokens.glassReflectionAlpha), Color.Transparent),
            endY = size.height.coerceAtLeast(1f) * .45f,
        )
        val upperRim = Brush.horizontalGradient(
            listOf(Color.Transparent, tokens.focusRim.copy(alpha = tokens.focusSweepAlpha), Color.Transparent),
            startX = size.width * .08f,
            endX = size.width * .92f,
        )
        val light = Brush.linearGradient(
            listOf(tokens.focusRim.copy(alpha = tokens.focusHaloAlpha), Color.Transparent),
            end = Offset(size.width, size.height),
        )
        val edge = scheme.onSurface.copy(alpha = .12f)
        val edgeHeight = .7.dp.toPx()
        onDrawBehind {
            drawRect(base)
            if (palette != Ps4Palette.MIDNIGHT) drawPath(reflection, sheen)
            if (palette == Ps4Palette.CLASSIC) drawPath(facet, scheme.onSurface, alpha = .025f)
            if (palette == Ps4Palette.WHITE) {
                val step = 18.dp.toPx()
                for (x in 1..(size.width / step).toInt()) drawLine(tokens.focusRim, Offset(x * step, 0f), Offset(x * step, size.height), .45.dp.toPx(), alpha = .08f)
            }
            drawLine(scheme.onSurface, Offset(0f, edgeHeight), Offset(size.width, edgeHeight), edgeHeight, alpha = .18f)
            drawRect(upperRim, size = Size(size.width, edgeHeight))
            drawRect(edge, Offset(0f, size.height - edgeHeight), Size(size.width, edgeHeight))
            val strength = highlight?.value?.coerceIn(0f, 1f) ?: 0f
            if (strength > 0f) drawRect(light, alpha = strength)
        }
    }
}

/** Thin retained HUD reflection; it has no clock and is suppressed with decorative motion. */
@Composable
internal fun Modifier.ps4HudChrome(): Modifier {
    if (LocalAppearance.current.highContrast) return this
    val tokens = ps4PaletteColors(LocalPs4Palette.current)
    return drawWithCache {
        val glow = Brush.horizontalGradient(
            listOf(Color.Transparent, tokens.focusRim.copy(alpha = tokens.hudGlowAlpha), Color.Transparent),
            startX = size.width * .12f,
            endX = size.width * .88f,
        )
        val rim = Brush.verticalGradient(
            listOf(Color.White.copy(alpha = tokens.glassReflectionAlpha), Color.Transparent),
            endY = size.height.coerceAtLeast(1f) * .62f,
        )
        onDrawBehind {
            drawRect(rim)
            drawRect(glow, topLeft = Offset.Zero, size = Size(size.width, 1.dp.toPx()))
        }
    }
}
