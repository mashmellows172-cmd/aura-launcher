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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import dev.senda.launcher.ui.theme.*
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

@Composable
internal fun Modifier.ps4PanelTransform(
    progress: State<Float>,
    effect: String,
    lateral: Boolean = false,
    origin: () -> Rect? = { null },
    destination: () -> Rect? = { null },
): Modifier {
    if (effect == "NONE") return this
    val material = themeSurfaceMotion(LocalRetroStyle.current.theme, LocalPs4Palette.current)
    val still = LocalMotionSettings.current.reduceMotion || LocalAppearance.current.highContrast ||
        LocalThemePreview.current || LocalSystemPowerSaving.current || !ValueAnimator.areAnimatorsEnabled()
    if (still) return this
    val direction = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1f else 1f
    return then(if (effect == "FADE") Modifier else Modifier.themeSurfaceReveal(material) { progress.value }).graphicsLayer {
        val remaining = 1f - progress.value.coerceIn(0f, 1f)
        alpha = 1f - remaining
        scaleX = 1f
        scaleY = 1f
        translationX = 0f
        translationY = 0f
        rotationX = 0f
        transformOrigin = TransformOrigin.Center
        when (effect) {
            "SLIDE" -> applyThemeSurfaceMotion(material, progress.value, lateral, direction)
            "SPRING" -> {
                val source = origin()
                val target = destination()
                if (source != null && target != null && source.width > 0f && source.height > 0f &&
                    target.width > 0f && target.height > 0f &&
                    source.center.x.isFinite() && source.center.y.isFinite() &&
                    target.center.x.isFinite() && target.center.y.isFinite()
                ) {
                    // Keep both endpoints exact; the material bends only the middle of the journey.
                    applyThemeSurfaceMotion(material, 1f - progress.value.coerceIn(0f, 1f) * remaining, lateral, direction)
                    val startScale = minOf(source.width / target.width, source.height / target.height).coerceIn(.15f, .8f)
                    transformOrigin = TransformOrigin.Center
                    val sharedScale = 1f - (1f - startScale) * remaining
                    scaleX *= sharedScale
                    scaleY *= sharedScale
                    translationX += (source.center.x - target.center.x) * remaining
                    translationY += (source.center.y - target.center.y) * remaining
                } else {
                    applyThemeSurfaceMotion(material, progress.value, lateral, direction)
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
    private val easing: State<Easing>,
) {
    val effect: String get() = settings.value.first
    var closing = false
        private set

    fun close(reduced: Boolean, done: () -> Unit) {
        if (closing) return
        closing = true
        if (reduced || effect == "NONE" || !ValueAnimator.areAnimatorsEnabled()) done() else scope.launch {
            val duration = (ps4PanelDuration(settings.value.second, closing = true) * progress.value).toInt()
            if (duration > 0) progress.animateTo(0f, tween(duration, easing = easing.value))
            done()
        }
    }
}

@Composable
internal fun rememberPs4PanelMotion(reduced: Boolean): Ps4PanelMotion {
    val scope = rememberCoroutineScope()
    val visual = LocalMotionSettings.current
    val material = themeSurfaceMotion(LocalRetroStyle.current.theme, LocalPs4Palette.current)
    val duration = (visual.durationMs * material.durationMs / 280).coerceIn(150, 800)
    val still = reduced || visual.reduceMotion || visual.menuEffect == "NONE" ||
        LocalAppearance.current.highContrast || LocalThemePreview.current ||
        LocalSystemPowerSaving.current || !ValueAnimator.areAnimatorsEnabled()
    val settings = rememberUpdatedState((if (still) "NONE" else visual.menuEffect) to duration)
    val easing = rememberUpdatedState(if (visual.menuEffect == "FADE") ps4PanelEasing("FADE") else material.easing)
    val motion = remember(scope) { Ps4PanelMotion(Animatable(if (still) 1f else 0f), scope, settings, easing) }
    val wallpaper = LocalPs4WallpaperMotion.current
    androidx.compose.runtime.DisposableEffect(wallpaper, motion) {
        wallpaper?.attachPanel(motion, motion.progress.asState())
        onDispose { wallpaper?.detachPanel(motion) }
    }
    LaunchedEffect(still, material, visual.menuEffect, duration) {
        if (!motion.closing) {
            if (still) motion.progress.snapTo(1f)
            else motion.progress.animateTo(1f, tween(ps4PanelDuration(duration, closing = false),
                easing = easing.value))
        }
    }
    return motion
}
