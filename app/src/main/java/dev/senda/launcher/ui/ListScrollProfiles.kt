package dev.senda.launcher.ui

import android.animation.ValueAnimator
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.data.Ps4ListProfile
import dev.senda.launcher.data.VisualSettings
import kotlin.math.abs

internal val modernListProfiles = listOf(
    "IOS_INERTIA" to "Inercia elástica · iOS",
    "LANDING_FOCUS" to "Foco al aterrizar · PS5",
    "DOT_PULSE" to "Pulso de puntos · Nothing",
)

@Composable
internal fun listMotionReduced(settings: VisualSettings): Boolean =
    settings.reduceMotion || LocalLightMode.current || LocalAppearance.current.highContrast ||
        LocalSystemPowerSaving.current || !ValueAnimator.areAnimatorsEnabled()

@Composable
internal fun rememberListFling(settings: VisualSettings): FlingBehavior {
    if (settings.motion != "IOS_INERTIA" || listMotionReduced(settings)) return ScrollableDefaults.flingBehavior()
    val intensity = settings.scrollIntensity.coerceIn(10, 100) / 100f
    val speedThreshold = with(LocalDensity.current) { 16.dp.toPx() }
    return remember(intensity, speedThreshold) {
        val decay = exponentialDecay<Float>(frictionMultiplier = 1.1f - .58f * intensity,
            absVelocityThreshold = speedThreshold)
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                if (abs(initialVelocity) < speedThreshold) return initialVelocity
                var previous = 0f
                var remainder = 0f
                AnimationState(0f, initialVelocity).animateDecay(decay) {
                    val delta = value - previous
                    previous = value
                    val consumed = scrollBy(delta)
                    if (abs(delta - consumed) > .5f) {
                        remainder = velocity
                        cancelAnimation()
                    }
                }
                // Compose's platform overscroll handles unconsumed momentum at both edges.
                return remainder
            }
        }
    }
}

@Composable
internal fun Modifier.listDotPulse(list: LazyListState, settings: VisualSettings, console: Boolean): Modifier {
    val motion = LocalMotionController.current
    if (settings.motion != "DOT_PULSE" || motion == null || listMotionReduced(settings)) return this
    val color = MaterialTheme.colorScheme.primary
    val iconSize = LocalAppearance.current.iconSize
    val intensity = settings.scrollIntensity.coerceIn(10, 100) / 100f
    return clipToBounds().drawBehind {
        val velocity = motion.velocity
        val energy = (abs(velocity) * 2.8f).coerceAtMost(1f) * intensity
        if (energy < .01f) return@drawBehind
        val layout = list.layoutInfo
        val slot = (iconSize + if (console) 27 else 8).dp.toPx().coerceAtMost(size.width * .30f)
        val originX = if (layoutDirection == LayoutDirection.Rtl) size.width - slot else slot
        val radius = 1.45.dp.toPx()
        val gap = 4.7.dp.toPx()
        val direction = if (velocity < 0f) -1f else 1f
        for (row in layout.visibleItemsInfo) {
            if (row.contentType != "app") continue
            val mid = row.offset - layout.viewportStartOffset + row.size / 2f
            if (mid < 0f || mid > size.height) continue
            for (dot in 0..4) {
                val opacity = energy * (1f - .16f * dot)
                val y = mid + (2f - dot) * gap * direction
                drawCircle(color, radius = radius * (1f - .08f * dot), center = Offset(originX, y), alpha = opacity)
            }
        }
    }
}


@Composable
internal fun ListScrollProfilePreview(settings: VisualSettings, ps4: Boolean = false) {
    val list = rememberLazyListState()
    val reduced = listMotionReduced(settings)
    val landing = settings.motion == "LANDING_FOCUS"
    val preferred = remember { mutableStateOf<String?>(null) }
    val focus = rememberPs4FocusedKey(list, ps4 || landing && !reduced, preferred)
    val observesScroll = settings.motion == "DOT_PULSE" ||
        ps4 && Ps4ListProfile.fromId(settings.motion)?.let { it.usesScrollInk || it.usesRowMotion } == true
    val motion = if (observesScroll && !reduced) rememberAuraMotion(list, settings) else null
    Text("Vista previa · desliza la lista", style = MaterialTheme.typography.labelMedium)
    CompositionLocalProvider(LocalPs4FocusedKey provides focus, LocalMotionController provides motion) {
        val shape = RoundedCornerShape(12.dp)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(164.dp).clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .listDotPulse(list, settings, console = ps4)
                .ps4ScrollDecoration(list, settings, console = ps4)
                .then(if (motion != null) Modifier.nestedScroll(motion.connection) else Modifier),
            state = list,
            flingBehavior = rememberListFling(settings),
            overscrollEffect = if (reduced || settings.motion == "NONE") null else rememberOverscrollEffect(),
        ) {
            items(20, key = { "demo-$it" }, contentType = { "app" }) { index ->
                Row(Modifier.fillMaxWidth().heightIn(min = 52.dp)
                    .then(if (ps4) Modifier.ps4ListRowMotion(list, "demo-$index", settings) else Modifier)
                    .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(Modifier.size(30.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(7.dp)),
                        contentAlignment = Alignment.Center) {
                        Text(('A' + index).toString(), color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Text("App de ejemplo ${index + 1}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
