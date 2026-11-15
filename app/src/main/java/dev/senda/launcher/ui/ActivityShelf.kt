package dev.senda.launcher.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.ActivityCard
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance

private data class ActivityShare(val card: ActivityCard, val clear: () -> Unit)
private val LocalActivityShare = compositionLocalOf<ActivityShare?> { null }

@Composable
internal fun ActivityShareScope(card: ActivityCard?, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalActivityShare provides card?.let { ActivityShare(it, onDismiss) }, content = content)
}

private val covers = listOf(
    "Azul" to Color(0xFF003087), "Violeta" to Color(0xFF483278),
    "Verde" to Color(0xFF075956), "Cobre" to Color(0xFF7D3B32),
)

@Composable
private fun Modifier.activityCover(cover: Int): Modifier {
    val shape = RoundedCornerShape(8.dp)
    return clip(shape).background(if (LocalAppearance.current.highContrast) {
        Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface))
    } else Brush.horizontalGradient(listOf(covers[cover].second, Color(0xFF111D33))))
}

@Composable
internal fun ActivityShelf(showShelf: Boolean, ps4: Boolean) {
    val context = LocalContext.current
    val share = LocalActivityShare.current
    val prefs = remember(context) { context.getSharedPreferences("home_activity_cards", 0) }
    var cards by remember(prefs) { mutableStateOf(ActivityCard.decode(prefs.getString("cards", "[]").orEmpty())) }
    var library by rememberSaveable { mutableStateOf(false) }
    var draftKey by rememberSaveable { mutableStateOf<String?>(null) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    fun store(next: List<ActivityCard>) {
        prefs.edit().putString("cards", ActivityCard.encode(next)).apply()
        cards = next
    }

    fun open(card: ActivityCard) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(card.link)).addCategory(Intent.CATEGORY_BROWSABLE)
        try {
            context.startActivity(intent)
            error = null
            library = false
        } catch (_: ActivityNotFoundException) {
            error = "No hay una aplicación disponible para abrir este enlace."
            library = true
        } catch (_: SecurityException) {
            error = "Android no permite abrir este enlace."
            library = true
        }
    }

    if (showShelf && (ps4 || cards.isNotEmpty())) {
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            cards.firstOrNull()?.let { card ->
                ActivityTile(card, Modifier.weight(1f), onClick = { open(card) })
            }
            OutlinedButton(onClick = { error = null; library = true },
                modifier = Modifier.heightIn(min = 48.dp).then(if (cards.isEmpty()) Modifier.fillMaxWidth() else Modifier),
                contentPadding = PaddingValues(horizontal = 10.dp)) {
                Text("Actividades")
            }
        }
    }

    val draft = share?.card ?: draftKey?.let { id ->
        cards.firstOrNull { it.id == id } ?: ActivityCard(id, "", "", 0)
    }
    if (draft != null) key(draft.id) {
        val existing = cards.any { it.id == draft.id }
        val hasRoom = existing || cards.size < ActivityCard.MAX_CARDS
        ActivityEditor(draft, existing, hasRoom, cards.firstOrNull()?.id == draft.id, onDismiss = {
            if (share != null) share.clear() else draftKey = null
        }, onSave = { updated, promote ->
            if (hasRoom) {
                store(if (!existing || promote) listOf(updated) + cards.filterNot { it.id == updated.id }
                    else cards.map { if (it.id == updated.id) updated else it })
                draftKey = null
                if (share != null) {
                    share.clear()
                    library = true
                }
            }
        }, onRemove = {
            store(cards.filterNot { it.id == draft.id })
            draftKey = null
        })
    } else if (library) {
        AuraAlertDialog(
            onDismissRequest = { library = false; error = null },
            title = { Text("Actividades fijadas") },
            text = {
                Column(Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (cards.isEmpty()) Text("Pega un enlace o comparte contenido con Aura para fijarlo aquí.")
                    error?.let { message ->
                        Text(message, color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                    }
                    cards.forEach { card ->
                        key(card.id) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ActivityTile(card, Modifier.weight(1f), onClick = { open(card) })
                                TextButton(onClick = { draftKey = card.id }, modifier = Modifier.heightIn(min = 48.dp)) {
                                    Text("Editar", modifier = Modifier.semantics {
                                        contentDescription = "Editar ${card.title}"
                                    })
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(enabled = cards.size < ActivityCard.MAX_CARDS,
                    onClick = { draftKey = ActivityCard.draft().id }) { Text("Añadir enlace") }
            },
            dismissButton = { TextButton(onClick = { library = false; error = null }) { Text("Cerrar") } },
        )
    }
}

@Composable
private fun ActivityTile(card: ActivityCard, modifier: Modifier, onClick: () -> Unit) {
    val ink = if (LocalAppearance.current.highContrast) MaterialTheme.colorScheme.onSurface else Color.White
    Row(modifier.heightIn(min = 52.dp).activityCover(card.cover).clickable(onClickLabel = "Abrir actividad", onClick = onClick)
        .padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CompositionLocalProvider(LocalContentColor provides ink) {
            ThemeSystemIcon("layers", LauncherTheme.SYSTEM, Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(card.title, style = MaterialTheme.typography.titleSmall, color = ink, maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Text(card.host, style = MaterialTheme.typography.labelSmall, color = ink,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ActivityEditor(
    draft: ActivityCard,
    existing: Boolean,
    hasRoom: Boolean,
    featured: Boolean,
    onDismiss: () -> Unit,
    onSave: (ActivityCard, Boolean) -> Unit,
    onRemove: () -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(draft.title) }
    var link by rememberSaveable { mutableStateOf(draft.link) }
    var cover by rememberSaveable { mutableIntStateOf(draft.cover) }
    var promote by rememberSaveable { mutableStateOf(false) }
    var deleting by rememberSaveable { mutableStateOf(false) }
    val validated = remember(link) { ActivityCard.normalizeLink(link) }
    if (deleting) {
        AuraAlertDialog(onDismissRequest = { deleting = false },
            title = { Text("¿Quitar actividad?") },
            text = { Text(draft.title) },
            confirmButton = { TextButton(onClick = onRemove) { Text("Quitar") } },
            dismissButton = { TextButton(onClick = { deleting = false }) { Text("Cancelar") } })
        return
    }
    AuraAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing) "Editar actividad" else "Fijar actividad") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!hasRoom) Text("Quita una actividad para guardar otra.")
                OutlinedTextField(title, { title = it.take(ActivityCard.MAX_TITLE) }, Modifier.fillMaxWidth(),
                    label = { Text("Título") }, singleLine = true)
                OutlinedTextField(link, { link = it.take(ActivityCard.MAX_LINK) }, Modifier.fillMaxWidth(),
                    label = { Text("Enlace") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    isError = link.isNotBlank() && validated == null,
                    supportingText = { Text("Usa un enlace http:// o https://") })
                Box(Modifier.fillMaxWidth().heightIn(min = 48.dp).activityCover(cover)
                    .clickable(onClickLabel = "Cambiar portada") { cover = (cover + 1) % covers.size }.padding(12.dp),
                    contentAlignment = Alignment.CenterStart) {
                    Text("Portada · ${covers[cover].first}", color = if (LocalAppearance.current.highContrast)
                        MaterialTheme.colorScheme.onSurface else Color.White)
                }
                if (existing) {
                    if (!featured) Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Mostrar en Inicio", Modifier.weight(1f))
                        Switch(checked = promote, onCheckedChange = { promote = it },
                            modifier = Modifier.semantics { contentDescription = "Mostrar en Inicio" })
                    }
                    TextButton(onClick = { deleting = true }, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text("Quitar actividad")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = hasRoom && validated != null, onClick = {
                validated?.let { uri ->
                    onSave(draft.copy(title = title.trim().ifBlank { Uri.parse(uri).host.orEmpty() }, link = uri, cover = cover), promote)
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
