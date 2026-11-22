package dev.senda.launcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.semantics.clearAndSetSemantics
import kotlin.math.roundToInt

/** Retains the measured destination while its layout folds back toward the live capsule. */
@Composable
internal fun QuickRailDetail(
    cardId: String,
    progress: State<Float>,
    source: () -> Rect?,
    interactive: Boolean,
    effect: String,
    content: @Composable () -> Unit,
) {
    var destination by remember(cardId) { mutableStateOf<Rect?>(null) }
    Box(Modifier.fillMaxWidth().clipToBounds().layout { measurable, constraints ->
        val child = measurable.measure(constraints)
        layout(child.width, (child.height * progress.value.coerceIn(0f, 1f)).roundToInt()) {
            child.placeRelative(0, 0)
        }
    }) {
        // Measure outside the transformed child so layer coordinates cannot feed back into themselves.
        Box(Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
            val point = coordinates.positionOnScreen()
            if (point.x.isFinite() && point.y.isFinite()) destination = Rect(point,
                Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat()))
        }) {
            Box(Modifier.fillMaxWidth()
                .ps4PanelTransform(progress, effect,
                    origin = source, destination = { destination })
                .then(if (!interactive) Modifier.clearAndSetSemantics {} else Modifier)
                .pointerInput(interactive) {
                    if (!interactive) awaitPointerEventScope {
                        while (true) awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                    }
                }) { content() }
        }
    }
}
