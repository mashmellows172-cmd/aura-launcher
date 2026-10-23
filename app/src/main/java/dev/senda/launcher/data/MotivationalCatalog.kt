package dev.senda.launcher.data

import java.time.LocalDate
import java.time.LocalTime

/**
 * Catálogo de frases motivacionales breves y amables en español latino semiformal.
 * Evita frases repetitivas, grandilocuentes, invasivas o que hagan suposiciones sobre el ánimo.
 */
object MotivationalCatalog {

    private val morning = listOf(
        "Que fluya el día.",
        "Paso a paso, vas bien.",
        "Hoy también cuenta.",
        "Buen comienzo, con calma.",
        "A tu ritmo, con intención.",
        "Cada momento suma.",
        "Con calma y buena energía."
    )

    private val afternoon = listOf(
        "A tu ritmo, con intención.",
        "Paso a paso, vas bien.",
        "Cada avance suma.",
        "Enfócate en lo importante.",
        "Que la tarde fluya bien.",
        "Un paso a la vez.",
        "Todo a su debido tiempo."
    )

    private val night = listOf(
        "El día de hoy ya sumó.",
        "Tiempo para bajar el ritmo.",
        "Cierra el día con tranquilidad.",
        "Descanso bien merecido.",
        "Todo a su debido tiempo.",
        "Paz para lo que viene.",
        "Mañana será otro buen día."
    )

    // Variaciones sutiles por lenguaje visual de tema
    private val themePhrases = mapOf(
        LauncherTheme.NOTHING to listOf(
            "Enfoque en lo esencial.",
            "Paso a paso, vas bien.",
            "Hoy también cuenta.",
            "A tu ritmo, con intención.",
            "Que fluya el día."
        ),
        LauncherTheme.SENSE to listOf(
            "Que fluya el día con calma.",
            "Paso a paso, vas bien.",
            "Hoy también cuenta.",
            "A tu ritmo, con intención.",
            "Tiempo de avanzar tranquilos."
        ),
        LauncherTheme.TOUCHWIZ to listOf(
            "Que todo fluya de forma natural.",
            "Paso a paso, vas bien.",
            "Hoy también cuenta.",
            "A tu ritmo, con intención.",
            "Cada instante suma."
        ),
        LauncherTheme.OPTIMUS to listOf(
            "Un paso a la vez, con energía.",
            "Paso a paso, vas bien.",
            "Hoy también cuenta.",
            "A tu ritmo, con intención.",
            "Que tengas una excelente jornada."
        ),
        LauncherTheme.XPERIA to listOf(
            "Que el día avance ligero.",
            "Paso a paso, vas bien.",
            "Hoy también cuenta.",
            "A tu ritmo, con intención.",
            "Enfócate en lo que importa."
        ),
        LauncherTheme.RAZR to listOf(
            "Paso a paso, vas bien.",
            "A tu ritmo, con intención.",
            "Hoy también cuenta.",
            "Enfoque y determinación.",
            "Cada minuto cuenta."
        )
    )

    /**
     * Retorna un mensaje motivacional estable basado en el día, la franja horaria y el tema activo.
     * No utiliza timers ni genera recomposiciones por segundo.
     */
    fun phraseFor(theme: LauncherTheme, date: LocalDate = LocalDate.now(), time: LocalTime = LocalTime.now()): String {
        val hour = time.hour
        val slotList = when {
            hour in 5..11 -> morning
            hour in 12..19 -> afternoon
            else -> night
        }
        val dayIndex = date.dayOfYear + theme.ordinal
        val themedList = themePhrases[theme]
        // Alternamos entre la lista contextual de franja y la lista tonal del tema de forma estable
        return if (dayIndex % 2 == 0 && themedList != null) {
            themedList[dayIndex % themedList.size]
        } else {
            slotList[dayIndex % slotList.size]
        }
    }
}
