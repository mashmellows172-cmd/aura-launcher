package dev.senda.launcher.ui

import android.os.SystemClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import dev.senda.launcher.ui.theme.*
import dev.senda.launcher.data.LauncherTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.VisualSettings
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.exp

private object RailTokens {
    val column = 32.dp
    val bubble = 52.dp
    val bubbleGap = 12.dp
    val magnetTravel = 16.dp
    const val bubbleHoldMs = 420L
    const val hapticGapMs = 45L
}

/** One gesture surface for scrub; per-letter semantics remain available to accessibility. */
@Composable
fun AlphabetRail(
    letters: List<String>, theme: LauncherTheme = LauncherTheme.NOTHING, modifier: Modifier = Modifier, selectedLetter: String?,
    visualSettings: VisualSettings, onClick: (String) -> Unit, onScrub: (String) -> Unit,
) {
    if (letters.isEmpty()) return
    val soundFeedback = dev.senda.launcher.audio.LocalSoundFeedback.current
    val profile = historicalProfile(theme).takeUnless { dev.senda.launcher.data.LocalAppearance.current.highContrast }
    val markerShape = profile.chromeShape()
    val haptics = LocalHapticFeedback.current
    var dragging by remember { mutableStateOf(false) }
    var fingerIndex by remember { mutableFloatStateOf(0f) }
    var chosen by remember { mutableStateOf<String?>(null) }
    var held by remember { mutableStateOf(false) }
    var lastHaptic by remember { mutableLongStateOf(0L) }
    var releaseToken by remember { mutableIntStateOf(0) }
    val reduced = dev.senda.launcher.data.LocalAppearance.current.highContrast || visualSettings.reduceMotion || visualSettings.railEffect == "NONE"
    LaunchedEffect(releaseToken, dragging) {
        if (!dragging) { delay(RailTokens.bubbleHoldMs); held = false }
    }
    val select: (Int, Boolean) -> Unit = { index, scrub ->
        val letter = letters[index.coerceIn(0, letters.lastIndex)]
        if (letter != chosen || !scrub) {
            soundFeedback.play(dev.senda.launcher.data.SoundEvent.INDEX)
            chosen = letter
            if (visualSettings.railHaptics && SystemClock.uptimeMillis() - lastHaptic >= RailTokens.hapticGapMs) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                lastHaptic = SystemClock.uptimeMillis()
            }
            if (scrub) onScrub(letter) else onClick(letter)
        }
        held = true
        releaseToken++
    }
    val currentSelect by rememberUpdatedState(select)
    BoxWithConstraints(modifier.padding(vertical = 4.dp)) {
        val columns = (maxWidth / RailTokens.column).toInt().coerceAtLeast(1)
        val rows = ((letters.size + columns - 1) / columns).coerceAtLeast(1)
        val columnWidth = maxWidth / columns
        val slot = maxHeight / rows
        val selected = letters.indexOf(if (held) chosen else selectedLetter).coerceAtLeast(0)
        val indicatorY by animateDpAsState(slot * (selected % rows), if (reduced || visualSettings.railEffect == "MAGNET" && dragging) snap() else if (visualSettings.railEffect == "SPRING") spring(.68f, 650f * (360f / visualSettings.durationMs) * (360f / visualSettings.durationMs)) else tween(130), label = "rail-position")
        Box(Modifier.offset { IntOffset((columnWidth * (selected / rows)).roundToPx(), indicatorY.roundToPx()) }
            .width(columnWidth).height(slot).padding(1.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = .18f), markerShape)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = .55f), markerShape))
        Row(Modifier.fillMaxSize().pointerInput(letters, columns, rows) {
            var previous = -1
            fun move(position: Offset) {
                val column = (position.x / (size.width / columns.toFloat())).toInt().coerceIn(0, columns - 1)
                val row = (position.y / (size.height / rows.toFloat())).coerceIn(0f, rows - .001f)
                fingerIndex = column * rows + row - .5f
                val index = (column * rows + row.toInt()).coerceIn(0, letters.lastIndex)
                if (index != previous) { currentSelect(index, true); previous = index }
            }
            detectDragGestures(onDragStart = { dragging = true; previous = -1; move(it) },
                onDrag = { change, _ -> change.consume(); move(change.position) },
                onDragEnd = { dragging = false; releaseToken++ }, onDragCancel = { dragging = false; releaseToken++ })
        }) {
            repeat(columns) { column ->
                Column(Modifier.weight(1f).fillMaxHeight()) {
                    repeat(rows) { row ->
                        val index = column * rows + row
                        val letter = letters.getOrNull(index)
                        Box(Modifier.weight(1f).fillMaxWidth().then(if (letter == null) Modifier else Modifier
                            .semantics { contentDescription = "Saltar a la sección $letter"; role = Role.Button }
                            .clickable { select(index, false) }), contentAlignment = Alignment.Center) {
                            if (letter != null) Text(letter, Modifier.graphicsLayer {
                                val influence = if (dragging && !reduced && visualSettings.railEffect == "MAGNET") exp(-abs(index - fingerIndex) * .9f) else 0f
                                translationX = if (columns == 1) -influence * RailTokens.magnetTravel.toPx() else 0f
                                scaleX = 1f + influence * .65f; scaleY = scaleX
                            }, color = if (selected == index) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold, fontSize = 10.sp, lineHeight = 12.sp)
                        }
                    }
                }
            }
        }
        val bubbleY = (slot * (selected % rows) + slot / 2 - RailTokens.bubble / 2).coerceIn(0.dp, (maxHeight - RailTokens.bubble).coerceAtLeast(0.dp))
        Box(Modifier.offset { IntOffset(-(RailTokens.bubble + RailTokens.bubbleGap).roundToPx(), bubbleY.roundToPx()) }) {
            AnimatedVisibility(held, enter = if (reduced) fadeIn(tween(0)) else if (visualSettings.railEffect == "FADE") fadeIn(tween(140)) else fadeIn(tween(100)) + scaleIn(spring(.7f, 600f), initialScale = .65f),
                exit = if (reduced) fadeOut(tween(0)) else fadeOut(tween(130)) + scaleOut(tween(130), targetScale = .8f)) {
                HistoricalPanel(theme, Modifier.size(RailTokens.bubble).clearAndSetSemantics {}, selected = true) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(letters[selected], fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    }
                }
            }
        }
    }
}
