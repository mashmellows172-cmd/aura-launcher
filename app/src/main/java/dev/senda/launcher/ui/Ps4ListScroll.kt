package dev.senda.launcher.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.data.Ps4ListProfile
import dev.senda.launcher.data.VisualSettings
import kotlin.math.abs

@Composable
internal fun Modifier.ps4ScrollDecoration(
    list: LazyListState,
    settings: VisualSettings,
    console: Boolean,
    tint: State<Color>? = null,
): Modifier {
    val reduced = listMotionReduced(settings)
    val landing = settings.motion == "LANDING_FOCUS"
    val profile = if (console) Ps4ListProfile.fromId(settings.motion) else null
    val decorated = profile?.usesScrollInk == true
    val result = ps4TravelFrame(list, console || landing && !reduced,
        reduced || settings.motion == "NONE" || decorated, tint,
        landing = landing, intensity = settings.scrollIntensity)
    return if (decorated && !reduced) result.ps4ScrollInk(list, profile, settings.scrollIntensity) else result
}

/** Paint one selected profile beneath the visible rows, alongside the native scrolling. */
@Composable
private fun Modifier.ps4ScrollInk(list: LazyListState, profile: Ps4ListProfile?, strength: Int): Modifier {
    val motion = LocalMotionController.current ?: return this
    val constrained = LocalDecorationConstrained.current
    val color = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface
    val tileWidth = (LocalAppearance.current.iconSize + 20).dp
    val intensity = strength.coerceIn(10, 100) / 100f
    return clipToBounds().drawWithCache {
        val w = size.width
        val h = size.height
        val rtl = layoutDirection == LayoutDirection.Rtl
        val edge = if (rtl) w - 3.dp.toPx() else 3.dp.toPx()
        val tiles = tileWidth.toPx().coerceAtMost(w * .28f)
        val tileEdge = if (rtl) w - tiles else tiles
        val hairline = 1.dp.toPx()
        val glass = Brush.linearGradient(
            0f to Color.Transparent, .38f to color.copy(alpha = .12f),
            .53f to color.copy(alpha = .44f), .62f to color.copy(alpha = .16f), 1f to Color.Transparent,
            start = Offset(w * .66f, 0f), end = Offset(w * .34f, h * .46f + 1f),
        )
        val railHeight = h * .38f
        val rail = Brush.verticalGradient(
            0f to Color.Transparent, .52f to color, 1f to Color.Transparent,
            endY = railHeight + 1f,
        )
        val ribbon = Path().apply {
            moveTo(-w * .08f, h * .60f)
            cubicTo(w * .32f, h * .72f, w * .71f, h * .32f, w * 1.08f, h * .43f)
            cubicTo(w * .72f, h * .50f, w * .36f, h * .76f, -w * .08f, h * .60f)
            close()
        }
        val silk = Brush.horizontalGradient(
            0f to color.copy(alpha = .05f), .56f to color, 1f to color.copy(alpha = .13f),
            endX = w + 1f,
        )
        val plate = Brush.horizontalGradient(
            0f to Color.Transparent, .24f to color, .82f to color.copy(alpha = .23f), 1f to Color.Transparent,
            endX = w + 1f,
        )
        val railWidth = 2.dp.toPx()
        val silkEdge = Stroke(hairline)
        val wakeGap = 4.dp.toPx()

        onDrawBehind {
            if (constrained.value) return@onDrawBehind
            val velocity = motion.velocity
            val speed = abs(velocity)
            val energy = (speed * 2.6f).coerceAtMost(1f) * intensity
            if (energy <= .01f || w <= 0f || h <= 0f) return@onDrawBehind

            when (profile) {
                Ps4ListProfile.CRYSTAL -> {
                    translate(top = h * (.27f + velocity * .34f)) {
                        drawRect(glass, size = Size(w, h * .52f), alpha = energy * .67f)
                    }
                }
                Ps4ListProfile.LIGHT_RAIL -> {
                    drawLine(color, Offset(edge, 0f), Offset(edge, h), hairline, alpha = energy * .16f)
                    translate(left = edge - railWidth / 2f, top = h * (.31f + velocity * .28f)) {
                        drawRect(rail, size = Size(railWidth, railHeight), alpha = energy * .88f)
                    }
                }
                Ps4ListProfile.RIBBONS -> {
                    translate(top = velocity * h * .20f) {
                        drawPath(ribbon, silk, alpha = energy * .20f)
                    }
                    translate(top = -h * .08f + velocity * h * .30f) {
                        drawPath(ribbon, silk, alpha = energy * .30f, style = silkEdge)
                    }
                }
                Ps4ListProfile.RELIEF, Ps4ListProfile.WAKE -> {
                    val layout = list.layoutInfo
                    val direction = if (velocity < 0f) -1f else 1f
                    for (row in layout.visibleItemsInfo) {
                        if (row.contentType != "app") continue
                        val top = (row.offset - layout.viewportStartOffset).toFloat()
                        val bottom = top + row.size
                        if (profile == Ps4ListProfile.RELIEF) {
                            val leading = if (direction < 0f) top else bottom - hairline
                            val trailing = if (direction < 0f) bottom - hairline else top
                            drawRect(plate, Offset(0f, leading), Size(w, hairline), alpha = energy * .39f)
                            drawRect(surface, Offset(0f, trailing), Size(w, hairline * 2f), alpha = energy * .78f)
                        } else {
                            val middle = (top + bottom) * .5f
                            val reach = row.size.toFloat().coerceAtMost(h) * (.16f + .37f * speed)
                            val end = middle - reach * direction
                            drawLine(color, Offset(tileEdge, middle), Offset(tileEdge, end),
                                hairline, alpha = energy * .65f)
                            val side = tileEdge + if (rtl) -wakeGap else wakeGap
                            drawLine(color, Offset(side, middle - reach * .23f * direction),
                                Offset(side, end), hairline, alpha = energy * .30f)
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}
