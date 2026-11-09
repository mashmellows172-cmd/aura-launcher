package dev.senda.launcher.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.ui.theme.rememberClockChange

@Composable
internal fun Ps4HeaderClock(time: String, size: TextUnit, reduced: Boolean, onOpen: () -> Unit) {
    val progress = rememberClockChange(time, LauncherTheme.PS4, reduced, durationMs = 540)
    val prominent = size.value >= 40f
    val accent = MaterialTheme.colorScheme.primary
    Box(
        Modifier.heightIn(min = 48.dp)
            .then(if (prominent && !reduced) Modifier.ps4HeaderSweep(progress, accent, .75f) else Modifier)
            .clickable(role = Role.Button, onClick = onOpen)
            .semantics { contentDescription = "Abrir alarmas · $time" }
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            time,
            modifier = Modifier.graphicsLayer {
                val p = progress.value
                alpha = .66f + .34f * p
                translationY = (1f - p) * (if (prominent) 12.dp else 4.dp).toPx()
            },
            fontSize = size,
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.displayMedium.copy(
                lineHeight = (size.value * 1.12f).sp,
                fontFeatureSettings = "tnum",
            ),
            maxLines = 1,
        )
    }
}

/** One cached beam, clipped to its readout. The idle draw exits before submitting a shape. */
internal fun Modifier.ps4HeaderSweep(progress: State<Float>, accent: Color, intensity: Float): Modifier =
    clipToBounds().drawWithCache {
        val band = size.width * .75f
        val beamSize = Size(band, size.height)
        val beam = Brush.linearGradient(
            listOf(Color.Transparent, accent.copy(alpha = .28f), Color.Transparent),
            end = Offset(band, size.height * .45f),
        )
        onDrawBehind {
            val p = progress.value
            if (p > 0f && p < 1f) {
                val x = (size.width + band) * p - band
                translate(left = x) {
                    drawRect(beam, size = beamSize, alpha = 4f * p * (1f - p) * intensity.coerceIn(0f, 1f))
                }
            }
        }
    }
