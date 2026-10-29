package dev.senda.launcher.data

enum class SoundEvent(val label: String) {
    LAUNCH("Abrir aplicaciones"), MENU("Abrir menús"), SELECT("Elegir opciones"),
    FAVORITE("Cambiar favoritas"), INDEX("Recorrer el índice A–Z"),
}

enum class SoundFamily(val label: String) {
    AUTO("Según el tema visual"), NOTHING("Nothing · pulso seco"), SENSE("Sense · madera y papel"),
    TOUCHWIZ("TouchWiz · gotas"), OPTIMUS("LG · campana cálida"), XPERIA("Xperia · cristal"), RAZR("RAZR · tecla digital"), PS4("PS4 · cristal y navegación");

    fun resolve(theme: LauncherTheme): LauncherTheme = if (this == AUTO) theme else LauncherTheme.valueOf(name)
    companion object { fun fromStored(value: String?) = entries.firstOrNull { it.name == value } ?: AUTO }
}

/** Conservative defaults: no unexpected audio, and no index tick during every drag unless requested. */
data class SoundSettings(
    val enabled: Boolean = false,
    val volume: Int = 35,
    val family: SoundFamily = SoundFamily.AUTO,
    val launch: Boolean = true,
    val menu: Boolean = true,
    val select: Boolean = true,
    val favorite: Boolean = true,
    val index: Boolean = false,
    val muteDuringMedia: Boolean = true,
) {
    // 100 retains the original cue level; higher values opt into extra digital headroom.
    companion object { const val MAX_VOLUME = 300 }
    fun bounded() = copy(volume = volume.coerceIn(0, MAX_VOLUME))
    fun allows(event: SoundEvent) = when (event) {
        SoundEvent.LAUNCH -> launch; SoundEvent.MENU -> menu; SoundEvent.SELECT -> select
        SoundEvent.FAVORITE -> favorite; SoundEvent.INDEX -> index
    }
}
