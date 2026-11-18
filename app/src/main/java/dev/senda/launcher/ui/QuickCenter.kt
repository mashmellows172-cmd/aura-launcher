package dev.senda.launcher.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.draw.drawBehind
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.ActiveAudioInfo
import dev.senda.launcher.data.AgendaEvent
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.data.QuickTimer
import dev.senda.launcher.data.QuickTimers
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
private val LocalQuickAccent = compositionLocalOf { quickBlue }

/** A lighter version of the sheet material lets the wallpaper show through on Inicio. */
@Composable
internal fun Modifier.ps4ControlGlass(shape: Shape, translucent: Boolean = false): Modifier {
    if (LocalAppearance.current.highContrast) return this
    val opacity = if (translucent) .66f else 1f
    val accent = LocalQuickAccent.current
    return background(Brush.verticalGradient(listOf(
        lerp(Color(0xFF1C3359), accent, .12f).copy(alpha = opacity), Color(0xFF101E39).copy(alpha = opacity),
    )), shape).border(1.dp, lerp(quickEdge, accent, .38f).copy(alpha = if (translucent) .65f else 1f), shape)
}

@Composable
internal fun QuickCenterButton(tint: State<Color>?, onClick: (Rect?) -> Unit) {
    val contrast = LocalAppearance.current.highContrast
    var bounds by remember { mutableStateOf<Rect?>(null) }
    OutlinedButton(
        onClick = { onClick(bounds) },
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 8.dp)
            .heightIn(min = 48.dp).onGloballyPositioned { coordinates ->
                val point = coordinates.positionOnScreen()
                bounds = if (point.x.isFinite() && point.y.isFinite()) Rect(point,
                    Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat())) else null
            }.clip(CircleShape).ps4ControlGlass(CircleShape)
            .then(if (tint != null && !contrast) Modifier.ps4SelectionGlow(tint) else Modifier),
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
    appAccent: Color?,
    target: QuickCenterTarget,
    miniTarget: QuickCenterTarget,
    onMiniTargetChange: (QuickCenterTarget) -> Unit,
    pinnedNote: String,
    onSavePinnedNote: (String) -> Unit,
    timers: List<QuickTimer>,
    timerStore: QuickTimers?,
    onDismiss: () -> Unit,
    showMusic: Boolean = true,
    decorationConstrained: State<Boolean>? = null,
    onOpenMediaDetails: ((Rect?) -> Unit)? = null,
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
    val contrast = LocalAppearance.current.highContrast
    val preview = LocalThemePreview.current
    val intensity = LocalPs4AccentIntensity.current
    val colors = MaterialTheme.colorScheme
    val accent = if (contrast) colors.primary else appAccent ?: quickBlue
    val edge = if (contrast) colors.outline else lerp(quickEdge, accent, .46f)
    val panel = if (contrast) colors.surface else Color(0xFF0B1730)
    val ink = if (contrast) colors.onSurface else quickInk
    val motion = rememberPs4PanelMotion(reduced)
    val close = { motion.close(reduced, onDismiss) }
    var expanded by rememberSaveable { mutableStateOf(target == QuickCenterTarget.MEDIA) }
    var timersExpanded by rememberSaveable { mutableStateOf(target == QuickCenterTarget.TIMERS) }
    var showNote by rememberSaveable { mutableStateOf(target == QuickCenterTarget.NOTE) }
    val hasMedia = audio != null
    val atmosphere = rememberMediaAtmosphere(audio ?: ActiveAudioInfo(), reduced, showMusic)
    val mediaColor = atmosphere.color
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
    fun expandMedia(value: Boolean) {
        expanded = value
        if (value) timersExpanded = false
    }
    fun expandTimers(value: Boolean) {
        timersExpanded = value
        if (value) expanded = false
    }
    val locale = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]
    val time = remember(locale) { DateTimeFormatter.ofPattern("HH:mm", locale) }
    val date = remember(locale) { DateTimeFormatter.ofPattern("EEE d MMM", locale) }
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    var destination by remember { mutableStateOf<Rect?>(null) }
    val prefs = LocalContext.current.getSharedPreferences("quick_center_shortcuts", 0).takeUnless { preview }
    val cards = listOf("Música", "Temporizadores", "Alarma", "Agenda")
    // Sample context once per opening. Playback and countdowns can update in place.
    val playingAtOpen = rememberSaveable(target) { audio?.isPlaying == true }
    val urgentTimerAtOpen = rememberSaveable(target) {
        val now = android.os.SystemClock.elapsedRealtime()
        timers.any { it.running && it.timeLeft(now) in 1L..60_000L }
    }
    val hasEventAtOpen = rememberSaveable(target) { event != null }
    var cardOrder by remember(prefs) { mutableStateOf(quickOrder(prefs?.getString("card_order", null), cards)) }
    var pinned by remember(prefs) { mutableStateOf(prefs?.getStringSet("pinned_cards", emptySet()).orEmpty().toSet()) }
    var editing by remember { mutableStateOf(false) }
    var selectingMini by remember { mutableStateOf(false) }
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
            val density = LocalDensity.current
            val imeBottom = with(density) { WindowInsets.ime.getBottom(this).toDp() }
            val availableHeight = maxHeight - imeBottom
            val compact = availableHeight < 490.dp || density.fontScale >= 1.35f
            val sheetMax = (maxHeight - 16.dp).coerceAtLeast(120.dp)
            val cornerRadius = 24.dp
            val shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
            CompositionLocalProvider(LocalQuickAccent provides accent) {
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
                color = panel,
                contentColor = ink,
            ) {
                Box {
                SonyGlassBackdrop(audio?.artwork?.takeIf { showMusic }, mediaColor, reduced,
                    decorationConstrained ?: LocalDecorationConstrained.current)
                val surfaceFinish = if (contrast) Modifier else Modifier
                    .background(Brush.verticalGradient(listOf(Color(0x40142447), panel.copy(alpha = .88f), Color(0xFF081328))))
                    .quickCenterCornerLight(accent, cornerRadius, intensity)
                Column(Modifier.border(BorderStroke(1.dp, if (contrast) edge else quickEdge), shape)
                    .then(surfaceFinish)
                    .navigationBarsPadding().imePadding()
                    .padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                            distance <= -expandDistance && hasMedia -> { expandMedia(true); settle() }
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
                                ) { expandMedia(!mediaExpanded); true })
                                add(CustomAccessibilityAction("Cerrar Centro rápido") { close(); true })
                            }
                        }, contentAlignment = Alignment.Center) {
                        Spacer(Modifier.width(if (mediaExpanded) 58.dp else 44.dp).height(4.dp)
                            .background(if (mediaExpanded) accent else edge, CircleShape))
                    }
                    Row(Modifier.fillMaxWidth().padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ThemeSystemIcon("layers", LauncherTheme.PS4, Modifier.size(26.dp))
                        Text("Centro rápido", Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                        if (hasMedia && !compact) {
                            TextButton(onClick = { expandMedia(!mediaExpanded) },
                                colors = ButtonDefaults.textButtonColors(contentColor = accent),
                                modifier = Modifier.heightIn(min = 48.dp).semantics {
                                    contentDescription = if (mediaExpanded) "Contraer reproductor" else "Ampliar reproductor"
                                }) { Text(if (mediaExpanded) "Contraer" else "Ampliar") }
                        }
                        IconButton(onClick = close, modifier = Modifier.semantics { contentDescription = "Cerrar Centro rápido" }) {
                            ThemeSystemIcon("close", LauncherTheme.PS4)
                        }
                    }
                    val configuration: @Composable () -> Unit = {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                TextButton(onClick = { editing = true }) { Text("Ordenar y fijar tarjetas") }
                                DropdownMenu(expanded = editing, onDismissRequest = { editing = false }) {
                                    QuickOrderEditor(cardOrder, onMove = { order ->
                                        cardOrder = order
                                        prefs?.edit()?.putString("card_order", order.joinToString("|"))?.apply()
                                    }) { label ->
                                        val selected = label in pinned
                                        TextButton(onClick = {
                                            pinned = if (selected) pinned - label else pinned + label
                                            prefs?.edit()?.putStringSet("pinned_cards", pinned)?.apply()
                                        }) { Text(if (selected) "Desfijar" else "Fijar") }
                                    }
                                }
                            }
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                TextButton(onClick = { selectingMini = true }) { Text("Miniatura") }
                                DropdownMenu(expanded = selectingMini, onDismissRequest = { selectingMini = false }) {
                                    for (option in QuickCenterTarget.entries) {
                                        DropdownMenuItem(text = { Text(option.label) },
                                            trailingIcon = if (option == miniTarget) ({ Text("Activa") }) else null,
                                            onClick = {
                                                onMiniTargetChange(option)
                                                showNote = option == QuickCenterTarget.NOTE
                                                if (showNote) scope.launch { cardsScroll.scrollTo(0) }
                                                selectingMini = false
                                            })
                                    }
                                }
                            }
                        }
                    }
                    Column(Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(cardsScroll),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (showNote) QuickPinnedNoteCard(pinnedNote, onSavePinnedNote)
                        val visibleCards = cardOrder.sortedBy { card ->
                            when {
                                card == "Música" && target == QuickCenterTarget.MEDIA -> 0
                                card == "Temporizadores" && target == QuickCenterTarget.TIMERS -> 0
                                card == "Temporizadores" && urgentTimerAtOpen -> 1
                                card == "Música" && playingAtOpen -> 2
                                card in pinned -> 3
                                else -> 4
                            }
                        }.filter { card ->
                            when (card) {
                                "Música" -> showMusic
                                "Agenda" -> card in pinned || hasEventAtOpen
                                else -> true
                            }
                        }
                        for (card in visibleCards) key(card) {
                            when (card) {
                                "Música" -> if (audio != null) QuickMediaCard(audio, animated, mediaExpanded, compact, mediaColor,
                                    onExpandedChange = ::expandMedia, onPlayback, onPrevious, onNext, onSeek, onOpenMediaSource,
                                    onOpenMediaDetails?.let { open ->
                                        { origin -> motion.close(reduced) { onDismiss(); open(origin) } }
                                    })
                                else QuickActionCard("play", "Música", "Sin reproducción activa", "Abrir", onOpenPlayer)
                                "Temporizadores" -> if (!preview && timerStore != null) {
                                    QuickTimerCard(timers, timerStore, timersExpanded, ::expandTimers)
                                } else QuickActionCard("clock", "Temporizadores", "Ejemplo · 5 min",
                                    if (timersExpanded) "Contraer" else "Ver") { expandTimers(!timersExpanded) }
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
                        if (compact && !preview) configuration()
                    }
                    HorizontalDivider(color = edge.copy(alpha = .65f))
                    if (!preview && !compact) configuration()
                }
            }
            }
        }
    }
    }
}

