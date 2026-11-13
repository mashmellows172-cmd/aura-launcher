package dev.senda.launcher.ui

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.VisualSettings

private object VisualTokens { val dialog = 560.dp; val gap = 10.dp; val target = 48.dp }

@Composable
fun VisualSettingsDialog(current: VisualSettings, onChange: (VisualSettings) -> Unit, onDismiss: () -> Unit, onAlphabetSettings: (() -> Unit)? = null, ps4: Boolean = false) {
    var draft by remember(current) { mutableStateOf(current) }
    fun commit(value: VisualSettings) { draft = value; onChange(value) }
    val profiles = listOf("DEPTH" to "Profundidad", "FOLD" to "Pliegue 3D", "WAVE" to "Onda", "ORBIT" to "Órbita", "GLIDE" to "Deslizamiento", "CASCADE" to "Cascada", "TILT" to "Abanico", "SILK" to "Seda", "NONE" to "Sin efecto")
    AuraAlertDialog(onDismissRequest = onDismiss, title = { Text("Movimiento y efectos") }, text = {
        Column(Modifier.heightIn(max = VisualTokens.dialog).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(VisualTokens.gap)) {
            if (ps4) {
                EffectChoice("Transiciones de paneles PS4", draft.menuEffect, listOf(
                    "SPRING" to "Expansión desde el origen",
                    "SLIDE" to "Deslizamiento",
                    "FADE" to "Fundido",
                    "NONE" to "Sin animación",
                )) { commit(draft.copy(menuEffect = it)) }
                Text("Se aplica al menú rápido, ficha de app, multimedia y clima.", style = MaterialTheme.typography.bodySmall)
                Text(if (draft.menuEffect == "NONE" || draft.reduceMotion) "Duración de entrada · instantánea"
                    else "Duración de entrada · ${ps4PanelDuration(draft.durationMs, closing = false)} ms")
                Slider(draft.durationMs.toFloat(), { draft = draft.copy(durationMs = it.toInt()) }, valueRange = 150f..800f,
                    onValueChangeFinished = { onChange(draft) }, enabled = !draft.reduceMotion && draft.menuEffect != "NONE",
                    modifier = Modifier.semantics { contentDescription = "Duración de los paneles PS4" })
                Ps4PanelPreview(draft)
                MotionToggle("Pulso entre reloj y clima", draft.headerDialogueEnabled) {
                    commit(draft.copy(headerDialogueEnabled = it))
                }
                MotionToggle("Animar el reproductor", draft.mediaDetailsMotionEnabled) {
                    commit(draft.copy(mediaDetailsMotionEnabled = it))
                }
                Ps4MediaSample(reduced = draft.reduceMotion || !draft.mediaDetailsMotionEnabled)
                HorizontalDivider()
                EffectChoice("Fondo PS4", draft.wallpaperMode, listOf("STATIC" to "Estático", "REACTIVE" to "Reactivo")) {
                    commit(draft.copy(wallpaperMode = it))
                }
                Text("Luz y cintas acompañan tus gestos y vuelven al reposo. Sin vídeo ni animación permanente.", style = MaterialTheme.typography.bodySmall)
                Text("Intensidad del fondo · ${draft.wallpaperIntensity}%")
                Slider(draft.wallpaperIntensity.toFloat(), { draft = draft.copy(wallpaperIntensity = it.toInt()) }, valueRange = 0f..100f,
                    onValueChangeFinished = { onChange(draft) }, enabled = !draft.reduceMotion && draft.wallpaperMode == "REACTIVE",
                    modifier = Modifier.semantics { contentDescription = "Intensidad del fondo PS4" })
            } else {
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
                    "SILK" -> "Profundidad y deslizamiento suaves, sin giros."
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
            }
            HorizontalDivider()
            if(onAlphabetSettings!=null) OutlinedButton(onClick=onAlphabetSettings,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) { Text("Barra A–Z · efectos y respuesta") }
            if (!ps4) EffectChoice("Entrada de los menús", draft.menuEffect, listOf("SPRING" to "Resorte 3D", "SLIDE" to "Deslizar", "FADE" to "Desvanecer", "NONE" to "Sin animación")) { commit(draft.copy(menuEffect = it)) }
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
private fun Ps4PanelPreview(settings: VisualSettings) {
    var open by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }
    val still = settings.reduceMotion || settings.menuEffect == "NONE" ||
        dev.senda.launcher.data.LocalAppearance.current.highContrast ||
        LocalSystemPowerSaving.current || !ValueAnimator.areAnimatorsEnabled()
    LaunchedEffect(open, still, settings.menuEffect, settings.durationMs) {
        val target = if (open) 1f else 0f
        if (still) progress.snapTo(target)
        else progress.animateTo(target, tween(ps4PanelDuration(settings.durationMs, closing = !open),
            easing = ps4PanelEasing(settings.menuEffect)))
    }
    OutlinedButton(onClick = { open = !open }, modifier = Modifier.fillMaxWidth()) {
        Text(if (open) "Cerrar vista previa" else "Probar transición")
    }
    Box(Modifier.fillMaxWidth().height(84.dp).clipToBounds().clearAndSetSemantics {}
        .background(MaterialTheme.colorScheme.surface), contentAlignment = Alignment.CenterStart) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(.8f)
            .ps4PanelTransform(progress.asState(), if (still) "FADE" else settings.menuEffect, lateral = true)
            .ps4Material().padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
            Text("Menú rápido", style = MaterialTheme.typography.titleMedium)
        }
    }
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
