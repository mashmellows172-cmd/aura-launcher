package dev.senda.launcher.data

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.AtomicFile
import androidx.compose.runtime.staticCompositionLocalOf
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID
import java.util.zip.ZipInputStream

data class RetroWallpaper(val label: String, val uri: String)
data class RetroThemeAssets(
    val label: String,
    val note: String,
    val icons: Map<String, String>,
    val packages: Map<String, String>,
    val wallpapers: List<RetroWallpaper>,
) {
    fun icon(packageName: String, category: String?): String? = packages[packageName] ?: icons[category]
}

val LocalRetroAssets = staticCompositionLocalOf<RetroThemeAssets?> { null }

/** Optional user library. The open source APK contains no manufacturer images or audio. All IO is off main. */
class RetroAssets(private val context: Context) {
    private val root = File(context.filesDir, "retro-library")
    private val pointer get() = AtomicFile(File(root, "active"))

    companion object {
        val themes = setOf("NOTHING", "SENSE", "TOUCHWIZ", "OPTIMUS", "XPERIA", "RAZR", "PS4")
        private val categories = setOf("PHONE", "CAMERA", "BROWSER", "MUSIC", "VIDEO", "MESSAGE", "CONTACTS", "CLOCK", "CALENDAR", "SETTINGS", "FILES", "GALLERY", "CALCULATOR", "STORE", "EMAIL", "MAPS", "NOTES", "WEATHER", "RADIO", "RECORDER")
        private val imagePath = Regex("(?:NOTHING|SENSE|TOUCHWIZ|OPTIMUS|XPERIA|RAZR|PS4)/(?:icons|wallpapers)/[A-Za-z0-9_-]+\\.(?:png|jpg|webp)")
        private val packageName = Regex("[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+")
    }

    fun load(): Map<String, RetroThemeAssets> = runCatching {
        val id = pointer.openRead().use { String(it.readBytes(), Charsets.UTF_8) }
        require(Regex("[a-f0-9-]{36}").matches(id))
        parse(File(root, id))
    }.getOrDefault(emptyMap())

    fun importLibrary(source: Uri): Map<String, RetroThemeAssets> {
        root.mkdirs()
        val stage = File(root, UUID.randomUUID().toString()).apply { check(mkdir()) }
        var committed = false
        try {
            var count = 0
            var total = 0L
            val seen = mutableSetOf<String>()
            context.contentResolver.openInputStream(source)?.use { stream ->
                ZipInputStream(stream.buffered()).use { zip ->
                    while (true) {
                        val entry = zip.nextEntry ?: break
                        require(++count <= 256) { "La biblioteca contiene demasiados archivos" }
                        require(!entry.isDirectory && seen.add(entry.name)) { "Entrada duplicada o directorio no permitido" }
                        val manifest = entry.name == "manifest.json"
                        require(manifest || imagePath.matches(entry.name)) { "La biblioteca contiene una ruta no permitida" }
                        val limit = if (manifest) 128 * 1024 else if ("/icons/" in entry.name) 1024 * 1024 else 6 * 1024 * 1024
                        val bytes = ByteArrayOutputStream().use { out ->
                            val buffer = ByteArray(8192)
                            while (true) {
                                val size = zip.read(buffer)
                                if (size < 0) break
                                total += size
                                require(total <= 64 * 1024 * 1024 && out.size() + size <= limit) { "La biblioteca supera el tamaño permitido" }
                                out.write(buffer, 0, size)
                            }
                            out.toByteArray()
                        }
                        if (!manifest) validateImage(bytes, "/icons/" in entry.name)
                        // The strict path grammar above excludes traversal, absolute paths and executable files.
                        File(stage, entry.name).apply { parentFile?.mkdirs(); writeBytes(bytes) }
                        zip.closeEntry()
                    }
                }
            } ?: error("No se pudo abrir la biblioteca")
            val catalog = parse(stage)
            val output = pointer.startWrite()
            try {
                output.write(stage.name.toByteArray(Charsets.UTF_8))
                pointer.finishWrite(output)
            } catch (error: Exception) {
                pointer.failWrite(output)
                throw error
            }
            committed = true
            // Keep the previous generation while Compose finishes outstanding image reads.
            runCatching { root.listFiles()?.filter { it.isDirectory && it != stage }?.sortedByDescending { it.lastModified() }?.drop(1)?.forEach { it.deleteRecursively() } }
            return catalog
        } finally {
            if (!committed) stage.deleteRecursively()
        }
    }

    private fun validateImage(bytes: ByteArray, icon: Boolean) {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        val max = if (icon) 1024 else 4096
        require(bounds.outWidth in 1..max && bounds.outHeight in 1..max) { "Imagen dañada o dimensiones no permitidas" }
        val options = BitmapFactory.Options().apply { inSampleSize = imageSampleSize(bounds.outWidth, bounds.outHeight, 128) }
        val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        require(decoded != null) { "No se pudo decodificar una imagen" }
        decoded.recycle()
    }

    private fun parse(directory: File): Map<String, RetroThemeAssets> {
        val file = File(directory, "manifest.json")
        require(file.length() in 1..128 * 1024) { "Falta el manifiesto de la biblioteca" }
        val text = file.readText(Charsets.UTF_8)
        // Cap nesting outside strings before JSONObject allocates a tree.
        var depth = 0; var quoted = false; var escaped = false
        text.forEach { c ->
            if (quoted) { if (escaped) escaped = false else if (c == '\\') escaped = true else if (c == '"') quoted = false }
            else when (c) { '"' -> quoted = true; '{', '[' -> { require(++depth <= 8) { "Manifiesto demasiado profundo" } }; '}', ']' -> require(--depth >= 0) }
        }
        require(!quoted && depth == 0) { "Manifiesto incompleto" }
        val json = JSONObject(text)
        require(json.getString("format") == "aura-retro-library" && json.getInt("version") == 1) { "Formato de biblioteca no compatible" }
        val entries = json.getJSONObject("themes")
        require(entries.length() in 1..themes.size) { "Número de temas no válido" }
        val used = mutableSetOf<String>()
        return entries.keys().asSequence().associateWith { theme ->
            require(theme in themes) { "Tema no compatible" }
            val item = entries.getJSONObject(theme)
            fun image(path: String, kind: String): String {
                require(imagePath.matches(path) && path.startsWith("$theme/$kind/")) { "Referencia de imagen no válida" }
                require(File(directory, path).isFile) { "Falta una imagen" }
                used.add(path)
                return Uri.fromFile(File(directory, path)).toString()
            }
            fun mapping(name: String, valid: (String) -> Boolean): Map<String, String> {
                val values = item.optJSONObject(name) ?: return emptyMap()
                require(values.length() <= 100) { "Demasiados iconos" }
                return values.keys().asSequence().associateWith { key ->
                    require(valid(key)) { "Asignación de icono no válida" }
                    image(values.getString(key), "icons")
                }
            }
            val walls = item.getJSONArray("wallpapers")
            require(walls.length() in 1..32) { "Número de fondos no válido" }
            fun label(value: String, limit: Int): String {
                require(value.length in 1..limit && value.none { it.isISOControl() }) { "Texto no válido" }
                return value
            }
            RetroThemeAssets(label(item.getString("label"), 80), label(item.getString("note"), 400),
                mapping("icons") { it in categories }, mapping("packages") { packageName.matches(it) },
                (0 until walls.length()).map { index -> val wall = walls.getJSONObject(index)
                    RetroWallpaper(label(wall.getString("label"), 80), image(wall.getString("path"), "wallpapers"))
                })
        }.also { require(used.size <= 255) { "Demasiados recursos" } }
    }
}
