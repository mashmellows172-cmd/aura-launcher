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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.text.font.FontWeight
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
        val backdrop = Brush.verticalGradient(listOf(Color(0xFF003791), Color(0xFF002266), Color(0xFF040A18)))
        val wash = Brush.verticalGradient(listOf(Color(0xFF0070D1).copy(alpha = .18f), Color.Transparent))
        val paths = List(5) { Path() }
        val contour = Stroke(1.dp.toPx())
        val glowContour = Stroke(2.2.dp.toPx())
        onDrawBehind {
            drawRect(backdrop)
            val time = phase.value * (2 * PI).toFloat()
            // Ambient console light-wave ribbons
            paths.forEachIndexed { index, path ->
                val lift = sin(time + index * .52f) * size.height * .038f
                val y = size.height * (.38f + index * .042f)
                path.reset()
                path.moveTo(-size.width * .2f, y + lift)
                path.cubicTo(
                    size.width * .28f, y - size.height * .14f - lift,
                    size.width * .58f, y + size.height * .18f + lift,
                    size.width * 1.2f, y - size.height * .07f + lift
                )
                // Soft neon aura
                drawPath(path, Color(0xFF0072CE).copy(alpha = .08f + index * .025f), style = glowContour)
                // Crisp light ribbon
                drawPath(path, Color(0xFFA9EAFF).copy(alpha = .14f + index * .04f), style = contour)
                path.lineTo(size.width * 1.2f, size.height)
                path.lineTo(-size.width * .2f, size.height)
                path.close()
                drawPath(path, wash)
            }
            // Ambient console dust motes drifting upward
            repeat(10) { i ->
                val t = (time * .22f + i * .10f) % 1f
                val mx = (size.width * (i * .095f + sin(time * .4f + i) * .035f)).coerceIn(8.dp.toPx(), size.width - 8.dp.toPx())
                val my = size.height * (1f - t)
                val alpha = (sin(t * PI.toFloat()) * .20f).coerceAtLeast(0f)
                drawCircle(Color(0xFFC7ECFF).copy(alpha = alpha), (1 + i % 2).dp.toPx(), Offset(mx, my))
            }
            // Sparse controller geometry drifts with the same clock, no per-particle timer.
            repeat(8) { index ->
                val x = size.width * (index * .137f + sin(time + index) * .025f).coerceIn(.04f, .96f)
                val y = size.height * (.12f + index * .096f) + sin(time + index) * 8.dp.toPx()
                val r = (3 + index % 3).dp.toPx()
                val ink = Color.White.copy(alpha = .09f)
                when (index % 4) {
                    0 -> drawCircle(ink, r, Offset(x, y), style = contour)
                    1 -> drawRect(ink, Offset(x - r, y - r), Size(r * 2, r * 2), style = contour)
                    2 -> {
                        drawLine(ink, Offset(x - r, y - r), Offset(x + r, y + r), contour.width)
                        drawLine(ink, Offset(x + r, y - r), Offset(x - r, y + r), contour.width)
                    }
                    else -> {
                        val triangle = paths[0]
                        triangle.reset()
                        triangle.moveTo(x, y - r)
                        triangle.lineTo(x + r, y + r)
                        triangle.lineTo(x - r, y + r)
                        triangle.close()
                        drawPath(triangle, ink, style = contour)
                    }
                }
            }
        }
    })
}

/** Controller-button motif, decorative; the surrounding avatar retains its actual actions. */
@Composable
internal fun Ps4Mark() {
    Canvas(Modifier.size(42.dp)) {
        val w = size.width
        val stroke = Stroke(1.5.dp.toPx())
        val ink = Color.White
        drawCircle(ink, w * .12f, Offset(w * .72f, w * .48f), style = stroke)
        drawRect(ink, Offset(w * .13f, w * .36f), Size(w * .23f, w * .23f), style = stroke)
        val triangle = Path().apply { moveTo(w * .5f, w * .10f); lineTo(w * .62f, w * .30f); lineTo(w * .38f, w * .30f); close() }
        drawPath(triangle, ink, style = stroke)
        drawLine(ink, Offset(w * .40f, w * .69f), Offset(w * .60f, w * .89f), stroke.width)
        drawLine(ink, Offset(w * .60f, w * .69f), Offset(w * .40f, w * .89f), stroke.width)
    }
}

/** Procedural PS4 blue beveled tile for newly installed or unmapped applications. */
@Composable
internal fun Ps4FallbackArtwork(label: String, modifier: Modifier = Modifier) {
    val initial = label.firstOrNull()?.uppercaseChar()?.toString() ?: "•"
    Box(
        modifier = modifier.drawWithCache {
            val plateBrush = Brush.verticalGradient(listOf(Color(0xFF003CA8), Color(0xFF002060), Color(0xFF060F26)))
            val rimBrush = Brush.verticalGradient(listOf(Color(0xFFA9EAFF).copy(alpha = .65f), Color(0xFF0070D1).copy(alpha = .25f)))
            val stroke = Stroke(1.dp.toPx())
            val corner = CornerRadius(4.dp.toPx())
            onDrawBehind {
                drawRoundRect(plateBrush, cornerRadius = corner)
                drawRoundRect(rimBrush, cornerRadius = corner, style = stroke)
            }
        },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color(0xFFF3FAFF),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

/** PlayStation 4 selection/focus highlight: subtle cyan/ice-blue glow and bevel contour. */
internal fun Modifier.ps4RowChrome(pressed: Boolean, focused: Boolean): Modifier = this.drawWithCache {
    val contour = Stroke(1.dp.toPx())
    val glowBrush = Brush.horizontalGradient(
        listOf(
            Color(0xFF0070D1).copy(alpha = if (pressed) .30f else .16f),
            Color(0xFF003087).copy(alpha = if (pressed) .15f else .06f),
            Color.Transparent
        )
    )
    val borderRim = if (focused) Color.White.copy(alpha = .85f)
    else if (pressed) Color(0xFFBDE8FF).copy(alpha = .55f)
    else Color.Transparent
    val corner = CornerRadius(4.dp.toPx())

    onDrawBehind {
        if (pressed || focused) {
            drawRoundRect(glowBrush, cornerRadius = corner)
            if (borderRim != Color.Transparent) {
                drawRoundRect(borderRim, cornerRadius = corner, style = contour)
            }
        }
    }
}
