package dev.senda.launcher

import dev.senda.launcher.data.LauncherTheme
import org.junit.Assert.*
import org.junit.Test

class MotorolaThemeTest {
    @Test fun razrSelectionRoundTripsAndUsesDarkSystemBars() {
        assertEquals(LauncherTheme.RAZR, LauncherTheme.fromStoredValue("RAZR"))
        assertEquals("RAZR", LauncherTheme.RAZR.name)
        assertTrue(LauncherTheme.RAZR.isDark(false))
        assertTrue(LauncherTheme.RAZR.isDark(true))
    }

    @Test fun existingSettingsRetainTheirMeaning() {
        assertEquals(LauncherTheme.NOTHING, LauncherTheme.fromStoredValue(null))
        assertEquals(LauncherTheme.NOTHING, LauncherTheme.fromStoredValue("ROKR_future"))
        assertEquals(LauncherTheme.SENSE, LauncherTheme.fromStoredValue("SENSE"))
        assertEquals(LauncherTheme.XPERIA, LauncherTheme.fromStoredValue("XPERIA"))
    }
}
