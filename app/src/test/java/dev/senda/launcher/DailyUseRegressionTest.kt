package dev.senda.launcher

import dev.senda.launcher.data.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class DailyUseRegressionTest {
    @Test fun everyExplicitThemeSurvivesReload() {
        LauncherTheme.entries.forEach { assertEquals(it, LauncherTheme.fromStoredValue(it.name)) }
    }
    @Test fun newAndUnknownThemesUseSignatureDefault() {
        assertEquals(LauncherTheme.NOTHING, LauncherTheme.fromStoredValue(null))
        assertEquals(LauncherTheme.NOTHING, LauncherTheme.fromStoredValue("removed-theme"))
    }
    @Test fun editingFavoritesPreservesHiddenAndTemporarilyMissingPackages() {
        assertEquals(listOf("mail", "hidden", "updating"), mergeEditedFavorites(
            listOf("maps", "hidden", "mail", "updating"), listOf("mail"), setOf("mail", "maps")))
    }
    @Test fun favoriteEditorCannotInjectDuplicatesOrUnavailableEntries() {
        assertEquals(listOf("mail", "hidden"), mergeEditedFavorites(listOf("hidden"), listOf("mail", "mail", "unknown"), setOf("mail")))
    }
    @Test fun alphabetTargetsAccountForWidgetsAndFavorites() {
        assertEquals(mapOf("A" to 6, "W" to 9, "Z" to 11), sectionStartIndices(listOf("A", "W", "Z"), mapOf("A" to 2, "W" to 1, "Z" to 3), 3, true))
        assertEquals(mapOf("A" to 1), sectionStartIndices(listOf("A"), mapOf("A" to 4), 0, false))
    }
    @Test fun largeGalleryImagesAreDownsampledBeforeAllocation() {
        assertEquals(64, imageSampleSize(8000, 6000, 128))
        assertEquals(1, imageSampleSize(96, 96, 128))
        val sample = imageSampleSize(12000, 9000, 1920)
        assertTrue(12000 / sample <= 1920)
    }
    @Test fun searchHandlesTurkishCaseBeforeRemovingDiacritics() {
        val turkish = Locale.forLanguageTag("tr")
        assertEquals("istanbul", normalizeSearchText("İSTANBUL", turkish))
        assertEquals("ıgdır", normalizeSearchText("IĞDIR", turkish))
        assertEquals("", normalizeSearchText("   "))
        assertEquals("🚀", normalizeSearchText("🚀"))
    }
}
