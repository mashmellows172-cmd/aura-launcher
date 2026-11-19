package dev.senda.launcher.ui.theme.icons

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * Gestor y cache local de arte PS4 dinámico.
 * Almacena miniaturas en disco e in-memory para apps sin recurso estático compilado.
 */
object Ps4ArtStorage {
    private val memoryCache = ConcurrentHashMap<String, ImageBitmap>()
    private val negativeCache = ConcurrentHashMap<String, Boolean>()

    fun getCachedArt(context: Context, packageName: String): ImageBitmap? {
        memoryCache[packageName]?.let { return it }
        if (negativeCache[packageName] == true) return null

        val file = getArtFile(context, packageName)
        if (file.exists() && file.length() > 0) {
            return try {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    val imageBitmap = bitmap.asImageBitmap()
                    memoryCache[packageName] = imageBitmap
                    imageBitmap
                } else {
                    negativeCache[packageName] = true
                    null
                }
            } catch (_: Exception) {
                negativeCache[packageName] = true
                null
            }
        }
        return null
    }

    suspend fun saveAppArt(context: Context, packageName: String, bitmap: Bitmap): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = getArtFile(context, packageName)
            file.parentFile?.mkdirs()
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
            }
            memoryCache[packageName] = bitmap.asImageBitmap()
            negativeCache.remove(packageName)
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun fetchRemoteArt(context: Context, packageName: String, query: String): Boolean = withContext(Dispatchers.IO) {
        if (negativeCache[packageName] == true) return@withContext false
        val file = getArtFile(context, packageName)
        if (file.exists() && file.length() > 0) return@withContext true

        try {
            // Consulta de arte de juegos/apps optimizada con timeout corto
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val targetUrl = "https://raw.githubusercontent.com/mashmellows172-cmd/aura-launcher/main/artwork/ps4-cards/$packageName.png"
            val connection = (URL(targetUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = 3000
                readTimeout = 4000
                instanceFollowRedirects = true
            }
            if (connection.responseCode == 200) {
                val bitmap = connection.inputStream.use { BitmapFactory.decodeStream(it) }
                if (bitmap != null) {
                    saveAppArt(context, packageName, bitmap)
                    return@withContext true
                }
            }
            negativeCache[packageName] = true
            false
        } catch (_: Exception) {
            negativeCache[packageName] = true
            false
        }
    }

    private fun getArtFile(context: Context, packageName: String): File {
        val dir = File(context.filesDir, "ps4-artwork")
        return File(dir, "$packageName.png")
    }
}
