package dev.senda.launcher.ui

import android.app.Activity
import android.content.ContextWrapper
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.FrameMetrics
import android.view.Window
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.awaitCancellation

internal val LocalDecorationConstrained = staticCompositionLocalOf<State<Boolean>> { mutableStateOf(false) }

/** Main-thread primitive counters; callback-only work, never a frame-producing clock. */
@Composable
internal fun rememberDecorationQuality(): State<Boolean> {
    val context = LocalContext.current
    val activity = remember(context) { generateSequence(context) { (it as? ContextWrapper)?.baseContext }
        .filterIsInstance<Activity>().firstOrNull() }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val enabled = LocalAdaptiveMotion.current && !LocalLightMode.current && !LocalSystemPowerSaving.current && !LocalThemePreview.current
    val constrained = remember { mutableStateOf(false) }
    LaunchedEffect(activity, lifecycle, enabled) {
        constrained.value = false
        val window = activity?.window ?: return@LaunchedEffect
        if (!enabled) return@LaunchedEffect
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            var active = true
            var frames = 0; var slow = 0; var healthy = 0; var changedAt = 0L
            val callback = Window.OnFrameMetricsAvailableListener { _, metrics, dropped ->
                if (!active) return@OnFrameMetricsAvailableListener
                val duration = metrics.getMetric(FrameMetrics.TOTAL_DURATION)
                val deadline = if (Build.VERSION.SDK_INT >= 31) metrics.getMetric(FrameMetrics.DEADLINE) else -1L
                if (duration >= 0 && metrics.getMetric(FrameMetrics.FIRST_DRAW_FRAME) != 1L) {
                    val budget = if (deadline > 0) deadline else (1e9 / (window.decorView.display?.refreshRate ?: 60f).coerceAtLeast(1f)).toLong()
                    frames++; if (duration > budget) slow++
                    if (dropped > 0) healthy = 0
                    if (frames >= 60) {
                        val now = SystemClock.elapsedRealtime()
                        if (slow >= 6) { constrained.value = true; healthy = 0; changedAt = now }
                        else if (slow <= 1) healthy++ else healthy = 0
                        if (constrained.value && healthy >= 3 && now - changedAt >= 2000) constrained.value = false
                        frames = 0; slow = 0
                    }
                }
            }
            var attached = false
            try {
                window.addOnFrameMetricsAvailableListener(callback, Handler(Looper.getMainLooper())); attached = true
                awaitCancellation()
            } catch (_: RuntimeException) { /* Keep full quality if window metrics are unavailable. */ }
            finally { active = false; if (attached) runCatching { window.removeOnFrameMetricsAvailableListener(callback) }; constrained.value = false }
        }
    }
    return constrained
}
