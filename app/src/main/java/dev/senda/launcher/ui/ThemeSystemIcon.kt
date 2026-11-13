package dev.senda.launcher.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance

/** Original system glyphs. Action labels and hit areas belong to the surrounding control. */
@Composable
internal fun ThemeSystemIcon(kind: String, theme: LauncherTheme, modifier: Modifier = Modifier) {
    val angular = theme == LauncherTheme.NOTHING || theme == LauncherTheme.RAZR
    val path = remember(kind, angular) {
        val source = when {
            angular && kind == "settings" -> "M9 3H15L16 6L20 8V16L16 18L15 21H9L8 18L4 16V8L8 6Z M9 9H15V15H9Z"
            angular && kind == "search" -> "M4 3H14V13H4Z M14 13L21 20"
            angular && kind in setOf("alarm", "clock") -> "M5 4H19V20H5Z M12 7V12L16 15"
            else -> systemGlyphs[kind] ?: systemGlyphs.getValue("apps")
        }
        PathParser().parsePathString(source).toPath()
    }
    val ink = LocalContentColor.current
    val plain = LocalAppearance.current.highContrast
    val chrome = theme in setOf(LauncherTheme.PS4, LauncherTheme.RAZR, LauncherTheme.SENSE)
    Spacer(modifier.size(22.dp).clearAndSetSemantics {}.drawWithCache {
        val face: Brush = if (!plain && chrome) Brush.verticalGradient(
            if (ink.luminance() > .5f) listOf(Color.White, Color(0xFFAEC7E4), Color(0xFFF1F7FF))
            else listOf(Color(0xFF426B9C), Color(0xFF14294A), Color(0xFF355B8A)), endY = 24f
        ) else SolidColor(ink.copy(alpha = 1f))
        val stroke = Stroke(if (theme == LauncherTheme.TOUCHWIZ) 1.9f else 1.65f,
            cap = if (angular) StrokeCap.Square else StrokeCap.Round, join = if (angular) StrokeJoin.Miter else StrokeJoin.Round)
        val unit = minOf(size.width, size.height) / 24f
        onDrawBehind {
            translate((size.width - 24 * unit) / 2, (size.height - 24 * unit) / 2) {
                scale(unit, unit, Offset.Zero) {
                    if (!plain && chrome) translate(top = .45f) {
                        drawPath(path, Color.Black, alpha = ink.alpha * .25f, style = stroke)
                    }
                    drawPath(path, face, alpha = ink.alpha, style = stroke)
                }
            }
        }
    })
}

private val systemGlyphs = mapOf(
    "search" to "M10 3A7 7 0 1 1 10 17A7 7 0 1 1 10 3 M15 15L21 21",
    "settings" to "M9 3H15L16 6L19 7L21 12L19 17L16 18L15 21H9L8 18L5 17L3 12L5 7L8 6Z M12 8A4 4 0 1 1 12 16A4 4 0 1 1 12 8",
    "close" to "M5 5L19 19 M19 5L5 19",
    "layers" to "M2 9L12 3L22 9L12 15Z M2 14L12 20L22 14",
    "more" to "M11 4H13V6H11Z M11 11H13V13H11Z M11 18H13V20H11Z",
    "play" to "M7 4L20 12L7 20Z",
    "pause" to "M8 5V19 M16 5V19",
    "previous" to "M5 5V19 M19 5L8 12L19 19Z",
    "next" to "M19 5V19 M5 5L16 12L5 19Z",
    "calendar" to "M4 6H20V21H4Z M4 10H20 M8 3V7 M16 3V7 M8 14H10 M14 14H16 M8 18H10",
    "clock" to "M12 3A9 9 0 1 1 12 21A9 9 0 1 1 12 3 M12 7V12L16 15",
    "alarm" to "M12 5A8 8 0 1 1 12 21A8 8 0 1 1 12 5 M12 8V13L16 15 M3 6L6 3 M18 3L21 6",
    "volume" to "M3 9H7L12 5V19L7 15H3Z M16 8Q21 12 16 16 M19 5Q26 12 19 19",
    "favorite" to "M12 3L15 9L21 10L17 15L18 21L12 18L6 21L7 15L3 10L9 9Z",
    "apps" to "M4 4H9V9H4Z M15 4H20V9H15Z M4 15H9V20H4Z M15 15H20V20H15Z",
    "status" to "M4 19V13H7V19 M11 19V8H14V19 M18 19V3H21V19",
    "add" to "M12 4V20 M4 12H20",
    "edit" to "M4 16L16 4L20 8L8 20H4Z M13 7L17 11",
    "back" to "M14 4L6 12L14 20 M6 12H21",
    "home" to "M3 11L12 3L21 11 M6 10V21H18V10 M10 21V14H14V21",
    "person" to "M8 3H16V10H8Z M4 21V16Q12 10 20 16V21",
    "weather" to "M5 19Q0 13 7 12Q7 3 15 7Q20 6 20 12Q26 18 19 19Z",
)
