package dev.senda.launcher.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.stateDescription
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val quickInk = Color(0xFFF4F8FF)
private val quickMuted = Color(0xFFCDDEF5)
internal val quickBlue = Color(0xFF65CEFF)
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
internal fun QuickCenterButton(onClick: (Rect?) -> Unit) {
    val contrast = LocalAppearance.current.highContrast
    var bounds by remember { mutableStateOf<Rect?>(null) }
    OutlinedButton(
        onClick = { onClick(bounds) },
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 8.dp)
            .heightIn(min = 48.dp).onGloballyPositioned { coordinates ->
                val point = coordinates.positionOnScreen()
                bounds = if (point.x.isFinite() && point.y.isFinite()) Rect(point,
                    Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat())) else null
            }.ps4ControlGlass(CircleShape),
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
    origin: Rect?,
    onDismiss: () -> Unit,
    onSearch: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onPlayback: () -> Unit,
    onNext: () -> Unit,
    onAlarm: () -> Unit,
    onEvent: (AgendaEvent) -> Unit,
    onCalendar: () -> Unit,
    onOpenPlayer: () -> Unit,
    onOpenMediaSource: () -> Unit,
) {
    val motion = rememberPs4PanelMotion(reduced)
    val close = { motion.close(reduced, onDismiss) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val hasMedia = audio != null
    val mediaExpanded = expanded && hasMedia
    val animated = motion.effect != "NONE" && LocalMotionSettings.current.mediaDetailsMotionEnabled
    val latestClose by rememberUpdatedState(close)
    val scope = rememberCoroutineScope()
    val cardsScroll = rememberScrollState()
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var settling by remember { mutableStateOf<Job?>(null) }
    fun settle() {
        settling?.cancel()
        if (!animated) dragOffset = 0f
        else settling = scope.launch {
            animate(dragOffset, 0f, animationSpec = tween(180, easing = Ps4Motion.easing)) { value, _ ->
                dragOffset = value
            }
        }
    }
    LaunchedEffect(hasMedia) { if (!hasMedia) expanded = false }
    LaunchedEffect(mediaExpanded) { if (mediaExpanded) cardsScroll.scrollTo(0) }
    val locale = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]
    val time = remember(locale) { DateTimeFormatter.ofPattern("HH:mm", locale) }
    val date = remember(locale) { DateTimeFormatter.ofPattern("EEE d MMM", locale) }
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    var destination by remember { mutableStateOf<Rect?>(null) }
    val prefs = LocalContext.current.getSharedPreferences("quick_center_shortcuts", 0)
    val cards = listOf("Música", "Alarma", "Agenda")
    var cardOrder by remember(prefs) { mutableStateOf(quickOrder(prefs.getString("card_order", null), cards)) }
    var pinned by remember(prefs) { mutableStateOf(prefs.getStringSet("pinned_cards", emptySet()).orEmpty().toSet()) }
    var editing by remember { mutableStateOf(false) }
    Dialog(
        onDismissRequest = close,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            Box(Modifier.matchParentSize().graphicsLayer {
                alpha = motion.progress.value * (1f - (dragOffset.coerceAtLeast(0f) / size.height.coerceAtLeast(1f)) * .65f)
            }
                .background(Color.Black.copy(alpha = .52f))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null,
                    onClickLabel = "Cerrar Centro rápido", onClick = close))
            val sheetMax = (maxHeight - 24.dp).coerceAtLeast(120.dp)
            val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).statusBarsPadding()
                    .widthIn(max = 560.dp).fillMaxWidth().heightIn(max = sheetMax)
                    .onGloballyPositioned { coordinates ->
                        val point = coordinates.positionOnScreen()
                        destination = if (point.x.isFinite() && point.y.isFinite()) Rect(point,
                            Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat())) else null
                    }
                    .ps4PanelTransform(motion.progress.asState(), motion.effect, origin = { origin }, destination = { destination })
                    .graphicsLayer { if (animated) translationY = dragOffset }
                    .semantics { paneTitle = "Centro rápido" },
                shape = shape,
                color = Color(0xFF0B1730),
                contentColor = quickInk,
            ) {
                Column(Modifier.background(Brush.verticalGradient(listOf(Color(0xFF142447), Color(0xFF081328))))
                    .border(BorderStroke(1.dp, quickEdge), shape)
                    .navigationBarsPadding()
                    .padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        .pointerInput(hasMedia, mediaExpanded, animated) {
                            val expandDistance = 44.dp.toPx()
                            val closeDistance = (if (mediaExpanded) 138.dp else 78.dp).toPx()
                            var distance = 0f
                            detectVerticalDragGestures(
                                onDragStart = {
                                    settling?.cancel()
                                    distance = 0f
                                    dragOffset = 0f
                                },
                                onVerticalDrag = { change, delta ->
                                    change.consume()
                                    distance += delta
                                    if (animated && !motion.closing) {
                                        val down = distance.coerceAtLeast(0f)
                                        dragOffset = if (distance < 0f) (distance * .35f).coerceAtLeast(-24.dp.toPx())
                                        else (down * .68f).coerceAtMost(180.dp.toPx())
                                    }
                                },
                                onDragCancel = { if (!motion.closing) settle() },
                                onDragEnd = {
                                    if (!motion.closing) {
                                        when {
                                            distance >= closeDistance -> latestClose()
                                            distance >= expandDistance && mediaExpanded -> { expanded = false; settle() }
                                            distance <= -expandDistance && hasMedia -> { expanded = true; settle() }
                                            else -> settle()
                                        }
                                    }
                                },
                            )
                        }
                        .semantics {
                            contentDescription = "Asa del Centro rápido"
                            stateDescription = if (mediaExpanded) "Reproductor ampliado" else "Panel compacto"
                            customActions = buildList {
                                if (hasMedia) add(CustomAccessibilityAction(
                                    if (mediaExpanded) "Contraer reproductor" else "Ampliar reproductor",
                                ) { expanded = !mediaExpanded; true })
                                add(CustomAccessibilityAction("Cerrar Centro rápido") { close(); true })
                            }
                        }, contentAlignment = Alignment.Center) {
                        Spacer(Modifier.width(if (mediaExpanded) 58.dp else 44.dp).height(4.dp)
                            .background(if (mediaExpanded) quickBlue else Color(0xFF59749F), CircleShape))
                    }
                    Row(Modifier.fillMaxWidth().padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ThemeSystemIcon("layers", LauncherTheme.PS4, Modifier.size(26.dp))
                        Text("Centro rápido", Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall)
                        IconButton(onClick = close, modifier = Modifier.semantics { contentDescription = "Cerrar Centro rápido" }) {
                            ThemeSystemIcon("close", LauncherTheme.PS4)
                        }
                    }
                    Column(Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(cardsScroll),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val visibleCards = cardOrder.sortedBy {
                        when {
                            mediaExpanded && it == "Música" -> 0
                            it in pinned -> 1
                            else -> 2
                        }
                    }.filter {
                        it in pinned || when (it) {
                            "Música" -> audio != null
                            "Alarma" -> alarmAt != null
                            else -> event != null
                        }
                    }
                    for (card in visibleCards) key(card) {
                    when (card) {
                    "Música" -> if (audio != null) QuickMediaCard(audio, animated, mediaExpanded,
                        onExpandedChange = { expanded = it }, onPlayback, onPrevious, onNext, onSeek, onOpenMediaSource)
                    else QuickActionCard("play", "Música", "Sin reproducción activa", "Abrir", onOpenPlayer)
                    "Alarma" -> if (alarmAt != null) {
                        val at = Instant.ofEpochMilli(alarmAt).atZone(zone)
                        val day = when (at.toLocalDate()) {
                            today -> "Hoy"
                            today.plusDays(1) -> "Mañana"
                            else -> date.format(at)
                        }
                        QuickActionCard("alarm", "Próxima alarma", "${time.format(at)} · $day", "Editar", onAlarm)
                    } else QuickActionCard("alarm", "Próxima alarma", "Sin alarma informada", "Abrir Reloj", onAlarm)
                    "Agenda" -> if (event != null) {
                        val at = Instant.ofEpochMilli(event.begin).atZone(if (event.allDay) ZoneOffset.UTC else zone)
                        val whenText = if (event.allDay) date.format(at) else
                            if (at.toLocalDate() == today) time.format(at) else "${date.format(at)} ${time.format(at)}"
                        QuickActionCard("calendar", "Próximo evento", "$whenText · ${event.title}", "Ver") { onEvent(event) }
                    } else QuickActionCard("calendar", "Agenda", "Sin evento disponible", "Abrir", onCalendar)
                    }
                    }
                    if (visibleCards.isEmpty()) {
                        Text("No hay música, alarmas ni eventos para mostrar.", color = quickMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp))
                    }
                    }
                    QuickCenterActions(onSearch = { motion.close(reduced, onSearch) },
                        onSettings = { action -> motion.close(reduced) { onDismiss(); action() } })
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TextButton(onClick = { editing = true }) { Text("Ordenar y fijar tarjetas") }
                        DropdownMenu(expanded = editing, onDismissRequest = { editing = false }) {
                            QuickOrderEditor(cardOrder, onMove = { order ->
                                cardOrder = order
                                prefs.edit().putString("card_order", order.joinToString("|")).apply()
                            }) { label ->
                                val selected = label in pinned
                                TextButton(onClick = {
                                    pinned = if (selected) pinned - label else pinned + label
                                    prefs.edit().putStringSet("pinned_cards", pinned).apply()
                                }) { Text(if (selected) "Desfijar" else "Fijar") }
                            }
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
private fun QuickMediaCard(audio: ActiveAudioInfo, animated: Boolean, expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit, onPlayback: () -> Unit,
    onPrevious: () -> Unit, onNext: () -> Unit, onSeek: (Long) -> Unit, onOpenSource: () -> Unit,
) {
    val cover = audio.artwork?.takeUnless { it.isRecycled }
    var scrub by remember(audio.sessionId, audio.title, audio.durationMs) { mutableStateOf<Float?>(null) }
    var pendingSeek by remember(audio.sessionId, audio.title, audio.durationMs) { mutableStateOf<Pair<Long, Long>?>(null) }
    var seekStatus by remember(audio.sessionId, audio.title, audio.durationMs) { mutableStateOf<String?>(null) }
    val duration = audio.durationMs?.takeIf { it > 0L }
    fun seek(target: Long) {
        val limit = duration ?: return
        if (!audio.canSeek) return
        val bounded = target.coerceIn(0L, limit)
        seekStatus = null
        pendingSeek = bounded to android.os.SystemClock.elapsedRealtime()
        onSeek(bounded)
    }
    var position by remember(audio.sessionId, audio.title) { mutableStateOf(audio.positionMs) }
    LaunchedEffect(pendingSeek, audio.positionMs, audio.positionUpdatedAt) {
        val request = pendingSeek ?: return@LaunchedEffect
        val reported = audio.positionMs ?: return@LaunchedEffect
        if (audio.positionUpdatedAt >= request.second && kotlin.math.abs(reported - request.first) <= 2_000L) {
            pendingSeek = null
            seekStatus = "Posición actualizada"
        }
    }
    LaunchedEffect(pendingSeek) {
        if (pendingSeek == null) return@LaunchedEffect
        delay(2_200)
        pendingSeek = null
        seekStatus = "El reproductor no confirmó el salto"
    }
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
        Column(Modifier.fillMaxWidth().then(if (animated) Modifier.animateContentSize(tween(260, easing = Ps4Motion.easing)) else Modifier),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val compactCover = when {
                maxWidth < 290.dp || LocalDensity.current.fontScale >= 1.4f -> 60.dp
                maxWidth < 340.dp -> 76.dp
                else -> 88.dp
            }
            val coverSize = if (expanded) (compactCover + 24.dp).coerceAtMost(maxWidth * .40f) else compactCover
            Row(Modifier.fillMaxWidth().heightIn(min = coverSize),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(coverSize)
                    .clip(RoundedCornerShape(10.dp)).background(Color(0xFF122B59))
                    .clickable(onClickLabel = if (expanded) "Minimizar reproductor" else "Ampliar reproductor") {
                        onExpandedChange(!expanded)
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
        if (expanded) {
            audio.artist?.takeIf { it.isNotBlank() }?.let {
                Text(it, color = quickMuted, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            }
            val current = position
            if (duration != null && current != null) {
                val fraction = scrub ?: ((pendingSeek?.first ?: current).toDouble() / duration).toFloat().coerceIn(0f, 1f)
                if (audio.canSeek) BoxWithConstraints(Modifier.fillMaxWidth().heightIn(min = 28.dp)) {
                    val bubbleWidth = 68.dp.coerceAtMost(maxWidth)
                    scrub?.let { active ->
                        Text(quickMediaTime((active.toDouble() * duration).toLong()),
                            modifier = Modifier.offset(x = (maxWidth * active - bubbleWidth / 2)
                                .coerceIn(0.dp, maxWidth - bubbleWidth))
                                .width(bubbleWidth).ps4ControlGlass(RoundedCornerShape(8.dp)).padding(3.dp),
                            color = quickInk, textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium)
                    }
                }
                if (audio.canSeek) Slider(value = fraction, onValueChange = { scrub = it },
                    onValueChangeFinished = {
                        scrub?.let {
                            seek((it.toDouble() * duration).toLong())
                        }
                        scrub = null
                    }, modifier = Modifier.semantics { contentDescription = "Posición de reproducción" })
                Text("${quickMediaTime((fraction.toDouble() * duration).toLong())} / ${quickMediaTime(duration)}",
                    color = quickMuted, style = MaterialTheme.typography.labelMedium)
                val status = if (pendingSeek != null) "Esperando confirmación…" else seekStatus
                if (status != null) Text(status, color = quickMuted, style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                if (audio.canSeek) {
                    val baseline = pendingSeek?.first ?: current
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { seek(baseline - 10_000L) }, enabled = baseline > 0,
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                                .semantics { contentDescription = "Retroceder 10 segundos" }) { Text("−10 s") }
                        OutlinedButton(onClick = { seek(baseline + 30_000L) }, enabled = baseline < duration,
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                                .semantics { contentDescription = "Adelantar 30 segundos" }) { Text("+30 s") }
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                QuickMediaButton("previous", "Pista anterior", audio.canPrevious, onPrevious)
                if (audio.sessionId != null) TextButton(onClick = onOpenSource, modifier = Modifier.weight(1f)) {
                    Text("Abrir app", color = quickBlue)
                }
                TextButton(onClick = { onExpandedChange(false) }) { Text("Minimizar", color = quickBlue) }
            }
        }
        }
    }
}

private fun quickMediaTime(ms: Long) = "${ms / 60_000}:${(ms / 1_000 % 60).toString().padStart(2, '0')}"

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
