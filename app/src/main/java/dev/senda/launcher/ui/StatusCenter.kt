package dev.senda.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics

internal data class StatusItem(
    val title: String,
    val status: String,
    val actionLabel: String,
    val action: () -> Unit,
    val group: String = "Configuración",
)

@Composable
internal fun StatusCenter(items: List<StatusItem>, movementStatus: String, onDismiss: () -> Unit) {
    AuraAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Centro de estado") },
        text = {
            Column(
                Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(movementStatus)
                for ((group, entries) in items.groupBy { it.group }) {
                    Text(group, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
                    for (item in entries) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(item.title, style = MaterialTheme.typography.titleSmall)
                        Text(item.status, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = item.action, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text(item.actionLabel)
                        }
                    }
                    }
                    HorizontalDivider()
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
    )
}
