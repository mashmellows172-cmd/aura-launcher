package dev.senda.launcher.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class Ps4PanelMotion(
    val progress: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope,
) {
    var closing = false
        private set

    fun close(reduced: Boolean, done: () -> Unit) {
        if (closing) return
        closing = true
        if (reduced) done() else scope.launch {
            progress.animateTo(0f, tween(Ps4Motion.exitMs))
            done()
        }
    }
}

@Composable
internal fun rememberPs4PanelMotion(reduced: Boolean): Ps4PanelMotion {
    val scope = rememberCoroutineScope()
    val motion = remember(scope) { Ps4PanelMotion(Animatable(if (reduced) 1f else 0f), scope) }
    LaunchedEffect(reduced) {
        if (!motion.closing) {
            if (reduced) motion.progress.snapTo(1f)
            else motion.progress.animateTo(1f, tween(Ps4Motion.enterMs, easing = Ps4Motion.easing))
        }
    }
    return motion
}
