package dev.senda.launcher.data

import android.content.Context

/** Bridges the two existing preference files into Aura's portable backup. */
object BackupExtras {
    val keys = setOf("unlock_style", "unlock_duration", "unlock_intensity", "performance_light")
    private val styles = setOf("AUTO", "NONE", "SLIDE", "CRT", "FOLD", "ORBITAL", "RIPPLE", "RIBBON")

    fun validate(key: String, value: Any) {
        require(when (key) {
            "unlock_style" -> value is String && value in styles
            "unlock_duration" -> value is Int && value in 200..650
            "unlock_intensity" -> value is Int && value in 0..100
            "performance_light" -> value is Boolean
            else -> false
        }) { "Ajuste de desbloqueo o rendimiento inválido" }
    }

    fun snapshot(context: Context): Map<String, Any> {
        val unlock = context.getSharedPreferences("aura_unlock", Context.MODE_PRIVATE)
        val intensity = unlock.getFloat("intensity", .7f)
        return mapOf(
            "unlock_style" to (unlock.getString("style", "AUTO")?.takeIf { it in styles } ?: "AUTO"),
            "unlock_duration" to unlock.getInt("duration", 420).coerceIn(200, 650),
            "unlock_intensity" to ((if (intensity.isFinite()) intensity else .7f).coerceIn(0f, 1f) * 100).toInt(),
            "performance_light" to context.getSharedPreferences("aura_performance", Context.MODE_PRIVATE).getBoolean("light", false),
        )
    }

    fun restore(context: Context, settings: Map<String, Any>) {
        val unlock = context.getSharedPreferences("aura_unlock", Context.MODE_PRIVATE).edit()
        settings["unlock_style"]?.let { unlock.putString("style", it as String) }
        settings["unlock_duration"]?.let { unlock.putInt("duration", it as Int) }
        settings["unlock_intensity"]?.let { unlock.putFloat("intensity", (it as Int) / 100f) }
        check(unlock.commit()) { "No se pudo guardar el desbloqueo" }
        settings["performance_light"]?.let {
            check(context.getSharedPreferences("aura_performance", Context.MODE_PRIVATE).edit().putBoolean("light", it as Boolean).commit()) {
                "No se pudo guardar el modo ligero"
            }
        }
    }
}
