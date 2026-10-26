package dev.senda.launcher.ui

import android.animation.ValueAnimator
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.senda.launcher.data.VisualSettings
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.*

/** One frame clock per list. No timer, sensor, bitmap snapshots or per-row coroutine. */
@Stable
class AuraMotionController {
    var velocity by mutableFloatStateOf(0f)
        private set
    private var pending = 0f
    private var previousItems: Map<Any, Int> = emptyMap()
    val connection = object : NestedScrollConnection {
        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            pending += consumed.y
            return Offset.Zero // Observation only: native scrolling retains all its movement.
        }
    }
    fun step(dt: Float, density: Float, active: Boolean, list: LazyListState, response: Float) {
        val items = list.layoutInfo.visibleItemsInfo
        val fallback = items.firstNotNullOfOrNull { item -> previousItems[item.key]?.let { item.offset - it } }?.toFloat() ?: 0f
        previousItems = items.associate { it.key to it.offset }
        val speed = (if (pending != 0f) pending else fallback) / (density * dt * MotionTokens.speedDpPerSecond)
        pending = 0f
        val target = if (active) speed.coerceIn(-1f, 1f) else 0f
        velocity += (target - velocity) * (1f - exp(-dt * response))
    }
    fun reset() { velocity = 0f; pending = 0f; previousItems = emptyMap() }
}

internal object MotionTokens {
    const val speedDpPerSecond = 1000f
    const val response = 20f
    const val idleEpsilon = .002f
    const val maxFrameSeconds = .032f
    const val cameraDistanceFactor = 1.5f
    const val foldDegrees = 52f
    const val orbitDegrees = 30f
    const val depthScale = .14f
    val depthTravel = 30.dp
    val waveTravel = 44.dp
    val orbitTravel = 52.dp
    val glideTravel = 26.dp
    val menuTravel = 28.dp
    val previewHeight = 170.dp
    val previewRow = 34.dp
    val previewRadius = 18.dp
    val previewPadding = 12.dp
    val previewIcon = 22.dp
    val previewIconRadius = 7.dp
    val previewIconGap = 12.dp
    val railPortrait = 40.dp
    val railLandscape = 96.dp
    const val glowRingAlpha = .28f
}

val LocalMotionSettings = staticCompositionLocalOf { VisualSettings() }
val LocalMotionController = staticCompositionLocalOf<AuraMotionController?> { null }

@Composable
fun rememberAuraMotion(list: LazyListState, settings: VisualSettings): AuraMotionController {
    val controller = remember(list) { AuraMotionController() }
    val density = LocalDensity.current.density
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(list, settings.reduceMotion, settings.motion, settings.durationMs, density, lifecycle) {
        try {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                snapshotFlow { list.isScrollInProgress }.collectLatest { active ->
                    if (settings.reduceMotion || settings.motion == "NONE" || !ValueAnimator.areAnimatorsEnabled()) {
                        controller.reset()
                        return@collectLatest
                    }
                    var previous = withFrameNanos { it }
                    do {
                        withFrameNanos { now ->
                            val dt = ((now - previous) / 1_000_000_000f).coerceIn(.001f, MotionTokens.maxFrameSeconds)
                            previous = now
                            controller.step(dt, density, active, list, MotionTokens.response * 360f / settings.durationMs)
                        }
                    } while (active || abs(controller.velocity) > MotionTokens.idleEpsilon)
                    controller.reset()
                }
                } finally { controller.reset() }
            }
        } finally { controller.reset() }
    }
    return controller
}

