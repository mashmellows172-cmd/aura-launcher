package dev.senda.launcher.data

enum class Ps4ListProfile(val label: String, val description: String) {
    DEPTH("Marco clásico PS4", "El marco acompaña la fila enfocada y se realza al detenerse."),
    CRYSTAL("Cristal en movimiento", "Un reflejo ancho recorre el cristal en el sentido del gesto."),
    LIGHT_RAIL("Carril de luz", "Una estela azul marca la dirección y la intensidad en el borde de la lista."),
    RIBBONS("Cintas de seda", "Dos cintas curvas se inclinan con el impulso y se disuelven al frenar."),
    RELIEF("Relieve de placas", "Las filas adquieren un borde de luz y una sombra fina que invierten su posición con el gesto."),
    WAKE("Estela de mosaicos", "Trazos cortos prolongan el movimiento de los iconos y pierden longitud al frenar."),
    IOS_INERTIA("Inercia elástica · iOS", "Desaceleración gradual y rebote en los extremos. Más intensidad, más recorrido."),
    LANDING_FOCUS("Foco al aterrizar · PS5", "El marco se atenúa mientras deslizas y vuelve a definirse al detenerse."),
    DOT_PULSE("Pulso de puntos · Nothing", "Los puntos siguen el sentido del gesto y se apagan al detenerse."),
    NONE("Sin efecto", "Desplazamiento nativo, con el marco de enfoque fijo sobre la fila."),
    ;

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