private fun Modifier.quickCenterCornerLight(accent: Color, cornerRadius: androidx.compose.ui.unit.Dp, intensity: Float) = drawWithCache {
    val strokeWidth = 1.5.dp.toPx()
    val inset = strokeWidth
    val right = size.width - inset
    val radius = (cornerRadius.toPx() - inset).coerceAtMost((right - inset) / 2f)
    val depth = (cornerRadius * 2.4f).toPx().coerceAtMost(size.height - inset)
    if (intensity == 0f || radius <= 0f || depth <= radius + inset) return@drawWithCache onDrawBehind { }

    val outline = Path().apply {
        moveTo(inset, depth)
        lineTo(inset, inset + radius)
        arcTo(Rect(inset, inset, inset + 2f * radius, inset + 2f * radius), 180f, 90f, false)
        lineTo(right - radius, inset)
        arcTo(Rect(right - 2f * radius, inset, right, inset + 2f * radius), 270f, 90f, false)
        lineTo(right, depth)
    }
    val rim = Brush.verticalGradient(
        0f to accent.copy(alpha = .78f * intensity),
        .45f to accent.copy(alpha = .37f * intensity),
        1f to accent.copy(alpha = 0f),
        startY = inset,
        endY = depth,
    )
    val bloom = Brush.verticalGradient(
        listOf(accent.copy(alpha = .12f * intensity), accent.copy(alpha = 0f)), endY = depth,
    )
    val stroke = Stroke(strokeWidth)
    onDrawBehind {
        drawRect(bloom, size = Size(size.width, depth))
        drawPath(outline, rim, style = stroke)
    }
}

