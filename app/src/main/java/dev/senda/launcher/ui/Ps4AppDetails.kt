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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    AuraAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(ps4ReadableLabel(app.label, "Aplicación"), maxLines = 3, overflow = TextOverflow.Ellipsis) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AppIcon(app, customIconUri, onLoadIcon)
                Text(category, style = MaterialTheme.typography.bodyMedium)
                Text("Accesos directos", style = MaterialTheme.typography.titleSmall)
                if (state.message.isNotEmpty()) Text(state.message, style = MaterialTheme.typography.bodyMedium)
                state.shortcuts.forEach { shortcut ->
                    TextButton(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        onClick = {
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
        },
        confirmButton = {
            TextButton(onClick = onLaunch, modifier = Modifier.heightIn(min = 48.dp)) { Text("Abrir aplicación") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) { Text("Cerrar") }
        },
    )
}
