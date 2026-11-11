package dev.senda.launcher.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.senda.launcher.data.*
import dev.senda.launcher.ui.theme.*

/** Shared by the real home and the editor. Never embeds a running widget or a second clock timer. */
@Composable
internal fun InformationCard(label:String,value:String,kind:HomeBlock,onClick:()->Unit,modifier:Modifier=Modifier,style:BlockStyle=BlockStyle(BlockAnchor.HEADER),selected:Boolean=false) {
    val theme = if (LocalIndustrialStyle.current.enabled) LauncherTheme.NOTHING else LocalRetroStyle.current.theme
    val scale = LocalDensity.current.fontScale
    val minHeight = informationMinHeight(style, scale)
    val lineCount = informationValueLines(style, scale)
    HistoricalPanel(theme, modifier.heightIn(min = minHeight.dp).semantics { contentDescription = "$label · $value" }, onClick, selected) {
        val scheme = MaterialTheme.colorScheme
        Row(Modifier.padding(horizontal = if (theme == LauncherTheme.OPTIMUS) 14.dp else 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ThemeSystemIcon(if (kind == HomeBlock.ALARM) "alarm" else "calendar", theme ?: LauncherTheme.SYSTEM)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 12.sp,
                    color = scheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(value, style = MaterialTheme.typography.bodyMedium, fontSize = style.textSize.sp,
                    lineHeight = (style.textSize * 1.3f).sp, maxLines = lineCount,
                    color = scheme.onSurface, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
