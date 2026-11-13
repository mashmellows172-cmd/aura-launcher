package dev.senda.launcher.ui

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LocalAppearance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun ps4PanelDuration(durationMs: Int, closing: Boolean): Int =
    (durationMs.coerceIn(150, 800) * if (closing) .48f else .68f).toInt()

private val ps4SlideEasing = CubicBezierEasing(.22f, .75f, .22f, 1f)
private val ps4FadeEasing = CubicBezierEasing(.25f, .1f, .25f, 1f)

internal fun ps4PanelEasing(effect: String): Easing = when (effect) {
    "SLIDE" -> ps4SlideEasing
    "FADE" -> ps4FadeEasing
    else -> Ps4Motion.easing
}

internal fun Modifier.ps4PanelTransform(
    progress: State<Float>,
    effect: String,
    lateral: Boolean = false,
    origin: () -> Rect? = { null },
    destination: () -> Rect? = { null },
): Modifier {
    if (effect == "NONE") return this
    return graphicsLayer {
        val remaining = 1f - progress.value.coerceIn(0f, 1f)
        alpha = 1f - remaining
        scaleX = 1f
        scaleY = 1f
        translationX = 0f
        translationY = 0f
        when (effect) {
            "SLIDE" -> if (lateral) translationX = -size.width * .16f * remaining
                else translationY = 24.dp.toPx() * remaining
            "SPRING" -> {
                val source = origin()
                val target = destination()
                if (source != null && target != null && source.width > 0f && source.height > 0f &&
                    target.width > 0f && target.height > 0f &&
                    source.center.x.isFinite() && source.center.y.isFinite() &&
                    target.center.x.isFinite() && target.center.y.isFinite()
                ) {
                    val startScale = minOf(source.width / target.width, source.height / target.height).coerceIn(.15f, .8f)
                    scaleX = 1f - (1f - startScale) * remaining
                    scaleY = scaleX
                    translationX = (source.center.x - target.center.x) * remaining
                    translationY = (source.center.y - target.center.y) * remaining
                } else if (lateral) {
                    translationX = -size.width * .1f * remaining
                } else {
                    scaleX = 1f - .06f * remaining
                    scaleY = scaleX
                }
            }
            else -> Unit
        }
    }
}

internal class Ps4PanelMotion(
    val progress: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope,
    private val settings: State<Pair<String, Int>>,
) {
    val effect: String get() = settings.value.first
    var closing = false
        private set

    fun close(reduced: Boolean, done: () -> Unit) {
        if (closing) return
        closing = true
        if (reduced || effect == "NONE" || !ValueAnimator.areAnimatorsEnabled()) done() else scope.launch {
            val duration = (ps4PanelDuration(settings.value.second, closing = true) * progress.value).toInt()
            if (duration > 0) progress.animateTo(0f, tween(duration, easing = ps4PanelEasing(effect)))
            done()
        }
    }
}

@Composable
internal fun rememberPs4PanelMotion(reduced: Boolean): Ps4PanelMotion {
    val scope = rememberCoroutineScope()
    val visual = LocalMotionSettings.current
    val still = reduced || visual.reduceMotion || visual.menuEffect == "NONE" ||
        LocalAppearance.current.highContrast || LocalThemePreview.current ||
        LocalSystemPowerSaving.current || !ValueAnimator.areAnimatorsEnabled()
    val settings = rememberUpdatedState((if (still) "NONE" else visual.menuEffect) to visual.durationMs)
    val motion = remember(scope) { Ps4PanelMotion(Animatable(if (still) 1f else 0f), scope, settings) }
    val wallpaper = LocalPs4WallpaperMotion.current
    androidx.compose.runtime.DisposableEffect(wallpaper, motion) {
        wallpaper?.attachPanel(motion, motion.progress.asState())
        onDispose { wallpaper?.detachPanel(motion) }
    }
    LaunchedEffect(still) {
        if (!motion.closing) {
            if (still) motion.progress.snapTo(1f)
            else motion.progress.animateTo(1f, tween(ps4PanelDuration(visual.durationMs, closing = false),
                easing = ps4PanelEasing(visual.menuEffect)))
        }
    }
    return motion
}
