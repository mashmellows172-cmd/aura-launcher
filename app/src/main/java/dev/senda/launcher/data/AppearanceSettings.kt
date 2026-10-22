package dev.senda.launcher.data

import androidx.compose.runtime.staticCompositionLocalOf

/** Values in dp/sp; independent of the system font-size preference. */
data class AppearanceSettings(
    val textSize: Int = 16,
    val iconSize: Int = 42,
    val rowSpacing: Int = 0,
    val highContrast: Boolean = false,
) {
    fun bounded() = copy(textSize = textSize.coerceIn(14, 24), iconSize = iconSize.coerceIn(32, 60), rowSpacing = rowSpacing.coerceIn(0, 20))
    val rowHeight: Int get() = maxOf(60, iconSize + 12) + rowSpacing
}

val LocalAppearance = staticCompositionLocalOf { AppearanceSettings() }
