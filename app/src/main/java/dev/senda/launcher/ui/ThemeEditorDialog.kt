package dev.senda.launcher.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.senda.launcher.HomeUiState
import dev.senda.launcher.LauncherViewModel
import dev.senda.launcher.data.*
import dev.senda.launcher.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private object ThemeEditorSize {
    val height = 520.dp; val preview = 174.dp; val controls = 210.dp
    val gap = 8.dp; val padding = 12.dp; val swatch = 48.dp; val radius = 16.dp
}

@Composable
fun ThemeEditorDialog(state: HomeUiState, viewModel: LauncherViewModel, onDismiss: () -> Unit) {
    var themeName by rememberSaveable { mutableStateOf(state.theme.name) }
    val theme = LauncherTheme.valueOf(themeName)
    var accentName by rememberSaveable { mutableStateOf((state.themeFinishes[themeName] ?: ThemeFinish()).accent.name) }
    var shapeName by rememberSaveable { mutableStateOf((state.themeFinishes[themeName] ?: ThemeFinish()).shape.name) }
    var iconsName by rememberSaveable { mutableStateOf((state.themeFinishes[themeName] ?: ThemeFinish()).icons.name) }
    var pack by rememberSaveable { mutableStateOf(state.iconPacks[themeName]) }
    var wallpaper by rememberSaveable { mutableStateOf(state.skinWallpapers[themeName]) }
    var useRetro by rememberSaveable { mutableStateOf(state.retroEnabled[themeName] == true) }
    var section by rememberSaveable { mutableIntStateOf(0) }
    var chooseTheme by remember { mutableStateOf(false) }
    var showIdentity by rememberSaveable { mutableStateOf(false) }
    val finish = ThemeFinish(AccentChoice.valueOf(accentName), ShapeChoice.valueOf(shapeName), IconTreatment.valueOf(iconsName))
    val scope = rememberCoroutineScope()
    val bodyScroll = rememberScrollState()
    val compact = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> if (uri != null) wallpaper = uri.toString() }
    val libraryPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let(viewModel::importRetroLibrary) }
    val library = state.retroLibrary[themeName]
    val activeLibrary = library?.takeIf { useRetro }
    val iconPreview by produceState<Pair<Boolean, IconPackMapping?>>(true to null, pack, state.availableIconPacks) {
        value = true to null
        value = false to viewModel.previewIconPack(pack)
    }
    fun selectTheme(candidate: LauncherTheme) {
        themeName = candidate.name; accentName = (state.themeFinishes[themeName] ?: ThemeFinish()).accent.name
        shapeName = (state.themeFinishes[themeName] ?: ThemeFinish()).shape.name
        iconsName = (state.themeFinishes[themeName] ?: ThemeFinish()).icons.name
        pack = state.iconPacks[themeName]; wallpaper = state.skinWallpapers[themeName]
        useRetro = state.retroEnabled[themeName] == true
        viewModel.clearThemeError(); chooseTheme = false
    }
    AuraAlertDialog(onDismissRequest = { if (!state.themeSaveBusy) onDismiss() }, title = { Text("Temas de Aura") }, text = {
        Column(Modifier.heightIn(max = ThemeEditorSize.height).then(if (compact) Modifier.verticalScroll(bodyScroll) else Modifier), verticalArrangement = Arrangement.spacedBy(ThemeEditorSize.gap)) {
            Box {
                OutlinedButton(onClick = { chooseTheme = true }, enabled = !state.themeSaveBusy, modifier = Modifier.fillMaxWidth()) { Text("${theme.title} ▾", maxLines = 1) }
                DropdownMenu(chooseTheme, { chooseTheme = false }, Modifier.heightIn(max = ThemeEditorSize.controls)) {
                    LauncherTheme.entries.forEach { candidate -> DropdownMenuItem(text = { Text(candidate.title) }, onClick = { selectTheme(candidate) }) }
                }
            }
            Text("Vista previa · Inicio cambia al aplicar", style = MaterialTheme.typography.labelSmall)
            AuraTheme(theme, isSystemInDarkTheme(), state.appearance.highContrast, finish, reduceMotion = state.visualSettings.reduceMotion) {
                CompositionLocalProvider(LocalIconPack provides iconPreview.second, LocalAppearance provides AppearanceSettings(iconSize = 32), LocalRetroAssets provides activeLibrary) {
                    ThemePreview(theme, wallpaper ?: activeLibrary?.wallpapers?.firstOrNull()?.uri, state, viewModel)
                }
            }
            TabRow(section) {
                listOf("Acabado", "Iconos", "Fondo").forEachIndexed { index, label -> Tab(section == index, onClick = { section = index }, modifier = Modifier.heightIn(min = 48.dp)) { Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis) } }
            }
            Column(Modifier.heightIn(max = ThemeEditorSize.controls).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(ThemeEditorSize.gap)) {
                when (section) {
                    0 -> {
                        TextButton(onClick = { showIdentity = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Explorar identidad y clima") }
                        Text(theme.subtitle, style = MaterialTheme.typography.bodySmall)
                        if (themeName in RetroAssets.themes) {
                            RetroLibraryToggle(library, useRetro, state.retroImportBusy || state.themeSaveBusy, { useRetro = it }, { libraryPicker.launch(arrayOf("application/zip", "application/x-zip-compressed", "application/octet-stream")) })
                            state.retroImportMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        }
                        TextButton(onClick = { accentName = AccentChoice.DEFAULT.name; shapeName = ShapeChoice.DEFAULT.name; iconsName = IconTreatment.AUTO.name; pack = null; wallpaper = null; useRetro = false }, enabled = !state.themeSaveBusy) { Text(if (theme == LauncherTheme.NOTHING) "Restablecer estilo Nothing" else "Restablecer estilo del tema") }
                        Text("Acento: ${finish.accent.label}", style = MaterialTheme.typography.labelLarge)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(ThemeEditorSize.gap)) {
                            items(AccentChoice.entries) { accent ->
                                Box(Modifier.size(ThemeEditorSize.swatch).background(accent.argb?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                    .clickable(enabled = !state.themeSaveBusy) { accentName = accent.name }
                                    .semantics { contentDescription = "Acento ${accent.label}"; selected = finish.accent == accent }, contentAlignment = Alignment.Center) {
                                    if (finish.accent == accent) Text("✓", color = if (accent.argb == null) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black)
                                }
                            }
                        }
                        Text("Forma del contenedor", style = MaterialTheme.typography.labelLarge)
                        ShapeChoice.entries.forEach { shape ->
                            TextButton(onClick = { shapeName = shape.name }, enabled = !state.themeSaveBusy, modifier = Modifier.fillMaxWidth()) {
                                RadioButton(shapeName == shape.name, null); Text(shape.label, Modifier.weight(1f))
                            }
                        }
                    }
                    1 -> {
                        Text("Estilo de iconos", style = MaterialTheme.typography.labelLarge)
                        IconTreatment.entries.forEach { choice ->
                            TextButton(onClick = { iconsName = choice.name }, enabled = !state.themeSaveBusy) { RadioButton(iconsName == choice.name, null); Text(choice.label) }
                        }
                        Text("Temáticos: acabado de ${theme.title}. Normales: iconos de cada aplicación, sin recolor ni máscara. Se guarda por tema.", style = MaterialTheme.typography.bodySmall)
                        Text("Recursos para iconos temáticos", style = MaterialTheme.typography.labelLarge)
                        val resourceLabel = if (activeLibrary == null) "Recursos de Aura y Android" else "Mi colección y recursos de Aura"
                        TextButton(onClick = { pack = null }, enabled = !state.themeSaveBusy && finish.icons != IconTreatment.ORIGINAL) { Text(if (pack == null) "✓ $resourceLabel" else resourceLabel) }
                        if (activeLibrary != null) Text("Los iconos importados conservan sus proporciones y transparencia. Las apps sin equivalencia usan el acabado de Aura. Normales muestra los iconos actuales de las apps.", style = MaterialTheme.typography.bodySmall)
                        state.availableIconPacks.forEach { option ->
                            TextButton(onClick = { pack = option.packageName }, enabled = !state.themeSaveBusy && finish.icons != IconTreatment.ORIGINAL) { Text(if (pack == option.packageName) "✓ ${option.label}" else option.label) }
                        }
                        if (state.availableIconPacks.isEmpty()) Text("No hay paquetes compatibles instalados.", style = MaterialTheme.typography.bodySmall)
                        if (iconPreview.first) LinearProgressIndicator(Modifier.fillMaxWidth())
                        else if (pack != null && iconPreview.second == null) Text("Paquete no disponible o no compatible. La vista previa usa los originales.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    else -> {
                        if (activeLibrary != null) RetroWallpaperGallery(activeLibrary, wallpaper) { wallpaper = it }
                        TextButton(onClick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, enabled = !state.themeSaveBusy) { Text("Elegir imagen de fondo") }
                        TextButton(onClick = { wallpaper = null }, enabled = !state.themeSaveBusy) { Text("Usar fondo del tema") }
                        Text("El fondo elegido se copia al almacenamiento privado al aplicar. Cancelar conserva tu configuración anterior.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (state.appearance.highContrast) Text("El alto contraste global sustituye colores y fondos en la vista previa y en Inicio.", style = MaterialTheme.typography.bodySmall)
                state.themeSaveError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }, confirmButton = {
        TextButton(onClick = { scope.launch { if (viewModel.applyTheme(theme, finish, pack, wallpaper, useRetro)) onDismiss() } }, enabled = !state.themeSaveBusy && !state.retroImportBusy && !iconPreview.first && !state.isLoading) { Text(if (state.themeSaveBusy) "Aplicando…" else "Aplicar tema") }
    }, dismissButton = { TextButton(onClick = onDismiss, enabled = !state.themeSaveBusy) { Text("Cancelar") } })
    if (showIdentity) ThemeIdentityDialog(theme, finish, state.visualSettings.reduceMotion, state.appearance.highContrast) { showIdentity = false }
}

@Composable
private fun ThemePreview(theme: LauncherTheme, wallpaper: String?, state: HomeUiState, viewModel: LauncherViewModel) {
    val context = LocalContext.current
    val bitmap = rememberLocalImage(wallpaper, 480)
    val style = LocalSkinStyle.current
    val highContrast = state.appearance.highContrast
    val apps = remember(state.apps, state.favoritePackages) { state.favoritePackages.mapNotNull { name -> state.apps.firstOrNull { it.packageName == name } }.ifEmpty { state.apps }.take(3) }
    Box(Modifier.fillMaxWidth().height(ThemeEditorSize.preview).clip(RoundedCornerShape(ThemeEditorSize.radius)).background(style.wallpaper).semantics { contentDescription = "Vista previa de ${theme.title}" }) {
        if (bitmap != null && !highContrast) Image(bitmap!!, null, Modifier.matchParentSize(), contentScale = ContentScale.Crop)
        Box(Modifier.matchParentSize().background(if (bitmap != null && !highContrast) wallpaperReadabilityScrim() else style.wallpaper).then(if (bitmap == null || highContrast) Modifier.skinPattern(style) else Modifier))
        Column(Modifier.padding(ThemeEditorSize.padding), verticalArrangement = Arrangement.spacedBy(ThemeEditorSize.gap)) {
            if (LocalIndustrialStyle.current.enabled) DotClock("09:41", Modifier.width(140.dp).height(30.dp)) else if (LocalRetroStyle.current.enabled) RetroClock("09:41", compact = true) else Text("Aura · 09:41", style = MaterialTheme.typography.titleSmall)
            apps.forEach { app ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(ThemeEditorSize.gap)) {
                    AppIcon(app, state.skinIcons["${theme.name}_${app.packageName}"], viewModel::icon)
                    Text(app.label, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (apps.isEmpty()) Text("Tus aplicaciones aparecerán aquí", style = MaterialTheme.typography.bodySmall)
        }
    }
}
