package dev.senda.launcher.ui

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.senda.launcher.data.LocalAppearance

internal data class Ps4QuickAction(
    val label: String,
    val enabled: Boolean = true,
    val onClick: () -> Unit,
)

/** A modal side panel; callers own navigation and dismissal after an action. */
@Composable
internal fun Ps4QuickMenu(
    actions: List<Ps4QuickAction>,
    onDismiss: () -> Unit,
    mediaContent: @Composable () -> Unit = {},
) {
    val highContrast = LocalAppearance.current.highContrast
    val settings = LocalMotionSettings.current
    val noMotion = settings.reduceMotion || highContrast || LocalThemePreview.current ||
        settings.menuEffect == "NONE" || !ValueAnimator.areAnimatorsEnabled()
    val entrance = remember { Animatable(if (noMotion) 1f else 0f) }
    LaunchedEffect(noMotion) {
        if (noMotion) entrance.snapTo(1f)
        else entrance.animateTo(1f, tween(320, easing = FastOutSlowInEasing))
    }

    val colors = MaterialTheme.colorScheme
    val panel = if (highContrast) colors.surface else Color(0xF00A1628)
    val ink = if (highContrast) colors.onSurface else Color.White
    val muted = if (highContrast) colors.onSurfaceVariant else Color(0xFFAFC0CC)
    val separator = if (highContrast) colors.outline else Color(0xFF0070D1)
    val actionColors = ButtonDefaults.textButtonColors(
        contentColor = ink,
        disabledContentColor = muted,
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier.fillMaxSize()
                    .graphicsLayer { alpha = entrance.value }
                    .background(colors.scrim.copy(alpha = .48f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClickLabel = "Cerrar menú rápido",
                        onClick = onDismiss,
                    )
                    .semantics { contentDescription = "Cerrar menú rápido" },
            )
            // Reserve an exposed strip even on narrow phones for outside dismissal.
            Box(Modifier.fillMaxSize().padding(end = 32.dp)) {
                Surface(
                    modifier = Modifier.align(Alignment.CenterStart)
                        .widthIn(max = 360.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .graphicsLayer {
                            val progress = entrance.value
                            alpha = progress
                            translationX = -size.width * (1f - progress)
                        }
                        .semantics { paneTitle = "Menú rápido" },
                    shape = RectangleShape,
                    color = panel,
                    contentColor = ink,
                    tonalElevation = 0.dp,
                ) {
                    Column(Modifier.fillMaxSize().safeDrawingPadding()) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.align(Alignment.End).heightIn(min = 48.dp),
                            colors = actionColors,
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        ) { Text("Cerrar") }
                        Column(
                            Modifier.weight(1f).fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 24.dp),
                        ) {
                            Text(
                                text = "Menú rápido",
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                                    .semantics { heading() },
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            HorizontalDivider(color = separator)
                            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                                mediaContent()
                            }
                            actions.forEach { action ->
                                TextButton(
                                    onClick = action.onClick,
                                    enabled = action.enabled,
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                    shape = RectangleShape,
                                    colors = actionColors,
                                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                                ) {
                                    Text(
                                        text = action.label,
                                        modifier = Modifier.fillMaxWidth(),
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
