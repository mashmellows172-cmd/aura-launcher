package dev.senda.launcher.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.asAndroidBitmap
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest

data class PreparedBackup(val settings: Map<String, Any>, val images: Map<String, ByteArray>, val summary: String)

/** Versioned, bounded JSON. No widget IDs, permissions, executable code or external paths. All IO runs off main. */
class SettingsBackup(private val context: Context) {
    companion object {
        private const val LIMIT = 24 * 1024 * 1024
        private val stringKeys = setOf("sound_family", "favorite_packages", "hidden_packages", "launcher_theme", "visual_motion", "weather_app_package", "home_rotation", "gesture_up", "gesture_down", "gesture_left", "gesture_right", "double_tap_lock", "rail_effect", "menu_effect", "block_order", "weather_motion_mode", "weather_scene_condition") + HomeBlock.entries.map { "block_anchor_${it.name}" }
        private val integerRanges = mapOf("sound_volume" to 0..100,"weather_motion_duration" to 3000..12000,"weather_motion_intensity" to 20..100,"visual_duration_ms" to 150..800, "scroll_motion_intensity" to 10..100,
            "rail_intensity" to 0..100,"rail_duration_ms" to 80..600,"rail_bubble_size" to 40..84,"rail_bubble_hold_ms" to 0..1000,
            "search_glow_strength" to 10..100, "appearance_text_size" to 14..24, "appearance_icon_size" to 32..60, "appearance_row_spacing" to 0..20) + HomeBlock.entries.flatMap { listOf("block_height_${it.name}" to 48..160, "block_text_${it.name}" to 12..20) }.toMap() + LauncherTheme.entries.associate { "skin_dim_${it.name}" to 60..90 }
        private val booleanKeys = setOf("sound_enabled", "sound_launch", "sound_menu", "sound_select", "sound_favorite", "sound_index", "sound_mute_media", "search_glow_enabled", "reduce_motion", "appearance_high_contrast", "header_show_alarm", "header_show_calendar", "header_show_motivational", "header_show_audio_widget", "rail_haptics") + HomeBlock.entries.map { "block_half_${it.name}" } + RetroAssets.themes.map { "retro_assets_$it" }
        private val themes = LauncherTheme.entries.map { it.name }.toSet()
        private val packagePattern = Regex("[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+")
        fun imageKey(key: String): Boolean = key == "profile_avatar" || themes.any { key == "skin_wallpaper_$it" || (key.startsWith("skin_icon_${it}_") && packagePattern.matches(key.removePrefix("skin_icon_${it}_"))) }
        fun allowed(key: String): Boolean = key in stringKeys || key in integerRanges || key in booleanKeys || imageKey(key) || themes.any { key == "icon_pack_$it" || key == "skin_accent_$it" || key == "skin_shape_$it" || key == "skin_icons_$it" }
        private fun validate(key: String, value: Any) {
            require(key.length <= 256) { "Nombre de ajuste demasiado largo" }
            require(allowed(key)) { "El respaldo contiene un ajuste desconocido: $key" }
            when {
                key in booleanKeys -> require(value is Boolean) { "Tipo de ajuste inválido" }
                key in integerRanges -> require(value is Int && value in integerRanges.getValue(key)) { "Valor de ajuste fuera de rango" }
                else -> {
                    require(value is String && value.length <= 200_000) { "Texto de ajuste inválido" }
                    when {
                        key == "block_order" -> require(value.split(',').size == HomeBlock.entries.size && value.split(',').toSet() == HomeBlock.entries.map { it.name }.toSet()) { "Orden de bloques inválido" }
                        key.startsWith("block_anchor_") -> require(value in BlockAnchor.entries.map { it.name }) { "Posición inválida" }
                        key == "sound_family" -> require(value in SoundFamily.entries.map { it.name }) { "Familia sonora no válida" }
                        key == "launcher_theme" -> require(value in themes) { "Tema desconocido" }
                        key == "weather_scene_condition" -> require(value in WeatherCondition.entries.map { it.name }) { "Escena del clima no válida" }
                        key == "weather_motion_mode" -> require(value in WeatherMotionMode.entries.map { it.name }) { "Animación del clima no válida" }
                        key == "double_tap_lock" -> require(value in LockGesture.entries.map { it.name }) { "Gesto de bloqueo no válido" }
                        key == "rail_effect" -> require(value in RailEffects.ids) { "Efecto A–Z no válido" }
                        key == "menu_effect" -> require(value in setOf("SPRING", "SLIDE", "FADE", "NONE")) { "Efecto de menú no válido" }
                        key == "home_rotation" -> require(value in HomeRotation.entries.map { it.name }) { "Rotación no válida" }
                        key.startsWith("gesture_") -> require(GestureBindings.valid(value)) { "Gesto no válido" }
                        key.startsWith("skin_accent_") -> require(value in AccentChoice.entries.map { it.name }) { "Acento no válido" }
                        key.startsWith("skin_icons_") -> require(value in IconTreatment.entries.map { it.name }) { "Estilo de iconos no válido" }
                        key.startsWith("skin_shape_") -> require(value in ShapeChoice.entries.map { it.name }) { "Forma no válida" }
                        key == "visual_motion" -> require(value in setOf("AURA_FLOW", "CUBE", "PRISM", "ORBIT", "RIPPLE", "GLIDE", "DEPTH", "FOLD", "WAVE", "NONE")) { "Perfil desconocido" }
                        key in setOf("favorite_packages", "hidden_packages") -> require(value.split('\n').size <= 2000 && value.split('\n').all { it.isEmpty() || packagePattern.matches(it) }) { "Lista de apps inválida" }
                        key == "weather_app_package" || key.startsWith("icon_pack_") -> require(value.isEmpty() || packagePattern.matches(value)) { "Paquete inválido" }
                        imageKey(key) -> require(value == "asset:$key") { "Referencia de imagen inválida" }
                    }
                }
            }
        }
    }

