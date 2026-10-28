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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.VisualSettings

private object VisualTokens { val dialog = 560.dp; val gap = 10.dp; val target = 48.dp }

@Composable
fun VisualSettingsDialog(current: VisualSettings, onChange: (VisualSettings) -> Unit, onDismiss: () -> Unit, onAlphabetSettings: (() -> Unit)? = null) {
    var draft by remember(current) { mutableStateOf(current) }
    fun commit(value: VisualSettings) { draft = value; onChange(value) }
    val profiles = listOf("DEPTH" to "Profundidad", "FOLD" to "Pliegue 3D", "WAVE" to "Onda", "ORBIT" to "Órbita", "GLIDE" to "Deslizamiento", "CASCADE" to "Cascada", "TILT" to "Abanico", "NONE" to "Sin efecto")
    AuraAlertDialog(onDismissRequest = onDismiss, title = { Text("Movimiento y efectos") }, text = {
        Column(Modifier.heightIn(max = VisualTokens.dialog).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(VisualTokens.gap)) {
            Text("Siete despliegues por tema: puntos, pliegue, gota, capas, onda, bisagra y foco. Reloj, clima y widgets usan el estilo del tema; movimiento reducido los desactiva.",style=MaterialTheme.typography.bodySmall)
            Text("El efecto sigue la velocidad real del desplazamiento y vuelve suavemente al reposo.", style = MaterialTheme.typography.bodySmall)
            Row(Modifier.fillMaxWidth()) {
                TextButton(onClick = { commit(draft.copy(motion = "DEPTH", scrollIntensity = 65, durationMs = 340, railEffect = "MAGNET", menuEffect = "SPRING", reduceMotion = false)) }, Modifier.weight(1f)) { Text("Equilibrado") }
                TextButton(onClick = { commit(draft.copy(motion = "FOLD", scrollIntensity = 95, durationMs = 440, railEffect = "SPRING", menuEffect = "SPRING", reduceMotion = false)) }, Modifier.weight(1f)) { Text("Intenso") }
            }
            EffectChoice("Lista de aplicaciones", draft.motion, profiles) { commit(draft.copy(motion = it)) }
            Text(when (draft.motion) {
                "FOLD" -> "Las filas se pliegan con perspectiva al cruzar los bordes."
                "WAVE" -> "Una onda lateral recorre las filas mientras deslizas."
                "ORBIT" -> "Las aplicaciones siguen un arco con giro lateral."
                "GLIDE" -> "Desplazamiento lateral y atenuación contenidos."
                "CASCADE" -> "Las filas se escalonan en profundidad vertical al recorrer los bordes."
                "TILT" -> "Las tarjetas se abren en abanico alrededor del borde de lectura."
                "NONE" -> "Desplazamiento nativo, sin transformación de filas."
                else -> "Las filas se alejan y giran al aproximarse a los bordes."
            }, style = MaterialTheme.typography.bodySmall)
            MotionPreview(draft)
            Text("Intensidad de la lista · ${draft.scrollIntensity}%")
            Slider(draft.scrollIntensity.toFloat(), { draft = draft.copy(scrollIntensity = it.toInt()) }, valueRange = 10f..100f,
                onValueChangeFinished = { onChange(draft) }, enabled = !draft.reduceMotion && draft.motion != "NONE",
                modifier = Modifier.semantics { contentDescription = "Intensidad de la lista" })
            Text("Respuesta y transición · ${draft.durationMs} ms")
            Slider(draft.durationMs.toFloat(), { draft = draft.copy(durationMs = it.toInt()) }, valueRange = 150f..800f,
                onValueChangeFinished = { onChange(draft) }, enabled = !draft.reduceMotion,
                modifier = Modifier.semantics { contentDescription = "Duración de las transiciones" })
            HorizontalDivider()
            if(onAlphabetSettings!=null) OutlinedButton(onClick=onAlphabetSettings,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) { Text("Barra A–Z · efectos y respuesta") }
            EffectChoice("Entrada de los menús", draft.menuEffect, listOf("SPRING" to "Resorte 3D", "SLIDE" to "Deslizar", "FADE" to "Desvanecer", "NONE" to "Sin animación")) { commit(draft.copy(menuEffect = it)) }
            HorizontalDivider()
            MotionToggle("Glow del buscador", draft.glowEnabled) { commit(draft.copy(glowEnabled = it)) }
            Text("Intensidad del glow · ${draft.glowStrength}%")
            Slider(draft.glowStrength.toFloat(), { draft = draft.copy(glowStrength = it.toInt()) }, valueRange = 10f..100f,
                onValueChangeFinished = { onChange(draft) }, enabled = draft.glowEnabled,
                modifier = Modifier.semantics { contentDescription = "Intensidad del glow" })
            MotionToggle("Reducir movimiento", draft.reduceMotion) { commit(draft.copy(reduceMotion = it)) }
            Text("Desactiva las transformaciones de la lista y el rebote. El sistema decide la frecuencia de pantalla; Aura no fuerza un refresco permanente.", style = MaterialTheme.typography.bodySmall)
        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("Listo") } })
}

@Composable
internal fun EffectChoice(label: String, value: String, options: List<Pair<String, String>>, onSelect: (String) -> Unit) {
    val soundFeedback = dev.senda.launcher.audio.LocalSoundFeedback.current
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.titleSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }, Modifier.fillMaxWidth().semantics { contentDescription = label }) {
                Text("${options.firstOrNull { it.first == value }?.second ?: value} ▾")
            }
            DropdownMenu(expanded, { expanded = false }) {
                options.forEach { (id, title) -> DropdownMenuItem(text = { Text(title) }, onClick = { expanded = false; soundFeedback.play(dev.senda.launcher.data.SoundEvent.SELECT); onSelect(id) }) }
            }
        }
    }
}

@Composable
private fun MotionToggle(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = VisualTokens.target).toggleable(value, role = Role.Switch, onValueChange = onChange)
        .semantics { contentDescription = label }, verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f)); Switch(value, null)
    }
}
