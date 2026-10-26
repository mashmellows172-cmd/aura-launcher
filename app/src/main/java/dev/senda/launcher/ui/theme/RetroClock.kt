package dev.senda.launcher.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.ui.LocalMotionSettings

/** The parent measures available space; only the visible clock receives the clickable modifier. */
@Composable
fun RetroClock(time: String, modifier: Modifier = Modifier, compact: Boolean = false) {
    val theme = LocalRetroStyle.current.theme ?: return
    val reduced = LocalMotionSettings.current.reduceMotion || LocalAppearance.current.highContrast
    val scale = LocalDensity.current.fontScale
    // Splitting, rather than taking the first/last two chars, also accepts a one-digit hour.
    val fields = time.split(':', '：', '.', '٫')
    if (fields.size != 2 || fields.any { it.length !in 1..2 || !it.all(Char::isDigit) }) {
        Text(time, modifier.heightIn(min = 48.dp), color = MaterialTheme.colorScheme.onBackground)
        return
    }
    val hour = fields[0].padStart(2, '0')
    val minute = fields[1].padStart(2, '0')
    val change = rememberClockChange(time, theme, reduced)
    BoxWithConstraints {
        val availableWidth = maxWidth
        val font = minOf(if (compact) 26f else 48f, (maxWidth.value - 18f).coerceAtLeast(42f) / (2.9f * scale)).sp
        val target = modifier.heightIn(min = 48.dp)
        when (theme) {
            LauncherTheme.SENSE -> Row(target, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(hour, minute).forEach { digits ->
                    val leaf = rememberClockChange(digits, theme, reduced)
                    Box(Modifier.graphicsLayer {
                        rotationX = if (reduced) 0f else (1f - leaf.value) * -72f
                        transformOrigin = TransformOrigin(.5f, .5f)
                        cameraDistance = 12f * density
                        alpha = .35f + .65f * leaf.value
                    }.background(Brush.verticalGradient(listOf(Color(0xFFFAFAF4), Color(0xFFBBC1B8))), RoundedCornerShape(4.dp))
                        .drawWithContent {
                            drawContent()
                            val middle = size.height / 2
                            drawLine(Color(0xFF4F5852).copy(alpha = .7f), Offset(0f, middle), Offset(size.width, middle), 1.dp.toPx())
                            drawLine(Color.White.copy(alpha = .8f), Offset(0f, middle + 1.dp.toPx()), Offset(size.width, middle + 1.dp.toPx()), .6.dp.toPx())
                            drawRoundRect(Color(0xFF7B837A), cornerRadius = CornerRadius(4.dp.toPx()), style = Stroke(.7.dp.toPx()))
                        }.padding(horizontal = 7.dp, vertical = 2.dp)) {
                        Text(digits, style = MaterialTheme.typography.headlineLarge, color = Color(0xFF242A24), fontWeight = FontWeight.Bold, fontSize = font, maxLines = 1)
                    }
                }
            }
            LauncherTheme.TOUCHWIZ -> Row(target.drawWithContent {
                drawContent()
                val p = change.value
                drawOval(Color(0xFFB6E9F2).copy(alpha = (1f - p) * .35f), Offset(size.width * .05f, size.height * .65f), Size(size.width * (.55f + p * .4f), size.height * .3f), style = Stroke(1.dp.toPx()))
            }.padding(horizontal = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(hour, color = Color.White, fontSize = font, fontWeight = FontWeight.Normal, maxLines = 1)
                Text(":", color = Color(0xFFD8F1FF), fontSize = font, fontWeight = FontWeight.Light, maxLines = 1)
                Text(minute, color = Color.White, fontSize = font, fontWeight = FontWeight.Light, maxLines = 1)
            }
            LauncherTheme.OPTIMUS -> HistoricalPanel(theme, target.graphicsLayer {
                translationY = if (reduced) 0f else (1f - change.value) * 5.dp.toPx()
            }) {
                Row(Modifier.padding(horizontal = 10.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(hour, color = MaterialTheme.colorScheme.onSurface, fontSize = font, fontWeight = FontWeight.Medium, maxLines = 1)
                    Text(":$minute", color = MaterialTheme.colorScheme.primary, fontSize = font, fontWeight = FontWeight.Light, maxLines = 1)
                }
            }
            LauncherTheme.XPERIA -> Row(target.graphicsLayer {
                translationX = if (reduced) 0f else (1f - change.value) * 8.dp.toPx()
                alpha = .6f + .4f * change.value
            }.drawWithContent {
                drawContent()
                drawLine(Color(0xFFC8C2F5).copy(alpha = .3f), Offset(4.dp.toPx(), size.height - 1.dp.toPx()), Offset(size.width - 4.dp.toPx(), size.height - 1.dp.toPx()), .6.dp.toPx())
            }.padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("$hour:$minute", color = Color(0xFFF4F1FF), fontSize = font, fontWeight = FontWeight.ExtraLight,
                    letterSpacing = .9.sp, maxLines = 1)
            }
            LauncherTheme.RAZR -> HistoricalPanel(theme, target) {
                val ink = MaterialTheme.colorScheme.primary
                Canvas(Modifier.width(minOf(availableWidth, if (compact) 130.dp else 180.dp)).height(if (compact) 42.dp else 52.dp).padding(horizontal = 8.dp, vertical = 5.dp)) {
                    val normalized = "$hour$minute".take(4).map { Character.getNumericValue(it) }
                    val pitch = minOf(size.width / 100f, size.height / 38f)
                    val vertical = (size.height - 38f * pitch) / 2
                    val masks = intArrayOf(0x3F, 0x06, 0x5B, 0x4F, 0x66, 0x6D, 0x7D, 0x07, 0x7F, 0x6F)
                    val segments = arrayOf(floatArrayOf(3f, 1f, 17f, 1f), floatArrayOf(19f, 3f, 19f, 17f),
                        floatArrayOf(19f, 21f, 19f, 35f), floatArrayOf(3f, 37f, 17f, 37f), floatArrayOf(1f, 21f, 1f, 35f),
                        floatArrayOf(1f, 3f, 1f, 17f), floatArrayOf(3f, 19f, 17f, 19f))
                    normalized.forEachIndexed { index, digit ->
                        val x = (index * 24 + if (index > 1) 6 else 0) * pitch
                        segments.forEachIndexed { segment, points ->
                            val active = digit in 0..9 && masks[digit] and (1 shl segment) != 0
                            drawLine(ink.copy(alpha = if (active) .6f + .4f * change.value else .08f),
                                Offset(x + points[0] * pitch, vertical + points[1] * pitch), Offset(x + points[2] * pitch, vertical + points[3] * pitch), 1.9f * pitch)
                        }
                    }
                    for (y in listOf(12f, 27f)) drawRect(ink, Offset(49f * pitch, vertical + y * pitch), Size(2f * pitch, 2f * pitch))
                }
            }
            else -> Text(time, target, fontSize = font, color = MaterialTheme.colorScheme.onBackground, maxLines = 1)
        }
    }
}
