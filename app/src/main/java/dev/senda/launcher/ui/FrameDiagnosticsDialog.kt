package dev.senda.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Locale

/** Explicit, local sampling. Opening the dialog never starts a recording. */
@Composable
internal fun FrameDiagnosticsDialog(
    lightMode: Boolean,
    onLightMode: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val controller = LocalFrameDiagnostics.current
    val report = if (controller != null) controller.report.collectAsStateWithLifecycle().value else FrameReport()
    fun number(value: Double) = String.format(Locale.getDefault(), "%.1f", value)
    AuraAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Diagnóstico de fluidez") },
        text = {
            Column(
                Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Muestra local de hasta 60 segundos. Inicia, desplázate por Inicio y vuelve aquí para consultar el resultado. No se envían datos.")
                if (controller == null) {
                    Text("El diagnóstico no está disponible en esta vista.", color = MaterialTheme.colorScheme.error)
                } else {
                    Text(if (report.running) "Medición en curso" else if (report.frames > 0) "Última muestra" else "Sin muestra todavía", style = MaterialTheme.typography.titleSmall)
                    Text("Fotogramas registrados: ${report.frames}")
                    Text("Informes omitidos por Android: ${report.droppedReports}", style = MaterialTheme.typography.bodySmall)
                    val slowPercent = if (report.frames > 0) report.slowFrames * 100.0 / report.frames else 0.0
                    Text("Fotogramas lentos: ${report.slowFrames} (${number(slowPercent)}%)")
                    Text("Tiempo medio: ${number(report.averageMs)} ms · máximo: ${number(report.worstMs)} ms")
                    Text("Frecuencia de referencia: ${number(report.refreshHz.toDouble())} Hz")
                    Text("Solo mide la ventana de Inicio, no los diálogos ni otras apps. Lento significa superar el plazo de Android (o el intervalo de pantalla en Android 10/11). La muestra orienta: no es un benchmark ni una medición exacta de FPS. Temperatura, carga y frecuencia de pantalla pueden cambiar el resultado.", style = MaterialTheme.typography.bodySmall)
                    if (report.running) {
                        OutlinedButton(onClick = controller::stop, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Detener y ver resultado") }
                    } else {
                        Button(onClick = { controller.start(); onDismiss() }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                            Text(if (report.frames > 0) "Repetir muestra de 60 s" else "Iniciar muestra de 60 s")
                        }
                    }
                }
                HorizontalDivider()
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 48.dp).toggleable(lightMode, enabled = !report.running, role = Role.Switch, onValueChange = onLightMode),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Modo ligero", Modifier.weight(1f).padding(end = 8.dp))
                    Switch(checked = lightMode, onCheckedChange = null, enabled = !report.running)
                }
                if (report.running) Text("Detén la muestra antes de cambiar de modo.", style = MaterialTheme.typography.bodySmall)
                Text("Reduce el movimiento sin reemplazar tus ajustes originales. Desactívalo para recuperarlos. Compara dos muestras con el mismo recorrido.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) { Text("Volver a Inicio") } },
    )
}
