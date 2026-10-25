package dev.senda.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.ui.theme.historicalProfile
import dev.senda.launcher.ui.theme.icons.*

/** Uses production vectors; browsing does not write preferences or change Home. */
@Composable
internal fun IconFamilyDialog(initialTheme: LauncherTheme, onUse: (LauncherTheme) -> Unit, onDismiss: () -> Unit, onPick: ((FamilyIcon, LauncherTheme) -> Unit)? = null) {
    var themeName by rememberSaveable { mutableStateOf(initialTheme.takeIf { it in iconFamilyThemes }?.name ?: LauncherTheme.NOTHING.name) }
    val theme = LauncherTheme.valueOf(themeName)
    var light by rememberSaveable { mutableStateOf(false) }
    var size by rememberSaveable { mutableIntStateOf(42) }
    var showInstalled by rememberSaveable { mutableStateOf(onPick == null) }
    val installedRows = remember { installedArtworkEntries.chunked(3) }
    val rows = remember { FamilyIcon.entries.chunked(3) }
    AuraAlertDialog(onDismissRequest = onDismiss, title = { Text(if (onPick == null) "Familias de iconos" else "Elegir dibujo de Aura") }, text = {
        LazyColumn(Modifier.fillMaxWidth().heightIn(max = 470.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item(key = "themes") {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(iconFamilyThemes, key = { it.name }) { candidate ->
                        FilterChip(theme == candidate, { themeName = candidate.name }, label = { Text(historicalProfile(candidate)?.reference ?: candidate.title) }, modifier = Modifier.heightIn(min = 48.dp))
                    }
                }
            }
            item(key = "explanation") {
                Text(if (onPick == null) "$installedArtworkCount apps del Motorola · seis reinterpretaciones por app, además de32 funciones. Tus reemplazos y colecciones tienen prioridad. Las apps instaladas después conservan su símbolo si no tienen equivalencia." else "Toca un dibujo para usarlo en esta app y este tema. Puedes explorar las seis familias. Normales sigue mostrando el icono de Android.", style = MaterialTheme.typography.bodySmall)
            }
            if (onPick == null) item(key = "catalog") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(showInstalled, { showInstalled = true }, label = { Text("Apps · $installedArtworkCount") }, modifier = Modifier.heightIn(min = 48.dp))
                    FilterChip(!showInstalled, { showInstalled = false }, label = { Text("Funciones ·32") }, modifier = Modifier.heightIn(min = 48.dp))
                }
            }
            item(key = "sizes") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(32, 42, 60).forEach { candidate ->
                        FilterChip(size == candidate, { size = candidate }, label = { Text("$candidate dp") }, modifier = Modifier.heightIn(min = 48.dp))
                    }
                }
                TextButton(onClick = { light = !light }, modifier = Modifier.heightIn(min = 48.dp)) { Text(if (light) "Ver sobre fondo oscuro" else "Ver sobre fondo claro") }
            }
            if (showInstalled && onPick == null) items(installedRows, key = { "app:" + it.first().packageName }) { row ->
                Row(Modifier.fillMaxWidth().background(if (light) Color(0xFFF0EDE4) else Color(0xFF121A21)).padding(vertical = 10.dp), verticalAlignment = Alignment.Top) {
                    row.forEach { app ->
                        Column(Modifier.weight(1f).heightIn(min = 80.dp).padding(horizontal = 2.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            renderInstalledAppArtwork(app.packageName, theme, Modifier.size(size.dp))
                            Text(app.label, color = if (light) Color(0xFF252A2C) else Color(0xFFF1F3EA), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                        }
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
            else items(rows, key = { it.first().name }) { row ->
                Row(Modifier.fillMaxWidth().background(if (light) Color(0xFFF0EDE4) else Color(0xFF121A21)).padding(vertical = 10.dp), verticalAlignment = Alignment.Top) {
                    row.forEach { icon ->
                        Column(Modifier.weight(1f).heightIn(min = 80.dp).then(if (onPick != null) Modifier.clickable(role = Role.Button, onClickLabel = "Usar ${icon.label}") { onPick(icon, theme) } else Modifier).padding(horizontal = 2.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            FamilyArtwork(icon, theme, Modifier.size(size.dp))
                            Text(icon.label, color = if (light) Color(0xFF252A2C) else Color(0xFFF1F3EA), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                        }
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
            if (onPick == null) item(key = "scope") { Text("Usar esta familia prepara el tema y los recursos de Aura en el editor. Inicio solo cambia al pulsar Aplicar tema. No sustituye las imágenes individuales que elegiste.", style = MaterialTheme.typography.bodySmall) }
        }
    }, confirmButton = { if (onPick == null) TextButton(onClick = { onUse(theme) }, modifier = Modifier.heightIn(min = 48.dp)) { Text("Usar esta familia") } },
        dismissButton = { TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) { Text("Volver") } })
}
