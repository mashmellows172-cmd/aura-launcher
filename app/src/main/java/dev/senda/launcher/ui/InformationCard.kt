package dev.senda.launcher.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    val scheme=MaterialTheme.colorScheme
    val skin=LocalSkinStyle.current
    val industrial=LocalIndustrialStyle.current.enabled
    val retro=LocalRetroStyle.current.enabled
    val scale=LocalDensity.current.fontScale
    val minHeight=informationMinHeight(style,scale)
    val lineCount=informationValueLines(style,scale)
    val retroTheme = LocalRetroStyle.current.theme
    val border = if (selected) skin.accent else when (retroTheme) {
        LauncherTheme.RAZR -> Color(0xFF62D0FF).copy(alpha = 0.55f)
        LauncherTheme.XPERIA -> Color(0xFFA370F7).copy(alpha = 0.5f)
        LauncherTheme.TOUCHWIZ -> Color(0xFF38B6FF).copy(alpha = 0.45f)
        LauncherTheme.SENSE -> Color(0xFF98C846).copy(alpha = 0.4f)
        LauncherTheme.OPTIMUS -> Color(0xFF00D4BB).copy(alpha = 0.45f)
        else -> if (industrial) IndustrialTokens.outline.copy(alpha = 0.55f) else scheme.outline.copy(alpha = 0.45f)
    }
    val cardBg = when (retroTheme) {
        LauncherTheme.RAZR -> Color(0xFF0D1722)
        LauncherTheme.XPERIA -> Color(0xFF161226)
        LauncherTheme.TOUCHWIZ -> Color(0xFF0B2138)
        LauncherTheme.SENSE -> Color(0xFF1F2827)
        LauncherTheme.OPTIMUS -> Color(0xFF1A1728)
        else -> scheme.surface
    }
    val gradient = if (retro && retroTheme == LauncherTheme.TOUCHWIZ) {
        Brush.verticalGradient(listOf(Color(0xFF143555), Color(0xFF0B2138)))
    } else if (retro && retroTheme == LauncherTheme.SENSE) {
        Brush.verticalGradient(listOf(Color(0xFF283432), Color(0xFF1F2827)))
    } else if (retro) {
        Brush.verticalGradient(listOf(scheme.surfaceVariant, scheme.surface))
    } else null

    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = minHeight.dp).semantics { contentDescription = "$label · $value" },
        shape = MaterialTheme.shapes.large,
        color = cardBg,
        contentColor = scheme.onSurface,
        border = BorderStroke(if (selected) 2.dp else 1.dp, border)
    ) {
        Row(
            Modifier.then(if (gradient != null) Modifier.background(gradient) else Modifier).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InformationGlyph(kind, if (industrial || retro) skin.accent else scheme.onSurfaceVariant)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    label,
                    style = if (retroTheme == LauncherTheme.RAZR) MaterialTheme.typography.labelSmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace) else MaterialTheme.typography.labelSmall,
                    fontSize = 12.sp,
                    color = if (retroTheme == LauncherTheme.RAZR) Color(0xFF62D0FF) else scheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    value,
                    style = if (retroTheme == LauncherTheme.RAZR) MaterialTheme.typography.bodyMedium.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace) else MaterialTheme.typography.bodyMedium,
                    fontSize = style.textSize.sp,
                    lineHeight = (style.textSize * 1.3f).sp,
                    maxLines = lineCount,
                    overflow = TextOverflow.Ellipsis
                )
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
