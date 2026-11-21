package dev.senda.launcher.data

enum class Ps4ListProfile(val label: String, val description: String) {
    DEPTH("Marco clásico PS4", "Las filas ganan profundidad al deslizar; el marco conserva el foco."),
    CRYSTAL("Cristal en movimiento", "Perspectiva lateral y reflejo de cristal en el sentido del gesto."),
    LIGHT_RAIL("Carril de luz", "Desplazamiento lateral contenido y estela de luz en el borde."),
    RIBBONS("Cintas de seda", "Las filas describen una onda lateral acompañada de cintas."),
    RELIEF("Relieve de placas", "Perspectiva vertical con borde iluminado; sin cambiar el orden o altura de las filas."),
    WAKE("Estela de mosaicos", "Desfase lateral escalonado y trazos que se recogen al frenar."),
    IOS_INERTIA("Inercia elástica · iOS", "Desaceleración gradual y rebote en los extremos. Más intensidad, más recorrido."),
    LANDING_FOCUS("Foco al aterrizar · PS5", "El marco se atenúa mientras deslizas y vuelve a definirse al detenerse."),
    DOT_PULSE("Pulso de puntos · Nothing", "Los puntos siguen el sentido del gesto y se apagan al detenerse."),
    NONE("Sin efecto", "Desplazamiento nativo, con el marco de enfoque fijo sobre la fila."),
    ;

    val usesRowMotion: Boolean
        get() = this in setOf(DEPTH, CRYSTAL, LIGHT_RAIL, RIBBONS, RELIEF, WAKE)

    val usesScrollInk: Boolean
        get() = when (this) {
            CRYSTAL, LIGHT_RAIL, RIBBONS, RELIEF, WAKE -> true
            else -> false
        }

    internal companion object {
        fun fromId(value: String?): Ps4ListProfile? = entries.firstOrNull { it.name == value }
    }
}

internal fun VisualSettings.activePs4ListProfile(): Ps4ListProfile =
    ps4ListProfile ?: Ps4ListProfile.fromId(motion) ?: Ps4ListProfile.DEPTH
