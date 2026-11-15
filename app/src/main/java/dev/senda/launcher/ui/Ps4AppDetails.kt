package dev.senda.launcher.ui

import android.content.ActivityNotFoundException
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Drawable
import android.os.Process
import android.util.LruCache
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.launch
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Surface
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import dev.senda.launcher.data.LocalAppearance
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LaunchableApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private data class Ps4CategoryKey(val packageName: String, val revision: Long, val locales: String)
private val ps4Categories = LruCache<Ps4CategoryKey, String>(96)

/** Only the settled focused row requests metadata; scrolling cancels the debounce. */
@Composable
internal fun rememberPs4AppCategory(app: LaunchableApp, active: Boolean, scrolling: Boolean): String {
    val context = LocalContext.current
    val locales = LocalConfiguration.current.locales.toLanguageTags()
    val cacheKey = remember(app.packageName, app.revision, locales) {
        Ps4CategoryKey(app.packageName, app.revision, locales)
    }
    var category by remember(cacheKey) { mutableStateOf(ps4Categories.get(cacheKey) ?: "Aplicación") }
    LaunchedEffect(cacheKey, active, scrolling) {
        if (!active || scrolling) return@LaunchedEffect
        ps4Categories.get(cacheKey)?.let { category = it; return@LaunchedEffect }
        delay(180)
        val loaded = withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                val info = context.packageManager.getApplicationInfo(app.packageName, 0)
                ApplicationInfo.getCategoryTitle(context, info.category)?.toString()
                    ?.takeIf { it.isNotBlank() } ?: "Aplicación"
            } catch (_: PackageManager.NameNotFoundException) {
                "Aplicación"
            } catch (_: SecurityException) {
                "Aplicación"
            }
        }
        ps4Categories.put(cacheKey, loaded)
        category = loaded
    }
    return category
}

private data class Ps4ShortcutState(
    val shortcuts: List<ShortcutInfo> = emptyList(),
    val message: String = "Cargando accesos directos…",
)

private fun ps4ReadableLabel(value: CharSequence?, fallback: String): String = value
    ?.toString()?.filterNot { it.isISOControl() }?.trim()?.take(160)
    ?.takeIf { it.isNotBlank() } ?: fallback

