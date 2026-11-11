package dev.senda.launcher.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LocalAppearance

/** Uses the existing vector silhouettes, with cached silver shading and no offscreen layer. */
@Composable
internal fun Ps4ChromeIcon(kind: String, modifier: Modifier = Modifier) {
    val vector = when (kind) {
        "settings" -> Icons.Default.Settings
        "search" -> Icons.Default.Search
        "play" -> Icons.Default.PlayArrow
        "calendar", "alarm", "pause", "previous", "next" -> null
        else -> Icons.Default.MoreVert
    }
    val paths = remember(kind) {
        vector?.root?.filterIsInstance<VectorPath>()?.map { node ->
            PathParser().addPathNodes(node.pathData).toPath().apply { fillType = node.pathFillType }
        } ?: listOf(PathParser().parsePathString(controlPaths.getValue(kind)).toPath().apply {
            fillType = PathFillType.EvenOdd
        })
    }
    val ink = MaterialTheme.colorScheme.onSurface
    val opacity = LocalContentColor.current.alpha
    val highContrast = LocalAppearance.current.highContrast
    val viewportWidth = vector?.viewportWidth ?: 24f
    val viewportHeight = vector?.viewportHeight ?: 24f
    Spacer(modifier.size(22.dp).clearAndSetSemantics {}.drawWithCache {
        val face = if (highContrast) SolidColor(ink) else Brush.verticalGradient(
            if (ink.luminance() > .5f) {
                listOf(Color(0xFFFFFFFF), Color(0xFFB3C8E2), Color(0xFFF1F7FF))
            } else {
                listOf(Color(0xFF426B9C), Color(0xFF14294A), Color(0xFF355B8A))
            },
            endY = viewportHeight,
        )
        val edge = Color.Black.copy(alpha = .25f * opacity)
        val unit = minOf(size.width / viewportWidth, size.height / viewportHeight)
        val outline = Stroke(.45f)
        onDrawBehind {
            translate((size.width - viewportWidth * unit) / 2f,
                (size.height - viewportHeight * unit) / 2f) {
                scale(unit, unit, Offset.Zero) {
                    for (path in paths) {
                        if (!highContrast) translate(top = .45f) { drawPath(path, edge, style = outline) }
                        drawPath(path, face, alpha = opacity)
                    }
                }
            }
        }
    })
}

private val controlPaths = mapOf(
    "calendar" to "M4 5H20V21H4Z M6 10V19H18V10Z M7 2H9V7H7Z M15 2H17V7H15Z " +
        "M8 12H10V14H8Z M13 12H15V14H13Z M8 16H10V18H8Z",
    "alarm" to "M12 5A8 8 0 1 1 12 21A8 8 0 1 1 12 5Z " +
        "M12 7A6 6 0 1 0 12 19A6 6 0 1 0 12 7Z " +
        "M11 9H13V13H16V15H11Z M2 5L6 2L7 3.5L3 6.5Z M17 3.5L18 2L22 5L21 6.5Z",
    "pause" to "M7 5H10V19H7Z M14 5H17V19H14Z",
    "previous" to "M5 5H7V19H5Z M19 5L8 12L19 19Z",
    "next" to "M17 5H19V19H17Z M5 5L16 12L5 19Z",
)
