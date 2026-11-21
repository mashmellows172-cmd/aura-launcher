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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.clearAndSetSemantics
import kotlin.math.roundToInt
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
import dev.senda.launcher.ui.theme.LocalPs4Palette
import dev.senda.launcher.ui.theme.ps4Design
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

private val quickInk: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface
internal val quickMuted: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
internal val quickBlue: Color
    @Composable get() = MaterialTheme.colorScheme.primary
private val quickEdge: Color
    @Composable get() = MaterialTheme.colorScheme.outline
private val LocalQuickAccent = compositionLocalOf<Color?> { null }

internal val quickAccent: Color
    @Composable get() = LocalQuickAccent.current ?: MaterialTheme.colorScheme.primary

private fun quickTargetRailCard(target: QuickCenterTarget) = when (target) {
    QuickCenterTarget.MEDIA -> "Música"
    QuickCenterTarget.TIMERS -> "Temporizadores"
    QuickCenterTarget.NOTE -> "Nota"
    QuickCenterTarget.PANEL -> "Accesos"
}

/** A lighter version of the sheet material lets the wallpaper show through on Inicio. */
@Composable
internal fun Modifier.ps4ControlGlass(shape: Shape, translucent: Boolean = false): Modifier {
    if (LocalAppearance.current.highContrast) return this
    val opacity = if (translucent) .66f else 1f
    val accent = quickAccent
    return background(Brush.verticalGradient(listOf(
        lerp(MaterialTheme.colorScheme.surfaceVariant, accent, .05f).copy(alpha = opacity), MaterialTheme.colorScheme.surface.copy(alpha = opacity),
    )), shape).border(1.dp, lerp(quickEdge, accent, .38f).copy(alpha = if (translucent) .65f else 1f), shape)
        .quickSurfaceDetail()
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
            }.clip(quickPanelShape()).ps4ControlGlass(quickPanelShape())
            .then(if (tint != null && !contrast) Modifier.ps4SelectionGlow(tint) else Modifier),
        shape = quickPanelShape(),
        border = BorderStroke(1.5.dp, if (contrast) MaterialTheme.colorScheme.outline else quickBlue),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = if (contrast) MaterialTheme.colorScheme.onBackground else quickInk,
        ),
    ) {
        ThemeSystemIcon("layers", activeQuickTheme)
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
    artworkOrigin: MediaArtworkOrigin? = null,
) {
    val contrast = LocalAppearance.current.highContrast
    val preview = LocalThemePreview.current
    val intensity = LocalPs4AccentIntensity.current
    val colors = MaterialTheme.colorScheme
    val accent = if (contrast) colors.primary else appAccent ?: quickBlue
    val edge = if (contrast) colors.outline else lerp(quickEdge, accent, .46f)
    val panel = colors.background
    val ink = if (contrast) colors.onSurface else quickInk
    val motion = rememberPs4PanelMotion(reduced)
    // Every opening starts as a compact rail. A target selects its capsule but does not
    // obscure the launcher until the user explicitly reveals that contextual detail.
    var expanded by rememberSaveable { mutableStateOf(false) }
    var timersExpanded by rememberSaveable { mutableStateOf(false) }
    var showNote by rememberSaveable { mutableStateOf(target == QuickCenterTarget.NOTE) }
    var selectedRailCard by rememberSaveable { mutableStateOf(quickTargetRailCard(target)) }
    var revealedRailCard by rememberSaveable { mutableStateOf<String?>(null) }
    val hasMedia = audio != null
    val atmosphere = rememberMediaAtmosphere(audio ?: ActiveAudioInfo(), reduced, showMusic)
    val mediaColor = atmosphere.color
    val mediaExpanded = expanded && hasMedia
    val animated = motion.effect != "NONE" && LocalMotionSettings.current.mediaDetailsMotionEnabled
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val panelDrag = remember(scope, density.density) {
        QuickPanelDragState(scope, with(density) { 180.dp.toPx() }, with(density) { 240.dp.toPx() },
            with(density) { 1000.dp.toPx() }, mediaExpanded)
    }
    val close = { panelDrag.interrupt(); motion.close(reduced, onDismiss) }
    val latestDismiss by rememberUpdatedState(onDismiss)
    val cardsScroll = rememberScrollState()
    val expansion = if (animated) panelDrag.expansion else rememberUpdatedState(if (mediaExpanded) 1f else 0f)
    val coverBounds = remember { mutableStateOf<Rect?>(null) }
    val visibleCoverBounds = remember { mutableStateOf<Rect?>(null) }
    val dialogOrigin = remember { mutableStateOf(Offset.Zero) }
    val artworkProgress = remember(panelDrag, motion, animated) { derivedStateOf {
        motion.progress.value * if (animated) (1f - panelDrag.offset.value.coerceAtLeast(0f) / panelDrag.dismissDistance).coerceIn(0f, 1f) else 1f
    } }
    val overlayArtwork = animated && !contrast && selectedRailCard == "Música" &&
        (mediaExpanded || expansion.value > .001f) && audio?.artwork?.let { !it.isRecycled } == true
    fun positionArtwork(coordinates: LayoutCoordinates) {
        if (!coordinates.isAttached) { coverBounds.value = null; visibleCoverBounds.value = null; return }
        val point = coordinates.positionOnScreen()
        coverBounds.value = Rect(point, Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat()))
        visibleCoverBounds.value = coordinates.boundsInWindow().translate(point - coordinates.positionInWindow())
    }
    LaunchedEffect(hasMedia) {
        if (!hasMedia) { expanded = false; panelDrag.expand(false, animated) }
    }
    fun expandMedia(value: Boolean) {
        expanded = value && hasMedia
        panelDrag.expand(expanded, animated)
        if (expanded) timersExpanded = false
    }
    fun expandTimers(value: Boolean) {
        timersExpanded = value
        if (value) expandMedia(false)
    }
    fun revealRailCard(card: String) {
        selectedRailCard = card
        revealedRailCard = card
        when (card) {
            "Música" -> expandMedia(true)
            "Temporizadores" -> expandTimers(true)
            else -> {
                expandMedia(false)
                timersExpanded = false
            }
        }
    }
    fun collapseRailCard(card: String) {
        if (revealedRailCard == card) revealedRailCard = null
        when (card) {
            "Música" -> expandMedia(false)
            "Temporizadores" -> expandTimers(false)
        }
    }
    fun selectRailCard(card: String) {
        selectedRailCard = card
        if (card != "Música") expandMedia(false)
        if (card != "Temporizadores") timersExpanded = false
        if (revealedRailCard != card) revealedRailCard = null
    }
    val locale = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]
    val time = remember(locale) { DateTimeFormatter.ofPattern("HH:mm", locale) }
    val date = remember(locale) { DateTimeFormatter.ofPattern("EEE d MMM", locale) }
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    var destination by remember { mutableStateOf<Rect?>(null) }
    val prefs = LocalContext.current.getSharedPreferences("quick_center_shortcuts", 0).takeUnless { preview }
    val cards = listOf("Música", "Temporizadores", "Alarma", "Agenda")
    var cardOrder by remember(prefs) { mutableStateOf(quickOrder(prefs?.getString("card_order", null), cards)) }
    var pinned by remember(prefs) { mutableStateOf(prefs?.getStringSet("pinned_cards", emptySet()).orEmpty().toSet()) }
    var editing by remember { mutableStateOf(false) }
    var selectingMini by remember { mutableStateOf(false) }
    Dialog(
        onDismissRequest = close,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize().onGloballyPositioned { dialogOrigin.value = it.positionOnScreen() }) {
            Box(Modifier.matchParentSize().graphicsLayer {
                alpha = motion.progress.value * (1f - (if (animated) panelDrag.offset.value.coerceAtLeast(0f) / panelDrag.dismissDistance else 0f) * .65f)
            }
                .background(Color.Black.copy(alpha = .52f))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null,
                    onClickLabel = "Cerrar Centro rápido", onClick = close))
            val density = LocalDensity.current
            val imeBottom = with(density) { WindowInsets.ime.getBottom(this).toDp() }
            val availableHeight = maxHeight - imeBottom
            val compact = availableHeight < 490.dp || density.fontScale >= 1.35f
            val sheetMax = (maxHeight - 16.dp).coerceAtLeast(120.dp)
            val cornerRadius = quickPanelRadius()
            val shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
            CompositionLocalProvider(LocalQuickAccent provides accent) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).statusBarsPadding()
                    .widthIn(max = 560.dp).fillMaxWidth().heightIn(max = sheetMax)
                    .onGloballyPositioned { coordinates ->
                        panelDrag.dismissDistance = coordinates.size.height.toFloat()
                        val point = coordinates.positionOnScreen()
                        destination = if (point.x.isFinite() && point.y.isFinite()) Rect(point,
                            Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat())) else null
                    }
                    .ps4PanelTransform(motion.progress.asState(), motion.effect, origin = { origin }, destination = { destination })
                    .graphicsLayer { if (animated) translationY = panelDrag.offset.value.coerceAtLeast(0f) }
                    .semantics { paneTitle = "Centro rápido" },
                shape = shape,
                color = panel,
                contentColor = ink,
            ) {
                Box {
                if (activeQuickTheme == LauncherTheme.PS4 || activeQuickTheme == LauncherTheme.XPERIA) {
                    SonyGlassBackdrop(audio?.artwork?.takeIf { showMusic }, mediaColor, reduced,
                        decorationConstrained ?: LocalDecorationConstrained.current)
                }
                val surfaceFinish = if (contrast) Modifier else Modifier
                    .background(Brush.verticalGradient(listOf(colors.surfaceVariant.copy(alpha = .35f), panel.copy(alpha = .88f), panel)))
                    .quickCenterCornerLight(accent, cornerRadius, intensity).quickSurfaceDetail()
                Column(Modifier.border(BorderStroke(1.dp, if (contrast) edge else quickEdge), shape)
                    .then(surfaceFinish)
                    .navigationBarsPadding().imePadding()
                    .padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        .pointerInput(panelDrag, hasMedia, animated) {
                            val tracker = VelocityTracker()
                            var trackedDistance = 0f
                            detectVerticalDragGestures(
                                onDragStart = { tracker.resetTracking(); trackedDistance = 0f; panelDrag.begin() },
                                onVerticalDrag = { change, delta ->
                                    change.consume()
                                    trackedDistance += delta
                                    tracker.addPosition(change.uptimeMillis, Offset(0f, trackedDistance))
                                    if (!motion.closing) panelDrag.dragBy(delta, hasMedia)
                                },
                                onDragCancel = { if (!motion.closing) panelDrag.cancel(animated, hasMedia) { value ->
                                    expanded = value
                                    if (value) { selectedRailCard = "Música"; revealedRailCard = "Música"; timersExpanded = false }
                                } },
                                onDragEnd = {
                                    if (!motion.closing) panelDrag.release(tracker.calculateVelocity().y, hasMedia, animated,
                                        onExpanded = { value ->
                                            expanded = value
                                            if (value) { selectedRailCard = "Música"; revealedRailCard = "Música"; timersExpanded = false }
                                            else if (revealedRailCard == "Música") revealedRailCard = null
                                        },
                                        onDismiss = { latestDismiss() })
                                },
                            )
                        }
                        .semantics {
                            contentDescription = "Asa del Centro rápido"
                            stateDescription = if (mediaExpanded) "Reproductor ampliado" else "Panel compacto"
                            customActions = buildList {
                                if (hasMedia) add(CustomAccessibilityAction(
                                    if (mediaExpanded) "Contraer reproductor" else "Ampliar reproductor",
                                ) { if (mediaExpanded) collapseRailCard("Música") else revealRailCard("Música"); true })
                                add(CustomAccessibilityAction("Cerrar Centro rápido") { close(); true })
                            }
                        }, contentAlignment = Alignment.Center) {
                        Spacer(Modifier.width(if (mediaExpanded) 58.dp else 44.dp).height(4.dp)
                            .background(if (mediaExpanded) accent else edge, CircleShape))
                    }
                    Row(Modifier.fillMaxWidth().padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ThemeSystemIcon("layers", activeQuickTheme, Modifier.size(26.dp))
                        Text("Centro rápido", Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                        IconButton(onClick = close, modifier = Modifier.semantics { contentDescription = "Cerrar Centro rápido" }) {
                            ThemeSystemIcon("close", activeQuickTheme)
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
                                    for (option in QuickCenterTarget.entries.filter { it != QuickCenterTarget.MEDIA }) {
                                        DropdownMenuItem(text = { Text(option.label) },
                                            trailingIcon = if (option == miniTarget) ({ Text("Activa") }) else null,
                                            onClick = {
                                                onMiniTargetChange(option)
                                                showNote = option == QuickCenterTarget.NOTE
                                                if (showNote) selectedRailCard = "Nota"
                                                if (showNote) scope.launch { cardsScroll.scrollTo(0) }
                                                selectingMini = false
                                            })
                                    }
                                }
                            }
                        }
                    }
                    val railItems = buildList {
                        for (card in cardOrder.sortedBy { if (it in pinned) 0 else 1 }) {
                            when (card) {
                                "Música" -> if (showMusic) add(QuickRailItemModel(card, "play", "Música",
                                    audio?.title ?: "Sin reproducción"))
                                "Temporizadores" -> add(QuickRailItemModel(card, "clock", "Temporizadores",
                                    timers.firstOrNull { it.running }?.let { "En marcha" } ?: "Gestionar"))
                                "Alarma" -> add(QuickRailItemModel(card, "alarm", "Alarma",
                                    if (alarmAt == null) "Sin alarma" else "Próxima"))
                                "Agenda" -> add(QuickRailItemModel(card, "calendar", "Agenda",
                                    event?.title ?: "Sin eventos"))
                            }
                        }
                        if (showNote || pinnedNote.isNotBlank()) add(QuickRailItemModel("Nota", "note", "Nota", "Fijada"))
                        add(QuickRailItemModel("Accesos", "layers", "Accesos", "Herramientas"))
                    }
                    val resolvedRailCard = selectedRailCard.takeIf { selected -> railItems.any { it.id == selected } }
                        ?: railItems.firstOrNull()?.id ?: "Accesos"
                    QuickCenterRail(railItems, resolvedRailCard, revealedRailCard,
                        onSelect = ::selectRailCard, onReveal = ::revealRailCard, onCollapse = ::collapseRailCard)
                    Column(Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(cardsScroll),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        when (resolvedRailCard) {
                            "Música" -> if (revealedRailCard == "Música" && (audio == null || mediaExpanded || expansion.value > .001f)) {
                                if (audio != null) QuickMediaCard(audio, animated, mediaExpanded, compact, mediaColor,
                                    onExpandedChange = ::expandMedia, onPlayback, onPrevious, onNext, onSeek, onOpenMediaSource,
                                    onOpenMediaDetails?.let { open ->
                                        { origin -> panelDrag.interrupt(); motion.close(reduced) { onDismiss(); open(origin) } }
                                    }, expansion = expansion, hideArtwork = overlayArtwork,
                                    onArtworkPositioned = ::positionArtwork)
                                else QuickActionCard("play", "Música", "Sin reproducción activa", "Abrir", onOpenPlayer)
                            }
                            "Temporizadores" -> if (timersExpanded) {
                                if (!preview && timerStore != null) QuickTimerCard(timers, timerStore, true, ::expandTimers)
                                else QuickActionCard("clock", "Temporizadores", "Ejemplo · 5 min", "Contraer") { expandTimers(false) }
                            }
                            "Alarma" -> if (revealedRailCard == "Alarma") {
                                if (alarmAt != null) {
                                    val at = Instant.ofEpochMilli(alarmAt).atZone(zone)
                                    val day = when (at.toLocalDate()) {
                                        today -> "Hoy"
                                        today.plusDays(1) -> "Mañana"
                                        else -> date.format(at)
                                    }
                                    QuickActionCard("alarm", "Próxima alarma", "${time.format(at)} · $day", "Editar", onAlarm)
                                } else QuickActionCard("alarm", "Próxima alarma", "Sin alarma informada", "Abrir Reloj", onAlarm)
                            }
                            "Agenda" -> if (revealedRailCard == "Agenda") {
                                if (event != null) {
                                    val at = Instant.ofEpochMilli(event.begin).atZone(if (event.allDay) ZoneOffset.UTC else zone)
                                    val whenText = if (event.allDay) date.format(at) else
                                        if (at.toLocalDate() == today) time.format(at) else "${date.format(at)} ${time.format(at)}"
                                    QuickActionCard("calendar", "Próximo evento", "$whenText · ${event.title}", "Ver") { onEvent(event) }
                                } else QuickActionCard("calendar", "Agenda", "Sin evento disponible", "Abrir", onCalendar)
                            }
                            "Nota" -> if (revealedRailCard == "Nota") QuickPinnedNoteCard(pinnedNote, onSavePinnedNote)
                            "Accesos" -> if (revealedRailCard == "Accesos") {
                                QuickCenterActions(onSearch = {}, onSettings = { action ->
                                    panelDrag.interrupt()
                                    motion.close(reduced) { onDismiss(); action() }
                                }, compact = true, toolsOnly = true)
                                if (!preview) configuration()
                            }
                        }
                    }
                    HorizontalDivider(color = edge.copy(alpha = .65f))
                    if (!preview && !compact && revealedRailCard != "Accesos") configuration()
                }
            }
            }
        }
        if (overlayArtwork) QuickArtworkOverlay(audio, artworkOrigin, coverBounds,
            dialogOrigin, artworkProgress, visibleTarget = visibleCoverBounds)
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
    val shape = quickPanelShape()
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
                else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center) { ThemeSystemIcon(icon, activeQuickTheme, Modifier.size(26.dp)) }
            Column(Modifier.weight(1f)) {
                Text(label, color = muted, style = MaterialTheme.typography.bodyMedium)
                Text(value, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            OutlinedButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 12.dp),
                border = BorderStroke(1.dp, quickAccent),
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
    expansion: State<Float>, hideArtwork: Boolean, onArtworkPositioned: (LayoutCoordinates) -> Unit,
) {
    val contrast = LocalAppearance.current.highContrast
    val ink = if (contrast) MaterialTheme.colorScheme.onSurface else quickInk
    val muted = if (contrast) MaterialTheme.colorScheme.onSurfaceVariant else quickMuted
    val track = MaterialTheme.colorScheme.surfaceVariant
    val cover = audio.artwork?.takeUnless { it.isRecycled }
    val fullyRevealed by remember(expansion) { derivedStateOf { expansion.value >= .95f } }
    val hasRevealedControls by remember(expansion) { derivedStateOf { expansion.value > .001f } }
    var coverCoordinates by remember { mutableStateOf<androidx.compose.ui.layout.LayoutCoordinates?>(null) }
    var scrub by remember(audio.sessionId, audio.mediaId, audio.title, audio.durationMs) { mutableStateOf<Float?>(null) }
    var pendingSeek by remember(audio.sessionId, audio.mediaId, audio.title, audio.durationMs) { mutableStateOf<Pair<Long, Long>?>(null) }
    var seekStatus by remember(audio.sessionId, audio.mediaId, audio.title, audio.durationMs) { mutableStateOf<String?>(null) }
    var swipeOffset by remember(audio.sessionId, audio.mediaId) { mutableFloatStateOf(0f) }
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

    // The cover is deliberately reactive to the real playback position rather than an infinite
    // decorative clock. It stays still when playback is paused or the panel is not visible.
    val playbackFraction by remember(position, duration) {
        derivedStateOf {
            val current = position.value ?: return@derivedStateOf 0f
            val total = duration ?: return@derivedStateOf 0f
            (current.toDouble() / total).toFloat().coerceIn(0f, 1f)
        }
    }
    val hubGesture = Modifier.pointerInput(expanded, audio.sessionId) {
        detectVerticalDragGestures(
            onVerticalDrag = { change, amount ->
                change.consume()
                swipeOffset = (swipeOffset + amount).coerceIn(-96f, 96f)
            },
            onDragCancel = { swipeOffset = 0f },
            onDragEnd = {
                when {
                    swipeOffset < -32f -> onExpandedChange(true)
                    swipeOffset > 44f -> onExpandedChange(false)
                }
                swipeOffset = 0f
            },
        )
    }
    QuickCard(if (contrast) Modifier else Modifier.sonyGlassRim(mediaColor)) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            BoxWithConstraints(Modifier.fillMaxWidth().then(hubGesture)) {
                // A wide compact frame reads as one controllable object. On expansion it becomes
                // a cinematic 16:9 surface for video, and a richer album frame for audio.
                val compactRatio = if (audio.isYouTubeVideo) 3.45f else 3.9f
                val expandedRatio = if (audio.isYouTubeVideo) 16f / 9f else 1.45f
                val ratio = compactRatio + (expandedRatio - compactRatio) * expansion.value
                val height = (maxWidth / ratio).coerceAtLeast(76.dp)
                Box(Modifier.fillMaxWidth().height(height)
                    .graphicsLayer {
                        translationY = swipeOffset * .16f
                        val pull = kotlin.math.abs(swipeOffset) / 96f
                        scaleX = 1f - pull * .012f
                        scaleY = 1f - pull * .012f
                    }
                    .onGloballyPositioned { coordinates ->
                        coverCoordinates = coordinates
                        onArtworkPositioned(coordinates)
                    }
                    .clip(RoundedCornerShape(if (expanded) 22.dp else 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .drawWithCache {
                        val lineX = size.width * playbackFraction
                        val halo = mediaColor.value.copy(alpha = if (audio.isPlaying) .20f else .08f)
                        onDrawWithContent {
                            drawContent()
                            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0xBD020814))))
                            if (audio.isPlaying) {
                                drawCircle(halo, radius = size.minDimension * .34f,
                                    center = Offset(lineX, size.height * .42f))
                                drawLine(mediaColor.value.copy(alpha = .58f), Offset(lineX, 0f),
                                    Offset(lineX, size.height), strokeWidth = 2.dp.toPx())
                            }
                        }
                    }
                    .semantics {
                        contentDescription = "${audio.playbackLabel}: ${audio.title ?: audio.appLabel ?: "Multimedia"}. " +
                            if (expanded) "Desliza abajo para contraer" else "Desliza arriba para mostrar controles"
                    }) {
                    if (cover != null && !hideArtwork) Image(cover.asImageBitmap(), null, Modifier.fillMaxSize(),
                        contentScale = if (audio.isYouTubeVideo) ContentScale.Fit else ContentScale.Crop)
                    else MediaCoverFallback(audio, activeQuickTheme, Modifier.fillMaxSize())
                    Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Text("${audio.appLabel ?: if (audio.isYouTubeVideo) "YouTube" else "Multimedia"} · ${audio.playbackLabel}",
                                color = Color.White, style = MaterialTheme.typography.labelMedium,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(if (expanded) "Desliza ↓" else "Desliza ↑", color = Color.White.copy(alpha = .82f),
                                style = MaterialTheme.typography.labelSmall)
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text(audio.title ?: audio.appLabel ?: "Multimedia", color = Color.White,
                                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium,
                                    maxLines = if (expanded) 2 else 1, overflow = TextOverflow.Ellipsis)
                                audio.artist?.takeIf(String::isNotBlank)?.let {
                                    Text(it, color = Color.White.copy(alpha = .80f), style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            QuickMediaButton(if (audio.isPlaying) "pause" else "play",
                                if (audio.isPlaying) "Pausar" else "Reanudar",
                                if (audio.isPlaying) audio.canPause else audio.canPlay, onPlayback)
                        }
                    }
                    if (duration != null && position.value != null) Box(
                        Modifier.align(Alignment.BottomCenter).padding(horizontal = 14.dp, vertical = 8.dp)
                    ) { QuickMediaProgress(position, duration, mediaColor) }
                }
            }
        if (hasRevealedControls) Box(Modifier.clipToBounds().layout { measurable, constraints ->
            val content = measurable.measure(constraints.copy(minHeight = 0))
            val progress = expansion.value.coerceIn(0f, 1f)
            layout(content.width, (content.height * progress).roundToInt()) { content.placeRelative(0, 0) }
        }.graphicsLayer { alpha = expansion.value }
            .then(if (!expanded || !fullyRevealed) Modifier.clearAndSetSemantics {} else Modifier)) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val current = position.value
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically) {
                    QuickMediaButton("previous", "Pista anterior", audio.canPrevious, onPrevious)
                    QuickMediaButton(if (audio.isPlaying) "pause" else "play",
                        if (audio.isPlaying) "Pausar" else "Reanudar",
                        if (audio.isPlaying) audio.canPause else audio.canPlay, onPlayback)
                    QuickMediaButton("next", "Siguiente pista", audio.canNext, onNext)
                }
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
                    if (audio.canSeek) Slider(value = fraction, enabled = expanded && fullyRevealed, onValueChange = { scrub = it },
                        onValueChangeFinished = {
                            if (expanded && fullyRevealed) scrub?.let {
                                seek((it.toDouble() * duration).toLong())
                            }
                            scrub = null
                        }, colors = if (contrast) SliderDefaults.colors() else SliderDefaults.colors(
                            thumbColor = quickMediaAccent(mediaColor.value, quickAccent, track),
                            activeTrackColor = quickMediaAccent(mediaColor.value, quickAccent, track),
                        ), modifier = Modifier.semantics { contentDescription = "Posición de reproducción" })
                    if (audio.canSeek) key(audio.sessionId, audio.title, duration) {
                        MediaSeekCue(requested, actual, confirmed = seekStatus == "Posición actualizada", animated = animated)
                    }
                    Text("${quickMediaTime(pendingSeek?.first?.takeIf { scrub == null } ?: (fraction.toDouble() * duration).toLong())} / ${quickMediaTime(duration)}",
                        color = muted, style = MaterialTheme.typography.labelMedium)
                    val status = if (pendingSeek != null) "Esperando confirmación…" else seekStatus
                    if (status != null) Text(status, color = muted, style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                    if (audio.canSeek) {
                        val baseline = pendingSeek?.first ?: current
                        val forwardMs = if (audio.isYouTubeVideo) 10_000L else 30_000L
                        val forwardLabel = if (audio.isYouTubeVideo) "Adelantar vídeo 10 segundos" else "Adelantar 30 segundos"
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { seek(baseline - 10_000L) }, enabled = expanded && fullyRevealed && baseline > 0,
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                                    .semantics { contentDescription = "Retroceder 10 segundos" }) { Text("−10 s") }
                            OutlinedButton(onClick = { seek(baseline + forwardMs) }, enabled = expanded && fullyRevealed && baseline < duration,
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                                    .semantics { contentDescription = forwardLabel }) { Text(if (audio.isYouTubeVideo) "+10 s" else "+30 s") }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    if (audio.sessionId != null) TextButton(onClick = onOpenSource, enabled = expanded && fullyRevealed, modifier = Modifier.weight(1f)) {
                        Text(if (audio.isYouTubeVideo) "Abrir vídeo" else "Abrir app", color = quickAccent)
                    }
                    if (onOpenDetails != null) TextButton(enabled = expanded && fullyRevealed, onClick = {
                        val origin = coverCoordinates?.takeIf { it.isAttached }?.let { coordinates ->
                            val p = coordinates.positionOnScreen()
                            if (p.x.isFinite() && p.y.isFinite()) Rect(p.x, p.y,
                                p.x + coordinates.size.width, p.y + coordinates.size.height) else null
                        }
                        onOpenDetails(origin)
                    }) { Text("Ficha", color = quickAccent) }
                    TextButton(onClick = { onExpandedChange(false) }, enabled = expanded && fullyRevealed) { Text("Minimizar", color = quickAccent) }
                }
            }
        }
        }
    }
}