@Composable
internal fun Ps4AppDetails(
    app: LaunchableApp,
    customIconUri: String?,
    onLoadIcon: (LaunchableApp) -> Drawable,
    onLaunch: () -> Unit,
    onDismiss: () -> Unit,
    origin: Rect? = null,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onChooseIcon: () -> Unit,
    onMoreOptions: () -> Unit,
) {
    val context = LocalContext.current
    val locales = LocalConfiguration.current.locales.toLanguageTags()
    val launcherApps = remember(context) { context.getSystemService(LauncherApps::class.java) }
    val category = rememberPs4AppCategory(app, active = true, scrolling = false)
    var state by remember(app.packageName, app.revision, locales) { mutableStateOf(Ps4ShortcutState()) }
    var launchError by remember(app.packageName, app.revision) { mutableStateOf<String?>(null) }
    LaunchedEffect(app.packageName, app.revision, locales, launcherApps) {
        state = withContext(Dispatchers.IO) {
            try {
                if (launcherApps == null || !launcherApps.hasShortcutHostPermission()) {
                    Ps4ShortcutState(message = "Android no ha concedido a Aura acceso a los accesos directos. Solo se mostrarán cuando el sistema permita consultarlos.")
                } else {
                    val query = LauncherApps.ShortcutQuery()
                        .setPackage(app.packageName)
                        .setQueryFlags(
                            LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or
                                LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or
                                LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED,
                        )
                    val shortcuts = launcherApps.getShortcuts(query, Process.myUserHandle()).orEmpty()
                        .asSequence()
                        .filter { it.isEnabled && it.`package` == app.packageName && it.userHandle == Process.myUserHandle() && it.id.isNotBlank() }
                        .distinctBy { it.id }
                        .sortedBy { it.rank }
                        .take(5)
                        .toList()
                    Ps4ShortcutState(shortcuts, if (shortcuts.isEmpty()) "Esta aplicación no ofrece accesos directos disponibles." else "")
                }
            } catch (_: SecurityException) {
                Ps4ShortcutState(message = "Android no permite consultar los accesos directos en este momento.")
            } catch (_: IllegalStateException) {
                Ps4ShortcutState(message = "Los accesos directos no están disponibles en este momento.")
            }
        }
    }
    val settings = LocalMotionSettings.current
    val reduced = settings.reduceMotion || settings.menuEffect == "NONE" || LocalAppearance.current.highContrast ||
        LocalThemePreview.current || LocalSystemPowerSaving.current || !android.animation.ValueAnimator.areAnimatorsEnabled()
    val motion = rememberPs4PanelMotion(reduced)
    val close = { motion.close(reduced, onDismiss) }
    var destination by remember { mutableStateOf<Rect?>(null) }
    val accent = MaterialTheme.colorScheme.primary
    val scope = rememberCoroutineScope()
    val drag = remember { Animatable(0f) }
    var dragFraction by remember { mutableFloatStateOf(0f) }
    var followsOrigin by remember { mutableStateOf(false) }
    val latestClose by rememberUpdatedState(close)
    val displayedProgress = remember(motion) {
        derivedStateOf { motion.progress.value * (1f - dragFraction) }
    }
    val transition = Modifier.ps4PanelTransform(displayedProgress,
        if (followsOrigin && !reduced && origin != null) "SPRING" else motion.effect,
        origin = { origin }, destination = { destination })
    val dragToClose = Modifier.pointerInput(reduced, motion) {
        var distance = 0f
        var accepted = false
        val threshold = 78.dp.toPx()
        fun restore() {
            scope.launch {
                if (reduced) dragFraction = 0f else {
                    drag.snapTo(dragFraction)
                    drag.animateTo(0f, tween(210, easing = Ps4Motion.easing)) { dragFraction = value }
                }
                followsOrigin = false
            }
        }
        detectVerticalDragGestures(
            onDragStart = {
                accepted = !motion.closing && motion.progress.value > .97f && !followsOrigin
                distance = 0f
            },
            onVerticalDrag = { change, amount ->
                if (accepted) {
                    distance = (distance + amount).coerceAtLeast(0f)
                    if (distance > 0f || followsOrigin) {
                        change.consume()
                        if (!reduced) {
                            followsOrigin = true
                            dragFraction = (distance / (threshold * 2.8f)).coerceIn(0f, .72f)
                        }
                    }
                }
            },
            onDragCancel = { if (accepted) restore() },
            onDragEnd = {
                if (accepted && distance >= threshold) latestClose()
                else if (accepted) restore()
            },
        )
    }
    val heading: @Composable () -> Unit = {
            val contrast = LocalAppearance.current.highContrast
            val coverShape = RoundedCornerShape(12.dp)
            val coverBase = MaterialTheme.colorScheme.surfaceVariant
            val coverBrush = remember(coverBase, accent, contrast) {
                Brush.horizontalGradient(listOf(if (contrast) coverBase else accent.copy(alpha = .24f), coverBase))
            }
            Row(Modifier.fillMaxWidth().defaultMinSize(minHeight = 106.dp).clip(coverShape)
                    .background(coverBrush).border(1.dp, accent.copy(alpha = .6f), coverShape)
                    .then(if (reduced) Modifier else Modifier.ps4HeaderSweep(motion.progress.asState(), accent, .65f))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(64.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = .12f), RoundedCornerShape(2.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .6f), RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center) {
                    AppIcon(app, customIconUri, onLoadIcon)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Ficha de aplicación", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(ps4ReadableLabel(app.label, "Aplicación"), style = MaterialTheme.typography.titleLarge,
                        maxLines = 3, overflow = TextOverflow.Ellipsis)
                    Text(category, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
    }
    val content: @Composable (Modifier) -> Unit = { modifier ->
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .4f))
                Text("Accesos directos", style = MaterialTheme.typography.titleSmall)
                if (state.message.isNotEmpty()) Text(state.message, style = MaterialTheme.typography.bodyMedium)
                state.shortcuts.forEach { shortcut ->
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .2f))
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                        onClick = {
                            if (motion.closing) return@OutlinedButton
                            launchError = null
                            try {
                                if (launcherApps == null || !launcherApps.hasShortcutHostPermission()) {
                                    launchError = "Android no permite abrir este acceso directo."
                                } else {
                                    launcherApps.startShortcut(shortcut, null, null)
                                    onDismiss()
                                }
                            } catch (_: SecurityException) {
                                launchError = "Android no permite abrir este acceso directo."
                            } catch (_: ActivityNotFoundException) {
                                launchError = "Este acceso directo ya no está disponible."
                            } catch (_: IllegalStateException) {
                                launchError = "No se puede abrir este acceso directo en este momento."
                            }
                        },
                    ) {
                        Text(ps4ReadableLabel(shortcut.shortLabel, "Abrir acceso directo"), maxLines = 3, overflow = TextOverflow.Ellipsis)
                    }
                }
                launchError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                }
            }
    }
    Dialog(
        onDismissRequest = close,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false, dismissOnClickOutside = false),
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(Modifier.matchParentSize().clickable(onClickLabel = "Cerrar ficha", onClick = close))
            Box(Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp), contentAlignment = Alignment.Center) {
                // This wrapper stays at the final position while the card moves inside the full-screen window.
                Box(Modifier.widthIn(max = 460.dp).fillMaxWidth().onGloballyPositioned { coordinates ->
                    val position = coordinates.positionOnScreen()
                    if (position.x.isFinite() && position.y.isFinite()) {
                        destination = Rect(position, Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat()))
                    }
                }) {
                    Surface(
                        Modifier.fillMaxWidth().then(transition)
                            .pointerInput(Unit) { detectTapGestures(onTap = {}) }
                            .semantics { paneTitle = "Ficha de ${app.label}" },
                        shape = RoundedCornerShape(2.dp),
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Column(Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.fillMaxWidth().heightIn(min = 48.dp).then(dragToClose)
                                .semantics { contentDescription = "Asa para deslizar y cerrar la ficha" },
                                contentAlignment = Alignment.Center) {
                                Box(Modifier.width(44.dp).height(4.dp)
                                    .background(MaterialTheme.colorScheme.outline, CircleShape))
                            }
                            Column(Modifier.weight(1f, fill = false).fillMaxWidth()
                                .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                heading()
                                Button(onClick = { if (!motion.closing) onLaunch() }, shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Abrir aplicación") }
                                content(Modifier)
                                Column(Modifier.fillMaxWidth()) {
                                    TextButton(onClick = onToggleFavorite, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                                        Text(if (isFavorite) "Quitar de favoritas" else "Añadir a favoritas")
                                    }
                                    TextButton(onClick = { motion.close(reduced, onChooseIcon) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                                        Text("Cambiar icono")
                                    }
                                    TextButton(onClick = { motion.close(reduced, onMoreOptions) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                                        Text("Más opciones")
                                    }
                                }
                            }
                            Column(Modifier.align(Alignment.End), horizontalAlignment = Alignment.End) {
                                TextButton(onClick = close, modifier = Modifier.heightIn(min = 48.dp)) { Text("Cerrar") }
                            }
                        }
                    }
                }
            }
        }
    }
}
