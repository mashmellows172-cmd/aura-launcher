package dev.senda.launcher.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/** The rail is intentionally the only always-visible context surface in Quick Center. */
internal data class QuickRailItemModel(
    val id: String,
    val icon: String,
    val title: String,
    val summary: String,
    val artwork: Bitmap? = null,
)

@Composable
internal fun QuickCenterRail(
    items: List<QuickRailItemModel>,
    selectedId: String,
    revealedId: String?,
    onSelect: (String) -> Unit,
    onReveal: (String) -> Unit,
    onCollapse: (String) -> Unit,
    motionEnabled: Boolean,
    artworkInFlight: Boolean,
    onBounds: (String, Rect?) -> Unit,
    onArtworkBounds: (Rect?) -> Unit,
) {
    val state = rememberLazyListState()
    val selectedIndex = items.indexOfFirst { it.id == selectedId }
    LaunchedEffect(selectedId, selectedIndex) {
        if (selectedIndex >= 0) state.scrollToItem(selectedIndex)
    }
    LazyRow(
        state = state,
        modifier = Modifier,
        contentPadding = PaddingValues(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items, key = { it.id }, contentType = { "quick-center-rail" }) { item ->
            QuickCenterRailItem(item, item.id == selectedId, item.id == revealedId,
                motionEnabled = motionEnabled, hideArtwork = artworkInFlight && item.id == "Música",
                onBounds = { onBounds(item.id, it) }, onArtworkBounds = onArtworkBounds,
                onClick = { onSelect(item.id) },
                onReveal = { onReveal(item.id) }, onCollapse = { onCollapse(item.id) })
        }
    }
}

@Composable
private fun QuickCenterRailItem(
    item: QuickRailItemModel,
    selected: Boolean,
    revealed: Boolean,
    motionEnabled: Boolean,
    hideArtwork: Boolean,
    onBounds: (Rect?) -> Unit,
    onArtworkBounds: (Rect?) -> Unit,
    onClick: () -> Unit,
    onReveal: () -> Unit,
    onCollapse: () -> Unit,
) {
    DisposableEffect(item.id) {
        onDispose {
            onBounds(null)
            if (item.id == "Música") onArtworkBounds(null)
        }
    }
    var dragDistance by remember(item.id) { mutableFloatStateOf(0f) }
    val shape = RoundedCornerShape(14.dp)
    val gesture = Modifier.pointerInput(item.id) {
        detectVerticalDragGestures(
            onVerticalDrag = { change, distance ->
                change.consume()
                dragDistance = (dragDistance + distance).coerceIn(-84f, 84f)
            },
            onDragCancel = { dragDistance = 0f },
            onDragEnd = {
                when {
                    dragDistance < -28f -> onReveal()
                    dragDistance > 42f -> onCollapse()
                }
                dragDistance = 0f
            },
        )
    }
    Column(
        modifier = Modifier.widthIn(min = 96.dp, max = 142.dp).heightIn(min = 64.dp)
            .onGloballyPositioned { coordinates ->
                val point = coordinates.positionOnScreen()
                if (point.x.isFinite() && point.y.isFinite()) onBounds(Rect(point,
                    Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat())))
            }
            .graphicsLayer {
                translationY = if (motionEnabled) dragDistance * .12f else 0f
                val pull = if (motionEnabled) abs(dragDistance) / 84f else 0f
                scaleX = 1f - pull * .015f
                scaleY = 1f - pull * .015f
            }
            .clip(shape)
            .ps4ControlGlass(shape, translucent = !selected)
            .then(if (selected) Modifier.background(quickAccent.copy(alpha = .12f), shape) else Modifier)
            .clickable(onClickLabel = if (selected && revealed) "Contraer ${item.title}" else "Mostrar ${item.title}",
                onClick = { if (selected && revealed) onCollapse() else if (selected) onReveal() else onClick() })
            .then(gesture)
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .semantics {
                this.selected = selected
                stateDescription = when {
                    revealed -> "Ampliado. Desliza abajo para contraer"
                    selected -> "Seleccionado. Desliza arriba para mostrar"
                    else -> "Desliza arriba para mostrar"
                }
                contentDescription = "${item.title}. ${item.summary}"
                customActions = listOf(
                    CustomAccessibilityAction("Mostrar ${item.title}") { onReveal(); true },
                    CustomAccessibilityAction("Contraer ${item.title}") { onCollapse(); true },
                )
            },
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        val cover = item.artwork?.takeUnless { it.isRecycled }
        if (cover != null) Image(cover.asImageBitmap(), null,
            Modifier.size(24.dp).onGloballyPositioned { coordinates ->
                val point = coordinates.positionOnScreen()
                if (point.x.isFinite() && point.y.isFinite()) onArtworkBounds(Rect(point,
                    Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat())))
            }.graphicsLayer { alpha = if (hideArtwork) 0f else 1f }
                .clip(RoundedCornerShape(6.dp)), contentScale = ContentScale.Crop)
        else ThemeSystemIcon(item.icon, activeQuickTheme, Modifier.height(20.dp))
        Text(item.title, maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium)
        Text(item.summary, maxLines = 1, overflow = TextOverflow.Ellipsis,
            color = quickMuted, style = MaterialTheme.typography.labelSmall)
    }
}
