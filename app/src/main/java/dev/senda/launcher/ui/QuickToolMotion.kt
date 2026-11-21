package dev.senda.launcher.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance

/** Press follows contact; illumination follows confirmed torch state, never an optimistic toggle. */
@Composable
internal fun Modifier.quickToolMotion(interactions: MutableInteractionSource, active: Boolean?): Modifier {
    val pressed by interactions.collectIsPressedAsState()
    val quiet = LocalMotionSettings.current.reduceMotion || LocalAppearance.current.highContrast ||
        LocalSystemPowerSaving.current || LocalLightMode.current || LocalThemePreview.current ||
        LocalMotionSettings.current.menuEffect == "NONE" || !android.animation.ValueAnimator.areAnimatorsEnabled()
    val contact = animateFloatAsState(if (!quiet && pressed) 1f else 0f,
        tween(if (quiet) 0 else 130, easing = Ps4Motion.easing), label = "quick-tool-contact")
    val pulse = remember { Animatable(0f) }
    LaunchedEffect(active, quiet) {
        pulse.snapTo(0f)
        if (active == true && !quiet) {
            pulse.snapTo(1f)
            pulse.animateTo(0f, tween(460, easing = Ps4Motion.easing))
        }
    }
    val theme = activeQuickTheme
    val accent = MaterialTheme.colorScheme.primary
    return graphicsLayer {
        val p = contact.value
        scaleX = 1f - p * .035f
        scaleY = scaleX
        translationY = p * 1.5.dp.toPx()
        if (theme == LauncherTheme.SENSE || theme == LauncherTheme.RAZR) {
            rotationX = p * 3f
            cameraDistance = 24.dp.toPx()
        }
    }.drawWithCache {
        val ring = Stroke(1.5.dp.toPx())
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = minOf(size.width, size.height) * .48f
        onDrawBehind {
            val p = pulse.value
            if (p > 0f) {
                drawCircle(accent, radius * (1.6f - p * .6f), center, alpha = p * .18f, style = ring)
                drawCircle(accent, radius, center, alpha = p * .07f)
            }
        }
    }
}
