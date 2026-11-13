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

/** The outline tracks the selected row's bounds directly, including during a fling. */
@Composable
internal fun Modifier.ps4TravelFrame(list: LazyListState, enabled: Boolean, reduced: Boolean): Modifier {
    if (!enabled || LocalAppearance.current.highContrast || LocalThemePreview.current) return this
    val focus = LocalPs4FocusedKey.current
    val arrival = remember(list) { Animatable(0f) }
    val quiet = reduced || LocalSystemPowerSaving.current
    val colors = MaterialTheme.colorScheme
    LaunchedEffect(list, focus, quiet) {
        snapshotFlow { if (list.isScrollInProgress) null else focus.value }.collectLatest { settledKey ->
            arrival.snapTo(0f)
            if (!quiet && settledKey != null) {
                arrival.snapTo(1f)
                arrival.animateTo(0f, tween(240, easing = Ps4Motion.easing))
            }
        }
    }
    return clipToBounds().drawWithCache {
        val outer = Stroke(1.5.dp.toPx())
        val halo = Stroke(6.dp.toPx())
        val corner = CornerRadius(3.dp.toPx())
        onDrawWithContent {
            drawContent()
            val key = focus.value ?: return@onDrawWithContent
            val layout = list.layoutInfo
            val item = layout.visibleItemsInfo.firstOrNull { it.key == key } ?: return@onDrawWithContent
            val inset = 2.dp.toPx()
            val y = item.offset - layout.viewportStartOffset + inset
            if (size.width > inset * 4 && item.size > inset * 4) {
                val topLeft = Offset(inset, y)
                val frameSize = Size(size.width - 2 * inset, item.size - 2 * inset)
                val flash = if (quiet || list.isScrollInProgress) 0f else arrival.value
                drawRoundRect(colors.primary.copy(alpha = .12f + .16f * flash), topLeft, frameSize, corner, style = halo)
                drawRoundRect(colors.primary, topLeft, frameSize, corner, style = outer)
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