@Composable
internal fun QuickCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    val cardContrast = if (LocalAppearance.current.highContrast) Modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape) else Modifier
    Box(Modifier.fillMaxWidth().clip(shape).ps4ControlGlass(shape).then(cardContrast).then(modifier).padding(12.dp)) {
        content()
    }
}

@Composable
private fun QuickActionCard(icon: String, label: String, value: String, action: String, onClick: () -> Unit) {
    val contrast = LocalAppearance.current.highContrast
    val ink = if (contrast) MaterialTheme.colorScheme.onSurface else quickInk
    val muted = if (contrast) MaterialTheme.colorScheme.onSurfaceVariant else quickMuted
    QuickCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(46.dp).background(if (contrast) MaterialTheme.colorScheme.surfaceVariant
                else Color(0xFF214479), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center) { ThemeSystemIcon(icon, LauncherTheme.PS4, Modifier.size(26.dp)) }
            Column(Modifier.weight(1f)) {
                Text(label, color = muted, style = MaterialTheme.typography.bodyMedium)
                Text(value, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            OutlinedButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 12.dp),
                border = BorderStroke(1.dp, LocalQuickAccent.current),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ink),
                modifier = Modifier.heightIn(min = 48.dp).semantics { contentDescription = "$action · $label" },
            ) { Text(action) }
        }
    }
}

