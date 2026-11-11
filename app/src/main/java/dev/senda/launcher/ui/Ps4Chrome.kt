package dev.senda.launcher.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.senda.launcher.data.LauncherTheme

/** PS4 uses the shared original glyph family with its silver material. */
@Composable
internal fun Ps4ChromeIcon(kind: String, modifier: Modifier = Modifier) =
    ThemeSystemIcon(kind, LauncherTheme.PS4, modifier)
