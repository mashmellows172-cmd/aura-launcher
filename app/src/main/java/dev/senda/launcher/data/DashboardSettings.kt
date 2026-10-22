package dev.senda.launcher.data

enum class HomeBlock(val label: String) { ALARM("Próxima alarma"), AGENDA("Agenda"), WIDGETS("Widgets") }
enum class BlockAnchor(val label: String) { HEADER("Debajo del reloj"), BEFORE_FAVORITES("Antes de favoritas"), AFTER_FAVORITES("Después de favoritas"), END("Al final de las apps") }
data class BlockStyle(val anchor: BlockAnchor, val height: Int = 48, val textSize: Int = 14, val halfWidth: Boolean = false)
data class DashboardSettings(val order: List<HomeBlock> = HomeBlock.entries.toList(), val styles: Map<HomeBlock, BlockStyle> = emptyMap()) {
    operator fun get(block: HomeBlock): BlockStyle = styles[block] ?: BlockStyle(if (block == HomeBlock.WIDGETS) BlockAnchor.BEFORE_FAVORITES else BlockAnchor.HEADER)
    fun with(block: HomeBlock, style: BlockStyle) = copy(styles = styles + (block to style.copy(height = style.height.coerceIn(48,160), textSize = style.textSize.coerceIn(12,20))))
    fun move(block: HomeBlock, delta: Int): DashboardSettings {
        val index = order.indexOf(block); val target = index + delta
        if (index < 0 || target !in order.indices) return this
        return copy(order = order.toMutableList().apply { add(target,removeAt(index)) })
    }
    fun blocks(anchor: BlockAnchor, alarm: Boolean, agenda: Boolean, widgets: Boolean) = order.filter { get(it).anchor == anchor && when(it) { HomeBlock.ALARM -> alarm; HomeBlock.AGENDA -> agenda; HomeBlock.WIDGETS -> widgets } }
}
