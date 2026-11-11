package dev.senda.launcher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.senda.launcher.data.HeaderSettings
import kotlin.math.roundToInt

@Composable
internal fun HomeSizingControls(settings: HeaderSettings, console: Boolean, onChange: (HeaderSettings) -> Unit) {
    Text("Tamaños de Inicio", style = MaterialTheme.typography.titleSmall)
    SizeControl("Buscador", settings.searchHeight, 56..88, 4) {
        onChange(settings.copy(searchHeight = it))
    }
    if (console) {
        SizeControl("Reloj", settings.clockSize, 32..64, 4, "sp") {
            onChange(settings.copy(clockSize = it))
        }
        SizeControl("Altura del encabezado", settings.headerHeight, 120..216, 8) {
            onChange(settings.copy(headerHeight = it))
        }
        Text("El reloj se ajusta al ancho disponible. En ventanas pequeñas se usa la cabecera compacta.",
            style = MaterialTheme.typography.bodySmall)
    }
    TextButton(onClick = {
        onChange(settings.copy(searchHeight = 56, clockSize = 48, headerHeight = 120))
    }) { Text("Restablecer tamaños") }
}

@Composable
private fun SizeControl(label: String, value: Int, range: IntRange, step: Int, unit: String = "dp",
    onApply: (Int) -> Unit) {
    var draft by remember(value) { mutableFloatStateOf(value.toFloat()) }
    Column {
        Text("$label · ${draft.roundToInt()} $unit", style = MaterialTheme.typography.labelLarge)
        Slider(
            value = draft,
            onValueChange = { draft = it },
            onValueChangeFinished = { onApply(draft.roundToInt().coerceIn(range)) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = (range.last - range.first) / step - 1,
            modifier = Modifier.semantics { contentDescription = "Tamaño de $label" },
        )
    }
}
