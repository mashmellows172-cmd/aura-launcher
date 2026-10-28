package dev.senda.launcher.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import dev.senda.launcher.data.loadLocalImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.RetroThemeAssets
import dev.senda.launcher.ui.theme.skin
import dev.senda.launcher.ui.theme.skinPattern
import dev.senda.launcher.ui.theme.wallpaper

private data class WallpaperEntry(
    val id: String,
    val label: String,
    val theme: LauncherTheme,
    val source: String,
    val uri: String?,
    val displayUri: String? = uri,
)

/** Browses existing resources only. Selection changes the editor draft, never base preferences. */
@Composable
internal fun ThemeWallpaperGallery(
    theme: LauncherTheme,
    libraries: Map<String, RetroThemeAssets>,
    selectedUri: String?,
    collectionEnabled: Boolean,
    onSelect: (String?) -> Unit,
    onChoosePhoto: () -> Unit,
    onDismiss: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable(theme) { mutableStateOf(theme.name) }
    var expandedId by rememberSaveable { mutableStateOf<String?>(null) }
    val entries = remember(theme, libraries, selectedUri, collectionEnabled) {
        val default = libraries[theme.name]?.wallpapers?.firstOrNull()?.takeIf { collectionEnabled }
        val imported = libraries.flatMap { (name, library) ->
            val family = LauncherTheme.entries.firstOrNull { it.name == name }
            if (family == null) emptyList() else library.wallpapers.map { wall ->
                WallpaperEntry("$name:${wall.uri}", wall.label, family, library.label, wall.uri)
            }
        }
        buildList {
            add(WallpaperEntry("default", if (default == null) "Fondo del tema" else "Predeterminado de mi colección", theme,
                if (default == null) "Diseño de Aura · no es una imagen OEM" else libraries[theme.name]?.label.orEmpty(), null, default?.uri))
            if (selectedUri != null && imported.none { it.uri == selectedUri }) {
                add(WallpaperEntry("current", "Tu fondo actual", theme, "Imagen personal", selectedUri))
            }
            addAll(imported)
        }
    }
    val filters = remember(entries, theme) {
        listOf(theme) + entries.map { it.theme }.distinct().filter { it != theme }.sortedBy { it.title }
    }
    val shown = remember(entries, filter, query) {
        val search = query.trim()
        entries.filter { (filter == "ALL" || it.theme.name == filter) &&
            (search.isEmpty() || "${it.label} ${it.theme.title} ${it.source}".contains(search, ignoreCase = true)) }
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).imePadding().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Fondos de pantalla", Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = onDismiss) { Text("Cerrar") }
                }
                OutlinedTextField(query, { query = it.take(100) }, label = { Text("Buscar por nombre, tema o colección") },
                    singleLine = true, modifier = Modifier.fillMaxWidth())
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { FilterChip(filter == "ALL", { filter = "ALL" }, label = { Text("Todos") }) }
                    items(filters, key = { it.name }) { family ->
                        FilterChip(filter == family.name, { filter = family.name }, label = { Text(family.title) })
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("${shown.size} fondos", Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                    TextButton(onClick = onChoosePhoto) { Text("Mis imágenes") }
                }
                Text("Toca una miniatura para ampliarla. El fondo se aplica al guardar la variante.", style = MaterialTheme.typography.bodySmall)
                if (shown.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(if (query.isBlank() && entries.size == 1) "Importa una colección retro o elige una imagen de tu galería." else "No hay fondos que coincidan con la búsqueda.")
                    }
                } else LazyVerticalGrid(GridCells.Adaptive(128.dp), Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(shown, key = { it.id }, contentType = { "wallpaper" }) { entry ->
                        val active = entry.uri == selectedUri
                        val outline = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        OutlinedCard(onClick = { expandedId = entry.id }, modifier = Modifier.fillMaxWidth().semantics {
                            selected = active
                            contentDescription = "${entry.label}, ${entry.theme.title}${if (active) ", seleccionado" else ""}. Ampliar fondo"
                        }) {
                            Box(Modifier.fillMaxWidth().aspectRatio(.7f).border(if (active) 3.dp else 1.dp, outline, RoundedCornerShape(12.dp)).padding(3.dp).clip(RoundedCornerShape(9.dp))) {
                                WallpaperArtwork(entry, Modifier.fillMaxSize(), 256)
                            }
                            Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(entry.label, style = MaterialTheme.typography.labelLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(if (active) "Seleccionado" else entry.theme.title, style = MaterialTheme.typography.labelSmall,
                                    color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
    val expanded = entries.firstOrNull { it.id == expandedId }
    if (expanded != null) WallpaperExpandedPreview(expanded, onDismiss = { expandedId = null }, onUse = {
        onSelect(expanded.uri)
        expandedId = null
        onDismiss()
    })
}

@Composable
private fun WallpaperArtwork(entry: WallpaperEntry, modifier: Modifier, maxEdge: Int) {
    val image = rememberWallpaperImage(entry.displayUri, maxEdge)
    if (entry.displayUri != null) {
        Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            when (image) {
                is WallpaperImage.Ready -> Image(image.bitmap, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                WallpaperImage.Loading -> CircularProgressIndicator(Modifier.size(24.dp))
                WallpaperImage.Missing -> Text("Imagen no disponible", Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
    } else if (entry.theme == LauncherTheme.PS4) Ps4Wallpaper(true, modifier)
    else {
        val style = skin(entry.theme, isSystemInDarkTheme()).first
        Box(modifier.background(style.wallpaper).skinPattern(style))
    }
}

@Composable
private fun WallpaperExpandedPreview(entry: WallpaperEntry, onDismiss: () -> Unit, onUse: () -> Unit) {
    // Explicit image choices must decode before they can be applied. Default remains selectable.
    val image = rememberWallpaperImage(entry.displayUri, 1280)
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(entry.label, style = MaterialTheme.typography.titleLarge)
                Text("${entry.theme.title} · ${entry.source}", style = MaterialTheme.typography.bodySmall)
                Box(Modifier.weight(1f).fillMaxWidth().clip(MaterialTheme.shapes.large)) {
                    if (entry.displayUri != null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            when (image) {
                                is WallpaperImage.Ready -> Image(image.bitmap, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                WallpaperImage.Loading -> CircularProgressIndicator()
                                WallpaperImage.Missing -> Text("No se pudo abrir esta imagen")
                            }
                        }
                    } else WallpaperArtwork(entry, Modifier.fillMaxSize(), 1280)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onDismiss) { Text("Volver") }
                    Button(onClick = onUse, enabled = entry.uri == null || image is WallpaperImage.Ready) { Text("Usar este fondo") }
                }
            }
        }
    }
}

private sealed interface WallpaperImage {
    data object Loading : WallpaperImage
    data object Missing : WallpaperImage
    data class Ready(val bitmap: ImageBitmap) : WallpaperImage
}

/** Uses the same bounded local decoder as Home, retaining explicit loading/failure state. */
@Composable
private fun rememberWallpaperImage(uri: String?, maxEdge: Int): WallpaperImage = key(uri, maxEdge) {
    val context = LocalContext.current
    val image by produceState<WallpaperImage>(if (uri == null) WallpaperImage.Missing else WallpaperImage.Loading) {
        value = if (uri == null) WallpaperImage.Missing else withContext(Dispatchers.IO) {
            loadLocalImage(context, uri.toUri(), maxEdge)?.let { WallpaperImage.Ready(it) } ?: WallpaperImage.Missing
        }
    }
    image
}
