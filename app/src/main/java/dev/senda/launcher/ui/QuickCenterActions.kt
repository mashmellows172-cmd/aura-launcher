package dev.senda.launcher.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import dev.senda.launcher.data.moveListItem
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dev.senda.launcher.NotificationShadeService
import dev.senda.launcher.data.LauncherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.semantics.contentDescription

internal fun quickOrder(saved: String?, defaults: List<String>): List<String> =
    (saved.orEmpty().split('|').filter { it in defaults } + defaults).distinct()

@Composable
internal fun QuickOrderEditor(order: List<String>, onMove: (List<String>) -> Unit,
    extra: @Composable (String) -> Unit,
) {
    val list = rememberLazyListState()
    val latestOrder by rememberUpdatedState(order)
    var preview by remember { mutableStateOf<List<String>?>(null) }
    var undo by remember { mutableStateOf<List<String>?>(null) }
    var dragged by remember { mutableStateOf<String?>(null) }
    var center by remember { mutableFloatStateOf(0f) }
    val commit by rememberUpdatedState(onMove)
    val current = preview ?: order
    val animated = !LocalMotionSettings.current.reduceMotion && LocalMotionSettings.current.menuEffect != "NONE" &&
        !LocalSystemPowerSaving.current &&
        !dev.senda.launcher.data.LocalAppearance.current.highContrast && android.animation.ValueAnimator.areAnimatorsEnabled()
    val height = 56.dp
    val accent = MaterialTheme.colorScheme.primary
    Column(Modifier.width(294.dp)) {
        LazyColumn(
            state = list,
            modifier = Modifier.fillMaxWidth().height(height * order.size).pointerInput(list) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { point ->
                        list.layoutInfo.visibleItemsInfo.firstOrNull { point.y >= it.offset && point.y < it.offset + it.size }?.let {
                            preview = latestOrder
                            dragged = it.key as? String
                            center = it.offset + it.size / 2f
                        }
                    },
                    onDrag = { change, amount ->
                        val draft = preview
                        val key = dragged
                        if (draft != null && key != null) {
                            change.consume()
                            center = (center + amount.y).coerceIn(0f, size.height.toFloat() - 1f)
                            val target = list.layoutInfo.visibleItemsInfo.firstOrNull {
                                center >= it.offset && center < it.offset + it.size
                            }
                            val from = draft.indexOf(key)
                            if (from >= 0 && target != null && target.index != from) {
                                preview = moveListItem(draft, from, target.index)
                            }
                        }
                    },
                    onDragEnd = {
                        preview?.takeIf { it != latestOrder }?.let {
                            undo = latestOrder
                            commit(it)
                        }
                        dragged = null
                        preview = null
                    },
                    onDragCancel = { dragged = null; preview = null },
                )
            },
        ) {
            itemsIndexed(current, key = { _, label -> label }) { index, label ->
                val active = dragged == label
                Box(Modifier.height(height).fillMaxWidth().zIndex(if (active) 1f else 0f)
                    .animateItem(fadeInSpec = null,
                        placementSpec = if (animated && !active) tween(175, easing = Ps4Motion.easing) else null,
                        fadeOutSpec = null)) {
                    if (active) Box(Modifier.matchParentSize().background(accent.copy(alpha = .16f))
                        .border(1.dp, accent.copy(alpha = .7f)))
                    Row(Modifier.fillMaxSize().graphicsLayer {
                        if (active) translationY = list.layoutInfo.visibleItemsInfo.firstOrNull { it.key == label }
                            ?.let { center - it.offset - it.size / 2f } ?: 0f
                    }.then(if (active) Modifier.background(MaterialTheme.colorScheme.primaryContainer) else Modifier)
                        .padding(horizontal = 8.dp)
                        .semantics { stateDescription = "Posición ${index + 1} de ${current.size}" },
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(label, Modifier.weight(1f), maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        extra(label)
                        for (direction in listOf(-1, 1)) {
                            val target = index + direction
                            IconButton(enabled = dragged == null && target in current.indices, onClick = {
                                undo = order
                                onMove(moveListItem(current, index, target))
                            }, modifier = Modifier.semantics {
                                contentDescription = if (direction < 0) "Subir $label" else "Bajar $label"
                            }) { Text(if (direction < 0) "↑" else "↓") }
                        }
                    }
                }
            }
        }
        if (undo != null) TextButton(onClick = {
            undo?.let(onMove)
            undo = null
        }, enabled = dragged == null, modifier = Modifier.heightIn(min = 48.dp)) { Text("Deshacer movimiento") }
    }
}

