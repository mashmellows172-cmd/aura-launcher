package dev.senda.launcher.widgets

import dev.senda.launcher.ui.AuraAlertDialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun WidgetPickerDialog(controller: WidgetController, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    val providers = remember(controller.providerRevision) { runCatching { controller.providers() }.getOrDefault(emptyList()) }
    val filtered = remember(query, providers) { providers.filter { it.loadLabel(context.packageManager).contains(query, ignoreCase = true) } }
    AuraAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir widget") },
        text = {
            Column {
                Text("Hasta cuatro widgets de tus aplicaciones. Android te pedirá permiso al añadirlos.")
                OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Buscar widget") }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), singleLine = true)
                LazyColumn(Modifier.heightIn(max = 380.dp)) {
                    if (filtered.isEmpty()) item { Text("No hay widgets disponibles para esta búsqueda.") }
                    items(filtered, key = { it.provider.flattenToString() + it.profile.hashCode() }) { info ->
                        TextButton(onClick = { onDismiss(); controller.add(info) }, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.fillMaxWidth()) {
                                Text(info.loadLabel(context.packageManager), style = MaterialTheme.typography.titleSmall)
                                Text(info.provider.packageName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
    )
}

@Composable
fun WidgetShelf(controller: WidgetController) {
    var editing by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tus widgets", style = MaterialTheme.typography.titleSmall)
            TextButton(onClick = { editing = !editing }) { Text(if (editing) "Listo" else "Editar widgets") }
        }
        controller.widgets.forEach { widget ->
            key(widget.id) {
                val info = remember(widget.id, controller.providerRevision) { controller.manager.getAppWidgetInfo(widget.id) }
                Card(Modifier.fillMaxWidth()) {
                    if (info == null) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Widget no disponible. Su aplicación puede estar desactivada o desinstalada.")
                            TextButton(onClick = { controller.remove(widget.id) }) { Text("Quitar widget no disponible") }
                        }
                    } else {
                        val density = LocalDensity.current
                        BoxWithConstraints(Modifier.fillMaxWidth().height(widget.heightDp.dp)) {
                            val width = maxWidth.value.toInt().coerceAtLeast(1)
                            AndroidView(
                                factory = { context -> controller.host.createView(context, widget.id, info) },
                                modifier = Modifier.fillMaxSize(),
                                update = { view ->
                                    val requestedSize = width to widget.heightDp
                                    if (view.tag != requestedSize) {
                                        @Suppress("DEPRECATION")
                                        view.updateAppWidgetSize(null, width, widget.heightDp, width, widget.heightDp)
                                        view.tag = requestedSize
                                    }
                                },
                            )
                        }
                    }
                    if (editing) {
                        val index = controller.widgets.indexOfFirst { it.id == widget.id }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            TextButton(onClick = { controller.resize(widget.id, -40) }, enabled = widget.heightDp > 100, modifier = Modifier.semantics { contentDescription = "Reducir altura del widget" }) { Text("− Alto") }
                            TextButton(onClick = { controller.resize(widget.id, 40) }, enabled = widget.heightDp < 420, modifier = Modifier.semantics { contentDescription = "Aumentar altura del widget" }) { Text("+ Alto") }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            TextButton(onClick = { controller.move(widget.id, -1) }, enabled = index > 0) { Text("Subir") }
                            TextButton(onClick = { controller.move(widget.id, 1) }, enabled = index < controller.widgets.lastIndex) { Text("Bajar") }
                            TextButton(onClick = { controller.remove(widget.id) }) { Text("Quitar") }
                        }
                    }
                }
            }
        }
    }
}
