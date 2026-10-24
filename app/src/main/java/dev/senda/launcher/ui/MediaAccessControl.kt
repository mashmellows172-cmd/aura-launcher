package dev.senda.launcher.ui

import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.senda.launcher.MediaAccessService

@Composable
internal fun MediaAccessControl(granted: Boolean) {
    val context = LocalContext.current
    var explain by rememberSaveable { mutableStateOf(false) }
    Text(if (granted) "Sesiones multimedia: acceso activo" else "Sesiones multimedia: modo básico", style = MaterialTheme.typography.bodySmall)
    TextButton(onClick = { explain = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
        Text(if (granted) "Administrar acceso multimedia" else "Activar carátula y controles")
    }
    if (explain) AuraAlertDialog(onDismissRequest = { explain = false }, title = { Text("Acceso a reproducción") }, text = {
        Column(Modifier.heightIn(max = 480.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Android ofrece las sesiones de otras apps mediante el acceso a notificaciones. Este permiso tiene un alcance amplio en el sistema.")
            Text("Aura usa la identidad de ese acceso para mostrar título, carátula y controles de la sesión activa. No lee, guarda ni envía el contenido de tus notificaciones.")
            Text("Sin acceso, el widget puede indicar audio local de forma básica; no identifica la app, no muestra carátula y no ofrece controles. Algunas reproducciones, como vídeo silencioso, no se detectan en modo básico.")
            Text("El widget aparece solo cuando se detecta reproducción. Puedes desactivarlo o revocar el acceso en cualquier momento.")
        }
    }, confirmButton = {
        TextButton(onClick = {
            explain = false
            if (!MediaAccessService.openSettings(context)) Toast.makeText(context, "No se pudo abrir el acceso a notificaciones", Toast.LENGTH_LONG).show()
        }) { Text("Abrir ajustes de Android") }
    }, dismissButton = { TextButton(onClick = { explain = false }) { Text("Ahora no") } })
}
