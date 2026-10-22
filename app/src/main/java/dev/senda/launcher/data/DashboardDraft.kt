package dev.senda.launcher.data

/** Local edits only. The caller persists current explicitly; discarding this value writes nothing. */
data class DashboardDraft(
    val current: DashboardSettings,
    private val history: List<DashboardSettings> = emptyList(),
    private val changeGroup: String? = null,
) {
    val canUndo get() = history.isNotEmpty()

    fun edit(settings: DashboardSettings, group: String? = null): DashboardDraft {
        if (settings == current) return this
        val next = if (group != null && group == changeGroup) history else (history + current).takeLast(32)
        return DashboardDraft(settings, next, group)
    }

    fun finishChange() = copy(changeGroup = null)
    fun undo() = history.lastOrNull()?.let { DashboardDraft(it, history.dropLast(1)) } ?: this
    fun reset() = edit(DashboardSettings())
}
