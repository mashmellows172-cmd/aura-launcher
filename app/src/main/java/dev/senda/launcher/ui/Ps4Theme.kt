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

/** Share the nearest app key across rows; only the two changed rows recompose. */
internal val LocalPs4FocusedKey = staticCompositionLocalOf<State<Any?>> { mutableStateOf(null) }

@Composable
internal fun rememberPs4FocusedKey(list: androidx.compose.foundation.lazy.LazyListState, enabled: Boolean): State<Any?> =
    remember(list, enabled) {
        derivedStateOf {
            if (!enabled) null else {
                val layout = list.layoutInfo
                val center = layout.viewportStartOffset + (layout.viewportEndOffset - layout.viewportStartOffset) * .42f
                var nearest: Any? = null
                var distance = Float.MAX_VALUE
                for (item in layout.visibleItemsInfo) {
                    if (item.contentType != "app") continue
                    val candidate = kotlin.math.abs(item.offset + item.size * .5f - center)
                    if (candidate < distance) { distance = candidate; nearest = item.key }
                }
                nearest
            }
        }
    }

/** Finite focus cue. No blur, idle oscillator or changes to the row's hit target. */
internal fun Modifier.ps4FocusFrame(progress: () -> Float): Modifier = drawWithCache {
    val corner = CornerRadius(3.dp.toPx())
    val fill = Brush.horizontalGradient(listOf(Color(0xFF0070D1).copy(alpha=.48f), Color(0xFF003087).copy(alpha=.12f)))
    val glows = (4 downTo 1).map { it to Stroke((it * 3).dp.toPx()) }
    onDrawBehind {
        val p = progress().coerceIn(0f,1f)
        if (p > .001f) {
            drawRoundRect(fill, cornerRadius=corner, alpha=p)
            glows.forEach { (index, stroke) ->
                drawRoundRect(Color(0xFF008CFF).copy(alpha=p*.13f/index), cornerRadius=corner, style=stroke)
            }
            drawRoundRect(Color(0xFF42C8FF).copy(alpha=p), cornerRadius=corner, style=Stroke(3.dp.toPx()))
            drawRoundRect(Color.White.copy(alpha=p), cornerRadius=corner, style=Stroke(1.dp.toPx()))
        }
    }
}

/** Controller-button motif, decorative; the surrounding avatar retains its actual actions. */
@Composable
internal fun Ps4Mark() {
    Canvas(Modifier.size(42.dp)) {
        val w=size.width; val stroke=Stroke(1.5.dp.toPx()); val ink=Color.White
        drawCircle(ink,w*.12f,Offset(w*.72f,w*.48f),style=stroke)
        drawRect(ink,Offset(w*.13f,w*.36f),Size(w*.23f,w*.23f),style=stroke)
        val triangle=Path().apply { moveTo(w*.5f,w*.10f);lineTo(w*.62f,w*.30f);lineTo(w*.38f,w*.30f);close() }
        drawPath(triangle,ink,style=stroke)
        drawLine(ink,Offset(w*.40f,w*.69f),Offset(w*.60f,w*.89f),stroke.width)
        drawLine(ink,Offset(w*.60f,w*.69f),Offset(w*.40f,w*.89f),stroke.width)
    }
}
