package dev.senda.launcher.ui

import android.animation.ValueAnimator
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
        if (reduced || !ValueAnimator.areAnimatorsEnabled()) done() else scope.launch {
            progress.animateTo(0f, tween(Ps4Motion.exitMs, easing = Ps4Motion.easing))
            done()
        }
    }
}

@Composable
internal fun rememberPs4PanelMotion(reduced: Boolean): Ps4PanelMotion {
    val scope = rememberCoroutineScope()
    val still = reduced || LocalSystemPowerSaving.current || !ValueAnimator.areAnimatorsEnabled()
    val motion = remember(scope) { Ps4PanelMotion(Animatable(if (still) 1f else 0f), scope) }
    val wallpaper = LocalPs4WallpaperMotion.current
    androidx.compose.runtime.DisposableEffect(wallpaper, motion) {
        wallpaper?.attachPanel(motion, motion.progress.asState())
        onDispose { wallpaper?.detachPanel(motion) }
    }
    LaunchedEffect(still) {
        if (!motion.closing) {
            if (still) motion.progress.snapTo(1f)
            else motion.progress.animateTo(1f, tween(Ps4Motion.enterMs, easing = Ps4Motion.easing))
        }
    }
    return motion
}
