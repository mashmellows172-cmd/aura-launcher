package dev.senda.launcher.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import dev.senda.launcher.ui.theme.icons.FamilyIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Manual choices become bounded local PNGs, compatible with existing per-theme overrides and backup9. */
internal suspend fun saveFamilyIcon(context: Context, theme: LauncherTheme, icon: FamilyIcon): String = withContext(Dispatchers.IO) {
    val directory = File(context.filesDir, "family-icons").apply { check(isDirectory || mkdirs()) }
    val destination = File(directory, "v1-${theme.name}-${icon.name}.png")
    if (!destination.isFile || destination.length() == 0L) {
        val temporary = File.createTempFile("family-", ".png", directory)
        try {
            val drawable = requireNotNull(context.getDrawable(icon.resource(theme)))
            val bitmap = drawable.toBitmap(128, 128, Bitmap.Config.ARGB_8888)
            temporary.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
            // Multiple apps can choose the same immutable artwork without rewriting an active file.
            check(temporary.renameTo(destination) || destination.isFile && destination.length() > 0L)
        } finally { temporary.delete() }
    }
    android.net.Uri.fromFile(destination).toString()
}
