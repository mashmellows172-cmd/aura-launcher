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

/** Original console light-wave scene; lifecycle/frame-clock driven, state read only in draw. */
@Composable
internal fun Ps4Wallpaper(reduced: Boolean, modifier: Modifier = Modifier) {
    val phase = rememberHistoricalPhase(LauncherTheme.PS4, true, reduced)
    Box(modifier.drawWithCache {
        val backdrop = Brush.verticalGradient(listOf(Color(0xFF003087), Color(0xFF003087), Color(0xFF060F26)))
        val wash = Brush.verticalGradient(listOf(Color(0xFF0070D1).copy(alpha = .16f), Color.Transparent))
        val paths = List(4) { Path() }
        val contour = Stroke(.8.dp.toPx())
        onDrawBehind {
            drawRect(backdrop)
            val time = phase.value * (2 * PI).toFloat()
            paths.forEachIndexed { index, path ->
                val lift = sin(time + index * .6f) * size.height * .035f
                val y = size.height * (.40f + index * .045f)
                path.reset(); path.moveTo(-size.width * .2f, y + lift)
                path.cubicTo(size.width * .28f, y - size.height * .15f - lift,
                    size.width * .59f, y + size.height * .19f + lift,
                    size.width * 1.2f, y - size.height * .08f + lift)
                drawPath(path, Color(0xFFA9EAFF).copy(alpha = .12f + index * .035f), style = contour)
                path.lineTo(size.width * 1.2f, size.height); path.lineTo(-size.width * .2f, size.height); path.close()
                drawPath(path, wash)
            }
            // Sparse controller geometry drifts with the same clock, no per-particle timer.
            repeat(8) { index ->
                val x = size.width * (index * .137f + sin(time + index) * .025f).coerceIn(.02f,.98f)
                val y = size.height * (.12f + index * .096f) + sin(time + index) * 8.dp.toPx()
                val r = (3 + index % 3).dp.toPx()
                val ink = Color.White.copy(alpha = .08f)
                when (index % 4) {
                    0 -> drawCircle(ink, r, Offset(x,y), style = contour)
                    1 -> drawRect(ink, Offset(x-r,y-r), Size(r*2,r*2), style = contour)
                    2 -> { drawLine(ink, Offset(x-r,y-r), Offset(x+r,y+r), contour.width); drawLine(ink, Offset(x+r,y-r), Offset(x-r,y+r), contour.width) }
                    else -> { val triangle = paths[0]; triangle.reset(); triangle.moveTo(x,y-r); triangle.lineTo(x+r,y+r); triangle.lineTo(x-r,y+r); triangle.close(); drawPath(triangle, ink, style = contour) }
                }
            }
        }
    })
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
