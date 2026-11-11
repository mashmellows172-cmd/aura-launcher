package dev.senda.launcher.ui.theme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.time.LocalTime

internal enum class Ps4AmbientPeriod { DAY, DUSK, NIGHT }
internal val LocalPs4Ambient = staticCompositionLocalOf<Ps4AmbientPeriod?> { null }

@Composable
internal fun rememberPs4Ambient(enabled: Boolean): Ps4AmbientPeriod? {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    fun current() = when (LocalTime.now().hour) {
        in 6..16 -> Ps4AmbientPeriod.DAY
        in 17..19 -> Ps4AmbientPeriod.DUSK
        else -> Ps4AmbientPeriod.NIGHT
    }
    var period by remember { mutableStateOf(current()) }
    DisposableEffect(context, lifecycle, enabled) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (enabled && lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) period = current()
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) period = current()
        }
        lifecycle.addObserver(observer)
        if (enabled) {
            period = current()
            ContextCompat.registerReceiver(context, receiver, IntentFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
            }, ContextCompat.RECEIVER_NOT_EXPORTED)
        }
        onDispose {
            lifecycle.removeObserver(observer)
            if (enabled) context.unregisterReceiver(receiver)
        }
    }
    return period.takeIf { enabled }
}
