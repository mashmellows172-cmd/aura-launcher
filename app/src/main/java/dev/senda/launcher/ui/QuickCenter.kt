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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.senda.launcher.data.ActiveAudioInfo
import dev.senda.launcher.data.AgendaEvent
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance
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

/** A lighter version of the sheet material lets the wallpaper show through on Inicio. */
@Composable
internal fun Modifier.ps4ControlGlass(shape: Shape, translucent: Boolean = false): Modifier {
    if (LocalAppearance.current.highContrast) return this
    val opacity = if (translucent) .66f else 1f
    return background(Brush.verticalGradient(listOf(
        Color(0xFF1C3359).copy(alpha = opacity), Color(0xFF101E39).copy(alpha = opacity),
    )), shape).border(1.dp, quickEdge.copy(alpha = if (translucent) .65f else 1f), shape)
}

@Composable
internal fun QuickCenterButton(onClick: () -> Unit) {
    val contrast = LocalAppearance.current.highContrast
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 8.dp)
            .heightIn(min = 48.dp).ps4ControlGlass(CircleShape),
        shape = CircleShape,
        border = BorderStroke(1.5.dp, if (contrast) MaterialTheme.colorScheme.outline else quickBlue),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = if (contrast) MaterialTheme.colorScheme.onBackground else quickInk,
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
                    .navigationBarsPadding()
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
                    Column(Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
}

@Composable
private fun QuickCard(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(Modifier.fillMaxWidth().clip(shape).ps4ControlGlass(shape).padding(12.dp)) {
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
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val coverSize = when {
                maxWidth < 290.dp || LocalDensity.current.fontScale >= 1.4f -> 60.dp
                maxWidth < 340.dp -> 76.dp
                else -> 88.dp
            }
            Row(Modifier.fillMaxWidth().heightIn(min = coverSize),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(coverSize).onGloballyPositioned { coverCoordinates = it }
                    .clip(RoundedCornerShape(10.dp)).background(Color(0xFF122B59))
                    .clickable(onClickLabel = "Ampliar reproductor") {
                        val coordinates = coverCoordinates?.takeIf { it.isAttached }
                        onMedia(coordinates?.let {
                            val origin = it.positionOnScreen()
                            if (origin.x.isFinite() && origin.y.isFinite()) androidx.compose.ui.geometry.Rect(
                                origin.x, origin.y, origin.x + it.size.width, origin.y + it.size.height,
                            ) else null
                        })
                    }, contentAlignment = Alignment.Center) {
                    if (cover != null) Image(cover.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else MediaCoverFallback(audio, LauncherTheme.PS4, Modifier.fillMaxSize())
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(if (audio.isPlaying) "En reproducción" else "En pausa", color = quickMuted,
                        style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(audio.title ?: audio.appLabel ?: "Multimedia", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    val current = position
                    if (duration != null && current != null) {
                        val fraction = (current.toDouble() / duration).toFloat().coerceIn(0f, 1f)
                        Box(Modifier.fillMaxWidth().padding(top = 8.dp).height(4.dp)
                            .semantics { progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f) }
                            .background(Color(0xFF304B73), CircleShape)) {
                            if (fraction > 0f) Box(Modifier.fillMaxWidth(fraction).fillMaxHeight()
                                .background(quickBlue, CircleShape))
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    QuickMediaButton(if (audio.isPlaying) "pause" else "play",
                        if (audio.isPlaying) "Pausar" else "Reanudar",
                        if (audio.isPlaying) audio.canPause else audio.canPlay, onPlayback)
                    QuickMediaButton("next", "Siguiente pista", audio.canNext, onNext)
                }
        }
        }
    }
}

@Composable
private fun QuickMediaButton(icon: String, label: String, enabled: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, enabled = enabled,
        modifier = Modifier.size(48.dp).semantics { contentDescription = label }) {
        Box(Modifier.size(36.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF2C4A79), Color(0xFF1A3056))), CircleShape)
            .border(1.dp, quickEdge.copy(alpha = if (enabled) .65f else .3f), CircleShape),
            contentAlignment = Alignment.Center) {
            CompositionLocalProvider(LocalContentColor provides quickInk.copy(alpha = if (enabled) 1f else .4f)) {
                ThemeSystemIcon(icon, LauncherTheme.SYSTEM, Modifier.size(20.dp))
            }
        }
    }
}
