package dev.senda.launcher.ui

import android.animation.ValueAnimator
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.data.VisualSettings
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.PI

internal val LocalPs4WallpaperMotion = staticCompositionLocalOf<Ps4WallpaperMotion?> { null }

/** Outputs are observed only by drawing/layers. Inputs never consume a gesture. */
@Stable
internal class Ps4WallpaperMotion {
    var drift by mutableFloatStateOf(0f); private set
    var search by mutableFloatStateOf(0f); private set
    var light by mutableFloatStateOf(0f); private set
    var lightY by mutableFloatStateOf(.4f); private set
    var panel by mutableStateOf<State<Float>?>(null); private set
    var enabled by mutableStateOf(false); private set
    var amount by mutableFloatStateOf(.5f); private set
    private var density = 1f
    private var pending = 0f
    private var pressed = false
    private var searchTarget = 0f
    private var lightTargetY = .4f
    private var pulseRemaining = 0f
    private var touch = 0f
    private var panelOwner: Any? = null
    private val wake = Channel<Unit>(Channel.CONFLATED)

    val connection = object : NestedScrollConnection {
        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (enabled && consumed.y != 0f) {
                pending = (pending + consumed.y).coerceIn(-500f * density, 500f * density)
                wake.trySend(Unit)
            }
            return Offset.Zero
        }
    }

    fun configure(active: Boolean, intensity: Int, pixelsPerDp: Float) {
        enabled = active
        amount = intensity.coerceIn(0, 100) / 100f
        density = pixelsPerDp
        if (!active) reset()
    }

    fun setSearch(value: Boolean) {
        searchTarget = if (enabled && value) 1f else 0f
        if (enabled) wake.trySend(Unit)
    }

    fun press(value: Boolean, y: Float = .4f) {
        if (!enabled) return
        pressed = value
        if (value) { lightTargetY = y.coerceIn(.05f, .95f); pulseRemaining = 0f }
        wake.trySend(Unit)
    }

    fun confirm(y: Float = .4f) {
        if (!enabled) return
        lightTargetY = y.coerceIn(.05f, .95f)
        pulseRemaining = .4f
        wake.trySend(Unit)
    }

    fun attachPanel(owner: Any, progress: State<Float>) { panelOwner = owner; panel = progress }
    fun detachPanel(owner: Any) { if (panelOwner === owner) { panel = null; panelOwner = null } }

    private fun reset() {
        pending = 0f; pressed = false; searchTarget = 0f; pulseRemaining = 0f
        drift = 0f; search = 0f; touch = 0f; light = 0f; lightY = .4f
        while (wake.tryReceive().isSuccess) { /* Discard hidden-screen events. */ }
    }

    suspend fun run() {
        for (signal in wake) {
            var previous = 0L
            do {
                var unsettled = false
                withFrameNanos { now ->
                    val dt = if (previous == 0L) 1f / 60f else ((now - previous) / 1e9f).coerceIn(0f, .05f)
                    previous = now
                    // -ln(.05)/.25: 95% settled in 250 ms, independent of refresh rate.
                    val alpha = 1f - exp(-11.98293f * dt)
                    val targetDrift = (pending / density / dt.coerceAtLeast(.001f) / 2400f).coerceIn(-1f, 1f)
                    pending = 0f
                    drift += (targetDrift - drift) * alpha
                    search += (searchTarget - search) * alpha
                    touch += ((if (pressed) 1f else 0f) - touch) * alpha
                    lightY += (lightTargetY - lightY) * alpha
                    pulseRemaining = (pulseRemaining - dt).coerceAtLeast(0f)
                    light = maxOf(touch * .6f, sin(PI.toFloat() * pulseRemaining / .4f) * .45f)
                    unsettled = abs(drift - targetDrift) > .001f || abs(targetDrift) > .001f ||
                        abs(searchTarget - search) > .001f || abs((if (pressed) 1f else 0f) - touch) > .001f ||
                        abs(lightTargetY - lightY) > .001f || pulseRemaining > 0f
                    if (!unsettled) {
                        drift = 0f; search = searchTarget; touch = if (pressed) 1f else 0f
                        light = touch * .6f; lightY = lightTargetY
                    }
                }
                // A conflated signal replaces the target; it never restarts the frame clock.
                while (wake.tryReceive().isSuccess) { unsettled = true }
            } while (enabled && unsettled)
        }
    }
}

@Composable
internal fun rememberPs4WallpaperMotion(
    console: Boolean, settings: VisualSettings, list: LazyListState,
    focusedKey: State<Any?>, searchFocused: Boolean,
): Ps4WallpaperMotion {
    val scene = remember { Ps4WallpaperMotion() }
    val density = LocalDensity.current.density
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val disabled = !console || settings.wallpaperMode == "STATIC" || settings.wallpaperIntensity == 0 ||
        settings.reduceMotion || LocalAppearance.current.highContrast || LocalSystemPowerSaving.current ||
        LocalThemePreview.current || !ValueAnimator.areAnimatorsEnabled()
    val currentSearch = rememberUpdatedState(searchFocused)
    LaunchedEffect(scene, lifecycle, disabled, settings.wallpaperIntensity, density, list, focusedKey) {
        try {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val active = !disabled && ValueAnimator.areAnimatorsEnabled()
                scene.configure(active, settings.wallpaperIntensity, density)
                if (active) try {
                    coroutineScope {
                        launch { snapshotFlow { currentSearch.value }.collect { scene.setSearch(it) } }
                        launch {
                            var previous: Any? = focusedKey.value
                            snapshotFlow { list.isScrollInProgress to focusedKey.value }.collectLatest { (moving, key) ->
                                if (!moving && !currentSearch.value && key != null && key != previous) {
                                    delay(150)
                                    val info = list.layoutInfo
                                    val row = info.visibleItemsInfo.firstOrNull { it.key == key }
                                    if (row != null && !currentSearch.value) scene.confirm((row.offset + row.size * .5f) /
                                        info.viewportEndOffset.coerceAtLeast(1))
                                    previous = key
                                }
                            }
                        }
                        scene.run()
                    }
                } finally { scene.configure(false, settings.wallpaperIntensity, density) }
            }
        } finally { scene.configure(false, settings.wallpaperIntensity, density) }
    }
    return scene
}

/** A personal bitmap keeps its resource and receives only the retained-layer transform. */
internal fun Modifier.ps4PersonalWallpaper(scene: Ps4WallpaperMotion): Modifier = graphicsLayer {
    val panel = if (scene.enabled) scene.panel?.value ?: 0f else 0f
    val depth = maxOf(scene.search * .45f, panel)
    scaleX = 1.02f + depth * .012f * scene.amount
    scaleY = scaleX
    translationY = scene.drift * 6.dp.toPx() * scene.amount
}
