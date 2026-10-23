package dev.senda.launcher

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.ui.theme.skin
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeContrastTest {
    @Test fun actualThemeTextPairsMeetBodyContrast() {
        LauncherTheme.entries.forEach { theme -> listOf(false,true).forEach { dark ->
            val (_,scheme)=skin(theme,dark)
            listOf(scheme.onBackground to scheme.background,scheme.onSurface to scheme.surface,
                scheme.onSurfaceVariant to scheme.surfaceVariant).forEach { (text,background) ->
                val ratio=(maxOf(text.luminance(),background.luminance())+.05f)/(minOf(text.luminance(),background.luminance())+.05f)
                assertTrue("$theme body contrast $ratio",ratio>=4.5f)
            }
        } }
    }
}
