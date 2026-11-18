package dev.senda.launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/** One clock and one weather host, moving between two destinations without replacing their tree. */
@Composable
internal fun Ps4FoldingHeader(
    progress: State<Float>?,
    modifier: Modifier = Modifier,
    top: @Composable () -> Unit,
    panel: @Composable () -> Unit,
    clock: @Composable () -> Unit,
    weather: @Composable () -> Unit,
    calendar: @Composable () -> Unit,
) {
    Layout(modifier = modifier.fillMaxWidth().clipToBounds(), content = {
        Box { top() }
        Box { panel() }
        Box { clock() }
        Box { weather() }
        Box { calendar() }
    }) { children, constraints ->
        val width = constraints.maxWidth
        val phase = (progress?.value ?: 0f).coerceIn(0f, 1f)
        val top = children[0].measure(Constraints.fixedWidth(width))
        val clock = children[2].measure(Constraints(maxWidth = (width * .42f).roundToInt()))
        val side = 48.dp.roundToPx()
        val gap = 6.dp.roundToPx()
        val weatherWidth = minOf(116.dp.roundToPx(),
            (width - side * 2 - (width * .42f * .76f).roundToInt() - gap).coerceAtLeast(48.dp.roundToPx()))
        val weather = children[3].measure(Constraints.fixedWidth(weatherWidth))
        val clockNaturalWidth = (clock.width / (1f - .24f * phase)).roundToInt()
        val calendarSpace = width - clockNaturalWidth - weather.width - gap * 4
        val stackCalendar = calendarSpace < side
        val calendarWidth = if (stackCalendar) (width - weather.width - gap * 3).coerceAtLeast(0)
            else calendarSpace
        val calendar = children[4].measure(Constraints(maxWidth = calendarWidth))
        val panelMin = if (stackCalendar) clock.height + calendar.height + gap * 2
            else maxOf(clock.height, weather.height, calendar.height)
        val panel = children[1].measure(Constraints.fixedWidth(width).copy(minHeight = panelMin))
        val panelHeight = (panel.height * (1f - phase)).roundToInt()
        layout(width, constraints.constrainHeight(top.height + panelHeight)) {
            top.placeRelative(0, 0)
            if (phase < .66f) panel.placeRelativeWithLayer(0, top.height) {
                compositingStrategy = CompositingStrategy.Offscreen
                alpha = (1f - phase * 1.5f).coerceIn(0f, 1f)
                scaleY = 1f - phase
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(.5f, 0f)
            }
            val expandedClockY = top.height + if (stackCalendar) gap.toFloat() else (panel.height - clock.height) / 2f
            val compactClockY = (top.height - clock.height) / 2f
            clock.placeRelative((gap + (side - gap) * phase).roundToInt(),
                (expandedClockY + (compactClockY - expandedClockY) * phase).roundToInt())
            if (phase < .32f) {
                val calendarX = if (stackCalendar) gap else clockNaturalWidth + gap * 2
                val calendarY = if (stackCalendar) top.height + clock.height + gap else
                    top.height + (panel.height - calendar.height) / 2
                calendar.placeRelativeWithLayer(calendarX, calendarY) {
                    alpha = (1f - phase / .32f).coerceIn(0f, 1f)
                    translationY = -4.dp.toPx() * phase
                }
            }
            val expandedWeatherX = width - weather.width - gap
            val compactWeatherX = width - side - weather.width
            val expandedWeatherY = top.height + (panel.height - weather.height) / 2f
            val compactWeatherY = (top.height - weather.height) / 2f
            weather.placeRelative(
                (expandedWeatherX + (compactWeatherX - expandedWeatherX) * phase).roundToInt(),
                (expandedWeatherY + (compactWeatherY - expandedWeatherY) * phase).roundToInt(),
            )
        }
    }
}

