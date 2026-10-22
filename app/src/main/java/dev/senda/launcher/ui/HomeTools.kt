package dev.senda.launcher.ui

import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background

import android.app.AlarmManager
import android.app.ActivityOptions
import android.os.Build
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.text.format.DateFormat
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.senda.launcher.HomeUiState
import dev.senda.launcher.LauncherViewModel
import dev.senda.launcher.NotificationShadeService
import dev.senda.launcher.data.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

private object HomeToolsSize { val gap = 8.dp; val target = 48.dp; val dialog = 460.dp; val gestureThreshold = 24.dp }

/** Children consume list, alphabet and widget drags first. Only unclaimed background/logo gestures reach this detector. */
@Composable
fun Modifier.homeGestures(onGesture: (GestureDirection) -> Unit): Modifier {
    val current by rememberUpdatedState(onGesture)
    return pointerInput(Unit) {
    var x = 0f; var y = 0f
    detectDragGestures(onDragStart = { x = 0f; y = 0f }, onDrag = { _, delta -> x += delta.x; y += delta.y }, onDragEnd = {
        if (maxOf(abs(x), abs(y)) >= HomeToolsSize.gestureThreshold.toPx()) {
            current(if (abs(x) > abs(y)) { if (x > 0) GestureDirection.RIGHT else GestureDirection.LEFT } else { if (y > 0) GestureDirection.DOWN else GestureDirection.UP })
        }
    })
}
}

@Composable
fun HomeInformation(state: HomeUiState, onCalendarPermission: () -> Unit, block: dev.senda.launcher.data.HomeBlock? = null, blockStyle: dev.senda.launcher.data.BlockStyle? = null) {
    val context = LocalContext.current
    val locale = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]
    val info = state.headerInfo
    val format = remember(locale, DateFormat.is24HourFormat(context)) { DateTimeFormatter.ofPattern(if (DateFormat.is24HourFormat(context)) "EEE HH:mm" else "EEE h:mm a", locale) }
    val entries = mutableListOf<Pair<String, () -> Unit>>()
    if (state.headerSettings.showAlarm && (block == null || block == dev.senda.launcher.data.HomeBlock.ALARM)) {
        val label = info.alarmAt?.let { "Próxima alarma · ${format.format(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()))}" } ?: "Sin alarmas programadas"
        entries += label to { openNextAlarm(context) }
    }
    if (state.headerSettings.showCalendar && (block == null || block == dev.senda.launcher.data.HomeBlock.AGENDA)) {
        val event = info.event
        val text = when {
            !info.calendarPermission -> "Activar permiso de calendario"
            info.calendarLoading -> "Consultando agenda…"
            info.calendarError -> "No se pudo leer la agenda · tocar para abrir"
            event == null -> "Sin eventos en los próximos 7 días"
            else -> {
                val time = if (event.allDay) "${DateTimeFormatter.ofPattern("EEE d MMM", locale).format(Instant.ofEpochMilli(event.begin).atZone(ZoneOffset.UTC))} · Todo el día"
                    else if (event.begin <= System.currentTimeMillis()) "En curso" else format.format(Instant.ofEpochMilli(event.begin).atZone(ZoneId.systemDefault()))
                "$time · ${event.title}"
            }
        }
        entries += text to { if (!info.calendarPermission) onCalendarPermission() else openAgenda(context, event) }
    }
    if (entries.isNotEmpty()) {
        if (androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HomeToolsSize.gap)) {
                entries.forEach { (text, action) -> InformationButton(text, action, Modifier.weight(1f), blockStyle) }
            }
        } else Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(HomeToolsSize.gap)) {
            entries.forEach { (text, action) -> InformationButton(text, action, Modifier.fillMaxWidth(), blockStyle) }
        }
    }
}

@Composable
private fun InformationButton(text: String, action: () -> Unit, modifier: Modifier, blockStyle: dev.senda.launcher.data.BlockStyle?) {
    val lines = if (blockStyle != null && blockStyle.height < blockStyle.textSize * androidx.compose.ui.platform.LocalDensity.current.fontScale * 2.6f + 16f) 1 else 2
    TextButton(onClick = action, modifier = modifier.then(if (blockStyle == null) Modifier.heightIn(min = HomeToolsSize.target) else Modifier.height(blockStyle.height.dp)).background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large), contentPadding = PaddingValues(horizontal = HomeToolsSize.gap)) {
        Text(text, Modifier.fillMaxWidth(), maxLines = lines, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelLarge, fontSize = (blockStyle?.textSize ?: 14).sp)
    }
}

fun openNextAlarm(context: Context) {
    val intent = context.getSystemService(AlarmManager::class.java)?.nextAlarmClock?.showIntent
    val options = if (Build.VERSION.SDK_INT >= 34) ActivityOptions.makeBasic().apply {
        pendingIntentBackgroundActivityStartMode = if (Build.VERSION.SDK_INT >= 36) ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOW_IF_VISIBLE else ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
    }.toBundle() else null
    if (intent != null && runCatching { intent.send(context, 0, null, null, null, null, options); true }.getOrDefault(false)) return
    runCatching { context.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS)) }
}

private fun openAgenda(context: Context, event: AgendaEvent?) {
    val intent = if (event == null) Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR)
        else Intent(Intent.ACTION_VIEW, ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.id))
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.begin).putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.end)
    runCatching { context.startActivity(intent) }.onFailure {
        runCatching { context.startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR)) }
    }
}

