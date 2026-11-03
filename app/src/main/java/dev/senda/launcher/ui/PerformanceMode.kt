package dev.senda.launcher.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import dev.senda.launcher.HomeUiState

val LocalLightMode = staticCompositionLocalOf { false }
val LocalSetLightMode = staticCompositionLocalOf<(Boolean) -> Unit> { {} }
val LocalAdaptiveMotion = staticCompositionLocalOf { true }
val LocalSetAdaptiveMotion = staticCompositionLocalOf<(Boolean) -> Unit> { {} }
val LocalSystemPowerSaving = staticCompositionLocalOf { false }

data class PerformancePreferences(val light: Boolean = false, val adaptive: Boolean = true)

@Composable
fun rememberPerformancePreferences(): PerformancePreferences {
    val context = LocalContext.current.applicationContext
    val prefs = remember(context) { context.getSharedPreferences("aura_performance", Context.MODE_PRIVATE) }
    fun read() = PerformancePreferences(prefs.getBoolean("light", false), prefs.getBoolean("adaptive", true))
    var settings by remember(prefs) { mutableStateOf(read()) }
    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "light" || key == "adaptive") settings = read()
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        settings = read()
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    return settings
}

/** Runtime overlay: the stored theme and its animation preferences are never overwritten. */
fun HomeUiState.withLightMode(enabled: Boolean): HomeUiState = if (!enabled) this else copy(
    visualSettings = visualSettings.copy(reduceMotion = true, glowEnabled = false)
)