/** Called only from a graphics layer: scrolling never recomposes the app row. */
internal fun GraphicsLayerScope.applyAuraMotion(position: Float, velocity: Float, settings: VisualSettings) {
    rotationX = 0f; rotationY = 0f; rotationZ = 0f
    translationX = 0f; translationY = 0f; scaleX = 1f; scaleY = 1f; alpha = 1f
    transformOrigin = TransformOrigin.Center
    if (settings.reduceMotion || settings.motion == "NONE" || abs(velocity) < MotionTokens.idleEpsilon) return
    val p = position.coerceIn(-1f, 1f)
    val distance = abs(p)
    val speed = abs(velocity).coerceAtMost(1f)
    // Fade the final low-speed tail instead of snapping a nonzero transform to rest.
    val tail = ((speed - MotionTokens.idleEpsilon) / .06f).coerceIn(0f, 1f)
    val settle = tail * tail * (3f - 2f * tail)
    val energy = sqrt(speed) * settle * settings.scrollIntensity / 100f
    val direction = if (velocity < 0f) -1f else 1f
    cameraDistance = max(size.width, size.height).coerceAtLeast(1f) * MotionTokens.cameraDistanceFactor
    when (settings.motion) {
        "FOLD" -> {
            transformOrigin = TransformOrigin(.5f, if (p < 0) 1f else 0f)
            rotationX = -p * MotionTokens.foldDegrees * energy
            scaleX = 1f - distance * .09f * energy
            alpha = 1f - distance * .18f * energy
        }
        "WAVE" -> {
            val wave = sin(p * PI.toFloat() + direction * .5f)
            translationX = (wave + 1f) * .5f * MotionTokens.waveTravel.toPx() * energy
            rotationZ = wave * 4f * energy
            scaleX = 1f - distance * .07f * energy
            scaleY = 1f - distance * .05f * energy
        }
        "ORBIT" -> {
            translationX = distance * distance * MotionTokens.orbitTravel.toPx() * energy
            rotationY = -p * MotionTokens.orbitDegrees * energy
            rotationZ = p * 3f * energy
            scaleX = 1f - distance * .12f * energy
        }
        "GLIDE" -> {
            translationX = distance * distance * MotionTokens.glideTravel.toPx() * energy
            scaleX = 1f - distance * .045f * energy
            alpha = 1f - distance * .12f * energy
        }
        else -> {
            val scale = 1f - distance * MotionTokens.depthScale * energy
            scaleX = scale; scaleY = scale
            translationX = distance * distance * MotionTokens.depthTravel.toPx() * energy
            rotationY = -p * 14f * energy
            alpha = 1f - distance * .16f * energy
        }
    }
}

@Composable
fun Modifier.auraListMotion(list: LazyListState?, key: String?, settings: VisualSettings): Modifier {
    val controller = LocalMotionController.current
    return graphicsLayer {
        val info = list?.layoutInfo
        val item = info?.visibleItemsInfo?.firstOrNull { it.key == key }
        val center = if (info == null) 0f else (info.viewportStartOffset + info.viewportEndOffset) / 2f
        val position = if (item == null) 0f else (item.offset + item.size / 2f - center) / ((info!!.viewportEndOffset - info.viewportStartOffset) / 2f).coerceAtLeast(1f)
        applyAuraMotion(position, controller?.velocity ?: 0f, settings)
    }
}

