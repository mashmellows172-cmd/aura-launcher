package dev.senda.launcher.ui

import android.animation.ValueAnimator
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.focus.onFocusChanged
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
    mediaContent: @Composable (() -> Unit) -> Unit = {},
) {
    val highContrast = LocalAppearance.current.highContrast
    val settings = LocalMotionSettings.current
    val noMotion = settings.reduceMotion || highContrast || LocalThemePreview.current ||
        settings.menuEffect == "NONE" || !ValueAnimator.areAnimatorsEnabled()
    val motion = rememberPs4PanelMotion(noMotion)
    val entrance = motion.progress
    val close = { motion.close(noMotion, onDismiss) }

    val colors = MaterialTheme.colorScheme
    val panel = colors.surface
    val ink = colors.onSurface
    val muted = colors.onSurfaceVariant
    val separator = colors.outline
    val actionColors = ButtonDefaults.textButtonColors(
        contentColor = ink,
        disabledContentColor = muted,
    )

    Dialog(
        onDismissRequest = close,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
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
                        onClick = close,
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
                            translationX = -size.width * .16f * (1f - progress)
                        }
                        .semantics { paneTitle = "Menú rápido" },
                    shape = RectangleShape,
                    color = panel,
                    contentColor = ink,
                    tonalElevation = 0.dp,
                ) {
                    Box(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxSize().safeDrawingPadding()) {
                        TextButton(
                            onClick = close,
                            modifier = Modifier.align(Alignment.End).heightIn(min = 48.dp),
                            colors = actionColors,
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        ) { Text("Cerrar") }
                        Column(
                            Modifier.weight(1f).fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 24.dp),
                        ) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Ps4Mark()
                                Column(Modifier.weight(1f)) {
                                    Text("Aura", style = MaterialTheme.typography.labelLarge, color = muted)
                                    Text("Menú rápido", modifier = Modifier.semantics { heading() },
                                        style = MaterialTheme.typography.headlineSmall)
                                }
                            }
                            HorizontalDivider(color = separator.copy(alpha = .65f))
                            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                                mediaContent(close)
                            }
                            actions.forEach { action ->
                                val section = when (action.label) {
                                    "Tema visual" -> "Inicio"
                                    "Clima en el reloj" -> "Clima y perfil"
                                    "Inicio y gestos" -> "Configuración"
                                    "Sonidos y respuesta" -> "Sonido y movimiento"
                                    "Centro de estado" -> "Estado y herramientas"
                                    else -> null
                                }
                                if (section != null) {
                                    HorizontalDivider(Modifier.padding(top = 12.dp), color = separator.copy(alpha = .3f))
                                    Text(section, modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 4.dp)
                                        .semantics { heading() }, style = MaterialTheme.typography.labelLarge, color = muted)
                                }
                                val interaction = remember(action.label) { MutableInteractionSource() }
                                val pressed by interaction.collectIsPressedAsState()
                                var focused by remember(action.label) { mutableStateOf(false) }
                                TextButton(
                                    onClick = { motion.close(noMotion, action.onClick) },
                                    enabled = action.enabled,
                                    interactionSource = interaction,
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                        .onFocusChanged { focused = it.hasFocus }
                                        .background(if (action.enabled && (focused || pressed))
                                            ink.copy(alpha = .14f) else Color.Transparent),
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
                    if (!noMotion) {
                        Box(Modifier.align(Alignment.CenterEnd).fillMaxHeight().width(2.dp)
                            .graphicsLayer { val p = entrance.value; alpha = 4f * p * (1f - p) }
                            .background(colors.primary))
                    }
                    }
                }
            }
        }
    }
}
