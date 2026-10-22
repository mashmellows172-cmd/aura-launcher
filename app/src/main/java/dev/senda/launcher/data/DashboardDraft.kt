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

    /** Bundle-safe snapshot, at most 33 frames (~2.4 KiB). A drag ends on recreation. */
    fun saveState(): IntArray = buildList {
        add(1)
        val frames=history.takeLast(32)+current
        add(frames.size)
        frames.forEach { settings ->
            addAll(settings.order.map { it.ordinal })
            HomeBlock.entries.forEach { block ->
                val style=settings[block]
                add(if(block in settings.styles)1 else 0)
                add(style.anchor.ordinal);add(style.height);add(style.textSize);add(if(style.halfWidth)1 else 0)
            }
        }
    }.toIntArray()

    companion object {
        fun restoreState(saved: IntArray): DashboardDraft? = runCatching {
            require(saved.size>=2 && saved[0]==1 && saved[1] in 1..33)
            require(saved.size==2+saved[1]*18)
            var offset=2
            val frames=List(saved[1]) {
                val order=List(3) { HomeBlock.entries[saved[offset++]] }
                require(order.toSet().size==3)
                val styles=buildMap {
                    HomeBlock.entries.forEach { block ->
                        val present=saved[offset++];require(present in 0..1)
                        val anchor=BlockAnchor.entries[saved[offset++]]
                        val height=saved[offset++];require(height in 48..160)
                        val text=saved[offset++];require(text in 12..20)
                        val half=saved[offset++];require(half in 0..1)
                        if(present==1)put(block,BlockStyle(anchor,height,text,half==1))
                    }
                }
                DashboardSettings(order,styles)
            }
            DashboardDraft(frames.last(),frames.dropLast(1))
        }.getOrNull()
    }
}
