package dev.senda.launcher.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp

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
