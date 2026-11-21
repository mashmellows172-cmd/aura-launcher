package dev.senda.launcher.ui

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal enum class QuickPanelAnchor { EXPANDED, COMPACT, DISMISSED }

/** Signed velocity decides direction; a short downward fling from expanded stops at compact. */
internal fun quickPanelReleaseAnchor(
    offset: Float, velocity: Float, expandDistance: Float, dismissDistance: Float,
    flingThreshold: Float, hasMedia: Boolean, startedExpanded: Boolean,
): QuickPanelAnchor {
    if (!offset.isFinite()) return if (hasMedia && startedExpanded) QuickPanelAnchor.EXPANDED else QuickPanelAnchor.COMPACT
    val speed = velocity.takeIf { it.isFinite() } ?: 0f
    if (speed < -flingThreshold) return if (hasMedia && offset <= 0f) QuickPanelAnchor.EXPANDED else QuickPanelAnchor.COMPACT
    if (speed > flingThreshold) return if (startedExpanded && offset < dismissDistance * .65f)
        QuickPanelAnchor.COMPACT else QuickPanelAnchor.DISMISSED
    return when {
        offset > dismissDistance * .55f -> QuickPanelAnchor.DISMISSED
        hasMedia && offset < -expandDistance * .45f -> QuickPanelAnchor.EXPANDED
        else -> QuickPanelAnchor.COMPACT
    }
}

/** One position owns gesture tracking, cover size and reveal. No coroutine per pointer sample. */
@Stable
internal class QuickPanelDragState(
    private val scope: CoroutineScope,
    private val expandDistance: Float,
    private val minimumDismissDistance: Float,
    private val flingThreshold: Float,
    initiallyExpanded: Boolean,
) {
    private val position = mutableFloatStateOf(if (initiallyExpanded) -expandDistance else 0f)
    val offset: State<Float> get() = position
    val expansion: State<Float> = derivedStateOf { (-position.floatValue / expandDistance).coerceIn(0f, 1f) }
    var dismissDistance: Float = minimumDismissDistance
        set(value) { if (value.isFinite()) field = value.coerceAtLeast(minimumDismissDistance) }
    private var settling: Job? = null
    private var startedExpanded = initiallyExpanded

    fun interrupt() { settling?.cancel(); settling = null }

    fun begin() {
        interrupt()
        startedExpanded = position.floatValue < -expandDistance * .5f
    }

    fun dragBy(delta: Float, hasMedia: Boolean) {
        if (!delta.isFinite()) return
        val upper = if (hasMedia) -expandDistance else 0f
        position.floatValue = (position.floatValue + delta).coerceIn(upper, dismissDistance)
    }

    fun expand(expanded: Boolean, animated: Boolean) = settle(
        if (expanded) QuickPanelAnchor.EXPANDED else QuickPanelAnchor.COMPACT, 0f, animated,
    )

    fun cancel(animated: Boolean, hasMedia: Boolean, onExpanded: (Boolean) -> Unit) {
        val expanded = startedExpanded && hasMedia
        onExpanded(expanded)
        settle(if (expanded) QuickPanelAnchor.EXPANDED else QuickPanelAnchor.COMPACT, 0f, animated)
    }

    fun release(
        velocity: Float, hasMedia: Boolean, animated: Boolean,
        onExpanded: (Boolean) -> Unit, onDismiss: () -> Unit,
    ) {
        val safeVelocity = (velocity.takeIf { it.isFinite() } ?: 0f).coerceIn(-flingThreshold * 5f, flingThreshold * 5f)
        val anchor = quickPanelReleaseAnchor(position.floatValue, safeVelocity, expandDistance,
            dismissDistance, flingThreshold, hasMedia, startedExpanded)
        if (anchor != QuickPanelAnchor.DISMISSED) onExpanded(anchor == QuickPanelAnchor.EXPANDED)
        settle(anchor, safeVelocity, animated, onDismiss)
    }

    private fun settle(anchor: QuickPanelAnchor, velocity: Float, animated: Boolean, onDismiss: () -> Unit = {}) {
        interrupt()
        val destination = when (anchor) {
            QuickPanelAnchor.EXPANDED -> -expandDistance
            QuickPanelAnchor.COMPACT -> 0f
            QuickPanelAnchor.DISMISSED -> dismissDistance
        }
        if (!animated) {
            position.floatValue = destination
            if (anchor == QuickPanelAnchor.DISMISSED) onDismiss()
            return
        }
        settling = scope.launch {
            animate(position.floatValue, destination, initialVelocity = velocity,
                animationSpec = spring(dampingRatio = .92f, stiffness = 420f)) { value, _ ->
                position.floatValue = value.coerceIn(-expandDistance, dismissDistance)
            }
            position.floatValue = destination
            if (anchor == QuickPanelAnchor.DISMISSED) onDismiss()
        }
    }
}
