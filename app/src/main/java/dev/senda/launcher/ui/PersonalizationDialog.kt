package dev.senda.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.senda.launcher.HomeUiState
import dev.senda.launcher.LauncherViewModel
import dev.senda.launcher.data.AppearanceSettings

private object PersonalizationSpace {
    val height = 520.dp
    val bodyHeight = 420.dp
    val gap = 12.dp
    val target = 48.dp
}

@Composable
fun PersonalizationDialog(state: HomeUiState, viewModel: LauncherViewModel, onExport: () -> Unit, onImport: () -> Unit, onDismiss: () -> Unit) {
    var section by rememberSaveable { mutableIntStateOf(0) }
    var textSize by rememberSaveable { mutableIntStateOf(state.appearance.textSize) }
    var iconSize by rememberSaveable { mutableIntStateOf(state.appearance.iconSize) }
    var spacing by rememberSaveable { mutableIntStateOf(state.appearance.rowSpacing) }
    var contrast by rememberSaveable { mutableStateOf(state.appearance.highContrast) }
    // A restore also refreshes the local editor instead of keeping stale draft values.
    LaunchedEffect(state.appearance) {
        textSize = state.appearance.textSize; iconSize = state.appearance.iconSize
        spacing = state.appearance.rowSpacing; contrast = state.appearance.highContrast
    }
    AuraAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Personalizar Aura") },
        text = {
            Column(Modifier.heightIn(max = PersonalizationSpace.height), verticalArrangement = Arrangement.spacedBy(PersonalizationSpace.gap)) {
                TabRow(selectedTabIndex = section) {
                    listOf("Tamaños", "Iconos", "Respaldo").forEachIndexed { index, title ->
                        Tab(selected = section == index, onClick = { section = index }, text = { Text(title, style = MaterialTheme.typography.labelSmall, maxLines = 1) })
                    }
                }
                Column(Modifier.heightIn(max = PersonalizationSpace.bodyHeight).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(PersonalizationSpace.gap)) {
                if (section == 0) {
                Text("Ajusta la lista. Se respeta el tamaño de fuente de Android.")
                SettingSlider("Texto de apps", textSize, 14..24, "sp") { textSize = it }
                SettingSlider("Tamaño de iconos", iconSize, 32..60, "dp") { iconSize = it }
                SettingSlider("Separación entre apps", spacing, 0..20, "dp") { spacing = it }
                Row(Modifier.fillMaxWidth().heightIn(min = PersonalizationSpace.target), verticalAlignment = Alignment.CenterVertically) {
                    Text("Alto contraste", Modifier.weight(1f))
                    Switch(checked = contrast, onCheckedChange = { contrast = it }, modifier = Modifier.semantics { contentDescription = "Alto contraste" })
                }
                Text("Alto contraste usa fondo uniforme y texto de máximo contraste; oculta el wallpaper mientras está activo.", style = MaterialTheme.typography.bodySmall)
                Row {
                    TextButton(onClick = {
                        viewModel.setAppearance(AppearanceSettings(textSize, iconSize, spacing, contrast))
                    }, enabled = !state.backupBusy) { Text("Aplicar tamaños") }
                    TextButton(onClick = { textSize = 16; iconSize = 42; spacing = 0; contrast = false }) { Text("Valores iniciales") }
                }
                }
                if (section == 1) {
                Text("Iconos · ${state.theme.title}", style = MaterialTheme.typography.titleSmall)
                Text("La selección se guarda para este tema. Los iconos PNG individuales tienen prioridad; las apps sin correspondencia conservan su icono.", style = MaterialTheme.typography.bodySmall)
                val selected = state.iconPacks[state.theme.name]
                PackChoice("Iconos originales", selected == null, !state.iconPackLoading && !state.backupBusy) { viewModel.setIconPack(null) }
                state.availableIconPacks.forEach { pack ->
                    PackChoice(pack.label, selected == pack.packageName, !state.iconPackLoading && !state.backupBusy) { viewModel.setIconPack(pack.packageName) }
                }
                if (state.iconPackLoading) Text("Leyendo paquetes de iconos…")
                else if (state.availableIconPacks.isEmpty()) Text("No hay paquetes compatibles instalados. Admite mapeos appfilter de paquetes ADW, Nova, GO y Lawnchair; no genera iconos para apps ausentes.", style = MaterialTheme.typography.bodySmall)
                state.iconPackStatus?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
                if (section == 2) {
                Text("Respaldo local", style = MaterialTheme.typography.titleSmall)
                Text("Guarda primero los tamaños con Aplicar. El archivo incluye ajustes, favoritas, apps ocultas e imágenes legibles. No incluye apps ni permisos de widgets. Tú eliges dónde guardarlo.", style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = onExport, enabled = !state.backupBusy) { Text("Exportar respaldo") }
                TextButton(onClick = onImport, enabled = !state.backupBusy) { Text("Importar respaldo") }
                if (state.backupBusy) LinearProgressIndicator(Modifier.fillMaxWidth())
                state.backupMessage?.let { Text(it) }
                }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Listo") } },
    )
}

@Composable
private fun SettingSlider(label: String, value: Int, range: IntRange, unit: String, onChange: (Int) -> Unit) {
    Column {
        Text("$label: $value $unit")
        Slider(value.toFloat(), { onChange(kotlin.math.round(it).toInt()) }, valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1, modifier = Modifier.semantics { contentDescription = label })
    }
}

@Composable
private fun PackChoice(label: String, selected: Boolean, enabled: Boolean, onChoose: () -> Unit) {
    TextButton(onClick = onChoose, enabled = enabled, contentPadding = PaddingValues(), modifier = Modifier.fillMaxWidth().heightIn(min = PersonalizationSpace.target)) {
        RadioButton(selected, null, enabled = enabled)
        Text(label, Modifier.weight(1f))
    }
}
