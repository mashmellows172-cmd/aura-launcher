package dev.senda.launcher

import dev.senda.launcher.data.normalizeSearchText
import dev.senda.launcher.data.moveListItem
import dev.senda.launcher.data.sectionLetterFor
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class LauncherTextTest {
    private val spanish = Locale.forLanguageTag("es-CL")

    @Test
    fun searchNormalizationIgnoresAccentsAndOuterSpaces() {
        assertEquals("arbol", normalizeSearchText("  Árbol ", spanish))
        assertEquals("nino", normalizeSearchText("NIÑO", spanish))
    }

    @Test
    fun sectionLetterPreservesSpanishEnye() {
        assertEquals("Ñ", sectionLetterFor("ñandú", spanish))
    }

    @Test
    fun sectionLetterGroupsPunctuationAndEmptyLabels() {
        assertEquals("#", sectionLetterFor(" 123 Maps", spanish))
        assertEquals("#", sectionLetterFor("   ", spanish))
    }

    @Test
    fun favoriteReorderingMovesOnlyTheSelectedItem() {
        assertEquals(listOf("chat", "maps", "mail"), moveListItem(listOf("maps", "chat", "mail"), 0, 1))
        assertEquals(listOf("maps", "mail", "chat"), moveListItem(listOf("maps", "chat", "mail"), 1, 2))
    }
}

