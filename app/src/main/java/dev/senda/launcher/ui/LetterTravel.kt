package dev.senda.launcher.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp

internal data class LetterFlight(val letter: String, val from: Offset, val to: Offset)

@Composable
internal fun LetterTravel(flight: LetterFlight, modifier: Modifier, onFinish: () -> Unit) {
    val progress = remember(flight) { Animatable(0f) }
    val finish by rememberUpdatedState(onFinish)
    LaunchedEffect(progress) {
        progress.animateTo(1f, tween(245, easing = Ps4Motion.easing))
        finish()
    }
    Box(modifier.size(29.dp).graphicsLayer {
        val p = progress.value
        val position = lerp(flight.from, flight.to, p)
        translationX = position.x - size.width / 2
        translationY = position.y - size.height / 2
        scaleX = 1f - .35f * p
        scaleY = scaleX
        alpha = ((1f - p) / .24f).coerceIn(0f, 1f)
    }.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp)).clearAndSetSemantics {},
        contentAlignment = Alignment.Center) {
        Text(flight.letter, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleSmall)
    }
}
