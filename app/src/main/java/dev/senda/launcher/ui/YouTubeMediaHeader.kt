package dev.senda.launcher.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.ActiveAudioInfo
import dev.senda.launcher.data.LauncherTheme
import kotlin.math.roundToInt

/** A video thumbnail keeps its full frame; all playback belongs to the source session. */
@Composable
internal fun YouTubeMediaHeader(
    audio: ActiveAudioInfo,
    expanded: Boolean,
    animated: Boolean,
    accent: State<Color>,
    onExpandedChange: () -> Unit,
    onOpen: () -> Unit,
    onCoverPositioned: (LayoutCoordinates) -> Unit = {},
    secondaryInk: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    actionInk: Color = MaterialTheme.colorScheme.primary,
    progress: @Composable () -> Unit = {},
) {
    val ratio = animateFloatAsState(if (expanded) 16f / 9f else 2.6f,
        tween(if (animated) 240 else 0, easing = Ps4Motion.easing), label = "youtube-thumbnail")
    val cover = audio.artwork?.takeUnless { it.isRecycled }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("YouTube · ${audio.playbackLabel}", modifier = Modifier.weight(1f),
                color = secondaryInk, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            TextButton(onClick = onOpen, modifier = Modifier.heightIn(min = 48.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = actionInk)) { Text("Ver vídeo") }
        }
        Box(Modifier.fillMaxWidth().layout { measurable, constraints ->
                val width = constraints.maxWidth
                val height = constraints.constrainHeight((width / ratio.value).roundToInt())
                val thumbnail = measurable.measure(Constraints.fixed(width, height))
                layout(width, height) { thumbnail.placeRelative(0, 0) }
            }.onGloballyPositioned(onCoverPositioned)
            .clip(RoundedCornerShape(12.dp)).background(Color(0xFF050912)).sonyGlassRim(accent)
            .clickable(onClickLabel = if (expanded) "Contraer tarjeta de vídeo" else "Ampliar tarjeta de vídeo",
                onClick = onExpandedChange)
            .semantics { contentDescription = "Miniatura de ${audio.title ?: "YouTube"}" },
            contentAlignment = Alignment.Center,
        ) {
            if (cover != null) Image(cover.asImageBitmap(), null,
                Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
            else CompositionLocalProvider(LocalContentColor provides Color.White) {
                ThemeSystemIcon("play", LauncherTheme.PS4, Modifier.size(42.dp))
            }
        }
        Text(audio.title ?: "Vídeo de YouTube", style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium, maxLines = if (expanded) 3 else 2, overflow = TextOverflow.Ellipsis)
        audio.artist?.takeIf(String::isNotBlank)?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium,
                color = secondaryInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        progress()
    }
}

