package dev.senda.launcher.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
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
            modifier = Modifier.fillMaxWidth().heightIn(min = fieldHeight.dp).focusRequester(focus),
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