@Composable
fun HomeSettingsDialog(state: HomeUiState, viewModel: LauncherViewModel, onRequestCalendar: () -> Unit, onAppPermissions: () -> Unit, onNotificationHelp: () -> Unit, onDismiss: () -> Unit) {
    var direction by remember { mutableStateOf<GestureDirection?>(null) }
    val notificationReady by NotificationShadeService.connection.collectAsStateWithLifecycle()
    AuraAlertDialog(onDismissRequest = onDismiss, title = { Text("Inicio y gestos") }, text = {
        Column(Modifier.heightIn(max = HomeToolsSize.dialog).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(HomeToolsSize.gap)) {
            Text("Cabecera", style = MaterialTheme.typography.titleSmall)
            SettingToggle("Próxima alarma", state.headerSettings.showAlarm) { viewModel.setHeaderSettings(state.headerSettings.copy(showAlarm = it)) }
            SettingToggle("Próximo evento", state.headerSettings.showCalendar) { enabled ->
                if (enabled && !state.headerInfo.calendarPermission) onRequestCalendar() else viewModel.setHeaderSettings(state.headerSettings.copy(showCalendar = enabled))
            }
            Text("La agenda consulta calendarios visibles durante los próximos 7 días, solo mientras Aura está visible. No escribe eventos ni los guarda en el respaldo.", style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = onAppPermissions) { Text("Permisos de Aura") }
            HorizontalDivider()
            Text("Gestos", style = MaterialTheme.typography.titleSmall)
            Text("Desliza sobre el logotipo o el fondo libre. La lista, el alfabeto y los widgets conservan sus propios gestos. Mantén pulsado el fondo para abrir los ajustes.", style = MaterialTheme.typography.bodySmall)
            EffectChoice("Doble toque para bloquear", state.lockGesture.name, LockGesture.entries.map { it.name to it.label }, onSelect = { viewModel.setLockGesture(LockGesture.valueOf(it)) })
            Text("Funciona en todos los espacios vacíos: cabecera, márgenes y huecos de la lista. Los iconos, el texto de las aplicaciones, los widgets y el índice conservan sus acciones. Requiere el acceso opcional de accesibilidad.", style = MaterialTheme.typography.bodySmall)
            GestureDirection.entries.forEach { item ->
                TextButton(onClick = { direction = item }, modifier = Modifier.fillMaxWidth().heightIn(min = HomeToolsSize.target)) {
                    Text("${item.label} · ${gestureLabel(state.gestures[item], state.apps)}", Modifier.fillMaxWidth())
                }
            }
            Text(if (notificationReady) "Gestos del dispositivo: acceso activo" else "Gestos del dispositivo: acceso desactivado", style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = onNotificationHelp) { Text("Acceso para bloquear y notificaciones") }
            HorizontalDivider()
            Text("Orientación de Aura", style = MaterialTheme.typography.titleSmall)
            HomeRotation.entries.forEach { rotation ->
                TextButton(onClick = { viewModel.setRotation(rotation) }, modifier = Modifier.fillMaxWidth().heightIn(min = HomeToolsSize.target)) {
                    RadioButton(state.rotation == rotation, null)
                    Text(rotation.label, Modifier.weight(1f))
                }
            }
            Text("Solo cambia la orientación del launcher. Android conserva el control en dispositivos donde impone otra política.", style = MaterialTheme.typography.bodySmall)
        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("Listo") } })
    direction?.let { chosen -> GestureActionDialog(chosen, state.apps, onSelect = { viewModel.setGesture(chosen, it); direction = null }, onDismiss = { direction = null }) }
}

private fun gestureLabel(action: String, apps: List<LaunchableApp>) = when (action) {
    "NONE" -> "Sin acción"; "SEARCH" -> "Buscar apps"; "NOTIFICATIONS" -> "Abrir notificaciones"
    else -> apps.firstOrNull { it.packageName == action.removePrefix("APP:") }?.label ?: "App no disponible"
}

@Composable
private fun SettingToggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = HomeToolsSize.target).toggleable(checked, role = Role.Switch, onValueChange = onChange)
        .semantics { contentDescription = label }, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f)); Switch(checked, null)
    }
}

@Composable
private fun GestureActionDialog(direction: GestureDirection, apps: List<LaunchableApp>, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    var query by remember { mutableStateOf("") }
    AuraAlertDialog(onDismissRequest = onDismiss, title = { Text("Gesto: ${direction.label}") }, text = {
        Column {
            OutlinedTextField(query, { query = it }, label = { Text("Buscar aplicación") }, singleLine = true)
            LazyColumn(Modifier.heightIn(max = HomeToolsSize.dialog)) {
                item { TextButton(onClick = { onSelect("NONE") }) { Text("Sin acción") } }
                item { TextButton(onClick = { onSelect("SEARCH") }) { Text("Buscar apps") } }
                item { TextButton(onClick = { onSelect("NOTIFICATIONS") }) { Text("Abrir notificaciones") } }
                items(apps.filter { normalizeSearchText(it.label, Locale.getDefault()).contains(normalizeSearchText(query, Locale.getDefault())) }, key = { it.packageName }) { app ->
                    TextButton(onClick = { onSelect("APP:${app.packageName}") }, modifier = Modifier.fillMaxWidth()) { Text(app.label, Modifier.fillMaxWidth(), maxLines = 1, overflow = TextOverflow.Ellipsis) }
                }
            }
        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}
