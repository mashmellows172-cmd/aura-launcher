package dev.senda.launcher.ui

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LaunchableApp
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.ui.theme.icons.s3AppArtwork

/**
 * Manual SIII review dialog. Displays the existing s3-review.json catalog off the main thread
 * via [S3ReviewCatalog]. No ViewModel dependency.
 *
 * @param apps all launchable apps (used to match native icons for pending entries)
 * @param onDismiss closes the dialog
 * @param onLoadIcon optional native icon loader; only used for PENDING entries when a matching
 *                   app is found. Otherwise a "Pending" placeholder is shown, never claiming
 *                   completion.
 */
@Composable
fun S3ReviewDialog(
    apps: List<LaunchableApp> = emptyList(),
    iconSize: Int? = null,
    onDismiss: () -> Unit,
    onLoadIcon: ((LaunchableApp) -> Drawable)? = null,
) {
    val context = LocalContext.current
    val appearance = LocalAppearance.current
    val size = (iconSize ?: appearance.iconSize).coerceIn(32, 60).dp
    val appByPackage = apps.associateBy { it.packageName }

    var rows by remember { mutableStateOf<List<dev.senda.launcher.data.S3ReviewEntry>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var showPendingOnly by remember { mutableStateOf(false) }
    var detail by remember { mutableStateOf<dev.senda.launcher.data.S3ReviewEntry?>(null) }

    LaunchedEffect(Unit) {
        try {
            rows = dev.senda.launcher.data.S3ReviewCatalog.load(context)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            error = "No se pudo cargar el catálogo"
        }
    }

    val shown = remember(rows, showPendingOnly) {
        rows.filter { if (showPendingOnly) it.status == "PENDING" else true }
    }

    val availableCount = rows.count { it.status != "PENDING" }
    val pendingCount = rows.count { it.status == "PENDING" }

    AuraAlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
        title = { Text("Galaxy SIII · revisión manual") },
        text = {
            Column {
                Text(
                    "$availableCount disponibles · $pendingCount pendientes",
                    style = MaterialTheme.typography.labelMedium,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = showPendingOnly,
                        onCheckedChange = { showPendingOnly = it },
                        modifier = Modifier.heightIn(min = 48.dp),
                    )
                    Text("Solo pendientes", style = MaterialTheme.typography.labelSmall)
                }
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(92.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 390.dp),
                ) {
                    items(
                        items = shown,
                        key = { it.packageName },
                        contentType = { "s3-review" },
                    ) { entry ->
                        val app = appByPackage[entry.packageName]
                        val resource = s3AppArtwork(entry.packageName)

                        Column(
                            modifier = Modifier
                                .clickable { detail = entry }
                                .heightIn(min = 48.dp)
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            // Icon: approved via existing s3AppArtwork only; no generic substitution as approved
                            if (entry.status == "EXISTING_CANDIDATE" && resource != null) {
                                Image(
                                    painter = painterResource(resource),
                                    contentDescription = null,
                                    modifier = Modifier.size(size),
                                )
                            } else if (entry.status == "PENDING" && app != null && onLoadIcon != null) {
                                // Native app icon via passed onLoadIcon only when a matching app exists
                                val nativeBitmap = remember(app) {
                                    onLoadIcon(app).let { drawable ->
                                        runCatching {
                                            drawable.toBitmap(
                                                drawable.intrinsicWidth.coerceAtLeast(32),
                                                drawable.intrinsicHeight.coerceAtLeast(32),
                                            ).asImageBitmap()
                                        }.getOrNull()
                                    }
                                }
                                nativeBitmap?.let { bitmap ->
                                    Image(
                                        bitmap = bitmap,
                                        contentDescription = null,
                                        modifier = Modifier.size(size),
                                    )
                                } ?: Box(
                                    modifier = Modifier.size(size),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("?", style = MaterialTheme.typography.labelSmall)
                                }
                            } else {
                                Box(
                                    modifier = Modifier.size(size),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("Pending", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Text(
                                entry.label,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall,
                            )
                            Text(
                                when {
                                    entry.status == "EXISTING_CANDIDATE" && entry.kind == "AI" ->
                                        "Interpretaci\u00f3n IA"
                                    entry.status == "EXISTING_CANDIDATE" && entry.kind == "HISTORICAL_ROLE" ->
                                        "Referencia de \u00e9poca"
                                    else -> "Pendiente"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        },
    )

    detail?.let { entry ->
        AuraAlertDialog(
            onDismissRequest = { detail = null },
            confirmButton = { TextButton(onClick = { detail = null }) { Text("Volver") } },
            title = { Text(entry.label) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(entry.packageName, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    Text(entry.confidence, style = MaterialTheme.typography.bodySmall, maxLines = 3)
                    if (entry.reference.isNotBlank()) {
                        Text(
                            "Referencia: ${entry.reference}",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                        )
                    }
                    Text("Tipo: ${entry.kind}", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    Text("Estado: ${entry.status}", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    Text(
                        "La revisi\u00f3n de este cat\u00e1logo no sustituye iconos ni aprueba recursos autom\u00e1ticamente.",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                    )
                }
            },
        )
    }
}
