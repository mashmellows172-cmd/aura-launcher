package dev.senda.launcher.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.senda.launcher.data.RetroThemeAssets
import dev.senda.launcher.data.loadLocalImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Same bounded scrim in preview and home; light themes protect dark text with a white veil. */
internal fun wallpaperReadabilityScrim(protection: Int = 64, foreground: Color = Color.White): Brush {
    val alpha = protection.coerceIn(60, 90) / 100f
    val veil = if (foreground.luminance() > .5f) Color.Black else Color.White
    return Brush.verticalGradient(listOf(veil.copy(alpha = alpha), veil.copy(alpha = (alpha + .06f).coerceAtMost(.96f))))
}

@Composable
internal fun RetroLibraryToggle(library: RetroThemeAssets?, enabled: Boolean, busy: Boolean, onToggle: (Boolean) -> Unit, onImport: () -> Unit) {
    if (library != null) {
        Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).toggleable(value = enabled, enabled = !busy, role = Role.Switch, onValueChange = onToggle), verticalAlignment = Alignment.CenterVertically) {
            Text("Recursos de mi colección", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
            Switch(enabled, onCheckedChange = null, enabled = !busy)
        }
        Text("${library.label} · ${library.wallpapers.size} fondos · ${(library.icons.values + library.packages.values).distinct().size} iconos", style = MaterialTheme.typography.bodySmall)
        Text(library.note, style = MaterialTheme.typography.bodySmall)
    }
    TextButton(onClick = onImport, enabled = !busy, modifier = Modifier.heightIn(min = 48.dp)) {
        Text(if (busy) "Importando biblioteca…" else if (library == null) "Importar colección retro (ZIP)" else "Actualizar colección retro (ZIP)")
    }
    if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
}

@Composable
internal fun RetroWallpaperGallery(library: RetroThemeAssets, selected: String?, onSelect: (String) -> Unit) {
    Text("Fondos de ${library.label}", style = MaterialTheme.typography.labelLarge)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(library.wallpapers, key = { it.uri }) { wallpaper ->
            val context = LocalContext.current
            val bitmap by produceState<ImageBitmap?>(null, wallpaper.uri) {
                value = withContext(Dispatchers.IO) { loadLocalImage(context, wallpaper.uri.toUri(), 192) }
            }
            val active = selected == wallpaper.uri || (selected == null && wallpaper == library.wallpapers.first())
            Column(Modifier.width(88.dp).selectable(active, role = Role.RadioButton, onClick = { onSelect(wallpaper.uri) }).padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(72.dp, 88.dp).clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(if (active) 3.dp else 1.dp, if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)) {
                    bitmap?.let { Image(it, null, Modifier.fillMaxSize().padding(3.dp), contentScale = ContentScale.Crop) }
                }
                Text(wallpaper.label, style = MaterialTheme.typography.labelSmall, maxLines = 2)
            }
        }
    }
}
