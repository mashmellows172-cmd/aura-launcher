package dev.senda.launcher.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.Ps4ListProfile
import dev.senda.launcher.data.VisualSettings
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

/** Distinct retained transforms. No vertical displacement, duplicated rows, or per-row clock. */
@Composable
internal fun Modifier.ps4ListRowMotion(list: LazyListState?, key: String?, settings: VisualSettings): Modifier {
    val profile = Ps4ListProfile.fromId(settings.motion)
    val controller = LocalMotionController.current
    if (list == null || key == null || profile?.usesRowMotion != true || controller == null || listMotionReduced(settings)) return this
    val constrained = LocalDecorationConstrained.current
    val intensity = settings.scrollIntensity.coerceIn(10, 100) / 100f
    return graphicsLayer {
        scaleX = 1f; scaleY = 1f; alpha = 1f
        rotationX = 0f; rotationY = 0f; rotationZ = 0f
        translationX = 0f; translationY = 0f
        transformOrigin = TransformOrigin.Center
        val velocity = controller.velocity
        if (abs(velocity) < MotionTokens.idleEpsilon) return@graphicsLayer
        val layout = list.layoutInfo
        val row = layout.visibleItemsInfo.firstOrNull { it.key == key } ?: return@graphicsLayer
        val viewport = (layout.viewportEndOffset - layout.viewportStartOffset).toFloat().coerceAtLeast(1f)
        val center = row.offset - layout.viewportStartOffset + row.size / 2f
        val position = (center / viewport * 2f - 1f).coerceIn(-1f, 1f)
        val energy = sqrt(abs(velocity).coerceAtMost(1f)) * intensity * if (constrained.value) .45f else 1f
        cameraDistance = maxOf(size.width, size.height, 1f) * 2f
        when (profile) {
            Ps4ListProfile.DEPTH -> {
                scaleX = 1f - abs(position) * .10f * energy
                scaleY = scaleX
                alpha = 1f - abs(position) * .12f * energy
            }
            Ps4ListProfile.CRYSTAL -> {
                rotationY = position * 12f * energy
                scaleX = 1f - abs(position) * .045f * energy
            }
            Ps4ListProfile.LIGHT_RAIL -> translationX = -velocity * 14.dp.toPx() * (1f - abs(position) * .5f) * energy
            Ps4ListProfile.RIBBONS -> translationX = sin(position * Math.PI / 2).toFloat() * 24.dp.toPx() * energy
            Ps4ListProfile.RELIEF -> {
                rotationX = -position * 10f * energy
                scaleX = 1f - abs(position) * .035f * energy
            }
            Ps4ListProfile.WAKE -> translationX = velocity * 18.dp.toPx() * ((row.index % 3 + 1) / 3f) * energy
            else -> Unit
        }
    }
}
