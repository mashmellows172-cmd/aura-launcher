package dev.senda.launcher.data

data class HeaderSettings(
    val showAlarm: Boolean = true,
    val showCalendar: Boolean = false,
    val weatherMotion: WeatherMotionSettings = WeatherMotionSettings(),
    val showMotivationalHeader: Boolean = true,
    val showAudioWidget: Boolean = true,
)
enum class HomeRotation(val label: String) { SYSTEM("Seguir el sistema"), PORTRAIT("Vertical"), LANDSCAPE("Horizontal") }
enum class GestureDirection(val label: String) { UP("Arriba"), DOWN("Abajo"), LEFT("Izquierda"), RIGHT("Derecha") }

data class GestureBindings(val up: String = "SEARCH", val down: String = "NONE", val left: String = "NONE", val right: String = "NONE") {
    operator fun get(direction: GestureDirection): String = when (direction) { GestureDirection.UP -> up; GestureDirection.DOWN -> down; GestureDirection.LEFT -> left; GestureDirection.RIGHT -> right }
    fun with(direction: GestureDirection, action: String): GestureBindings = when (direction) {
        GestureDirection.UP -> copy(up = action); GestureDirection.DOWN -> copy(down = action)
        GestureDirection.LEFT -> copy(left = action); GestureDirection.RIGHT -> copy(right = action)
    }
    companion object {
        fun valid(value: String): Boolean = value in setOf("NONE", "SEARCH", "NOTIFICATIONS") || (value.startsWith("APP:") && Regex("[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+").matches(value.removePrefix("APP:")))
    }
}

enum class AccentChoice(val label: String, val argb: Long?) {
    DEFAULT("Del tema", null), RED("Rojo", 0xFFFF3654), MINT("Menta", 0xFFA6D1B5), BLUE("Azul", 0xFF80BFFF),
    CMF("Naranja CMF", 0xFFFF7A3D), AMBER("Ámbar", 0xFFFFBF69), VIOLET("Violeta", 0xFFB8A1FF), PINK("Rosa", 0xFFF08CC8), WHITE("Neutro", 0xFFF4F5F2)
}
enum class ShapeChoice(val label: String) { DEFAULT("Del tema"), SQUIRCLE("Suave"), CIRCLE("Círculo"), ROUNDED("Redondeado"), BEVELED("Angular"), PILL("Cápsula") }
enum class IconTreatment(val label: String) { AUTO("Temáticos"), ORIGINAL("Normales"), MONOCHROME("Monocromáticos") }

data class ThemeFinish(val accent: AccentChoice = AccentChoice.DEFAULT, val shape: ShapeChoice = ShapeChoice.DEFAULT, val icons: IconTreatment = IconTreatment.AUTO)

/** Optional double tap; accessibility permission is never granted by this preference. */
enum class LockGesture(val label: String) {
    OFF("Desactivado"), LOGO("Solo logotipo"), BACKGROUND("Todos los espacios vacíos"), BOTH("Logotipo y espacios vacíos");
    val onLogo get() = this == LOGO || this == BOTH
    val onBackground get() = this == BACKGROUND || this == BOTH
}