@Composable
internal fun QuickCenterActions(onSearch: () -> Unit, onSettings: (() -> Unit) -> Unit, compact: Boolean = false, toolsOnly: Boolean = false) {
    val context = LocalContext.current
    val manager = remember(context) { context.getSystemService(CameraManager::class.java) }
    val scope = rememberCoroutineScope()
    val cameraId by produceState<String?>(null, manager) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                manager?.cameraIdList?.firstOrNull { id ->
                    val camera = manager.getCameraCharacteristics(id)
                    camera.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true &&
                        camera.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
                }
            }.getOrNull()
        }
    }
    var torch by remember(cameraId) { mutableStateOf<Boolean?>(null) }
    var torchUnavailable by remember(cameraId) { mutableStateOf(false) }
    var permitted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var configuring by rememberSaveable { mutableStateOf(false) }
    val preferences = remember(context) { context.getSharedPreferences("quick_center_shortcuts", 0) }
    val defaults = if (toolsOnly) listOf("Linterna", "Calculadora", "Gemini") else listOf("Linterna", "Buscar apps", "Ajustes rápidos")
    var order by remember(preferences, toolsOnly) { mutableStateOf(quickOrder(preferences.getString("shortcut_order", null), defaults)) }
    var showTorch by remember { mutableStateOf(preferences.getBoolean("torch", true)) }
    var showSearch by remember { mutableStateOf(preferences.getBoolean("search", true)) }
    var showSettings by remember { mutableStateOf(preferences.getBoolean("settings", true)) }
    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    fun toggleTorch(enabled: Boolean) {
        val id = cameraId ?: return
        val service = manager ?: return
        scope.launch {
            val changed = withContext(Dispatchers.IO) { runCatching { service.setTorchMode(id, enabled) }.isSuccess }
            if (!changed) toast("La linterna no está disponible en este momento")
        }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permitted = granted
        if (granted) toggleTorch(torch != true) else toast("Concede acceso a la cámara para usar el flash")
    }
    DisposableEffect(manager, cameraId, permitted, showTorch, toolsOnly) {
        val callback = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(id: String, enabled: Boolean) {
                if (id == cameraId) { torch = enabled; torchUnavailable = false }
            }
            override fun onTorchModeUnavailable(id: String) {
                if (id == cameraId) { torch = null; torchUnavailable = true }
            }
        }
        val registered = manager != null && cameraId != null && (showTorch || toolsOnly) &&
            runCatching { manager.registerTorchCallback(context.mainExecutor, callback) }.isSuccess
        onDispose { if (registered) manager?.unregisterTorchCallback(callback) }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (label in order) key(label) {
        when (label) {
        "Linterna" -> if (showTorch || toolsOnly) QuickShortcut("torch", label, Modifier.weight(1f), compact = compact, enabled = cameraId != null && (!permitted || torch != null),
            active = torch, detail = if (permitted && torchUnavailable) "Flash ocupado o bloqueado" else null) {
            if (permitted) toggleTorch(torch != true) else permission.launch(Manifest.permission.CAMERA)
        }
        "Calculadora" -> QuickShortcut("calculator", label, Modifier.weight(1f), compact = compact) {
            onSettings {
                runCatching { context.startActivity(Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALCULATOR)) }
                    .onFailure { toast("No hay una calculadora disponible") }
            }
        }
        "Gemini" -> QuickShortcut("assistant", label, Modifier.weight(1f), compact = compact) {
            val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.apps.bard")
            if (intent == null) toast("Gemini no está instalado o no tiene acceso de inicio")
            else onSettings { runCatching { context.startActivity(intent) }.onFailure { toast("No se pudo abrir Gemini") } }
        }
        "Buscar apps" -> if (showSearch) QuickShortcut("search", label, Modifier.weight(1f), compact = compact, onClick = onSearch)
        else -> if (showSettings) QuickShortcut("settings", label, Modifier.weight(1f), compact = compact) {
            onSettings {
                if (!NotificationShadeService.openQuickSettings()) {
                    runCatching { context.startActivity(Intent(Settings.ACTION_SETTINGS)) }
                        .onFailure { toast("No se pudieron abrir los ajustes") }
                }
            }
        }
        }
        }
    }
    if (!toolsOnly) Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TextButton(onClick = { configuring = true }) { Text(if (compact) "Editar accesos" else "Personalizar accesos") }
        DropdownMenu(expanded = configuring, onDismissRequest = { configuring = false }) {
            QuickOrderEditor(order, onMove = {
                order = it
                preferences.edit().putString("shortcut_order", it.joinToString("|")).apply()
            }) { }
            HorizontalDivider()
            QuickShortcutToggle("Linterna", showTorch) {
                showTorch = it
                preferences.edit().putBoolean("torch", it).apply()
            }
            QuickShortcutToggle("Buscar apps", showSearch) {
                showSearch = it
                preferences.edit().putBoolean("search", it).apply()
            }
            QuickShortcutToggle("Ajustes rápidos", showSettings) {
                showSettings = it
                preferences.edit().putBoolean("settings", it).apply()
            }
        }
    }
}

@Composable
private fun QuickShortcutToggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.widthIn(min = 220.dp).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f))
        Switch(checked, onChange)
    }
}

@Composable
private fun QuickShortcut(icon: String, label: String, modifier: Modifier, enabled: Boolean = true,
    compact: Boolean = false, active: Boolean? = null, detail: String? = null, onClick: () -> Unit,
) {
    val shape = quickPanelShape()
    val interactions = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    TextButton(onClick = onClick, interactionSource = interactions, enabled = enabled, shape = shape, contentPadding = PaddingValues(6.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        modifier = modifier.heightIn(min = if (compact) 48.dp else 64.dp).ps4ControlGlass(shape, translucent = active != true).quickToolMotion(interactions, active)
            .semantics {
                if (detail != null) stateDescription = detail
                else if (active != null) stateDescription = if (active) "Encendida" else "Apagada"
            }) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ThemeSystemIcon(icon, activeQuickTheme)
            Text(label, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
            if (detail != null) Text(detail, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
        }
    }
}