@Composable
private fun QuickMediaCard(audio: ActiveAudioInfo, animated: Boolean, expanded: Boolean, compact: Boolean,
    mediaColor: State<Color>, onExpandedChange: (Boolean) -> Unit, onPlayback: () -> Unit,
    onPrevious: () -> Unit, onNext: () -> Unit, onSeek: (Long) -> Unit, onOpenSource: () -> Unit,
    onOpenDetails: ((Rect?) -> Unit)?,
) {
    val contrast = LocalAppearance.current.highContrast
    val ink = if (contrast) MaterialTheme.colorScheme.onSurface else quickInk
    val muted = if (contrast) MaterialTheme.colorScheme.onSurfaceVariant else quickMuted
    val cover = audio.artwork?.takeUnless { it.isRecycled }
    var coverCoordinates by remember { mutableStateOf<androidx.compose.ui.layout.LayoutCoordinates?>(null) }
    var scrub by remember(audio.sessionId, audio.mediaId, audio.title, audio.durationMs) { mutableStateOf<Float?>(null) }
    var pendingSeek by remember(audio.sessionId, audio.mediaId, audio.title, audio.durationMs) { mutableStateOf<Pair<Long, Long>?>(null) }
    var seekStatus by remember(audio.sessionId, audio.mediaId, audio.title, audio.durationMs) { mutableStateOf<String?>(null) }
    val duration = audio.durationMs?.takeIf { it > 0L }
    fun seek(target: Long) {
        val limit = duration ?: return
        if (!audio.canSeek) return
        val bounded = target.coerceIn(0L, limit)
        seekStatus = null
        pendingSeek = bounded to android.os.SystemClock.elapsedRealtime()
        onSeek(bounded)
    }
    val position = remember(audio.sessionId, audio.mediaId, audio.title) { mutableStateOf(audio.positionMs) }
    LaunchedEffect(pendingSeek, audio.positionMs, audio.positionUpdatedAt) {
        val request = pendingSeek ?: return@LaunchedEffect
        val reported = audio.positionMs ?: return@LaunchedEffect
        if (audio.positionUpdatedAt >= request.second && kotlin.math.abs(reported - request.first) <= 2_000L) {
            position.value = reported.coerceIn(0L, duration ?: Long.MAX_VALUE)
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
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle, position, audio.positionMs, audio.positionUpdatedAt, audio.isPlaying, audio.speed, duration) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            val base = audio.positionMs
            while (true) {
                val elapsed = if (audio.isPlaying && audio.positionUpdatedAt > 0)
                    (android.os.SystemClock.elapsedRealtime() - audio.positionUpdatedAt).coerceAtLeast(0) else 0L
                val current = base?.let { (it + (elapsed * audio.speed).toLong()).coerceIn(0L, duration ?: Long.MAX_VALUE) }
                position.value = current
                if (!audio.isPlaying || audio.speed == 0f || current == null || duration == null || current >= duration) break
                delay(500)
            }
        }
    }
    QuickCard(if (contrast) Modifier else Modifier.sonyGlassRim(mediaColor)) {
        Column(Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (audio.isYouTubeVideo) {
            YouTubeMediaHeader(audio, expanded, animated, mediaColor,
                onExpandedChange = { onExpandedChange(!expanded) }, onOpen = onOpenSource,
                onCoverPositioned = { coverCoordinates = it }, secondaryInk = muted, actionInk = ink) {
                if (duration != null && audio.positionMs != null) QuickMediaProgress(position, duration, mediaColor)
            }
        } else         BoxWithConstraints(Modifier.fillMaxWidth()) {
            val compactCover = when {
                maxWidth < 290.dp || LocalDensity.current.fontScale >= 1.4f -> 60.dp
                maxWidth < 340.dp -> 76.dp
                else -> 88.dp
            }
            val coverSize = animateDpAsState(
                if (expanded) (compactCover + 28.dp).coerceAtMost(maxWidth * .38f) else compactCover,
                tween(if (animated) 240 else 0, easing = Ps4Motion.easing), label = "quick-media-cover")
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(Modifier.layout { measurable, constraints ->
                        val side = coverSize.value.roundToPx().coerceAtMost(constraints.maxWidth)
                        val image = measurable.measure(Constraints.fixed(side, side))
                        layout(side, side) { image.placeRelative(0, 0) }
                    }.onGloballyPositioned { coverCoordinates = it }
                    .clip(RoundedCornerShape(12.dp)).background(Color(0xFF122B59))
                    .clickable(onClickLabel = if (expanded) "Minimizar reproductor" else "Ampliar reproductor") {
                        onExpandedChange(!expanded)
                    }, contentAlignment = Alignment.Center) {
                    if (cover != null) Image(cover.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else MediaCoverFallback(audio, LauncherTheme.PS4, Modifier.fillMaxSize())
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(audio.playbackLabel, color = muted,
                        style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(audio.title ?: audio.appLabel ?: "Multimedia", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    audio.artist?.takeIf { it.isNotBlank() }?.let {
                        Text(it, color = muted, style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    if (duration != null && audio.positionMs != null) QuickMediaProgress(position, duration, mediaColor)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuickMediaButton("previous", "Pista anterior", audio.canPrevious, onPrevious)
                QuickMediaButton(if (audio.isPlaying) "pause" else "play",
                    if (audio.isPlaying) "Pausar" else "Reanudar",
                    if (audio.isPlaying) audio.canPause else audio.canPlay, onPlayback)
                QuickMediaButton("next", "Siguiente pista", audio.canNext, onNext)
            }
            TextButton(onClick = { onExpandedChange(!expanded) },
                modifier = Modifier.weight(1f).heightIn(min = 48.dp)) { Text(if (expanded) "Contraer" else "Ampliar") }
        }
        if (audio.isYouTubeVideo && audio.canSeek && duration != null && audio.positionMs != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    val baseline = pendingSeek?.first ?: position.value ?: 0L
                    seek(baseline - 10_000L)
                }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    .semantics { contentDescription = "Retroceder vídeo diez segundos" }) { Text("−10 s") }
                OutlinedButton(onClick = {
                    val baseline = pendingSeek?.first ?: position.value ?: 0L
                    seek(baseline + 10_000L)
                }, modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    .semantics { contentDescription = "Adelantar vídeo diez segundos" }) { Text("+10 s") }
            }
            val seekFeedback = if (pendingSeek != null) "Esperando confirmación…" else seekStatus
            if (!expanded && seekFeedback != null) Text(seekFeedback, color = muted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
        }
        if (audio.isYouTubeVideo && (!audio.canSeek || duration == null)) {
            Text(if (audio.isBuffering) "Esperando datos del vídeo…" else "YouTube no comunica búsqueda temporal para esta sesión",
                color = muted, style = MaterialTheme.typography.bodySmall)
        }
        AnimatedVisibility(
            visible = expanded,
            enter = if (animated) expandVertically(tween(220, easing = Ps4Motion.easing), expandFrom = Alignment.Top) +
                fadeIn(tween(160)) else androidx.compose.animation.EnterTransition.None,
            exit = if (animated) shrinkVertically(tween(180, easing = Ps4Motion.easing), shrinkTowards = Alignment.Top) +
                fadeOut(tween(110)) else androidx.compose.animation.ExitTransition.None,
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onOpenDetails != null) {
                    TextButton(onClick = {
                        val origin = coverCoordinates?.takeIf { it.isAttached }?.let { coordinates ->
                            val p = coordinates.positionOnScreen()
                            if (p.x.isFinite() && p.y.isFinite()) Rect(p.x, p.y,
                                p.x + coordinates.size.width, p.y + coordinates.size.height) else null
                        }
                        onOpenDetails(origin)
                    }, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text("Abrir ficha multimedia")
                    }
                }
                val current = position.value
                if (duration != null && current != null) {
                    val actual = (current.toDouble() / duration).toFloat().coerceIn(0f, 1f)
                    val fraction = scrub ?: actual
                    val requested = pendingSeek?.let { (it.first.toDouble() / duration).toFloat().coerceIn(0f, 1f) }
                    if (audio.canSeek) BoxWithConstraints(Modifier.fillMaxWidth().heightIn(min = 28.dp)) {
                        val bubbleWidth = 68.dp.coerceAtMost(maxWidth)
                        scrub?.let { active ->
                            Text(quickMediaTime((active.toDouble() * duration).toLong()),
                                modifier = Modifier.offset(x = (maxWidth * active - bubbleWidth / 2)
                                    .coerceIn(0.dp, maxWidth - bubbleWidth))
                                    .width(bubbleWidth).ps4ControlGlass(RoundedCornerShape(8.dp)).padding(3.dp),
                                color = ink, textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    if (audio.canSeek) Slider(value = fraction, onValueChange = { scrub = it },
                        onValueChangeFinished = {
                            scrub?.let {
                                seek((it.toDouble() * duration).toLong())
                            }
                            scrub = null
                        }, colors = if (contrast) SliderDefaults.colors() else SliderDefaults.colors(
                            thumbColor = quickMediaAccent(mediaColor.value, LocalQuickAccent.current),
                            activeTrackColor = quickMediaAccent(mediaColor.value, LocalQuickAccent.current),
                        ), modifier = Modifier.semantics { contentDescription = "Posición de reproducción" })
                    if (audio.canSeek) key(audio.sessionId, audio.title, duration) {
                        MediaSeekCue(requested, actual, confirmed = seekStatus == "Posición actualizada", animated = animated)
                    }
                    Text("${quickMediaTime(pendingSeek?.first?.takeIf { scrub == null } ?: (fraction.toDouble() * duration).toLong())} / ${quickMediaTime(duration)}",
                        color = muted, style = MaterialTheme.typography.labelMedium)
                    val status = if (pendingSeek != null) "Esperando confirmación…" else seekStatus
                    if (status != null) Text(status, color = muted, style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                    if (audio.canSeek && !audio.isYouTubeVideo) {
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
                    if (audio.sessionId != null && !audio.isYouTubeVideo) TextButton(onClick = onOpenSource, modifier = Modifier.weight(1f)) {
                        Text("Abrir app", color = LocalQuickAccent.current)
                    }
                    TextButton(onClick = { onExpandedChange(false) }) { Text("Minimizar", color = LocalQuickAccent.current) }
                }
            }
        }
        }
    }
}

@Composable
private fun QuickMediaProgress(position: State<Long?>, duration: Long, mediaColor: State<Color>) {
    val accent = LocalQuickAccent.current
    Box(Modifier.fillMaxWidth().padding(top = 8.dp).height(4.dp)
        .semantics {
            val fraction = ((position.value ?: 0L).toDouble() / duration).toFloat().coerceIn(0f, 1f)
            progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f)
        }
        .drawBehind {
            val current = position.value ?: return@drawBehind
            val fraction = (current.toDouble() / duration).toFloat().coerceIn(0f, 1f)
            val radius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f)
            drawRoundRect(Color(0xFF304B73), cornerRadius = radius)
            if (fraction > 0f) {
                val width = size.width * fraction
                val x = if (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Rtl) size.width - width else 0f
                val progressInk = quickMediaAccent(mediaColor.value, accent)
                drawRoundRect(progressInk, topLeft = androidx.compose.ui.geometry.Offset(x, 0f),
                    size = Size(width, size.height), cornerRadius = radius)
            }
        })
}

private fun quickMediaAccent(color: Color, fallback: Color): Color {
    val track = Color(0xFF304B73).luminance()
    fun contrast(ink: Color) = (maxOf(ink.luminance(), track) + .05f) /
        (minOf(ink.luminance(), track) + .05f)
    return when {
        contrast(color) >= 3f -> color
        contrast(fallback) >= 3f -> fallback
        else -> Color.White
    }
}

private fun quickMediaTime(ms: Long) = "${ms / 60_000}:${(ms / 1_000 % 60).toString().padStart(2, '0')}"

@Composable
private fun QuickMediaButton(icon: String, label: String, enabled: Boolean, onClick: () -> Unit) {
    val contrast = LocalAppearance.current.highContrast
    val ink = if (contrast) MaterialTheme.colorScheme.onSurface else quickInk
    val edge = if (contrast) MaterialTheme.colorScheme.outline else lerp(quickEdge, LocalQuickAccent.current, .46f)
    val fill = if (contrast) listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
        else listOf(Color(0xFF2C4A79), Color(0xFF1A3056))
    IconButton(onClick = onClick, enabled = enabled,
        modifier = Modifier.size(48.dp).semantics { contentDescription = label }) {
        Box(Modifier.size(36.dp)
            .background(Brush.verticalGradient(fill), CircleShape)
            .border(1.dp, edge.copy(alpha = if (enabled) .65f else .3f), CircleShape),
            contentAlignment = Alignment.Center) {
            CompositionLocalProvider(LocalContentColor provides ink.copy(alpha = if (enabled) 1f else .4f)) {
                ThemeSystemIcon(icon, LauncherTheme.SYSTEM, Modifier.size(20.dp))
            }
        }
    }
}
