package dev.senda.launcher.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/** Original static scenes. Paths, gradients and seed geometry are rebuilt only with cache inputs. */
fun Modifier.skinPattern(skin: SkinStyle): Modifier = drawWithCache {
    val w = size.width
    val h = size.height
    val hairline = .7.dp.toPx()
    val commands = mutableListOf<DrawScope.() -> Unit>()
    fun line(a: Offset, b: Offset, color: Color, width: Float = hairline) {
        commands += { drawLine(color, a, b, width) }
    }
    fun circle(center: Offset, radius: Float, color: Color, stroke: Float = 0f) {
        if (stroke > 0f) {
            val style = Stroke(stroke)
            commands += { drawCircle(color, radius, center, style = style) }
        } else commands += { drawCircle(color, radius, center) }
    }
    fun path(path: Path, color: Color, stroke: Float = 0f) {
        if (stroke > 0f) {
            val style = Stroke(stroke)
            commands += { drawPath(path, color, style = style) }
        } else commands += { drawPath(path, color) }
    }
    val accent = skin.accent
    when (skin.pattern) {
        WallpaperPattern.DOTS -> {
            val step = 20.dp.toPx()
            for (x in 0..(w / step).toInt()) for (y in 0..(h / step).toInt())
                circle(Offset(x * step, y * step), 1.25.dp.toPx(), accent.copy(alpha = .19f))
        }
        WallpaperPattern.SENSE_LANDSCAPE -> {
            val sun = Offset(w * .80f, h * .16f)
            val glow = Brush.radialGradient(listOf(Color(0xFFE9C384).copy(alpha = .25f), Color.Transparent), sun, w * .75f)
            commands += { drawCircle(glow, w * .75f, sun) }
            // Layered valley silhouettes recall the era without embedding a manufacturer's wallpaper.
            repeat(4) { layer ->
                val y = h * (.61f + layer * .09f)
                val hill = Path().apply {
                    moveTo(0f, y)
                    cubicTo(w * .23f, y - h * .13f, w * .52f, y + h * .09f, w, y - h * .05f)
                    lineTo(w, h); lineTo(0f, h); close()
                }
                path(hill, Color(0xFF82A18A).copy(alpha = .07f + layer * .026f))
                val ridge = Path().apply { moveTo(0f, y); cubicTo(w * .23f, y - h * .13f, w * .52f, y + h * .09f, w, y - h * .05f) }
                path(ridge, Color(0xFFD0C7A1).copy(alpha = .07f), hairline)
            }
        }
        WallpaperPattern.NATURE -> {
            val head = Offset(w * .82f, h * .49f)
            path(Path().apply { moveTo(w * .95f, h); cubicTo(w * .68f, h * .79f, w * .72f, h * .64f, head.x, head.y) }, Color(0xFFCDE6B3).copy(alpha = .28f), 1.5.dp.toPx())
            repeat(24) { seed ->
                val angle = seed * Math.PI.toFloat() * 2 / 24
                val radius = w * (.14f + seed % 3 * .013f)
                val tip = head + Offset(cos(angle) * radius, sin(angle) * radius)
                line(head, tip, Color.White.copy(alpha = .17f))
                // Fine parachute tufts rather than identical solid dots.
                repeat(3) { ray ->
                    val spread = angle + (ray - 1) * .65f
                    line(tip, tip + Offset(cos(spread) * w * .017f, sin(spread) * w * .017f), Color.White.copy(alpha = .24f))
                }
            }
            repeat(3) { seed ->
                val tip = Offset(w * (.38f + seed * .10f), h * (.30f + seed * .035f))
                line(tip, tip + Offset(5.dp.toPx(), 8.dp.toPx()), Color.White.copy(alpha = .22f))
                line(tip + Offset(-4.dp.toPx(), -2.dp.toPx()), tip + Offset(5.dp.toPx(), 1.dp.toPx()), Color.White.copy(alpha = .24f))
            }
            for (ring in 1..4) {
                val origin = Offset(w * .08f - ring * w * .10f, h * .90f - ring * h * .018f)
                val bounds = Size(ring * w * .42f, ring * h * .06f)
                val stroke = Stroke(hairline)
                commands += { drawOval(accent.copy(alpha = .10f), origin, bounds, style = stroke) }
            }
        }
        WallpaperPattern.OPTIMUS_RIBBONS -> {
            // Paper swatches: rectilinear, warm, visibly separate from Flow's continuous curves.
            repeat(5) { tile ->
                val origin = Offset(w * (.40f + tile * .12f), h * (.60f + tile % 2 * .10f))
                val bounds = Size(w * .24f, h * .24f)
                val corner = CornerRadius(2.dp.toPx())
                val color = if (tile % 2 == 0) Color(0xFFE8BA75) else Color(0xFFB79674)
                commands += { drawRoundRect(color.copy(alpha = .11f + tile * .008f), origin, bounds, corner) }
                line(origin, origin + Offset(bounds.width, 0f), color.copy(alpha = .24f), 1.dp.toPx())
                repeat(3) { rule ->
                    val y = origin.y + 12.dp.toPx() + rule * 6.dp.toPx()
                    line(Offset(origin.x + 6.dp.toPx(), y), Offset(origin.x + bounds.width * .65f, y), color.copy(alpha = .10f))
                }
            }
        }
        WallpaperPattern.XPERIA_FLOW -> {
            repeat(7) { ribbon ->
                val offset = ribbon * h * .022f
                val wave = Path().apply {
                    moveTo(-w * .15f, h * .83f + offset)
                    cubicTo(w * .24f, h * .37f + offset, w * .63f, h * 1.05f - offset, w * 1.15f, h * .45f + offset)
                }
                val color = if (ribbon % 2 == 0) accent else Color(0xFF5C9DFF)
                path(wave, color.copy(alpha = .035f), (ribbon + 7).dp.toPx())
                path(wave, color.copy(alpha = .19f), (.5f + ribbon * .32f).dp.toPx())
            }
        }
        WallpaperPattern.RAZR_METAL -> {
            val gap = 9.dp.toPx()
            for (row in 0..(h / gap).toInt()) {
                val y = row * gap
                line(Offset(0f, y), Offset(w, y), Color.White.copy(alpha = .025f))
            }
            val crease = Path().apply {
                moveTo(w * .58f, h * .12f); lineTo(w * .90f, h * .28f); lineTo(w * .70f, h * .44f)
                lineTo(w * .94f, h * .59f); lineTo(w * .62f, h * .78f)
            }
            path(crease, Color.Black.copy(alpha = .24f), 4.dp.toPx())
            path(crease, accent.copy(alpha = .17f), hairline)
            circle(Offset(w * .82f, h * .30f), w * .24f, accent.copy(alpha = .05f), 8.dp.toPx())
        }
        WallpaperPattern.ORBS -> {
            circle(Offset(w * .98f, h * .12f), w * .35f, accent.copy(alpha = .10f))
            circle(Offset(w * .03f, h * .82f), w * .43f, accent.copy(alpha = .045f), 1.5.dp.toPx())
        }
        WallpaperPattern.DIAGONAL -> for (i in -10..30) {
            val x = i * 38.dp.toPx()
            line(Offset(x, 0f), Offset(x - h * .35f, h), accent.copy(alpha = .10f), 1.dp.toPx())
        }
        WallpaperPattern.WATER_RIPPLE -> for (ring in 1..9)
            circle(Offset(w * .50f, h * .39f), ring * w * .105f, accent.copy(alpha = .035f + ring % 3 * .012f), (if (ring % 3 == 0) 2 else 1).dp.toPx())
        WallpaperPattern.WAVE -> path(Path().apply {
            moveTo(0f, h * .68f); cubicTo(w * .32f, h * .45f, w * .68f, h * .86f, w, h * .57f)
            lineTo(w, h); lineTo(0f, h); close()
        }, accent.copy(alpha = .045f))
        WallpaperPattern.HORIZON -> {
            line(Offset(w * .52f, 0f), Offset(w * .52f, h), accent.copy(alpha = .16f), 1.dp.toPx())
            line(Offset(0f, h * .72f), Offset(w, h * .72f), accent.copy(alpha = .08f), 1.dp.toPx())
        }
        WallpaperPattern.NONE -> Unit
    }
    onDrawBehind { commands.forEach { it() } }
}
