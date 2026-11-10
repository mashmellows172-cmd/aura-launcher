package dev.senda.launcher.ui

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.LaunchableApp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.ui.theme.rememberHistoricalPhase
import kotlin.math.PI
import kotlin.math.sin

internal val LocalPs4FocusedKey = staticCompositionLocalOf<State<Any?>> { mutableStateOf(null) }
internal val LocalPs4InputFocus = staticCompositionLocalOf<MutableState<String?>?> { null }

@Composable
internal fun rememberPs4FocusedKey(list: LazyListState, enabled: Boolean, input: State<String?>): State<Any?> =
    remember(list, enabled, input) {
        derivedStateOf {
            if (!enabled) null else {
                val layout = list.layoutInfo
                val preferred = input.value
                val center = layout.viewportStartOffset + (layout.viewportEndOffset - layout.viewportStartOffset) * .42f
                var nearest: Any? = null
                var distance = Float.MAX_VALUE
                for (item in layout.visibleItemsInfo) {
                    if (item.contentType != "app") continue
                    if (item.key == preferred) { nearest = item.key; break }
                    val candidate = kotlin.math.abs(item.offset + item.size * .5f - center)
                    if (candidate < distance) { distance = candidate; nearest = item.key }
                }
                nearest
            }
        }
    }

/** Draw one outline for the list. Scroll position is read in draw, and a swipe snaps the travel offset. */
@Composable
internal fun Modifier.ps4TravelFrame(list: LazyListState, enabled: Boolean, reduced: Boolean): Modifier {
    if (!enabled || LocalAppearance.current.highContrast || LocalThemePreview.current) return this
    val focus = LocalPs4FocusedKey.current
    val delta = remember(list) { Animatable(0f) }
    val colors = MaterialTheme.colorScheme
    LaunchedEffect(list, focus, reduced) {
        var previous: Any? = null
        snapshotFlow { focus.value to list.isScrollInProgress }.collectLatest { (key, scrolling) ->
            val items = list.layoutInfo.visibleItemsInfo
            val from = items.firstOrNull { it.key == previous }
            val to = items.firstOrNull { it.key == key }
            val offset = if (!reduced && !scrolling && key != previous && from != null && to != null) {
                (from.offset - to.offset + delta.value).coerceIn(-to.size * 1.5f, to.size * 1.5f)
            } else 0f
            previous = key
            delta.snapTo(offset)
            if (offset != 0f) delta.animateTo(0f, tween(Ps4Motion.focusMs, easing = Ps4Motion.easing))
        }
    }
    return clipToBounds().drawWithCache {
        val outer = Stroke(1.5.dp.toPx())
        val inner = Stroke(.7.dp.toPx())
        val corner = CornerRadius(3.dp.toPx())
        onDrawWithContent {
            drawContent()
            val key = focus.value ?: return@onDrawWithContent
            val layout = list.layoutInfo
            val item = layout.visibleItemsInfo.firstOrNull { it.key == key } ?: return@onDrawWithContent
            val inset = 2.dp.toPx()
            val y = item.offset - layout.viewportStartOffset + delta.value + inset
            if (size.width > inset * 4 && item.size > inset * 4) {
                drawRoundRect(colors.primary, Offset(inset, y), Size(size.width - 2 * inset, item.size - 2 * inset), corner, style = outer)
                drawRoundRect(colors.onSurface.copy(alpha = .52f), Offset(2 * inset, y + inset),
                    Size(size.width - 4 * inset, item.size - 4 * inset), corner, style = inner)
            }
        }
    }
}

/** Controller-button motif, decorative; the surrounding avatar retains its actual actions. */
@Composable
internal fun Ps4Mark() {
    val ink = MaterialTheme.colorScheme.onSurface
    Canvas(Modifier.size(42.dp)) {
        val w=size.width; val stroke=Stroke(1.5.dp.toPx())
        drawCircle(ink,w*.12f,Offset(w*.72f,w*.48f),style=stroke)
        drawRect(ink,Offset(w*.13f,w*.36f),Size(w*.23f,w*.23f),style=stroke)
        val triangle=Path().apply { moveTo(w*.5f,w*.10f);lineTo(w*.62f,w*.30f);lineTo(w*.38f,w*.30f);close() }
        drawPath(triangle,ink,style=stroke)
        drawLine(ink,Offset(w*.40f,w*.69f),Offset(w*.60f,w*.89f),stroke.width)
        drawLine(ink,Offset(w*.60f,w*.69f),Offset(w*.40f,w*.89f),stroke.width)
    }
}

/** Shared finite timing for PS4 focus, overlays and media. */
internal object Ps4Motion {
    const val focusMs = 160
    const val enterMs = 220
    const val exitMs = 160
    val easing = androidx.compose.animation.core.CubicBezierEasing(.23f, 1f, .32f, 1f)
}
