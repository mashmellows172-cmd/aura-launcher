package dev.senda.launcher.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.ActiveAudioInfo
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.QuickTimer
import dev.senda.launcher.data.QuickTimers
import kotlinx.coroutines.delay
import java.util.Locale

internal enum class QuickCenterTarget(val label: String) {
    PANEL("Oculta"), TIMERS("Temporizadores"), MEDIA("Reproductor"), NOTE("Nota fijada"),
}

private fun List<QuickTimer>.nextTimer(): QuickTimer? =
    filter(QuickTimer::running).minByOrNull(QuickTimer::deadline) ?: firstOrNull()

private fun QuickTimer.displayTime(now: Long): String {
    val seconds = (timeLeft(now) + 999) / 1_000
    return if (seconds >= 3_600) String.format(Locale.ROOT, "%d:%02d:%02d", seconds / 3_600,
        seconds / 60 % 60, seconds % 60)
    else String.format(Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60)
}

@Composable
private fun rememberTimerClock(deadline: Long?, store: QuickTimers): State<Long> {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return produceState(SystemClock.elapsedRealtime(), deadline, lifecycle, store) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            var now = SystemClock.elapsedRealtime()
            value = now
            if (deadline != null) {
                while (now < deadline) {
                    delay((deadline - now).coerceAtMost(1_000L))
                    now = SystemClock.elapsedRealtime()
                    value = now
                }
                store.expire()
            }
        }
    }
}

@Composable
internal fun QuickCenterMiniature(
    target: QuickCenterTarget,
    audio: ActiveAudioInfo,
    note: String,
    timers: List<QuickTimer>,
    store: QuickTimers,
    onOpen: (Rect?) -> Unit,
) {
    val timer = if (target == QuickCenterTarget.TIMERS) timers.nextTimer() else null
    val now by rememberTimerClock(timer?.takeIf(QuickTimer::running)?.deadline, store)
    val shape = RoundedCornerShape(14.dp)
    var bounds by remember { mutableStateOf<Rect?>(null) }
    val media = target == QuickCenterTarget.MEDIA
    val cover = if (media) audio.artwork?.takeUnless { it.isRecycled } else null
    val title = when (target) {
        QuickCenterTarget.MEDIA -> audio.title ?: audio.appLabel ?: "Reproductor"
        QuickCenterTarget.NOTE -> note.lineSequence().firstOrNull { it.isNotBlank() } ?: "Nota fijada"
        else -> timer?.name ?: "Temporizadores"
    }
    val subtitle = if (target == QuickCenterTarget.NOTE) {
        if (note.isBlank()) "Escribir nota" else "Ver nota fijada"
    } else if (media) {
        when {
            audio.isPlaying -> "En reproducción"
            audio.sessionId != null -> "En pausa"
            else -> "Sin reproducción"
        }
    } else if (timer == null) "Iniciar temporizador" else {
        val status = when {
            timer.running -> ""
            timer.remaining > 0L -> " · En pausa"
            else -> " · Terminado"
        }
        timer.displayTime(now) + status
    }
    Row(
        Modifier.fillMaxWidth().heightIn(min = 54.dp).clip(shape)
            .onGloballyPositioned { coordinates ->
                val point = coordinates.positionOnScreen()
                bounds = if (point.x.isFinite() && point.y.isFinite()) Rect(point,
                    Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat())) else null
            }
            .ps4ControlGlass(shape, translucent = true)
            .clickable(onClickLabel = "Abrir ${target.label}") { onOpen(bounds) }
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (cover != null) Image(cover.asImageBitmap(), null,
            Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)), contentScale = ContentScale.Crop)
        else ThemeSystemIcon(when (target) {
            QuickCenterTarget.MEDIA -> "play"
            QuickCenterTarget.NOTE -> "edit"
            else -> "clock"
        }, LauncherTheme.PS4, Modifier.size(28.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
internal fun QuickPinnedNoteCard(note: String, onSave: (String) -> Unit) {
    var draft by rememberSaveable(note) { mutableStateOf(note) }
    QuickCard {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Nota fijada", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(draft, { draft = it.take(500) }, Modifier.fillMaxWidth(),
                label = { Text("Nota") }, minLines = 2, maxLines = 5)
            TextButton(onClick = { onSave(draft.trim()) }, enabled = draft.trim() != note,
                modifier = Modifier.heightIn(min = 48.dp)) { Text("Guardar nota") }
        }
    }
}

