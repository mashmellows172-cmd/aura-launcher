package dev.senda.launcher.data

import java.time.LocalDate
import java.time.LocalTime

/** Short, stable Latin American Spanish. No timer, live announcement or assumption about mood. */
object MotivationalCatalog {
    fun timeBucket(time: LocalTime): Int = when (time.hour) { in 5..11 -> 0; in 12..18 -> 1; else -> 2 }
    private val phrases = mapOf(
        LauncherTheme.NOTHING to listOf(
            listOf("Hoy, con enfoque.", "Lo esencial va primero.", "Tu ritmo tiene valor.", "Una cosa a la vez.", "Comienza con intención.", "Haz espacio para lo bueno."),
            listOf("Lo simple también suma.", "Cada avance cuenta.", "Sigue con calma.", "Menos ruido, más rumbo.", "Un paso claro, y seguimos.", "Hoy también cuenta."),
            listOf("Por hoy, baja el ruido.", "Haz una pausa con calma.", "Date un momento de aire.", "Tu día puede cerrar en paz.", "Mañana, a tu ritmo.", "Descansar también suma.")
        ),
        LauncherTheme.SENSE to listOf(
            listOf("Que el día arranque bonito.", "Hoy vamos paso a paso.", "Buen comienzo, sin apuro.", "Que te acompañe la calma.", "Hazle espacio a un buen día.", "Comienza con buena energía."),
            listOf("Que la tarde rinda bonito.", "Cada esfuerzo va sumando.", "Vas construyendo tu camino.", "Lo importante toma su tiempo.", "Un respiro y seguimos.", "Acompaña tu día con calma."),
            listOf("Que la noche te trate bien.", "Ahora, una buena pausa.", "Date tiempo para descansar.", "Lo avanzado tiene su valor.", "Deja un rato para ti.", "Cierra el día con cariño.")
        ),
        LauncherTheme.TOUCHWIZ to listOf(
            listOf("Que hoy todo fluya.", "Empieza ligero, con rumbo.", "Deja espacio para lo nuevo.", "Un comienzo fresco para ti.", "Encuentra tu propio cauce.", "Haz tuyo este momento."),
            listOf("Fluye, a tu propio ritmo.", "Toma aire y sigue adelante.", "Las pausas también cuentan.", "Un poco de calma viene bien.", "Que lo bueno siga su curso.", "Cada paso abre camino."),
            listOf("Baja un poco la intensidad.", "Deja que el día se acomode.", "Un cierre tranquilo viene bien.", "Ahora cuida tu descanso.", "Suéltalo por hoy, sin prisa.", "Mañana habrá espacio para más.")
        ),
        LauncherTheme.OPTIMUS to listOf(
            listOf("Hoy también puedes crear.", "Una idea puede empezar aquí.", "Dale forma a lo importante.", "A tu manera, con intención.", "Cada detalle puede sumar.", "Un buen día para lo sencillo."),
            listOf("Va quedando bien, sigue así.", "Una idea a la vez.", "Tu esfuerzo toma forma.", "Haz sitio para esa buena idea.", "Lo pequeño también transforma.", "Ajusta el rumbo sin apuro."),
            listOf("Guarda tus ideas para mañana.", "Lo de hoy ya es parte del camino.", "Ponle pausa a la lista.", "Un descanso también inspira.", "Deja una página para mañana.", "Por hoy, reconoce tu avance.")
        ),
        LauncherTheme.XPERIA to listOf(
            listOf("Que tu día avance ligero.", "Encuentra tu propia frecuencia.", "Un comienzo con aire nuevo.", "Dale espacio a lo que importa.", "A tu ritmo suena mejor.", "Haz de hoy un buen momento."),
            listOf("Sigue el pulso de tu día.", "Una pausa también da rumbo.", "Tu camino tiene su ritmo.", "Que lo simple te acompañe.", "Sigue con claridad y buena onda.", "Respira, todavía hay espacio."),
            listOf("Baja el volumen del día.", "Un momento de calma para ti.", "Que la noche vaya suave.", "A tu ritmo y sin ruido.", "Haz espacio para descansar.", "Que tu cierre sea tranquilo.")
        ),
        LauncherTheme.RAZR to listOf(
            listOf("Activa lo importante.", "Buen comienzo, a tu manera.", "Un paso firme para empezar.", "Que tu día tenga buen ritmo.", "Con calma y decisión.", "Tú marcas el rumbo."),
            listOf("Enfócate en el siguiente paso.", "Buen rumbo, sigue a tu ritmo.", "Cada minuto puede sumar.", "Lo constante deja huella.", "Tu avance merece su espacio.", "Sin prisa, con dirección."),
            listOf("Modo pausa por hoy.", "Deja energía para mañana.", "Cierra la jornada con calma.", "Tu tiempo también es para ti.", "Lo pendiente puede esperar.", "Mañana retomamos el camino.")
        )
    )

    /** Deterministic for a theme, local date and time bucket; reuses the Home clock updates. */
    fun phraseFor(theme: LauncherTheme, date: LocalDate = LocalDate.now(), time: LocalTime = LocalTime.now()): String {
        val list = (phrases[theme] ?: phrases.getValue(LauncherTheme.NOTHING))[timeBucket(time)]
        return list[Math.floorMod(date.toEpochDay() + theme.ordinal * 17L, list.size.toLong()).toInt()]
    }
}
