package dev.senda.launcher.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.ui.theme.*

/** The text field still owns input/IME semantics. The external halo never intercepts a tap. */
@Composable
internal fun ThemedSearchField(
    theme: LauncherTheme, query: String, onQuery: (String) -> Unit, onClear: () -> Unit,
    onSearch: () -> Unit, focus: FocusRequester, interactions: MutableInteractionSource,
    glow: () -> Float, readOnly: Boolean = false, heightDp: Int = 56,
) {
    val profile = LocalHistoricalProfile.current
    val halo = profile?.readableAccent(LocalSkinStyle.current.accent) ?: MaterialTheme.colorScheme.primary
    val radius = (profile?.radius ?: 18).dp
    val fieldHeight = heightDp.coerceIn(56, 88)
    val fieldStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = (16 + (fieldHeight - 56) / 8).sp)
    val settings = LocalMotionSettings.current
    val focused = interactions.collectIsFocusedAsState()
    val sony = theme == LauncherTheme.PS4 || theme == LauncherTheme.XPERIA
    val animateLens = sony && !readOnly && settings.glowEnabled && !settings.reduceMotion &&
        !dev.senda.launcher.data.LocalAppearance.current.highContrast && !LocalSystemPowerSaving.current &&
        android.animation.ValueAnimator.areAnimatorsEnabled()
    val lens = animateFloatAsState(if (animateLens && focused.value) 1f else 0f,
        tween(if (animateLens) 270 else 0, easing = Ps4Motion.easing), label = "search-field-lens")
    HistoricalPanel(theme, Modifier.fillMaxWidth().drawWithCache {
        val rings = (3 downTo 1).map { ring ->
            val inset = ring * 2.8.dp.toPx()
            Triple(Offset(-inset, -inset), Size(size.width + inset * 2, size.height + inset * 2), Stroke((8 - ring).dp.toPx()))
        }
        val corner = CornerRadius(radius.toPx())
        onDrawBehind {
            val alpha = glow()
            if (alpha > .01f) rings.forEachIndexed { index, (origin, bounds, stroke) ->
                drawRoundRect(halo.copy(alpha = alpha * .09f / (3 - index)), origin, bounds, corner, style = stroke)
            }
        }
    }) {
        OutlinedTextField(
            value = query, onValueChange = onQuery,
            modifier = Modifier.fillMaxWidth().heightIn(min = fieldHeight.dp).focusRequester(focus)
                .then(if (animateLens) Modifier.drawWithCache {
                    val inset = 1.5.dp.toPx()
                    val stroke = Stroke(1.dp.toPx())
                    val ink = Brush.horizontalGradient(listOf(halo, halo.copy(alpha = .28f)))
                    val bounds = Size((size.width - 2 * inset).coerceAtLeast(0f), (size.height - 2 * inset).coerceAtLeast(0f))
                    onDrawWithContent {
                        drawContent()
                        val p = lens.value
                        if (p > 0f) {
                            val half = size.width * p / 2f
                            clipRect(left = center.x - half, right = center.x + half) {
                                drawRoundRect(ink, Offset(inset, inset), bounds, CornerRadius(radius.toPx()),
                                    alpha = settings.glowStrength.coerceIn(0, 100) / 100f, style = stroke)
                            }
                        }
                    }
                } else Modifier),
            interactionSource = interactions,
            textStyle = fieldStyle,
            placeholder = { Text("Buscar apps", style = fieldStyle) },
            leadingIcon = {
                ThemeSystemIcon("search", theme)
            },
            trailingIcon = if (query.isNotEmpty()) ({
                IconButton(onClick = onClear, modifier = Modifier.semantics { contentDescription = "Borrar búsqueda" }) { ThemeSystemIcon("close", theme) }
            }) else null,
            singleLine = true, readOnly = readOnly, shape = profile.chromeShape(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent, focusedContainerColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent, focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        )
    }
}