@Composable
internal fun QuickTimerCard(
    timers: List<QuickTimer>,
    store: QuickTimers,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val next = timers.nextTimer()
    val now by rememberTimerClock(timers.filter(QuickTimer::running).minOfOrNull(QuickTimer::deadline), store)
    var name by rememberSaveable { mutableStateOf("") }
    var minutes by rememberSaveable { mutableStateOf("5") }
    var alertsEnabled by remember(store) { mutableStateOf(store.alertsEnabled()) }
    var exactAlerts by remember(store) { mutableStateOf(store.exactAlertsEnabled()) }
    var asked by rememberSaveable { mutableStateOf(false) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        alertsEnabled = store.alertsEnabled()
    }
    LaunchedEffect(lifecycle, store) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            alertsEnabled = store.alertsEnabled()
            exactAlerts = store.exactAlertsEnabled()
            store.expire()
        }
    }
    QuickCard {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeSystemIcon("clock", LauncherTheme.PS4, Modifier.size(25.dp))
                Column(Modifier.weight(1f)) {
                    Text("Temporizadores", style = MaterialTheme.typography.titleMedium)
                    if (!expanded) {
                        val summary = when {
                            next == null -> "Sin temporizadores"
                            next.running -> "${next.name} · ${next.displayTime(now)}"
                            next.remaining > 0L -> "${next.name} · En pausa"
                            else -> "${next.name} · Terminado"
                        }
                        Text(summary, style = MaterialTheme.typography.bodySmall,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                TextButton(onClick = { onExpandedChange(!expanded) }, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(if (expanded) "Contraer" else "Ver")
                }
            }
            if (expanded) {
                if (timers.size < QuickTimers.MAX_TIMERS) {
                    OutlinedTextField(name, { name = it.take(40) }, Modifier.fillMaxWidth(),
                        label = { Text("Nombre") }, singleLine = true)
                    val duration = minutes.toIntOrNull()
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(minutes, { minutes = it.filter { char -> char in '0'..'9' }.take(3) },
                            Modifier.weight(1f), label = { Text("Minutos") }, singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedButton(onClick = {
                            store.add(name.trim().ifBlank { "Temporizador" }, duration ?: return@OutlinedButton)
                            name = ""
                        }, enabled = duration != null && duration in 1..999,
                            modifier = Modifier.heightIn(min = 48.dp)) { Text("Iniciar") }
                    }
                } else Text("Elimina un temporizador para agregar otro.", style = MaterialTheme.typography.bodySmall)
                if (!alertsEnabled) {
                    Text("Activa los avisos para recibir una notificación al terminar.",
                        style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = {
                        if (Build.VERSION.SDK_INT >= 33 && !asked) {
                            asked = true
                            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName))
                    }, Modifier.heightIn(min = 48.dp)) { Text("Activar avisos") }
                }
                if (Build.VERSION.SDK_INT >= 31 && alertsEnabled && !exactAlerts) {
                    Text("El aviso puede demorarse con la pantalla apagada.",
                        style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = {
                        val settings = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.fromParts("package", context.packageName, null))
                        try {
                            context.startActivity(settings)
                        } catch (_: android.content.ActivityNotFoundException) {
                            context.startActivity(Intent(Settings.ACTION_SETTINGS))
                        }
                    }, modifier = Modifier.heightIn(min = 48.dp)) { Text("Permitir avisos exactos") }
                }
                timers.forEach { timer ->
                    key(timer.id) { QuickTimerRow(timer, now, store) }
                }
            }
        }
    }
}

@Composable
private fun QuickTimerRow(timer: QuickTimer, now: Long, store: QuickTimers) {
    val status = when {
        timer.running -> timer.displayTime(now)
        timer.remaining > 0L -> "${timer.displayTime(now)} · En pausa"
        else -> "Terminado"
    }
    HorizontalDivider()
    Text(timer.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
    Text(status, style = MaterialTheme.typography.bodyMedium)
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val toggle = if (timer.running) "Pausar" else "Reanudar"
            TimerButton(toggle, timer, Modifier.weight(1f), timer.running || timer.remaining > 0L) { store.toggle(timer.id) }
            TimerButton("+1 min", timer, Modifier.weight(1f)) { store.addMinute(timer.id) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TimerButton("Reiniciar", timer, Modifier.weight(1f)) { store.restart(timer.id) }
            TimerButton("Eliminar", timer, Modifier.weight(1f)) { store.remove(timer.id) }
        }
    }
}

@Composable
private fun TimerButton(label: String, timer: QuickTimer, modifier: Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(onClick, modifier.heightIn(min = 48.dp).semantics { contentDescription = "$label · ${timer.name}" },
        enabled = enabled) { Text(label) }
}