/** Shared menu entrance, with a local override for its settings preview. Compose honors system duration scale. */
@Composable
fun AuraAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
) {
    val settings = LocalMotionSettings.current
    val progress = remember { Animatable(0f) }
    val theme = if (LocalIndustrialStyle.current.enabled) LauncherTheme.NOTHING else LocalRetroStyle.current.theme
    val profile = theme?.let(::historicalProfile)?.takeUnless { LocalAppearance.current.highContrast }
    val noMotion = settings.reduceMotion || LocalAppearance.current.highContrast || !ValueAnimator.areAnimatorsEnabled()
    LaunchedEffect(settings.menuEffect, noMotion, profile?.motion) {
        if (noMotion || settings.menuEffect == "NONE") progress.snapTo(1f)
        else {
            progress.snapTo(0f)
            progress.animateTo(1f, if (settings.menuEffect == "SPRING") spring(dampingRatio = .78f, stiffness = 420f * (360f / settings.durationMs).pow(2)) else tween(settings.durationMs.coerceAtMost(450)))
        }
    }
    val colors = MaterialTheme.colorScheme
    val container = profile?.bottom ?: colors.surface
    val ink = profile?.ink ?: colors.onSurface
    val muted = profile?.muted ?: colors.onSurfaceVariant
    val accent = profile?.readableAccent(LocalSkinStyle.current.accent) ?: colors.primary
    MaterialTheme(colorScheme = profile?.let { historicalColors(it, colors, accent) } ?: colors) {
    AlertDialog(onDismissRequest, confirmButton, modifier.graphicsLayer {
        val p = progress.value
        scaleX = 1f; scaleY = 1f; rotationX = 0f; translationY = 0f; translationX = 0f
        transformOrigin = TransformOrigin.Center
        alpha = p.coerceIn(0f, 1f)
        if (settings.menuEffect == "SPRING" && !noMotion) {
            when (profile?.motion) {
                HistoricalMotion.HINGE -> { rotationX = (1f - p) * 12f; transformOrigin = TransformOrigin(.5f, .1f) }
                HistoricalMotion.RIPPLE -> { scaleX = .94f + .06f * p; scaleY = .94f + .06f * p }
                HistoricalMotion.LAYERS -> translationY = (1f - p) * 14.dp.toPx()
                HistoricalMotion.GLIDE -> translationX = (1f - p) * 24.dp.toPx()
                HistoricalMotion.SEGMENTS -> { scaleY = .98f + .02f * p }
                HistoricalMotion.DOT_REVEAL -> { scaleX = .98f + .02f * p; scaleY = scaleX }
                else -> { scaleX = .9f + .1f * p; scaleY = scaleX }
            }
            cameraDistance = max(size.width, size.height).coerceAtLeast(1f) * MotionTokens.cameraDistanceFactor
        }
        if (settings.menuEffect == "SLIDE" && !noMotion) translationY = (1f - p) * MotionTokens.menuTravel.toPx()
    }, dismissButton = dismissButton, title = title, text = text,
        shape = profile?.chromeShape() ?: MaterialTheme.shapes.extraLarge,
        containerColor = container,
        tonalElevation = 0.dp,
        titleContentColor = ink,
        textContentColor = muted)
    }
}

/** Explicit replay and drag preview. No endless animation while the settings screen is idle. */
@Composable
fun MotionPreview(settings: VisualSettings) {
    val pulse = remember { Animatable(0f) }
    var replay by remember { mutableIntStateOf(0) }
    var dragging by remember { mutableFloatStateOf(0f) }
    val noMotion = settings.reduceMotion || LocalAppearance.current.highContrast || !ValueAnimator.areAnimatorsEnabled()
    LaunchedEffect(replay, settings.motion, settings.scrollIntensity, noMotion, settings.durationMs) {
        pulse.snapTo(0f)
        if (!noMotion && settings.motion != "NONE") {
            pulse.animateTo(-.8f, tween(250))
            pulse.animateTo(.8f, tween(650))
            pulse.animateTo(0f, tween(settings.durationMs))
        }
    }
    Column {
        Box(Modifier.fillMaxWidth().height(MotionTokens.previewHeight)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(MotionTokens.previewRadius))
            .pointerInput(noMotion) { if (noMotion) return@pointerInput; detectVerticalDragGestures(onDragEnd = { dragging = 0f }, onDragCancel = { dragging = 0f }) { change, amount -> change.consume(); dragging = (amount / 35f).coerceIn(-1f, 1f) } }
            .padding(MotionTokens.previewPadding)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                listOf("Mensajes", "Cámara", "Calendario", "Música").forEachIndexed { index, name ->
                    Row(Modifier.fillMaxWidth().height(MotionTokens.previewRow).graphicsLayer {
                        val v = if (noMotion) 0f else if (dragging != 0f) dragging else pulse.value
                        applyAuraMotion((index - 1.5f) / 1.7f, v, settings)
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(MotionTokens.previewIcon).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(MotionTokens.previewIconRadius)))
                        Spacer(Modifier.width(MotionTokens.previewIconGap)); Text(name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        TextButton(onClick = { replay++ }, enabled = !noMotion && settings.motion != "NONE") { Text("Reproducir vista previa") }
    }
}
