package dev.senda.launcher.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.delay

@Composable
internal fun MediaSeekCue(requested: Float?, actual: Float, confirmed: Boolean, animated: Boolean) {
    val marker = remember { Animatable(actual) }
    val opacity = remember { Animatable(0f) }
    val latestActual by rememberUpdatedState(actual)
    var lastRequest by remember { mutableStateOf<Float?>(null) }
    val accent = MaterialTheme.colorScheme.primary
    val backdrop = MaterialTheme.colorScheme.outlineVariant
    LaunchedEffect(requested, confirmed, animated) {
        if (requested != null) {
            if (lastRequest == null) marker.snapTo(latestActual)
            lastRequest = requested
            opacity.snapTo(1f)
            if (animated) marker.animateTo(requested, tween(190, easing = Ps4Motion.easing))
            else marker.snapTo(requested)
        } else if (lastRequest != null) {
            if (confirmed || !animated) marker.snapTo(latestActual)
            else marker.animateTo(latestActual, tween(200, easing = Ps4Motion.easing))
            if (confirmed) delay(320)
            if (animated) opacity.animateTo(0f, tween(160)) else opacity.snapTo(0f)
            lastRequest = null
        }
    }
    Box(Modifier.fillMaxWidth().height(11.dp).drawBehind {
        val radius = 5.dp.toPx()
        val horizontal = (size.width - 2 * radius).coerceAtLeast(0f)
        fun x(fraction: Float): Float =
            radius + horizontal * if (layoutDirection == LayoutDirection.Rtl) (1f - fraction) else fraction
        val center = Offset(x(marker.value), size.height / 2)
        val line = 2.dp.toPx()
        drawRoundRect(backdrop, Offset(radius, (size.height - line) / 2), Size(horizontal, line), CornerRadius(line))
        drawCircle(accent.copy(alpha = .64f), 2.dp.toPx(), Offset(x(actual), size.height / 2))
        if (opacity.value > 0f) {
            val ink = accent.copy(alpha = opacity.value)
            if (confirmed && requested == null) drawCircle(ink, radius - 1.dp.toPx(), center)
            else drawCircle(ink, radius - 1.dp.toPx(), center, style = Stroke(1.5.dp.toPx()))
        }
    })
}
