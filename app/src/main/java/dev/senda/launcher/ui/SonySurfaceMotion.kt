package dev.senda.launcher.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import dev.senda.launcher.ui.theme.SurfaceMotionKind
import dev.senda.launcher.ui.theme.ThemeSurfaceMotion

/** A bounded material reveal; content is recorded once and input geometry never changes. */
internal fun Modifier.themeSurfaceReveal(motion: ThemeSurfaceMotion, progress: () -> Float): Modifier = drawWithCache {
    val mask = Path()
    onDrawWithContent {
        val phase = progress().coerceIn(0f, 1f)
        if (phase >= 1f || (motion.kind != SurfaceMotionKind.DOTS &&
                motion.kind != SurfaceMotionKind.SEGMENTS && motion.kind != SurfaceMotionKind.LAYERS)) {
            drawContent()
        } else {
            mask.reset()
            val bands = if (motion.kind == SurfaceMotionKind.DOTS) 5 else 3
            for (index in 0 until bands) {
                val local = (phase * 1.18f - index * .18f / (bands - 1)).coerceIn(0f, 1f)
                val top = size.height * index / bands
                val bottom = size.height * (index + 1) / bands
                if (layoutDirection == LayoutDirection.Rtl) mask.addRect(Rect(size.width * (1f - local), top, size.width, bottom))
                else mask.addRect(Rect(0f, top, size.width * local, bottom))
            }
            val contentScope = this
            clipPath(mask) { contentScope.drawContent() }
        }
    }
}

/** Focus is acknowledged by the material's edge, without transforming the text or caret. */
internal fun Modifier.themeSearchFocus(
    motion: ThemeSurfaceMotion, accent: Color, radiusDp: Int, strength: Float, progress: () -> Float,
): Modifier = drawWithCache {
    val inset = 1.5.dp.toPx()
    val stroke = Stroke(1.dp.toPx())
    val corner = CornerRadius(radiusDp.dp.toPx())
    val bounds = Size((size.width - inset * 2).coerceAtLeast(0f), (size.height - inset * 2).coerceAtLeast(0f))
    onDrawWithContent {
        drawContent()
        val phase = progress().coerceIn(0f, 1f)
        if (phase > 0f) {
            val alpha = strength.coerceIn(0f, 1f) * phase
            when (motion.kind) {
                SurfaceMotionKind.DOTS, SurfaceMotionKind.SEGMENTS -> {
                    val segments = if (motion.kind == SurfaceMotionKind.DOTS) 9 else 5
                    val gap = 4.dp.toPx()
                    val step = bounds.width / segments
                    repeat(segments) { index ->
                        val reveal = (phase * segments - index).coerceIn(0f, 1f)
                        val rtl = layoutDirection == LayoutDirection.Rtl
                        val start = if (rtl) size.width - inset - index * step else inset + index * step
                        val end = start + (step - gap).coerceAtLeast(0f) * reveal * if (rtl) -1f else 1f
                        drawLine(accent, Offset(start, size.height - inset),
                            Offset(end, size.height - inset),
                            strokeWidth = 2.dp.toPx(), alpha = alpha)
                    }
                }
                SurfaceMotionKind.HINGE -> clipRect(bottom = size.height * phase) {
                    drawRoundRect(accent, Offset(inset, inset), bounds, corner, alpha = alpha, style = stroke)
                }
                SurfaceMotionKind.LAYERS -> {
                    val rtl = layoutDirection == LayoutDirection.Rtl
                    val start = if (rtl) size.width - inset else inset
                    val end = if (rtl) inset else size.width - inset
                    drawLine(accent, Offset(start, size.height - inset), Offset(start + (end - start) * phase, size.height - inset), alpha = alpha)
                    drawLine(accent, Offset(end, size.height - inset), Offset(end, inset + bounds.height * (1f - phase)), alpha = alpha * .65f)
                }
                else -> {
                    val fromCenter = motion.kind == SurfaceMotionKind.RIPPLE || motion.kind == SurfaceMotionKind.PS5
                    val rtl = layoutDirection == LayoutDirection.Rtl
                    val left = if (fromCenter) center.x * (1f - phase) else if (rtl) size.width * (1f - phase) else 0f
                    val right = if (fromCenter) center.x * (1f + phase) else if (rtl) size.width else size.width * phase
                    clipRect(left = left, right = right) {
                        drawRoundRect(accent, Offset(inset, inset), bounds, corner, alpha = alpha, style = stroke)
                    }
                }
            }
        }
    }
}

internal fun Modifier.sonySearchLens(accent: Color, progress: () -> Float): Modifier = drawWithCache {
    val inset = 2.dp.toPx()
    val stroke = Stroke(1.dp.toPx())
    val edge = Brush.verticalGradient(listOf(accent, accent.copy(alpha = .35f), Color.Transparent))
    onDrawWithContent {
        drawContent()
        val phase = progress().coerceIn(0f, 1f)
        val bottom = (size.height - 2 * inset) * phase
        if (bottom > inset && size.width > 2 * inset) {
            drawRoundRect(edge, Offset(inset, inset), Size(size.width - 2 * inset, bottom),
                CornerRadius(7.dp.toPx()), alpha = .7f, style = stroke)
        }
    }
}

internal fun Modifier.sonyThemeReveal(accent: Color, veil: Color, progress: () -> Float): Modifier = drawWithCache {
    val ribbon = Brush.horizontalGradient(listOf(Color.Transparent, accent.copy(alpha = .62f), Color.Transparent),
        startX = 0f, endX = size.width * .34f)
    onDrawWithContent {
        drawContent()
        val phase = progress().coerceIn(0f, 1f)
        if (phase < 1f) {
            val front = (phase * 1.34f - .17f) * size.width
            val veilStart = front.coerceIn(0f, size.width)
            drawRect(veil, Offset(veilStart, 0f), Size(size.width - veilStart, size.height),
                alpha = .64f * (1f - phase))
            val band = size.width * .34f
            translate(left = front - band / 2f) {
                drawRect(ribbon, size = Size(band, size.height), alpha = 1f - phase)
            }
        }
    }
}
