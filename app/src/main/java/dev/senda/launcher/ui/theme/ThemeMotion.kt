package dev.senda.launcher.ui.theme

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.LauncherTheme

/** Consumers read .value in draw/graphicsLayer, never in the Home composition. */
@Composable
fun rememberHistoricalPhase(theme: LauncherTheme, moving: Boolean, reduced: Boolean): State<Float> {
    val phase = remember { Animatable(.25f) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val duration = when (theme) {
        LauncherTheme.NOTHING -> 2200
        LauncherTheme.SENSE -> 1800
        LauncherTheme.TOUCHWIZ -> 3200
        LauncherTheme.OPTIMUS -> 2600
        LauncherTheme.XPERIA -> 4400
        LauncherTheme.RAZR -> 1600
        LauncherTheme.PS4 -> 20000
        else -> 2400
    }
    LaunchedEffect(lifecycle, theme, moving, reduced) {
        if (!moving || reduced || !ValueAnimator.areAnimatorsEnabled()) { phase.snapTo(.25f); return@LaunchedEffect }
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (ValueAnimator.areAnimatorsEnabled()) {
                phase.snapTo(0f)
                phase.animateTo(1f, tween(duration, easing = LinearEasing))
            }
        }
    }
    return phase.asState()
}

/** A finite, theme-specific change cue. No infinite timer belongs to the clock. */
@Composable
fun rememberClockChange(value: String, theme: LauncherTheme, reduced: Boolean): State<Float> {
    val phase = remember { Animatable(1f) }
    var previous by remember { mutableStateOf(value) }
    LaunchedEffect(value, theme, reduced) {
        if (reduced || !ValueAnimator.areAnimatorsEnabled()) { phase.snapTo(1f); previous = value; return@LaunchedEffect }
        if (previous == value) return@LaunchedEffect
        previous = value
        phase.snapTo(0f)
        phase.animateTo(1f, tween(historicalProfile(theme)?.durationMs ?: 240))
    }
    return phase.asState()
}

/** One subscription per widget; no polling and no timer while saving power. */
@Composable
fun rememberMotionPowerSaver(): Boolean {
    val context = androidx.compose.ui.platform.LocalContext.current.applicationContext
    val manager = remember(context) { context.getSystemService(android.os.PowerManager::class.java) }
    var saving by remember(manager) { mutableStateOf(manager?.isPowerSaveMode == true) }
    DisposableEffect(context, manager) {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
                saving = manager?.isPowerSaveMode == true
            }
        }
        androidx.core.content.ContextCompat.registerReceiver(context, receiver,
            android.content.IntentFilter(android.os.PowerManager.ACTION_POWER_SAVE_MODE_CHANGED),
            androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED)
        saving = manager?.isPowerSaveMode == true
        onDispose { context.unregisterReceiver(receiver) }
    }
    return saving
}
