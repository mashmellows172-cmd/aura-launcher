package dev.senda.launcher.ui

import androidx.compose.runtime.staticCompositionLocalOf
import dev.senda.launcher.HomeUiState

val LocalLightMode = staticCompositionLocalOf { false }
val LocalSetLightMode = staticCompositionLocalOf<(Boolean) -> Unit> { {} }

/** Runtime overlay: the stored theme and its animation preferences are never overwritten. */
fun HomeUiState.withLightMode(enabled: Boolean): HomeUiState = if (!enabled) this else copy(
    visualSettings = visualSettings.copy(reduceMotion = true, glowEnabled = false)
)
