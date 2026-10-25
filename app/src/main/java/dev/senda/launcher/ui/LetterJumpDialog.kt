package dev.senda.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Accessible alternative to scrubbing the compact rail; it uses the same section navigation. */
@Composable
internal fun LetterJumpDialog(letters: List<String>, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    AuraAlertDialog(onDismissRequest = onDismiss, title = { Text("Saltar a una letra") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Elige una sección de tu lista de aplicaciones.")
            LazyVerticalGrid(columns = GridCells.Adaptive(64.dp), modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(letters, key = { it }) { letter ->
                    TextButton(onClick = { onSelect(letter) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        .semantics { contentDescription = "Saltar a la sección $letter" }) { Text(letter) }
                }
            }
        }
    }, confirmButton = { TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) { Text("Volver") } })
}
