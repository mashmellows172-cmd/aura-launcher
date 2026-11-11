package dev.senda.launcher.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance

/** Cached signatures for the shared panels, not an extra wallpaper or animation clock. */
@Composable
internal fun Modifier.themeSurfaceDetail(theme: LauncherTheme?, reaction: State<Float>?): Modifier {
    if (theme == null || LocalAppearance.current.highContrast) return this
    val constrained = dev.senda.launcher.ui.LocalDecorationConstrained.current
    val ink = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    val accent = LocalSkinStyle.current.accent
    return drawWithCache {
        val edge = .65.dp.toPx()
        val water = Path().apply {
            moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width, size.height * .12f)
            cubicTo(size.width * .7f, size.height * .06f, size.width * .25f, size.height * .4f, 0f, size.height * .24f); close()
        }
        val waterLine = Path().apply {
            moveTo(0f, size.height * .72f)
            cubicTo(size.width * .3f, size.height * .64f, size.width * .7f, size.height * .88f, size.width, size.height * .78f)
        }
        onDrawBehind {
            if (constrained.value) return@onDrawBehind
            val p = reaction?.value ?: 0f
            when (theme) {
                LauncherTheme.NOTHING -> {
                    repeat(3) { i -> drawRect(ink, Offset(size.width - (12 + i * 5).dp.toPx(), 5.dp.toPx()),
                        Size(2.dp.toPx(), 2.dp.toPx()), alpha = .28f + p * .25f) }
                    drawLine(accent, Offset(0f, size.height - edge), Offset(size.width * (.1f + p * .12f), size.height - edge), edge)
                }
                LauncherTheme.RAZR -> {
                    repeat(5) { i -> drawLine(Color.White, Offset(3.dp.toPx(), size.height * (i + 1) / 6),
                        Offset(size.width - 3.dp.toPx(), size.height * (i + 1) / 6), edge, alpha = .025f) }
                    drawLine(Color.Black, Offset(0f, size.height * .5f), Offset(size.width, size.height * .5f), edge, alpha = .15f)
                    drawLine(ink, Offset(3.dp.toPx(), edge), Offset(size.width - 3.dp.toPx(), edge), edge, alpha = .28f + p * .18f)
                }
                LauncherTheme.TOUCHWIZ -> {
                    drawPath(water, Color.White, alpha = .08f + p * .04f)
                    translate(top = -p * 2.dp.toPx()) { drawPath(waterLine, accent, alpha = .12f + p * .15f, style = Stroke(edge)) }
                }
                LauncherTheme.SENSE -> {
                    drawLine(Color.White, Offset(3.dp.toPx(), edge), Offset(size.width - 3.dp.toPx(), edge), edge, alpha = .24f)
                    drawLine(Color.Black, Offset(0f, size.height - edge), Offset(size.width, size.height - edge), edge * 2, alpha = .4f)
                }
                LauncherTheme.OPTIMUS -> {
                    drawRect(Color(0xFFC87F36), size = Size(3.dp.toPx(), size.height))
                    repeat(3) { i -> drawLine(Color(0xFF8E846E), Offset(0f, size.height * (i + 1) / 4),
                        Offset(size.width, size.height * (i + 1) / 4), edge, alpha = .08f) }
                }
                LauncherTheme.XPERIA -> {
                    drawLine(ink, Offset(0f, edge), Offset(size.width, edge), edge, alpha = .18f)
                    drawLine(accent, Offset(size.width * (.65f - p * .2f), size.height - edge),
                        Offset(size.width, size.height - edge), edge, alpha = .3f)
                }
                else -> Unit
            }
        }
    }
}
