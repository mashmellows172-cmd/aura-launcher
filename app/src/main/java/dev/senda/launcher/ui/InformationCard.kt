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
import androidx.compose.ui.text.font.FontFamily
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
            InformationGlyph(kind, scheme.primary)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 12.sp,
                    fontFamily = if (theme == LauncherTheme.RAZR) FontFamily.Monospace else FontFamily.SansSerif,
                    color = scheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(value, style = MaterialTheme.typography.bodyMedium, fontSize = style.textSize.sp,
                    lineHeight = (style.textSize * 1.3f).sp, maxLines = lineCount,
                    color = scheme.onSurface, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
@Composable
private fun InformationGlyph(kind:HomeBlock,ink:androidx.compose.ui.graphics.Color) {
    Canvas(Modifier.size(20.dp)) {
        val stroke=Stroke(1.4.dp.toPx())
        val w=size.width;val h=size.height
        if(kind==HomeBlock.ALARM) {
            drawCircle(ink,w*.34f,Offset(w*.5f,h*.54f),style=stroke)
            drawLine(ink,Offset(w*.5f,h*.31f),Offset(w*.5f,h*.54f),stroke.width)
            drawLine(ink,Offset(w*.5f,h*.54f),Offset(w*.68f,h*.60f),stroke.width)
            drawLine(ink,Offset(w*.12f,h*.23f),Offset(w*.28f,h*.10f),stroke.width)
            drawLine(ink,Offset(w*.72f,h*.10f),Offset(w*.88f,h*.23f),stroke.width)
        } else {
            drawRect(ink,Offset(w*.15f,h*.20f),Size(w*.70f,h*.65f),style=stroke)
            drawLine(ink,Offset(w*.15f,h*.40f),Offset(w*.85f,h*.40f),stroke.width)
            drawLine(ink,Offset(w*.32f,h*.10f),Offset(w*.32f,h*.28f),stroke.width)
            drawLine(ink,Offset(w*.68f,h*.10f),Offset(w*.68f,h*.28f),stroke.width)
        }
    }
}
