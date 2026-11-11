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
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LocalAppearance

/** Local Ollama proposal corrected to Material 3 and draw-only state observation. */
@Composable
internal fun Modifier.ps4Material(highlight: State<Float>? = null): Modifier {
    if (LocalAppearance.current.highContrast) return this
    val scheme = MaterialTheme.colorScheme
    return drawWithCache {
        val base = Brush.verticalGradient(listOf(scheme.surfaceVariant, scheme.surface))
        val reflection = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * .14f)
            cubicTo(size.width * .7f, size.height * .08f,
                size.width * .3f, size.height * .4f, 0f, size.height * .25f)
            close()
        }
        val sheen = Brush.verticalGradient(
            listOf(scheme.onSurface.copy(alpha = .065f), Color.Transparent),
            endY = size.height.coerceAtLeast(1f) * .45f,
        )
        val light = Brush.linearGradient(
            listOf(scheme.primary.copy(alpha = .19f), Color.Transparent),
            end = Offset(size.width, size.height),
        )
        val edge = scheme.onSurface.copy(alpha = .12f)
        val edgeHeight = .7.dp.toPx()
        onDrawBehind {
            drawRect(base)
            drawPath(reflection, sheen)
            drawRect(edge, Offset(0f, size.height - edgeHeight), Size(size.width, edgeHeight))
            val strength = highlight?.value?.coerceIn(0f, 1f) ?: 0f
            if (strength > 0f) drawRect(light, alpha = strength)
        }
    }
}