    fun export(settings: Map<String, Any>, destination: Uri): String {
        val values = JSONObject(); val assets = JSONObject(); var skipped = 0; var total = 0
        for ((key, value) in settings) {
            if (!allowed(key)) continue
            if (imageKey(key)) {
                val bitmap = loadLocalImage(context, Uri.parse(value as String), if (key.startsWith("skin_wallpaper_")) 1920 else 128)?.asAndroidBitmap()
                if (bitmap == null) { skipped++; continue }
                val bytes = ByteArrayOutputStream().use { stream -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); stream.toByteArray() }
                total += bytes.size
                require(total <= 16 * 1024 * 1024) { "Las imágenes superan 16 MB. Reduce las imágenes personalizadas e inténtalo de nuevo." }
                assets.put(key, Base64.encodeToString(bytes, Base64.NO_WRAP)); values.put(key, "asset:$key")
            } else { validate(key, value); values.put(key, value) }
        }
        val root = JSONObject().put("format", "aura-settings").put("version", 12).put("settings", values).put("assets", assets).put("skippedImages", skipped)
        val bytes = root.toString().toByteArray(Charsets.UTF_8)
        require(bytes.size <= LIMIT) { "El respaldo supera 24 MB" }
        context.contentResolver.openOutputStream(destination, "wt")?.use { it.write(bytes) } ?: error("No se pudo abrir el archivo de destino")
        return if (skipped == 0) "Respaldo guardado con ${assets.length()} imágenes." else "Respaldo guardado. Se omitieron $skipped imágenes que ya no estaban disponibles."
    }

    fun prepare(source: Uri): PreparedBackup {
        val bytes = context.contentResolver.openInputStream(source)?.use { input ->
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            var size = 0
            while (true) {
                val count = input.read(buffer)
                if (count == -1) break
                size += count; require(size <= LIMIT) { "El archivo supera 24 MB" }
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        } ?: error("No se pudo abrir el archivo")
        require(bytes.size <= LIMIT) { "El archivo supera 24 MB" }
        val text = String(bytes, Charsets.UTF_8)
        // Reject pathological nesting/collections before JSONObject allocates a tree.
        var quoted = false; var escaped = false; var depth = 0; var separators = 0
        for (char in text) {
            if (quoted) {
                if (escaped) escaped = false else if (char == '\\') escaped = true else if (char == '"') quoted = false
            } else when (char) {
                '"' -> quoted = true
                '{', '[' -> { depth++; require(depth <= 8) { "Estructura de respaldo demasiado profunda" } }
                '}', ']' -> { depth--; require(depth >= 0) { "Estructura de respaldo inválida" } }
                ',', ':' -> { separators++; require(separators <= 12_000) { "Demasiados elementos en el archivo" } }
            }
        }
        require(!quoted && depth == 0) { "Archivo JSON incompleto" }
        val root = JSONObject(text)
        require(root.getString("format") == "aura-settings" && root.getInt("version") in 1..12) { "Formato o versión de respaldo no compatible" }
        val json = root.getJSONObject("settings"); require(json.length() <= 2500) { "Demasiados ajustes" }
        val values = json.keys().asSequence().associateWith { key -> json.get(key).also { validate(key, it) } }
        val jsonImages = root.getJSONObject("assets"); require(jsonImages.length() <= 2000) { "Demasiadas imágenes" }
        var total = 0
        val images = jsonImages.keys().asSequence().associateWith { key ->
            require(imageKey(key) && values[key] == "asset:$key") { "Imagen sin referencia válida" }
            val image = Base64.decode(jsonImages.getString(key), Base64.NO_WRAP)
            total += image.size; require(total <= 16 * 1024 * 1024) { "Las imágenes superan 16 MB" }
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(image, 0, image.size, bounds)
            require(bounds.outWidth in 1..5096 && bounds.outHeight in 1..5096) { "Imagen dañada o demasiado grande" }
            // A valid header alone does not prove the image can be decoded.
            val decoded = BitmapFactory.decodeByteArray(image, 0, image.size, BitmapFactory.Options().apply { inSampleSize = imageSampleSize(bounds.outWidth, bounds.outHeight, 128) })
            require(decoded != null) { "Imagen dañada" }; decoded.recycle()
            image
        }
        require(values.keys.filter(::imageKey).all(images::containsKey)) { "Faltan imágenes del respaldo" }
        val favorites = (values["favorite_packages"] as? String).orEmpty().lineSequence().count { it.isNotBlank() }
        val hidden = (values["hidden_packages"] as? String).orEmpty().lineSequence().count { it.isNotBlank() }
        val skipped = root.optInt("skippedImages", 0).coerceAtLeast(0)
        return PreparedBackup(values, images, "$favorites favoritas · $hidden apps ocultas · ${images.size} imágenes.\nReemplaza los ajustes actuales. Los widgets existentes se conservan; los paquetes de iconos deben estar instalados. La biblioteca retro se importa por separado." + if (skipped > 0) "\nEl archivo omitió $skipped imágenes no disponibles." else "")
    }

    fun materialize(backup: PreparedBackup): Map<String, Any> {
        val dir = File(context.filesDir, "backup-images").apply { mkdirs() }
        return backup.settings.mapValues { (key, value) ->
            if (!imageKey(key)) value else {
                val bytes = backup.images.getValue(key)
                val digest = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
                val file = File(dir, "$digest.png")
                if (!file.exists()) file.writeBytes(bytes)
                Uri.fromFile(file).toString()
            }
        }
    }
}
