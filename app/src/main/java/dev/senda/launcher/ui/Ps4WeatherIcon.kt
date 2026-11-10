package dev.senda.launcher.ui

import android.animation.ValueAnimator
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.WeatherCondition
import dev.senda.launcher.data.WeatherMotionMode
import dev.senda.launcher.data.WeatherMotionSettings
import dev.senda.launcher.ui.theme.rememberClockChange
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin

@Composable
internal fun Ps4WeatherIcon(
    condition: WeatherCondition,
    settings: WeatherMotionSettings,
    reduced: Boolean,
    modifier: Modifier = Modifier,
) {
    val still = reduced || settings.mode == WeatherMotionMode.OFF || !ValueAnimator.areAnimatorsEnabled()
    if (still) {
        WeatherEmblem(condition, settings, true, modifier)
    } else {
        Crossfade(condition, modifier, animationSpec = tween(340), label = "Cambio de clima PS4") {
            WeatherEmblem(it, settings, false, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun WeatherEmblem(
    condition: WeatherCondition,
    settings: WeatherMotionSettings,
    still: Boolean,
    modifier: Modifier,
) {
    val phase = rememberClockChange(
        condition.name, LauncherTheme.PS4, still,
        durationMs = settings.durationMs.coerceIn(3000, 5000), playOnEntry = true,
    )
    val intensity = settings.intensity.coerceIn(20, 100) / 100f
    Spacer(modifier.drawWithCache {
        val unit = min(size.width, size.height) / 64f
        val glow = Brush.radialGradient(
            listOf(Color(0xFF3296E8), Color(0x553992DD), Color.Transparent),
            center = Offset(32f, 33f), radius = 31f,
        )
        onDrawBehind {
            // Read phase during drawing so the surrounding header and list do not recompose.
            val progress = phase.value
            val envelope = (sin(progress * Math.PI).toFloat() * 2f).coerceIn(0f, 1f) * intensity
            translate((size.width - 64f * unit) / 2f, (size.height - 64f * unit) / 2f) {
                scale(unit, unit, Offset.Zero) {
                    clipRect(0f, 0f, 64f, 64f) {
                        drawCircle(glow, 31f, Offset(32f, 33f), alpha = .30f + envelope * .22f)
                        drawWeatherEmblem(condition, progress, envelope)
                    }
                }
            }
        }
    })
}

private fun DrawScope.drawWeatherEmblem(condition: WeatherCondition, progress: Float, motion: Float) {
    when (condition) {
        WeatherCondition.SUN -> {
            val center = Offset(32f, 32f)
            drawCircle(ice.copy(alpha = .32f + .35f * motion), 27f + 2f * motion, center, style = Stroke(1.2f))
            rotate(18f * motion, center) { emblem(sunRays) }
            drawCircle(shadow, 14.5f, center + Offset(0f, 1f))
            drawCircle(sunFace, 13.2f, center)
            drawCircle(ice, 13.2f, center, style = Stroke(.9f))
        }
        WeatherCondition.CLOUDS -> {
            val glide = sin(progress * 2 * Math.PI).toFloat() * motion
            translate(4f * glide, -2.2f * motion) { cloud(progress, motion) }
        }
        WeatherCondition.PARTLY_CLOUDY, WeatherCondition.CLOUDY_NIGHT -> {
            translate(-8f - 5f * motion, -9f - 3f * motion) {
                scale(.72f, .72f, Offset(32f, 32f)) {
                    if (condition == WeatherCondition.PARTLY_CLOUDY) {
                        rotate(24f * motion, Offset(32f, 32f)) { emblem(sunRays) }
                        drawCircle(sunFace, 13.2f, Offset(32f, 32f))
                    } else {
                        emblem(moon)
                        emblem(stars.first(), alpha = .75f + .25f * motion)
                    }
                }
            }
            translate(5f + 3f * motion, 9f) {
                scale(.87f, .87f, Offset(32f, 32f)) { cloud(progress, motion) }
            }
        }
        WeatherCondition.DRIZZLE, WeatherCondition.RAIN, WeatherCondition.DOWNPOUR,
        WeatherCondition.SLEET, WeatherCondition.SNOW, WeatherCondition.HAIL, WeatherCondition.STORM -> {
            translate(0f, -8f) { cloud(progress, motion, dark = condition == WeatherCondition.STORM || condition == WeatherCondition.DOWNPOUR) }
            when (condition) {
                WeatherCondition.RAIN, WeatherCondition.DOWNPOUR -> {
                    val heavy = condition == WeatherCondition.DOWNPOUR
                    val count = if (heavy) 8 else 6
                    val speed = if (heavy) 3f else 2f
                    repeat(count) { index ->
                        val travel = (progress * speed + index * .38f) % 1f
                        val start = Offset(13f + index * (39f / (count - 1)), 40f + index % 2 * 6f)
                        val length = if (heavy) 12f else 8f
                        raindrop(start, .88f - .65f * motion, length)
                        val visibility = (min(travel, 1f - travel) * 6f).coerceIn(0f, 1f)
                        if (motion > 0f) {
                            raindrop(start + Offset(-5f, 6f) * travel, visibility * motion, length)
                        }
                    }
                }
                WeatherCondition.DRIZZLE -> repeat(7) { index ->
                    val ripple = sin((progress * 2f - index * .18f) * Math.PI).toFloat() * motion
                    val point = Offset(16f + index * 5.2f, 46f + index % 2 * 7f + ripple * 4f)
                    drawCircle(shadow, 2.2f, point)
                    drawCircle(ice.copy(alpha = .77f + ripple * .23f), 1.45f, point)
                }
                WeatherCondition.SNOW -> {
                    snowflake(24f - 3f * motion, 49f + 3f * motion, 1f, -24f * motion)
                    snowflake(43f + 3f * motion, 46f + 7f * motion, .72f, 34f * motion)
                    drawCircle(ice.copy(alpha = .75f), 1.4f, Offset(13f, 53f + 4f * motion))
                    drawCircle(ice.copy(alpha = .75f), 1.2f, Offset(51f, 56f + 2f * motion))
                }
                WeatherCondition.SLEET -> {
                    snowflake(39f + 3f * motion, 49f + 3f * motion, .78f, 32f * motion)
                    repeat(3) { index ->
                        val slide = (sin((progress * 2f - index * .12f) * Math.PI).toFloat() + 1f) * motion
                        raindrop(Offset(15f + index * 6f - slide, 42f + index % 2 * 5f + 4f * slide), .89f, 7f)
                    }
                }
                WeatherCondition.HAIL -> repeat(5) { index ->
                    val impact = abs(sin((progress * 2f - index * .16f) * Math.PI).toFloat())
                    translate(14f + index * 8.6f, 49f + index % 2 * 6f - 8f * impact * motion) {
                        rotate(index * 17f + 38f * motion, Offset.Zero) { emblem(icePellet) }
                    }
                }
                else -> {
                    val charge = motion * motion
                    drawCircle(boltGlow, 20f, Offset(34f, 46f), alpha = .20f + .65f * charge)
                    emblem(bolt)
                }
            }
        }
        WeatherCondition.WIND -> windTrails.forEachIndexed { index, trail ->
            val gust = sin((progress * 2f - index * .13f) * Math.PI).toFloat() * motion
            translate(gust * 5f, 0f) {
                drawPath(trail, shadow, style = Stroke(5f, cap = StrokeCap.Round))
                drawPath(trail, stream, style = Stroke(3.2f, cap = StrokeCap.Round))
            }
        }
        WeatherCondition.FOG -> fogBands.forEachIndexed { index, band ->
            val drift = sin((progress * 2f - index * .18f) * Math.PI).toFloat() * motion
            translate(drift * 4f, 0f) {
                val opacity = .95f - abs(index - 2) * .14f
                drawPath(band, shadow, style = Stroke(5.5f, cap = StrokeCap.Round))
                drawPath(band, mist, alpha = opacity, style = Stroke(3.6f, cap = StrokeCap.Round))
            }
        }
        WeatherCondition.NIGHT -> {
            translate(0f, -1.5f * motion) { emblem(moon) }
            stars.forEachIndexed { index, star ->
                val light = sin((progress * 2f - index * .31f) * Math.PI).toFloat()
                emblem(star, alpha = .83f + .16f * light * motion)
            }
        }
        WeatherCondition.AUTO, WeatherCondition.AMBIENT -> {
            val frame = Size(42f, 33f)
            val origin = Offset(11f, 15.5f)
            val corners = CornerRadius(9f)
            drawRoundRect(shadow, origin, frame, corners)
            drawRoundRect(rim, origin, frame, corners, style = Stroke(1.8f))
            repeat(3) { index ->
                val signal = sin((progress * 2f - index * .19f) * Math.PI).toFloat() * motion
                drawCircle(ice.copy(alpha = .83f + signal * .17f), 2.3f + signal * .7f,
                    Offset(22f + 10f * index, 32f))
            }
        }
    }
}

private fun DrawScope.raindrop(start: Offset, alpha: Float, length: Float) {
    val end = start + Offset(-.43f * length, length)
    drawLine(shadow.copy(alpha = shadow.alpha * alpha), start, end, 3.5f, StrokeCap.Round)
    drawLine(ice.copy(alpha = alpha), start, end, 1.8f, StrokeCap.Round)
}

private fun DrawScope.cloud(progress: Float, motion: Float, dark: Boolean = false) {
    emblem(cloudOutline, if (dark) stormFace else cloudFace)
    drawPath(cloudOutline, rim, alpha = .74f, style = Stroke(2.8f))
    if (motion > 0f) {
        clipPath(cloudOutline) {
            translate(-24f + progress * 92f, 0f) {
                drawRect(reflection, Offset(0f, 10f), Size(18f, 39f), alpha = .38f * motion)
            }
        }
    }
}

private fun DrawScope.emblem(path: Path, face: Brush = silver, alpha: Float = 1f) {
    translate(0f, 1f) { drawPath(path, shadow, alpha, style = Stroke(2.8f)) }
    drawPath(path, face, alpha)
    drawPath(path, rim, alpha, style = Stroke(1.1f))
}

private fun DrawScope.snowflake(x: Float, y: Float, zoom: Float, angle: Float) {
    translate(x, y) {
        scale(zoom, zoom, Offset.Zero) {
            rotate(angle, Offset.Zero) {
                drawPath(flake, shadow, style = Stroke(3.8f, cap = StrokeCap.Round))
                drawPath(flake, ice, style = Stroke(2f, cap = StrokeCap.Round))
            }
        }
    }
}

private val ice = Color(0xFFE2F4FF)
private val shadow = Color(0xAD10294B)
private val silver = Brush.verticalGradient(
    0f to Color.White, .52f to Color(0xFFEDF6FF), 1f to Color(0xFF91AED1), endY = 60f,
)
private val rim = Brush.verticalGradient(
    listOf(Color.White, Color(0xFF93AFD1), Color(0xFFD3EDFF)), endY = 54f,
)
private val sunFace = Brush.radialGradient(
    listOf(Color.White, Color(0xFFF3F8FF), Color(0xFFAAC6E4)), Offset(29f, 27f), 18f,
)
private val cloudFace = Brush.verticalGradient(
    .10f to Color.White, .54f to Color(0xFFF5F9FF), .93f to Color(0xFF9AB3D0), endY = 49f,
)
private val stormFace = Brush.verticalGradient(
    listOf(Color(0xFFC6D7ED), Color(0xFF849DBD), Color(0xFF4E6B93)), startY = 15f, endY = 45f,
)
private val boltGlow = Brush.radialGradient(
    listOf(Color(0xFF63CEFF), Color.Transparent), Offset(34f, 46f), 20f,
)
private val stream = Brush.horizontalGradient(listOf(Color(0xFF92ACCB), ice, Color.White), startX = 7f, endX = 53f)
private val mist = Brush.horizontalGradient(
    listOf(Color(0xFF8CA6C5), Color(0xFFDBE9F7), Color(0xFFA3BCD5)), startX = 6f, endX = 58f,
)

private val reflection = Brush.horizontalGradient(
    listOf(Color.Transparent, Color.White, Color.Transparent), startX = 0f, endX = 18f,
)
private val cloudOutline = Path().apply {
    moveTo(17f, 46f)
    cubicTo(2f, 46f, 3f, 26f, 17f, 27f)
    cubicTo(19f, 10f, 43f, 9f, 47f, 27f)
    cubicTo(62f, 24f, 63f, 46f, 49f, 46f)
    close()
}
private val icePellet = Path().apply {
    moveTo(0f, -3.7f)
    lineTo(3.7f, 0f)
    lineTo(0f, 3.7f)
    lineTo(-3.7f, 0f)
    close()
}
private val bolt = Path().apply {
    moveTo(33f, 32f); lineTo(44f, 32f); lineTo(37f, 43f)
    lineTo(47f, 43f); lineTo(28f, 61f); lineTo(33f, 48f); lineTo(23f, 48f)
    close()
}
private val moon = Path().apply {
    moveTo(27f, 9f)
    cubicTo(4f, 14f, 3f, 45f, 23f, 55f)
    cubicTo(35f, 62f, 48f, 56f, 54f, 46f)
    cubicTo(29f, 54f, 14f, 28f, 27f, 9f)
    close()
}
private val sunRays = Path().apply {
    repeat(8) { index ->
        val angle = index * Math.PI / 4
        val x = kotlin.math.cos(angle).toFloat()
        val y = sin(angle).toFloat()
        moveTo(32f + 25f * x, 32f + 25f * y)
        lineTo(32f + 17f * x - 3.6f * y, 32f + 17f * y + 3.6f * x)
        lineTo(32f + 17f * x + 3.6f * y, 32f + 17f * y - 3.6f * x)
        close()
    }
}
private val flake = Path().apply {
    repeat(6) { index ->
        val angle = Math.PI * index / 3
        val x = kotlin.math.cos(angle).toFloat()
        val y = sin(angle).toFloat()
        moveTo(0f, 0f); lineTo(x * 10f, y * 10f)
        moveTo(8f * x - 3f * y, 8f * y + 3f * x)
        lineTo(5f * x, 5f * y)
        lineTo(8f * x + 3f * y, 8f * y - 3f * x)
    }
}
private val windTrails = listOf(
    Path().apply {
        moveTo(10f, 23f); lineTo(36f, 23f)
        cubicTo(45f, 23f, 45f, 10f, 37f, 10f)
        cubicTo(33f, 10f, 31f, 12f, 31f, 16f)
    },
    Path().apply {
        moveTo(6f, 33f); lineTo(49f, 33f)
        cubicTo(58f, 33f, 58f, 21f, 51f, 21f)
        cubicTo(47f, 21f, 45f, 23f, 45f, 26f)
    },
    Path().apply {
        moveTo(12f, 43f); lineTo(39f, 43f)
        cubicTo(48f, 43f, 48f, 55f, 40f, 55f)
        cubicTo(36f, 55f, 34f, 53f, 34f, 50f)
    },
)
private val fogBands = List(5) { index ->
    val y = 16f + index * 8f
    Path().apply {
        moveTo(8f, y)
        cubicTo(28f, y - 7f, 32f, y + 7f, 56f, y)
    }
}
private val stars = listOf(
    sparkle(41f, 18f, 6.6f),
    sparkle(54f, 11f, 3.8f),
    sparkle(52f, 32f, 4.5f),
)

private fun sparkle(x: Float, y: Float, radius: Float) = Path().apply {
    val core = radius * .26f
    moveTo(x, y - radius); lineTo(x + core, y - core)
    lineTo(x + radius, y); lineTo(x + core, y + core)
    lineTo(x, y + radius); lineTo(x - core, y + core)
    lineTo(x - radius, y); lineTo(x - core, y - core)
    close()
}
