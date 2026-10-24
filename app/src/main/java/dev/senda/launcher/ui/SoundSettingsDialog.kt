package dev.senda.launcher.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.senda.launcher.audio.LocalSoundFeedback
import dev.senda.launcher.audio.SoundResult
import dev.senda.launcher.data.*

@Composable
internal fun SoundSettingsDialog(current: SoundSettings, theme: LauncherTheme, onChange: (SoundSettings) -> Unit, onDismiss: () -> Unit) {
    var draft by remember(current) { mutableStateOf(current) }
    val feedback = LocalSoundFeedback.current
    val context = LocalContext.current
    DisposableEffect(feedback) { feedback.beginPreview(); onDispose { feedback.endPreview() } }
    fun commit(value: SoundSettings) { draft = value.bounded(); onChange(draft) }
    AuraAlertDialog(onDismissRequest = onDismiss, title = { Text("Sonidos y respuesta") }, text = {
        Column(Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Cues breves y originales para cada tema. Solo responden a tus acciones; no hay audio de fondo.", style = MaterialTheme.typography.bodySmall)
            SoundToggle("Activar sonidos de Aura", draft.enabled) { commit(draft.copy(enabled = it)) }
            EffectChoice("Familia sonora", draft.family.name, SoundFamily.entries.map { it.name to it.label }) { commit(draft.copy(family = SoundFamily.fromStored(it))) }
            Text("Volumen relativo · ${draft.volume}%")
            Slider(draft.volume.toFloat(), { draft = draft.copy(volume = it.toInt()) }, valueRange = 0f..100f,
                onValueChangeFinished = { onChange(draft.bounded()) }, modifier = Modifier.semantics { contentDescription = "Volumen relativo de los sonidos" })
            OutlinedButton(onClick = {
                val message = when (feedback.preview(theme)) {
                    SoundResult.SYSTEM_MUTED -> "El sistema está en silencio, No molestar o tiene desactivados los sonidos táctiles."
                    SoundResult.MEDIA_ACTIVE -> "La muestra está silenciada mientras se reproduce contenido."
                    SoundResult.UNAVAILABLE -> "La muestra no está disponible en este momento."
                    else -> null
                }
                if (message != null) Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Escuchar muestra") }
            Text("La muestra requiere una pulsación y funciona aunque los sonidos automáticos estén desactivados. Respeta el volumen del sistema, silencio/vibración, No molestar y los sonidos táctiles de Android.", style = MaterialTheme.typography.bodySmall)
            HorizontalDivider()
            SoundToggle("Abrir aplicaciones", draft.launch) { commit(draft.copy(launch = it)) }
            SoundToggle("Abrir menús", draft.menu) { commit(draft.copy(menu = it)) }
            SoundToggle("Elegir opciones", draft.select) { commit(draft.copy(select = it)) }
            SoundToggle("Cambiar favoritas", draft.favorite) { commit(draft.copy(favorite = it)) }
            SoundToggle("Sonido al recorrer A–Z", draft.index) { commit(draft.copy(index = it)) }
            Text("El índice tiene un límite de frecuencia para evitar una ráfaga continua al arrastrar.", style = MaterialTheme.typography.bodySmall)
            SoundToggle("Silenciar durante reproducción", draft.muteDuringMedia) { commit(draft.copy(muteDuringMedia = it)) }
            Text("Aura no sube el volumen, cambia modos del sistema ni solicita acceso al micrófono. La respuesta háptica se configura por separado en Movimiento y efectos.", style = MaterialTheme.typography.bodySmall)
        }
    }, confirmButton = { TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) { Text("Listo") } })
}

@Composable
private fun SoundToggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).toggleable(checked, role = Role.Switch, onValueChange = onChange), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f).padding(end = 8.dp)); Switch(checked, null)
    }
}
