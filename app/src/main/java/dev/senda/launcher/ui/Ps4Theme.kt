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

/** Original console light-wave scene; lifecycle/frame-clock driven, state read only in draw. */
@Composable
internal fun Ps4Wallpaper(reduced: Boolean, modifier: Modifier = Modifier) {
    val phase = rememberHistoricalPhase(LauncherTheme.PS4, true, reduced)
    Box(modifier.drawWithCache {
        val backdrop = Brush.linearGradient(listOf(Color(0xFF003087), Color(0xFF00164B), Color(0xFF000C29)), end=Offset(size.width*.55f,size.height))
        val wash = Brush.linearGradient(listOf(Color(0x000070D1), Color(0x990070D1), Color(0x330043BE)), end=Offset(size.width,size.height))
        val glow = Brush.radialGradient(listOf(Color(0xFF0069EF).copy(alpha=.48f),Color.Transparent),center=Offset(size.width*.9f,size.height*.22f),radius=size.height*.65f)
        val paths = List(6) { Path() }
        val contour = Stroke(.8.dp.toPx())
        onDrawBehind {
            drawRect(backdrop)
            drawRect(glow)
            val time = phase.value * (2 * PI).toFloat()
            paths.forEachIndexed { index, path ->
                val lift = sin(time + index*.48f)*size.height*.026f
                val base = size.height*(.82f+index*.022f)
                val crest = size.height*(.18f+index*.031f)
                val thickness = size.height*(.025f+index*.004f)
                path.reset(); path.moveTo(-size.width*.12f,base+lift)
                path.cubicTo(size.width*.68f,base+size.height*.22f-lift,
                    size.width*.54f,crest+size.height*.07f+lift,size.width*1.12f,crest-lift)
                drawPath(path,Color(0xFF43BEFF).copy(alpha=.17f+index*.025f),style=contour)
                path.cubicTo(size.width*.58f,crest+thickness+lift,
                    size.width*.72f,base+size.height*.22f+thickness-lift,-size.width*.12f,base+thickness+lift)
                path.close()
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
