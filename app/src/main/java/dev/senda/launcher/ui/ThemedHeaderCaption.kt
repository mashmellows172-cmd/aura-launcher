package dev.senda.launcher.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.LauncherTheme
import dev.senda.launcher.data.LocalAppearance
import dev.senda.launcher.ui.theme.historicalProfile
import java.util.Locale

@Composable
internal fun ThemedHeaderCaption(theme: LauncherTheme, message: String, reduced: Boolean) {
    val profile = historicalProfile(theme)
    val highContrast = LocalAppearance.current.highContrast
    val paper = theme == LauncherTheme.OPTIMUS && !highContrast
    val ink = if (paper) profile!!.ink else MaterialTheme.colorScheme.onBackground
    val style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = when (theme) { LauncherTheme.XPERIA -> FontWeight.Light; LauncherTheme.SENSE -> FontWeight.Medium; else -> FontWeight.Normal },
        letterSpacing = if (theme == LauncherTheme.XPERIA) .6.sp else .1.sp,
    )
    Crossfade(message, animationSpec = tween(if (reduced || highContrast) 0 else profile?.durationMs ?: 240), label = "header-message",
        modifier = Modifier.fillMaxWidth().heightIn(min = 30.dp)) { text ->
        Text(if (theme == LauncherTheme.RAZR) text.uppercase(Locale.forLanguageTag("es")) else text,
            modifier = if (paper) Modifier.background(profile!!.top, RoundedCornerShape(2.dp)).padding(horizontal = 7.dp, vertical = 3.dp) else Modifier.padding(vertical = 2.dp),
            style = style, color = ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}
