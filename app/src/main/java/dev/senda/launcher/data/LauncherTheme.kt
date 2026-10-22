package dev.senda.launcher.data

/** Original Aura skin presets. Each skin changes colors, wallpaper treatment, icon holders and motion. */
enum class LauncherTheme(val title: String, val subtitle: String) {
    NOTHING("Aura · Nothing", "Principal · AMOLED, Geist, reloj de puntos e iconos monocromáticos"),
    SENSE("Aura Bloom · Sense", "HTC Sense clásico · reloj de láminas, paisaje y relieve"),
    TOUCHWIZ("Aura Prism · TouchWiz", "Galaxy S III · naturaleza, agua y brillo"),
    OPTIMUS("Aura Flow · LG", "LG Optimus · iconos esmaltados y cintas de color"),
    XPERIA("Aura Horizon · Xperia", "Xperia clásico · tipografía ligera, cristal y ondas luminosas"),
    AMOLED("AMOLED puro", "Negro absoluto y acento menta"),
    DARK("Oscuro", "Gris carbón y acento menta"),
    LIGHT("Claro", "Papel cálido y acento verde"),
    SYSTEM("Seguir el sistema", "Usa el modo claro u oscuro del teléfono");

    fun isDark(systemIsDark: Boolean): Boolean = when (this) {
        NOTHING, XPERIA, AMOLED, DARK -> true
        LIGHT, SENSE, TOUCHWIZ, OPTIMUS -> false
        SYSTEM -> systemIsDark
    }

    companion object {
        fun fromStoredValue(value: String?): LauncherTheme = when (value) {
            // Missing settings use the signature skin; explicit selections must round-trip.
            null -> NOTHING
            else -> entries.firstOrNull { it.name == value } ?: NOTHING
        }
    }
}
