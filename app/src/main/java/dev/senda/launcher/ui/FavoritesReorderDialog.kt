package dev.senda.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.senda.launcher.data.LaunchableApp
import dev.senda.launcher.data.moveListItem
import kotlin.math.abs

/** Draft ordering: gestures and accessibility actions never persist before Guardar. */
@Composable
internal fun FavoritesReorderDialog(
    favorites: List<LaunchableApp>,
    onSave: (List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    var ordered by remember { mutableStateOf(favorites) }
    val list = rememberLazyListState()
    val edgePx = with(LocalDensity.current) { 72.dp.toPx() }
    var dragged by remember { mutableStateOf<String?>(null) }
    var center by remember { mutableFloatStateOf(0f) }
    var pointerY by remember { mutableFloatStateOf(0f) }
    fun move(from: Int, to: Int) {
        if (from !in ordered.indices || to !in ordered.indices || from == to) return
        ordered = moveListItem(ordered, from, to)
    }
    fun settleTarget() {
        val key = dragged ?: return
        val target = list.layoutInfo.visibleItemsInfo.firstOrNull {
            center >= it.offset && center < it.offset + it.size
        } ?: return
        move(ordered.indexOfFirst { it.packageName == key }, target.index)
    }
    LaunchedEffect(dragged, edgePx) {
        if (dragged == null) return@LaunchedEffect
        var previous = withFrameNanos { it }
        while (dragged != null) {
            val now = withFrameNanos { it }
            val dt = ((now - previous) / 1_000_000_000f).coerceIn(0f, .032f)
            previous = now
            val layout = list.layoutInfo
            val edge = edgePx
            val distance = when {
                pointerY < layout.viewportStartOffset + edge -> pointerY - layout.viewportStartOffset - edge
                pointerY > layout.viewportEndOffset - edge -> pointerY - layout.viewportEndOffset + edge
                else -> 0f
            }
            // Scroll outside the pointer callback so a held drag also advances at the edges.
            if (abs(distance) > 1f) {
                list.scrollBy(distance.coerceIn(-edge, edge) * dt * 8f)
                settleTarget()
            }
        }
    }
    val accent = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.primaryContainer
    AuraAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ordenar favoritas") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(if (ordered.isEmpty()) "Añade favoritas desde las opciones de una app." else "Mantén pulsada una fila y arrástrala. También puedes usar las flechas.",
                    style = MaterialTheme.typography.bodyMedium)
                LazyColumn(
                    state = list,
                    modifier = Modifier.heightIn(max = 480.dp).pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { point ->
                                list.layoutInfo.visibleItemsInfo.firstOrNull { point.y >= it.offset && point.y < it.offset + it.size }?.let {
                                    dragged = it.key as? String
                                    center = it.offset + it.size / 2f
                                    pointerY = point.y
                                }
                            },
                            onDragEnd = { dragged = null },
                            onDragCancel = { dragged = null },
                            onDrag = { change, amount ->
                                if (dragged != null) {
                                    change.consume()
                                    pointerY += amount.y
                                    center += amount.y
                                    settleTarget()
                                }
                            },
                        )
                    },
                ) {
                    itemsIndexed(ordered, key = { _, app -> app.packageName }) { index, app ->
                        val active = dragged == app.packageName
                        Row(
                            Modifier.fillMaxWidth().heightIn(min = 64.dp).zIndex(if (active) 1f else 0f)
                                .graphicsLayer {
                                    translationY = if (active) list.layoutInfo.visibleItemsInfo.firstOrNull { it.key == app.packageName }
                                        ?.let { center - it.offset - it.size / 2f } ?: 0f else 0f
                                }
                                .background(if (active) surface else MaterialTheme.colorScheme.surface)
                                .then(if (active) Modifier.border(2.dp, accent) else Modifier)
                                .semantics {
                                    stateDescription = "Posición ${index + 1} de ${ordered.size}" + if (active) ", moviendo" else ""
                                    customActions = listOf(
                                        CustomAccessibilityAction("Subir") { if (index > 0) { move(index, index - 1); true } else false },
                                        CustomAccessibilityAction("Bajar") { if (index < ordered.lastIndex) { move(index, index + 1); true } else false },
                                    )
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.Menu, null, Modifier.padding(end = 8.dp).size(20.dp))
                            Text(app.label, Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                            IconButton(onClick = { move(index, index - 1) }, enabled = index > 0 && dragged == null, modifier = Modifier.size(48.dp)) {
                                Icon(Icons.Default.KeyboardArrowUp, "Subir ${app.label}")
                            }
                            IconButton(onClick = { move(index, index + 1) }, enabled = index < ordered.lastIndex && dragged == null, modifier = Modifier.size(48.dp)) {
                                Icon(Icons.Default.KeyboardArrowDown, "Bajar ${app.label}")
                            }
                            IconButton(onClick = { ordered = ordered.filterNot { it.packageName == app.packageName } }, enabled = dragged == null, modifier = Modifier.size(48.dp)) {
                                Icon(Icons.Default.Close, "Quitar ${app.label} de favoritas")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(ordered.map { it.packageName }); onDismiss() }, enabled = dragged == null) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