@Composable
private fun QuickMediaProgress(position: State<Long?>, duration: Long, mediaColor: State<Color>) {
    val accent = quickAccent
    val track = MaterialTheme.colorScheme.surfaceVariant
    Box(Modifier.fillMaxWidth().padding(top = 8.dp).height(4.dp)
        .semantics {
            val fraction = ((position.value ?: 0L).toDouble() / duration).toFloat().coerceIn(0f, 1f)
            progressBarRangeInfo = ProgressBarRangeInfo(fraction, 0f..1f)
        }
        .drawBehind {
            val current = position.value ?: return@drawBehind
            val fraction = (current.toDouble() / duration).toFloat().coerceIn(0f, 1f)
            val radius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f)
            drawRoundRect(track, cornerRadius = radius)
            if (fraction > 0f) {
                val width = size.width * fraction
                val x = if (layoutDirection == androidx.compose.ui.unit.LayoutDirection.Rtl) size.width - width else 0f
                val progressInk = quickMediaAccent(mediaColor.value, accent, track)
                drawRoundRect(progressInk, topLeft = androidx.compose.ui.geometry.Offset(x, 0f),
                    size = Size(width, size.height), cornerRadius = radius)
            }
        })
}

private fun quickMediaAccent(color: Color, fallback: Color, trackColor: Color): Color {
    val track = trackColor.luminance()
    fun contrast(ink: Color) = (maxOf(ink.luminance(), track) + .05f) /
        (minOf(ink.luminance(), track) + .05f)
    return when {
        contrast(color) >= 3f -> color
        contrast(fallback) >= 3f -> fallback
        else -> if (contrast(Color.Black) >= contrast(Color.White)) Color.Black else Color.White
    }
}

private fun quickMediaTime(ms: Long) = "${ms / 60_000}:${(ms / 1_000 % 60).toString().padStart(2, '0')}"

@Composable
private fun QuickMediaButton(icon: String, label: String, enabled: Boolean, onClick: () -> Unit) {
    val contrast = LocalAppearance.current.highContrast
    val ink = if (contrast) MaterialTheme.colorScheme.onSurface else quickInk
    val edge = if (contrast) MaterialTheme.colorScheme.outline else lerp(quickEdge, quickAccent, .46f)
    val fill = if (contrast) listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
        else listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)
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
