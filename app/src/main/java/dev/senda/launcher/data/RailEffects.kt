package dev.senda.launcher.data

/** Shared IDs for persistence, backup validation and the real/preview alphabet rail. */
object RailEffects {
    val options = listOf("MAGNET" to "Imán", "WAVE" to "Onda", "LENS" to "Lupa", "CASCADE" to "Cascada", "ORBIT" to "Órbita", "SPRING" to "Resorte", "FADE" to "Suave", "NONE" to "Sin efecto")
    val ids = options.map { it.first }.toSet()
    fun canonical(value: String?): String = value?.takeIf { it in ids } ?: "MAGNET"
}
