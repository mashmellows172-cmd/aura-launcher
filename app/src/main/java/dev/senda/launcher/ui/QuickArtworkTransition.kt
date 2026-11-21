package dev.senda.launcher.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.ActiveAudioInfo
import kotlin.math.max
import kotlin.math.min

internal data class MediaArtworkOrigin(val bounds: Rect, val mediaKey: String)

internal fun mediaArtworkKey(audio: ActiveAudioInfo): String =
    listOf(audio.sessionId, audio.mediaId, audio.title).joinToString("|") {
        if (it == null) "-1:" else "${it.length}:$it"
    }

/** The caller hides the slot's image while this decorative overlay is mounted. */
@Composable
internal fun BoxScope.QuickArtworkOverlay(
    audio: ActiveAudioInfo,
    origin: MediaArtworkOrigin?,
    target: State<Rect?>,
    windowOrigin: State<Offset>,
    progress: State<Float>,
    modifier: Modifier = Modifier,
    visibleTarget: State<Rect?>? = null,
) {
    val bitmap = audio.artwork
    val image = remember(bitmap) { bitmap?.takeUnless { it.isRecycled }?.asImageBitmap() }
    val mediaKey = mediaArtworkKey(audio)
    val video = audio.isYouTubeVideo
    Spacer(modifier.matchParentSize().drawWithCache {
        val clip = Path()
        val sourceRadius = 6.dp.toPx()
        val targetRadius = 12.dp.toPx()
        onDrawBehind {
            if (bitmap == null || bitmap.isRecycled || image == null ||
                image.width <= 0 || image.height <= 0) return@onDrawBehind
            val destination = target.value?.takeIf { it.isArtworkRect() } ?: return@onDrawBehind
            val window = windowOrigin.value
            val rawProgress = progress.value
            if (!window.x.isFinite() || !window.y.isFinite() || !rawProgress.isFinite()) return@onDrawBehind
            val fraction = rawProgress.coerceIn(0f, 1f)
            val source = origin?.takeIf { it.mediaKey == mediaKey && it.bounds.isArtworkRect() }?.bounds
            val viewport = if ((source == null || fraction >= .999f) && visibleTarget != null) {
                val visible = visibleTarget.value?.takeIf { it.isArtworkRect() } ?: return@onDrawBehind
                visible.translate(-window).takeIf { it.isArtworkRect() } ?: return@onDrawBehind
            } else null
            // Always end at the live slot, including while the card itself is resizing.
            val absolute = if (source == null || fraction == 1f) destination else Rect(
                source.left + (destination.left - source.left) * fraction,
                source.top + (destination.top - source.top) * fraction,
                source.right + (destination.right - source.right) * fraction,
                source.bottom + (destination.bottom - source.bottom) * fraction,
            )
            val bounds = absolute.translate(-window)
            if (!bounds.isArtworkRect()) return@onDrawBehind
            val alpha = if (source == null) fraction else 1f
            if (alpha <= 0f) return@onDrawBehind
            val scaleX = bounds.width / image.width
            val scaleY = bounds.height / image.height
            val scale = if (video) min(scaleX, scaleY) else max(scaleX, scaleY)
            if (!scale.isFinite() || scale <= 0f) return@onDrawBehind
            val left = bounds.left + (bounds.width - image.width * scale) / 2f
            val top = bounds.top + (bounds.height - image.height * scale) / 2f
            val radius = if (source == null) targetRadius else
                sourceRadius + (targetRadius - sourceRadius) * fraction
            clip.reset()
            clip.addRoundRect(RoundRect(bounds, CornerRadius(min(radius, min(bounds.width, bounds.height) / 2f))))
            clipPath(clip) {
                clipRect(
                    left = viewport?.left ?: bounds.left,
                    top = viewport?.top ?: bounds.top,
                    right = viewport?.right ?: bounds.right,
                    bottom = viewport?.bottom ?: bounds.bottom,
                ) {
                    if (video) drawRect(Color(0xFF050912), bounds.topLeft, bounds.size, alpha = alpha)
                    withTransform({
                        translate(left, top)
                        scale(scale, scale, pivot = Offset.Zero)
                    }) {
                        drawImage(image, topLeft = Offset.Zero, alpha = alpha)
                    }
                }
            }
        }
    })
}

private fun Rect.isArtworkRect(): Boolean =
    left.isFinite() && top.isFinite() && right.isFinite() && bottom.isFinite() &&
        width.isFinite() && height.isFinite() && width > 0f && height > 0f
