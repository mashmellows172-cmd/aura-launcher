package dev.senda.launcher.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.senda.launcher.data.ActiveAudioInfo
import dev.senda.launcher.data.AgendaEvent
import dev.senda.launcher.data.LauncherTheme
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val quickInk = Color(0xFFF4F8FF)
private val quickMuted = Color(0xFFCDDEF5)
private val quickBlue = Color(0xFF65CEFF)
private val quickEdge = Color(0xFF456EAD)

@Composable
internal fun QuickCenterButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 8.dp)
            .heightIn(min = 48.dp),
        shape = CircleShape,
        border = BorderStroke(1.5.dp, quickBlue),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0xFF0B1935), contentColor = quickInk,
        ),
    ) {
        ThemeSystemIcon("layers", LauncherTheme.PS4)
        Spacer(Modifier.width(10.dp))
        Text("Centro rápido", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
internal fun QuickCenter(
    audio: ActiveAudioInfo?,
    alarmAt: Long?,
    event: AgendaEvent?,
    reduced: Boolean,
    onDismiss: () -> Unit,
    onMedia: (androidx.compose.ui.geometry.Rect?) -> Unit,
    onPlayback: () -> Unit,
    onNext: () -> Unit,
    onAlarm: () -> Unit,
    onEvent: (AgendaEvent) -> Unit,
) {
    val motion = rememberPs4PanelMotion(reduced)
    val close = { motion.close(reduced, onDismiss) }
    val locale = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]
    val time = remember(locale) { DateTimeFormatter.ofPattern("HH:mm", locale) }
    val date = remember(locale) { DateTimeFormatter.ofPattern("EEE d MMM", locale) }
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    Dialog(
        onDismissRequest = close,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            Box(Modifier.matchParentSize().graphicsLayer { alpha = motion.progress.value }
                .background(Color.Black.copy(alpha = .52f))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null,
                    onClickLabel = "Cerrar Centro rápido", onClick = close))
            val sheetMax = (maxHeight - 24.dp).coerceAtLeast(120.dp)
            val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).statusBarsPadding()
                    .widthIn(max = 560.dp).fillMaxWidth().heightIn(max = sheetMax)
                    .graphicsLayer {
                        val p = motion.progress.value
                        translationY = (1f - p) * size.height
                    }.semantics { paneTitle = "Centro rápido" },
                shape = shape,
                color = Color(0xFF0B1730),
                contentColor = quickInk,
            ) {
                Column(Modifier.background(Brush.verticalGradient(listOf(Color(0xFF142447), Color(0xFF081328))))
                    .border(BorderStroke(1.dp, quickEdge), shape)
                    .navigationBarsPadding().verticalScroll(rememberScrollState())
                    .padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Spacer(Modifier.align(Alignment.CenterHorizontally).width(44.dp).height(4.dp)
                        .background(Color(0xFF59749F), CircleShape))
                    Row(Modifier.fillMaxWidth().padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ThemeSystemIcon("layers", LauncherTheme.PS4, Modifier.size(26.dp))
                        Text("Centro rápido", Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall)
                        IconButton(onClick = close, modifier = Modifier.semantics { contentDescription = "Cerrar Centro rápido" }) {
                            ThemeSystemIcon("close", LauncherTheme.PS4)
                        }
                    }
                    if (audio != null) QuickMediaCard(audio, onMedia, onPlayback, onNext)
                    if (alarmAt != null) {
                        val at = Instant.ofEpochMilli(alarmAt).atZone(zone)
                        val day = when (at.toLocalDate()) {
                            today -> "Hoy"
                            today.plusDays(1) -> "Mañana"
                            else -> date.format(at)
                        }
                        QuickActionCard("alarm", "Próxima alarma", "${time.format(at)} · $day", "Editar", onAlarm)
                    }
                    if (event != null) {
                        val at = Instant.ofEpochMilli(event.begin).atZone(if (event.allDay) ZoneOffset.UTC else zone)
                        val whenText = if (event.allDay) date.format(at) else
                            if (at.toLocalDate() == today) time.format(at) else "${date.format(at)} ${time.format(at)}"
                        QuickActionCard("calendar", "Próximo evento", "$whenText · ${event.title}", "Ver") { onEvent(event) }
                    }
                    if (audio == null && alarmAt == null && event == null) {
                        Text("No hay música, alarmas ni eventos para mostrar.", color = quickMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickCard(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(Modifier.fillMaxWidth().clip(shape)
        .background(Brush.horizontalGradient(listOf(Color(0xFF172C51), Color(0xFF10203D))))
        .border(1.dp, quickEdge, shape).padding(12.dp)) {
        content()
    }
}

@Composable
private fun QuickActionCard(icon: String, label: String, value: String, action: String, onClick: () -> Unit) {
    QuickCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(46.dp).background(Color(0xFF214479), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center) { ThemeSystemIcon(icon, LauncherTheme.PS4, Modifier.size(26.dp)) }
            Column(Modifier.weight(1f)) {
                Text(label, color = quickMuted, style = MaterialTheme.typography.bodyMedium)
                Text(value, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            OutlinedButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 12.dp),
                border = BorderStroke(1.dp, quickBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = quickInk),
                modifier = Modifier.heightIn(min = 48.dp).semantics { contentDescription = "$action · $label" },
            ) { Text(action) }
        }
    }
}

@Composable
private fun QuickMediaCard(audio: ActiveAudioInfo, onMedia: (androidx.compose.ui.geometry.Rect?) -> Unit, onPlayback: () -> Unit, onNext: () -> Unit) {
    val cover = audio.artwork?.takeUnless { it.isRecycled }
    var coverCoordinates by remember { mutableStateOf<androidx.compose.ui.layout.LayoutCoordinates?>(null) }
    val duration = audio.durationMs?.takeIf { it > 0L }
    var position by remember(audio.sessionId, audio.title) { mutableStateOf(audio.positionMs) }
    LaunchedEffect(audio.positionMs, audio.positionUpdatedAt, audio.isPlaying, audio.speed, duration) {
        val base = audio.positionMs
        do {
            val elapsed = if (audio.isPlaying && audio.positionUpdatedAt > 0)
                (android.os.SystemClock.elapsedRealtime() - audio.positionUpdatedAt).coerceAtLeast(0) else 0L
            position = base?.let { (it + (elapsed * audio.speed).toLong()).coerceIn(0L, duration ?: Long.MAX_VALUE) }
            if (audio.isPlaying && base != null && duration != null) delay(500) else break
        } while (duration != null && (position ?: 0L) < duration)
    }
    QuickCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(88.dp).onGloballyPositioned { coverCoordinates = it }
                .clip(RoundedCornerShape(10.dp)).background(Color(0xFF122B59))
                .clickable(onClickLabel = "Ampliar reproductor") {
                    val coordinates = coverCoordinates?.takeIf { it.isAttached }
                    val origin = coordinates?.positionOnScreen()?.takeIf { it.x.isFinite() && it.y.isFinite() }
                    onMedia(if (origin != null && coordinates != null) androidx.compose.ui.geometry.Rect(
                        origin.x, origin.y, origin.x + coordinates.size.width, origin.y + coordinates.size.height,
                    ) else null)
                }, contentAlignment = Alignment.Center) {
                if (cover != null) Image(cover.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else MediaCoverFallback(audio, LauncherTheme.PS4, Modifier.fillMaxSize())
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(if (audio.isPlaying) "En reproducción" else "En pausa", color = quickMuted,
                    style = MaterialTheme.typography.bodySmall)
                Text(audio.title ?: audio.appLabel ?: "Multimedia", style = MaterialTheme.typography.titleMedium,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                val current = position
                if (duration != null && current != null) LinearProgressIndicator(
                    progress = { (current.toDouble() / duration).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp).height(4.dp),
                    color = quickBlue, trackColor = Color(0xFF304B73),
                )
            }
            if (if (audio.isPlaying) audio.canPause else audio.canPlay) QuickMediaButton(
                if (audio.isPlaying) "pause" else "play",
                if (audio.isPlaying) "Pausar" else "Reanudar", onPlayback,
            )
            if (audio.canNext) QuickMediaButton("next", "Siguiente pista", onNext)
        }
    }
}

@Composable
private fun QuickMediaButton(icon: String, label: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp).semantics { contentDescription = label }) {
        Box(Modifier.size(38.dp).background(Color(0xFF233D6A), CircleShape), contentAlignment = Alignment.Center) {
            ThemeSystemIcon(icon, LauncherTheme.PS4)
        }
    }
}
