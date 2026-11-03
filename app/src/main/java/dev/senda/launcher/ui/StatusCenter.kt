package dev.senda.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal data class StatusItem(
    val title: String,
    val status: String,
    val actionLabel: String,
    val action: () -> Unit,
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
                for (item in items) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(item.title, style = MaterialTheme.typography.titleSmall)
                        Text(item.status)
                        OutlinedButton(onClick = item.action, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                            Text(item.actionLabel)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
    )
}
