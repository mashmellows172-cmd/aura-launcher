package dev.senda.launcher.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.ui.theme.LocalHistoricalProfile
import dev.senda.launcher.ui.theme.LocalPs4Palette
import dev.senda.launcher.ui.theme.LocalRetroStyle
import dev.senda.launcher.ui.theme.LocalSkinStyle
import dev.senda.launcher.ui.theme.chromeShape
import dev.senda.launcher.ui.theme.historicalColors
import dev.senda.launcher.ui.theme.ps4Design
import dev.senda.launcher.ui.theme.themeSurfaceDetail

internal val activeQuickTheme: LauncherTheme
    @Composable get() = LocalRetroStyle.current.theme ?: LauncherTheme.NOTHING

/** The same controls use the active family's surface and ink, including LG's light paper. */
@Composable
internal fun ThemedQuickCenter(content: @Composable () -> Unit) {
    val profile = LocalHistoricalProfile.current
    val colors = MaterialTheme.colorScheme
    val themed = if (profile != null && profile.material != dev.senda.launcher.ui.theme.HistoricalMaterial.CONSOLE &&
        !LocalAppearance.current.highContrast) historicalColors(profile, colors, LocalSkinStyle.current.accent)
            .copy(background = profile.bottom, onBackground = profile.ink) else colors
    MaterialTheme(colorScheme = themed) {
        CompositionLocalProvider(androidx.compose.material3.LocalContentColor provides themed.onSurface, content = content)
    }
}

@Composable
internal fun quickPanelRadius(): Dp = if (LocalRetroStyle.current.theme == LauncherTheme.PS4)
    ps4Design(LocalPs4Palette.current).panelRadius.dp else (LocalHistoricalProfile.current?.radius ?: 16).dp

@Composable
internal fun quickPanelShape(): Shape = if (LocalRetroStyle.current.customHolder)
    dev.senda.launcher.ui.theme.animatedIconHolderShape(LocalSkinStyle.current.iconHolder,
        LocalHistoricalProfile.current?.durationMs ?: 280)
    else LocalHistoricalProfile.current.chromeShape()

@Composable
internal fun Modifier.quickSurfaceDetail(): Modifier =
    if (LocalRetroStyle.current.theme == LauncherTheme.PS4) this
    else themeSurfaceDetail(LocalRetroStyle.current.theme, null)
