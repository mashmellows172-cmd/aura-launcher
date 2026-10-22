package dev.senda.launcher

import dev.senda.launcher.data.*
import org.junit.Assert.*
import org.junit.Test

class DashboardDraftTest {
    @Test fun movesSwapOnlyBlocksInTheSameZone() {
        val settings = DashboardSettings(order = listOf(HomeBlock.ALARM, HomeBlock.WIDGETS, HomeBlock.AGENDA))
        val moved = settings.move(HomeBlock.AGENDA, -1)
        assertEquals(listOf(HomeBlock.AGENDA, HomeBlock.WIDGETS, HomeBlock.ALARM), moved.order)
        assertEquals(listOf(HomeBlock.WIDGETS), moved.blocks(BlockAnchor.BEFORE_FAVORITES, true, true, true))
        assertEquals(settings, settings.move(HomeBlock.WIDGETS, -1))
        assertEquals(settings, settings.move(HomeBlock.ALARM, -1))
    }

    @Test fun sequentialEditsUndoInReverseOrderWithoutChangingBase() {
        val base = DashboardSettings()
        val first = base.with(HomeBlock.ALARM, base[HomeBlock.ALARM].copy(height = 96))
        val second = first.with(HomeBlock.AGENDA, first[HomeBlock.AGENDA].copy(anchor = BlockAnchor.END))
        val draft = DashboardDraft(base).edit(first).edit(second)
        assertEquals(48, base[HomeBlock.ALARM].height)
        assertEquals(second, draft.current)
        assertEquals(first, draft.undo().current)
        assertEquals(base, draft.undo().undo().current)
        assertFalse(draft.undo().undo().canUndo)
        assertEquals(base, draft.undo().undo().undo().current)
    }

    @Test fun noOpDoesNotConsumeUndoAndResetCanBeUndone() {
        val custom = DashboardSettings().with(HomeBlock.ALARM, BlockStyle(BlockAnchor.END, 120))
        val draft = DashboardDraft(custom)
        assertFalse(draft.edit(custom).canUndo)
        assertEquals(DashboardSettings(), draft.reset().current)
        assertEquals(custom, draft.reset().undo().current)
    }

    @Test fun sliderGestureIsOneUndoStep() {
        val base = DashboardSettings()
        val one = base.with(HomeBlock.ALARM, base[HomeBlock.ALARM].copy(height = 64))
        val two = base.with(HomeBlock.ALARM, base[HomeBlock.ALARM].copy(height = 96))
        val draft = DashboardDraft(base).edit(one, "height").edit(two, "height").finishChange()
        assertEquals(two, draft.current)
        assertEquals(base, draft.undo().current)
        assertFalse(draft.undo().canUndo)
        assertEquals(two, draft.edit(one, "height").undo().current)
    }

    @Test fun dimensionsAreBoundedAndZoneChangePreservesOtherSettings() {
        val base = DashboardSettings()
        val edited = base.with(HomeBlock.ALARM, BlockStyle(BlockAnchor.END, 10000, -1, true))
        assertEquals(BlockStyle(BlockAnchor.END, 160, 12, true), edited[HomeBlock.ALARM])
        assertEquals(base[HomeBlock.AGENDA], edited[HomeBlock.AGENDA])
    }
}
