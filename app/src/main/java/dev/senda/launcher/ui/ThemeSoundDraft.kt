package dev.senda.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.senda.launcher.audio.SoundFeedbackController
import dev.senda.launcher.audio.SoundResult
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.SoundFamily
import dev.senda.launcher.data.SoundSettings
import kotlin.math.roundToInt

/** An isolated audition: edits never configure the launcher's live sound controller. */
@Composable
internal fun ThemeSoundDraft(value: SoundSettings, theme: LauncherTheme, enabled: Boolean, onFinish: () -> Unit, onChange: (SoundSettings, Boolean) -> Unit) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val feedback = remember(context) { SoundFeedbackController(context) }
    var familyMenu by remember { mutableStateOf(false) }
    var result by remember(value, theme) { mutableStateOf<String?>(null) }
    SideEffect { feedback.configure(value, theme) }
    DisposableEffect(feedback, lifecycle) {
        fun visibility() { feedback.setVisible(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) }
        val observer = LifecycleEventObserver { _, _ -> visibility() }
        lifecycle.addObserver(observer)
        feedback.beginPreview()
        visibility()
        onDispose { lifecycle.removeObserver(observer); feedback.close() }
    }
    Text("Los cambios se guardan en esta variante. Cancelar conserva tus sonidos actuales.", style=MaterialTheme.typography.bodySmall)
    Row(Modifier.fillMaxWidth().heightIn(min=48.dp), verticalAlignment=Alignment.CenterVertically) {
        Text("Sonidos de Aura", Modifier.weight(1f))
        Switch(value.enabled, { onChange(value.copy(enabled=it), true) }, enabled=enabled)
    }
    Box {
        OutlinedButton(onClick={familyMenu=true},enabled=enabled,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) { Text("Familia: ${value.family.label}") }
        DropdownMenu(familyMenu,{familyMenu=false}) {
            SoundFamily.entries.forEach { family -> DropdownMenuItem(text={Text(family.label)},onClick={familyMenu=false;onChange(value.copy(family=family), true)}) }
        }
    }
    Text("Volumen de Aura: ${value.volume}%",style=MaterialTheme.typography.labelLarge)
    Slider(value.volume.toFloat(), { onChange(value.copy(volume=it.roundToInt()).bounded(), false) }, enabled=enabled,
        valueRange=0f..SoundSettings.MAX_VOLUME.toFloat(), onValueChangeFinished=onFinish)
    Row(Modifier.fillMaxWidth().heightIn(min=48.dp),verticalAlignment=Alignment.CenterVertically) {
        Text("Silenciar durante reproducción",Modifier.weight(1f))
        Switch(value.muteDuringMedia,{onChange(value.copy(muteDuringMedia=it), true)},enabled=enabled)
    }
    TextButton(onClick={
        result=when(feedback.preview(theme)) {
            SoundResult.PLAYED -> "Muestra reproducida"
            SoundResult.LOADING -> "Cargando muestra"
            SoundResult.SYSTEM_MUTED -> "El sistema está silenciado o el volumen es cero"
            SoundResult.MEDIA_ACTIVE -> "Muestra silenciada durante reproducción"
            SoundResult.DISABLED -> "Muestra desactivada"
            SoundResult.UNAVAILABLE -> "Muestra no disponible"
        }
    },enabled=enabled,modifier=Modifier.heightIn(min=48.dp)) { Text("Escuchar muestra") }
    result?.let { Text(it,style=MaterialTheme.typography.bodySmall) }
    Text("La muestra solo suena al tocarla y respeta silencio, No molestar y reproducción. No cambia el volumen del teléfono.",style=MaterialTheme.typography.bodySmall)
}
